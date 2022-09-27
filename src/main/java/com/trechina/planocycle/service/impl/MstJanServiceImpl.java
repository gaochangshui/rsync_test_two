package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.po.JanHeaderAttr;
import com.trechina.planocycle.entity.po.JanInfoList;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.MstBranchMapper;
import com.trechina.planocycle.mapper.MstJanMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.mapper.ZokuseiMstMapper;
import com.trechina.planocycle.service.MstCommodityService;
import com.trechina.planocycle.service.MstJanService;
import com.trechina.planocycle.service.ZokuseiMstDataService;
import com.trechina.planocycle.utils.CacheUtil;
import com.trechina.planocycle.utils.ExcelUtils;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.dataConverUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MstJanServiceImpl implements MstJanService {

    @Autowired
    MstJanMapper mstJanMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private HttpSession session;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private ZokuseiMstDataService zokuseiMstDataService;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private LogAspect logAspect;
    @Autowired
    private MstCommodityService mstCommodityService;
    @Autowired
    private MstBranchMapper mstBranchMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * janデータのチェック
     * @param janParamVO 検索条件
     * commonPartsData 商品軸情報
     *     prodIsCore    0企業1自社
     *     prodMstClass  商品軸ID
     *     janContain    含まれる商品
     *     janKato       除外された商品
     *     fuzzyQuery    ファジィ照会、商品名の照会
     * @return
     */
    @Override
    public CheckVO getJanListCheck(JanParamVO janParamVO) {
        CheckVO checkVO = new CheckVO();
        //2類のパラメーター
        if("1".equals(janParamVO.getCommonPartsData().getProdIsCore())){
            janParamVO.setCompanyCd(sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY));
        }
        String janInfoTable = MessageFormat.format(MagicString.PROD_JAN_INFO, janParamVO.getCompanyCd(),
                janParamVO.getCommonPartsData().getProdMstClass());
        String janInfoTablePlanoCycle = MessageFormat.format(MagicString.PROD_JAN_INFO,
                MagicString.PLANO_CYCLE_COMPANY_CD,MagicString.FIRST_CLASS_CD);
        checkVO.setTotal(mstJanMapper.getJanCount(janParamVO, janInfoTable, janInfoTablePlanoCycle, "count(a.\"1\")"));
        String taskId = UUID.randomUUID().toString();
        JSONObject json = new JSONObject();
        json.put("janParamVO",janParamVO);
        json.put("janInfoTable",janInfoTable);
        json.put("janInfoTablePlanoCycle",janInfoTablePlanoCycle);
        json.put("janColumn",janParamVO.getClassCd());
        cacheUtil.put(taskId, json);
        checkVO.setTaskID(taskId);
        return checkVO;
    }

    /**
     * janデータの取得
     * @param downFlagVO 検索Task
     * @return
     */
    @Override
    public Map<String, Object> getJanList(DownFlagVO downFlagVO) {
        JanInfoVO janInfoVO = new JanInfoVO();
        if (cacheUtil.get(downFlagVO.getTaskID()) == null) {
            return ResultMaps.result(ResultEnum.FAILURE, "taskId not exists");
        }

        if(MagicString.TASK_STATUS_PROCESSING.equals(cacheUtil.get(downFlagVO.getTaskID()+MagicString.STATUS_STR))){
            JSONObject returnJson = new JSONObject();
            returnJson.put(MagicString.STATUS, "9");
            returnJson.put(MagicString.TASKID, downFlagVO.getTaskID());
            return ResultMaps.result(ResultEnum.SUCCESS, returnJson);
        }else if(MagicString.TASK_STATUS_EXCEPTION.equals(cacheUtil.get(downFlagVO.getTaskID()+MagicString.STATUS_STR))){
            return ResultMaps.result(ResultEnum.FAILURE);
        }else if(MagicString.TASK_STATUS_SUCCESS.equals(cacheUtil.get(downFlagVO.getTaskID()+MagicString.STATUS_STR))){
            JSONObject json = new JSONObject();
            json.put(MagicString.TASKID, downFlagVO.getTaskID());
            return ResultMaps.result(ResultEnum.SUCCESS, json);
        }

        cacheUtil.put(downFlagVO.getTaskID()+MagicString.STATUS_STR, MagicString.TASK_STATUS_PROCESSING);
        cacheUtil.put(downFlagVO.getTaskID()+MagicString.FLAG_STR, downFlagVO.getDownFlag());
        Future<?> futureTask = executor.submit(()->{
            try {
               this.janHandle(janInfoVO,downFlagVO);
            }catch (Exception e){
                logger.error("", e);
                cacheUtil.put(downFlagVO.getTaskID()+MagicString.STATUS_STR, MagicString.TASK_STATUS_EXCEPTION);
                cacheUtil.remove(downFlagVO.getTaskID()+MagicString.STATUS_STR);
            }
        });

        try {
            futureTask.get(MagicString.TASK_TIME_OUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            JSONObject returnJson = new JSONObject();
            returnJson.put(MagicString.STATUS, "9");
            returnJson.put(MagicString.TASKID, downFlagVO.getTaskID());
            return ResultMaps.result(ResultEnum.SUCCESS, returnJson);
        } catch(InterruptedException e ){
            Thread.currentThread().interrupt();
            logger.error("", e);
            cacheUtil.remove(downFlagVO.getTaskID()+MagicString.STATUS_STR);
            return ResultMaps.result(ResultEnum.FAILURE);
        } catch (ExecutionException e){
            logger.error("", e);
            cacheUtil.remove(downFlagVO.getTaskID()+MagicString.STATUS_STR);
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        JSONObject json = new JSONObject();
        json.put(MagicString.TASKID, downFlagVO.getTaskID());
        return ResultMaps.result(ResultEnum.SUCCESS, json);
    }

    public void janHandle(JanInfoVO janInfoVO, DownFlagVO downFlagVO){
        JSONObject json =JSON.parseObject(cacheUtil.get(downFlagVO.getTaskID()).toString());
        JanParamVO janParamVO = JSON.parseObject(json.getString("janParamVO"),JanParamVO.class);
        String janInfoTable = json.getString("janInfoTable");
        String janInfoTablePlanoCycle = json.getString("janInfoTablePlanoCycle");
        String tableNameAttr = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, janParamVO.getCompanyCd(),
                janParamVO.getCommonPartsData().getProdMstClass());
        String tableNameKaisou = MessageFormat.format(MagicString.PROD_JAN_KAISOU_HEADER_SYS, janParamVO.getCompanyCd(),
                janParamVO.getCommonPartsData().getProdMstClass());
        String tableNamePlanoCycle = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS,
                MagicString.PLANO_CYCLE_COMPANY_CD,MagicString.FIRST_CLASS_CD);
        String janColumn = json.getString("janColumn");
        List<JanHeaderAttr> janHeader = mstJanMapper.getJanHeader(tableNameAttr, tableNameKaisou,tableNamePlanoCycle, janColumn);
        List<JanHeaderAttr> janHeaderSort = new ArrayList<>();
        for (String column : janColumn.split(",")) {
            Optional<JanHeaderAttr> optional = janHeader.stream().filter(e->column.equals(e.getAttr())).findFirst();
            optional.ifPresent(janHeaderSort::add);
        }
        janHeader = janHeaderSort;
        //SQL文の列： a."1" "jan_cd",a."2" "jan_name",a."21" "kikaku",b."104" "planoWidth"
        String column = janHeader.stream().map(map -> "COALESCE(" + ("5".equals(map.getType()) ||"6".equals(map.getType()) ? "b" : "a") + ".\""
                        + map.getSort() + "\",'') AS \"" + dataConverUtils.camelize(map.getAttr()) + "\"")
                .collect(Collectors.joining(","));
        janInfoVO.setJanDataList(mstJanMapper.getJanList(janParamVO, janInfoTable, janInfoTablePlanoCycle, column));
        janInfoVO.setJanHeader(janHeader.stream().map(map -> String.valueOf(map.getAttrVal()))
                .collect(Collectors.joining(",")));
        janInfoVO.setJanColumn(dataConverUtils.camelize(janColumn));
        if(Integer.valueOf(1).equals(downFlagVO.getDownFlag())) {
            List<String[]> excelData = new ArrayList<>();
            excelData.add(janInfoVO.getJanHeader().split(","));
            String fileName = String.format("商品明細-%s.xlsx",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(MagicString.DATE_FORMATER_SS)));
            ApplicationHome h = new ApplicationHome(this.getClass());
            File jarF = h.getSource();
            String path = jarF.getParentFile().toString();
            String filePath = Joiner.on(File.separator).join(Lists.newArrayList(path, fileName));

            for (LinkedHashMap<String, Object> map : janInfoVO.getJanDataList()) {
                excelData.add(map.values().stream().map(Object::toString).toArray(String[]::new));
            }

            ExcelUtils.generateNormalExcelToFile(excelData, filePath);
            cacheUtil.put(downFlagVO.getTaskID() + MagicString.STATUS_STR, MagicString.TASK_STATUS_SUCCESS);
            cacheUtil.put(downFlagVO.getTaskID() + MagicString.FILEPATH_STR, fileName);
        }else{
            cacheUtil.put(downFlagVO.getTaskID() + MagicString.STATUS_STR, MagicString.TASK_STATUS_SUCCESS);
            cacheUtil.put(downFlagVO.getTaskID() + MagicString.RETURN_STR, janInfoVO);
        }
    }
    @Override
    public Map<String, Object> getJanListInfo(JanInfoList janInfoList) {
        String companyCd = "1000";

        if ("0".equals(janInfoList.getCommonPartsData().getProdIsCore())) {
            companyCd = janInfoList.getCompanyCd();
        }
        String tableNameAttr = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, companyCd,
                janInfoList.getCommonPartsData().getProdMstClass());
        String tableNameKaisou = MessageFormat.format(MagicString.PROD_JAN_KAISOU_HEADER_SYS, companyCd,
                janInfoList.getCommonPartsData().getProdMstClass());
        String janInfoTableName = MessageFormat.format(MagicString.PROD_JAN_INFO, companyCd,
                janInfoList.getCommonPartsData().getProdMstClass());

        List<LinkedHashMap<String,Object>> janAttrList = mstJanMapper.getJanAttrList(tableNameAttr);
        List<String> janInfoHeader = mstJanMapper.getJanInfoHeader(tableNameAttr, tableNameKaisou);
        LinkedHashMap<String, Object> janInfoList1 = mstJanMapper.getJanInfoList(janInfoTableName, janInfoList.getJan(),janInfoHeader);
        List<LinkedHashMap<String,Object>> update = janAttrList.stream().filter(map->map.get("11").equals("4")).collect(Collectors.toList());

        List<LinkedHashMap<String,Object>> janKaisouList = mstJanMapper.getJanKaisouList(tableNameKaisou);
        List<LinkedHashMap<String,Object>> janAttrGroup1 = janAttrList.stream().filter(map->map.get("11").equals("1") || map.get("11").equals("3"))
                .sorted(Comparator.comparing(map->MapUtils.getInteger(map,"3"))).collect(Collectors.toList());
        List<LinkedHashMap<String,Object>> janAttrGroup3 = janAttrList.stream().filter(map->map.get("11").equals("6") )
                .sorted(Comparator.comparing(map->MapUtils.getInteger(map,"4"))).collect(Collectors.toList());
        List<LinkedHashMap<String,Object>> janAttrGroup2 = janAttrList.stream().filter(map->map.get("11").equals("5"))
                .sorted(Comparator.comparing(map->MapUtils.getInteger(map,"3"))).collect(Collectors.toList());
        if (janInfoList1 == null && !"".equals(janInfoList.getJan())){
            return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
        }
        Map<String, Object> janInfoMap = this.janInfoHandle(janInfoList1, update);

        Map<String,Object> janInfo = new HashMap<>();
       List<Map<String,Object>> janClass = new ArrayList<>();

       janKaisouList.stream().forEach(stringObjectLinkedHashMap->{
           Map<String,Object> janKaisouInfo = new HashMap<>();
           janKaisouInfo.put("name",stringObjectLinkedHashMap.get("2"));
           janKaisouInfo.put("id",janInfoList1!=null?janInfoList1.get((Integer.parseInt(stringObjectLinkedHashMap.get("3").toString())-1)+""):"");
           janKaisouInfo.put(MagicString.TITLE,janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
           janKaisouInfo.put(MagicString.VALUE,MagicString.ZOKUSEI_PREFIX+stringObjectLinkedHashMap.get("3"));

           janClass.add(janKaisouInfo);
       });

        janInfo.put("janClass",janClass);
        List<Object> janAttr = this.janAttrHandle(janAttrGroup1, janAttrGroup3, janInfoList1);
        janInfo.put("janAttr",janAttr);
        janInfoMap.put("janInfo",janInfo);

        List<Object> janBulk = new ArrayList<>();
        janBulk.add(null);
        List<Object> janBulk1 = new ArrayList<>();
            janAttrGroup2.forEach(stringObjectLinkedHashMap->{
                Map<String,Object> janAttrInfo = new HashMap<>();
                janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
                janAttrInfo.put(MagicString.VALUE,stringObjectLinkedHashMap.get("1"));
                janAttrInfo.put(MagicString.TITLE,janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
                janAttrInfo.put("id",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");

                if (stringObjectLinkedHashMap.get("13").equals("0")) {
                    janAttrInfo.put("type",MagicString.NUMBER);
                }else {
                    janAttrInfo.put("type",MagicString.STRING);
                }
                janBulk1.add(janAttrInfo);
            });

        janBulk.add(janBulk1);
        janInfoMap.put("janBulk",janBulk);
        return ResultMaps.result(ResultEnum.SUCCESS,janInfoMap);
    }

    public Map<String,Object> janInfoHandle(Map<String, Object> janInfoList1, List<LinkedHashMap<String, Object>> update){
        Map<String,Object> janInfoMap = new HashMap<>();
        janInfoMap.put(MagicString.JAN, janInfoList1!=null?janInfoList1.getOrDefault("1", ""):"");
        janInfoMap.put(MagicString.JAN_NAME, janInfoList1!=null?janInfoList1.getOrDefault("2", ""):"");

        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : update) {
            if (stringObjectLinkedHashMap.get("1").toString().equals("update_time")){
                janInfoMap.put("updateTime",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");

            }else {
                janInfoMap.put(stringObjectLinkedHashMap.get("1").toString(),janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");

            }

        }
        if ( janInfoMap.get("sync").equals("") || janInfoMap.get("sync").equals("1")){
            janInfoMap.put("sync",true);
        }else {
            janInfoMap.put("sync",false);
        }
        return janInfoMap;
    }

    public List<Object> janAttrHandle(List<LinkedHashMap<String, Object>> janAttrGroup1, List<LinkedHashMap<String, Object>> janAttrGroup3, Map<String, Object> janInfoList1){
        List<Object> janAttr = new ArrayList<>();
        List<Object> group1 = new ArrayList<>();
        janAttrGroup1.stream().forEach(stringObjectLinkedHashMap->{
            Map<String,Object> janAttrInfo = new HashMap<>();
            janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janAttrInfo.put(MagicString.TITLE,janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put("id",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put(MagicString.VALUE,stringObjectLinkedHashMap.get("1"));
            if (stringObjectLinkedHashMap.get("11").equals("6")) {
                janAttrInfo.put("isDelete",1);
            }else {
                janAttrInfo.put("isDelete",0);
            }
            if (stringObjectLinkedHashMap.get("13").equals("0")) {
                janAttrInfo.put("type",MagicString.NUMBER);
            }else {
                janAttrInfo.put("type",MagicString.STRING);
            }

            group1.add(janAttrInfo);
        });

        janAttr.add(group1);

        List<Object> group2 = this.janAttrGroup2Handle(janAttrGroup3,janInfoList1);
        janAttr.add(group2);
        return janAttr;
    }

    public List<Object> janAttrGroup2Handle(List<LinkedHashMap<String, Object>> janAttrGroup3, Map<String, Object> janInfoList1){
        List<Object> group2 = new ArrayList<>();
        janAttrGroup3.stream().forEach(stringObjectLinkedHashMap->{
            Map<String,Object> janAttrInfo = new HashMap<>();
            janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janAttrInfo.put(MagicString.TITLE,janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put("id",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put(MagicString.VALUE,stringObjectLinkedHashMap.get("1"));
            if (stringObjectLinkedHashMap.get("13").equals("0")) {
                janAttrInfo.put("type",MagicString.NUMBER);
            }else {
                janAttrInfo.put("type",MagicString.STRING);
            }
            group2.add(janAttrInfo);
        });
        return group2;
    }
    /**
     * 表示項目設定の取得
     * @param janPresetAttribute
     * @return
     */
    @Override
    public Map<String, Object> getAttrName(JanPresetAttribute janPresetAttribute) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = janPresetAttribute.getCompanyCd();
        String prodMstClass = janPresetAttribute.getCommonPartsData().getProdMstClass();
        String prodIsCore = janPresetAttribute.getCommonPartsData().getProdIsCore();
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        String isCompanyCd;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        String tableNameAttr = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, isCompanyCd, prodMstClass);
        String tableNamePreset = MessageFormat.format(MagicString.PROD_JAN_PRESET_PARAM, isCompanyCd, prodMstClass);
        String tableNameKaisou = MessageFormat.format(MagicString.PROD_JAN_KAISOU_HEADER_SYS, isCompanyCd, prodMstClass);
        String tableNamePlanoCycle = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS,MagicString.PLANO_CYCLE_COMPANY_CD,MagicString.FIRST_CLASS_CD);
        return ResultMaps.result(ResultEnum.SUCCESS, mstJanMapper.getAttrName(aud, tableNameAttr, tableNamePreset, tableNameKaisou,tableNamePlanoCycle));
    }

    /**
     * 表示項目設定のプリセット
     *
     * @param janPresetAttribute
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setPresetAttribute(JanPresetAttribute janPresetAttribute) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = janPresetAttribute.getCompanyCd();
        String prodMstClass = janPresetAttribute.getCommonPartsData().getProdMstClass();
        String prodIsCore = janPresetAttribute.getCommonPartsData().getProdIsCore();
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        String isCompanyCd;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        String tableNamePreset = MessageFormat.format(MagicString.PROD_JAN_PRESET_PARAM, isCompanyCd, prodMstClass);
        mstJanMapper.deleteByAuthorCd(aud, tableNamePreset);
        mstJanMapper.insertPresetAttribute(aud, janPresetAttribute.getClassCd().split(","), tableNamePreset);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setJanListInfo(Map<String, Object> map) {
       map=  map.entrySet().stream().filter(newMap->!"".equals(newMap.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String,Object> commonPartsDto = (Map<String,Object>) map.get(MagicString.COMMON_PARTS_DATA);

        String companyCd = "1000";
        String authorCd = session.getAttribute("aud").toString();
        if ("0".equals(commonPartsDto.get(MagicString.PROD_IS_CORE))) {
            companyCd = map.get("companyCd").toString();
        }
        String tableNameAttr = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, companyCd,
                commonPartsDto.get(MagicString.PROD_MST_CLASS));

        String janInfoTableName = MessageFormat.format(MagicString.PROD_JAN_INFO, companyCd,
                commonPartsDto.get(MagicString.PROD_MST_CLASS));

        LinkedHashMap <String,Object> setInfoMap = new LinkedHashMap<>();
        LinkedHashMap <String,Object> getKaisouNameMap = new LinkedHashMap<>();
        String jan = map.get(MagicString.JAN).toString();
        List<String> list = new ArrayList<>();
        for (String s : map.keySet()) {
            if (s.contains(MagicString.ZOKUSEI_PREFIX)){
                Integer zokuseiId = Integer.parseInt(s.replace(MagicString.ZOKUSEI_PREFIX, ""))-1;
                setInfoMap.put(zokuseiId+"",map.get(s));
                // map.get(s)!= null && !map.get(s).equals("")
                if (!Strings.isNullOrEmpty(MapUtils.getString(map, s))){
                    list.add(s.replace(MagicString.ZOKUSEI_PREFIX, ""));
                    getKaisouNameMap.put(zokuseiId+"",map.get(s));
                }else {
                    setInfoMap.put(zokuseiId+"","9999");
                    setInfoMap.put(zokuseiId+1+"","未登録");
                }

            }
        }
        Map<String, Object> kaiSouName =new HashMap<>();
        if (!getKaisouNameMap.isEmpty()) {
             kaiSouName = mstJanMapper.getKaiSouName(getKaisouNameMap, janInfoTableName, list);
        }

       this.setJanInfo(tableNameAttr,authorCd,setInfoMap,map,kaiSouName,jan,janInfoTableName,companyCd,commonPartsDto);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    public void setJanInfo(String tableNameAttr, String authorCd, Map<String, Object> setInfoMap, Map<String, Object> map, Map<String, Object> kaiSouName, String jan, String janInfoTableName, String companyCd, Map<String, Object> commonPartsDto){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        List<LinkedHashMap<String,Object>> janAttrList = mstJanMapper.getJanAttrList(tableNameAttr);
        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janAttrList) {
            map.entrySet().stream().forEach(stringObjectEntry->{
                if (stringObjectLinkedHashMap.get("1").equals(stringObjectEntry.getKey())){
                    setInfoMap.put(stringObjectLinkedHashMap.get("3").toString(),stringObjectEntry.getValue());
                }
            });

            if ("update_time".equals(stringObjectLinkedHashMap.get("1"))){
                setInfoMap.put(stringObjectLinkedHashMap.get("3").toString(),simpleDateFormat.format(date));
            }

            if ("updater".equals(stringObjectLinkedHashMap.get("1"))){
                setInfoMap.put(stringObjectLinkedHashMap.get("3").toString(),authorCd);
            }
        }
        setInfoMap.put("2", map.get(MagicString.JAN_NAME).toString());
        setInfoMap.putAll(kaiSouName);
        List<String> janInfoCol = mstJanMapper.getJanInfoCol();
        LinkedHashMap<String,Object> janInfoData= setInfoMap.entrySet().stream()
                .filter(infoMap->!janInfoCol.contains(infoMap.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(k1,k2)->k1,LinkedHashMap ::new));
        LinkedHashMap<String,Object> janSpecialData= setInfoMap.entrySet().stream()
                .filter(special->janInfoCol.contains(special.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(k1,k2)->k1,LinkedHashMap ::new));
        if (!janInfoData.isEmpty()) {
            mstJanMapper.setJanInfo(janInfoData, jan, janInfoTableName);
        }

        if (!janSpecialData.isEmpty()) {
            mstJanMapper.setJanSpecial(janSpecialData, jan);
        }

        List<Map<String, Object>> zokuseiIdAndCol = zokuseiMstMapper.getZokuseiIdAndCol(companyCd, commonPartsDto.get(MagicString.PROD_MST_CLASS).toString());
        LinkedHashMap<String,Object> maps = new LinkedHashMap<>();
        for (Map<String, Object> objectMap : zokuseiIdAndCol) {
            setInfoMap.entrySet().stream().forEach(stringObjectEntry->{
                if (objectMap.get(MagicString.ZOKUSEI_COL).equals(stringObjectEntry.getKey())){
                    maps.put(objectMap.get(MagicString.ZOKUSEI_ID).toString(),stringObjectEntry.getValue());
                }
            });
        }
        zokuseiMstMapper.setVal(maps,companyCd,commonPartsDto.get(MagicString.PROD_MST_CLASS).toString());

    }

    /**
     * データ一括取込
     * @param file
     * @param fileName
     * @param classCd
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> uploadJanData(MultipartFile file, String fileName, String classCd,
                                             String commonPartsData, String companyCd) {
        Pattern numberPattern = Pattern.compile("\\d+");
        AtomicInteger count = new AtomicInteger();
        if (!fileName.endsWith(".xlsx")) {
            return ResultMaps.result(ResultEnum.FAILURE.getCode(), MagicString.MSG_UPLOAD_CORRECT_FILE);
        }
        List<String[]> excelData = ExcelUtils.readExcel(file);
        JSONObject jsonObject = JSON.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        if ("1".equals(prodIsCore)) {
            companyCd = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        }
        String tableNameAttr = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, companyCd, prodMstClass);
        String tableNameKaisou = MessageFormat.format(MagicString.PROD_JAN_KAISOU_HEADER_SYS, companyCd, prodMstClass);
        String tableNameInfo = MessageFormat.format(MagicString.PROD_JAN_INFO, companyCd, prodMstClass);
        String tableNameKaisouData = MessageFormat.format(MagicString.PROD_JAN_KAISOU, companyCd, prodMstClass);
        String[] header = excelData.get(0);
        Optional<String> optional = Stream.of(header).filter(MagicString.JAN::equalsIgnoreCase).findAny();
        if (!optional.isPresent()) {
            return ResultMaps.result(ResultEnum.FAILURE.getCode(), MagicString.MSG_NOT_HAVE_JAN_CODE);
        }
        String janColumn = String.join(",", header);
        List<JanHeaderAttr> janHeader = mstJanMapper.getJanHeaderByName(tableNameAttr, tableNameKaisou, janColumn);
        if (header.length > janHeader.size()) {
            return ResultMaps.result(ResultEnum.FAILURE.getCode(), MagicString.MSG_UNIDENTIFIED_COLUMN);
        }

        String taskId = UUID.randomUUID().toString();
        String finalCompanyCd = companyCd;
        String authorCd = session.getAttribute("aud").toString();
        cacheUtil.put(taskId+MagicString.STATUS_STR, MagicString.TASK_STATUS_PROCESSING);
        executor.execute(()->{
            Map<String, String> headerNameIndex = new HashMap<>();
            janHeader.forEach(headerAttr->{
                headerNameIndex.put(headerAttr.getAttrVal(), headerAttr.getSort());
            });

            List<String> columnHeader = new ArrayList<>();

            for (String column : header) {
                columnHeader.add(headerNameIndex.get(column));
            }

            List<LinkedHashMap<String, Object>> janData = new ArrayList<>();
            LinkedHashMap<String, Object> jan;
            List<LinkedHashMap<String, Object>> janKaisouList = mstJanMapper.getJanKaisouList(tableNameKaisou);
            Integer kaiSouLength = mstJanMapper.getKaiSouLength(tableNameKaisouData);
            List<JanHeaderAttr> planoType = mstJanMapper.getPlanoType(tableNameAttr);
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                for (int i = 1; i < excelData.size(); i++) {
                    String[] row = excelData.get(i);
                    jan = new LinkedHashMap<>();
                    Map<String,Object> map = new HashMap<>();

                    for (int j = 0; j < header.length; j++) {
                        for (JanHeaderAttr janHeaderAttr : planoType) {
                            if (janHeaderAttr.getSort().equals(headerNameIndex.get(header[j]))&& !numberPattern.matcher(row[j]).matches()) {
                                row[j] = "0";
                            }
                        }

                        for (int k = 0; k < janKaisouList.size(); k++) {

                            if (String.valueOf(Integer.parseInt(janKaisouList.get(k).get("3").toString())-1).equals(headerNameIndex.get(header[j]))){

                                int diff = kaiSouLength - row[j].length();
                                StringBuilder branchStr = new StringBuilder();
                                for (int l = 0; l < diff; l++) {
                                    branchStr.append("0");
                                }
                                row[j] = branchStr + row[j];
                                map.put(String.valueOf(Integer.parseInt(janKaisouList.get(k).get("4").toString())-1),row[j]);
                                String s = mstJanMapper.checkKaisou(tableNameKaisouData, map);
                                if (s != null){
                                    jan.put(headerNameIndex.get(header[j]), row[j]);
                                    jan.put(String.valueOf(Integer.parseInt(headerNameIndex.get(header[j]))+1), s);

                                }else {
                                    jan.put(headerNameIndex.get(header[j]), "9999");
                                    jan.put(String.valueOf(Integer.parseInt(headerNameIndex.get(header[j]))+1), "未登録");

                                }
                                break;
                            }else {
                                if (!"未登録".equals(jan.get(headerNameIndex.get(header[j])))){
                                    jan.put(headerNameIndex.get(header[j]), row[j]);
                                }
                            }
                        }
                    }

                    jan.remove("102");
                    jan.remove("103");
                    janData.add(jan);
                }
                String dateStr = simpleDateFormat.format(date);
                List<Map<String, Object>> zokuseiIdAndCol = zokuseiMstMapper.getZokuseiIdAndCol(finalCompanyCd, prodMstClass);
                List<String> janInfoCol = mstJanMapper.getJanInfoCol();

                List<LinkedHashMap<Object, Object>> janInfoData = janData.stream()
                        .map(map -> map.entrySet().stream().filter(infoMap -> !janInfoCol.contains(infoMap.getKey()))
                                .collect(LinkedHashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), LinkedHashMap::putAll))
                        .collect(Collectors.toList());


                List<LinkedHashMap<Object, Object>> janSpecialData = janData.stream()
                        .map(map -> map.entrySet().stream().filter(infoMap -> janInfoCol.contains(infoMap.getKey()) || "1".equals(infoMap.getKey()))
                                .collect(LinkedHashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), LinkedHashMap::putAll))
                        .collect(Collectors.toList());

                int batchSize = 1000;
                int janDataNum = (int) Math.ceil(janInfoData.size() / 1000.0);
                for (int i = 0; i < janDataNum; i++) {
                    int end = Math.min(batchSize * (i + 1), janInfoData.size());
                    int i1 = mstJanMapper.insertJanList(tableNameInfo, janInfoData.subList(batchSize * i, end), dateStr, authorCd);
                    mstJanMapper.insertJanSpecialList(janSpecialData.subList(batchSize * i, end));

                    count.addAndGet(i1);
                }

                Set<Map<String,Object>> zokuseiList = new HashSet<>();
                for (LinkedHashMap<String, Object> janDatum : janData) {
                    for (Map.Entry<String, Object> stringObjectEntry : janDatum.entrySet()) {
                        for (Map<String, Object> map : zokuseiIdAndCol) {
                            if (stringObjectEntry.getKey().equals(map.get(MagicString.ZOKUSEI_COL))) {
                                Map <String,Object> zokuseiMap = new HashMap<>();
                                zokuseiMap.put(MagicString.ZOKUSEI_ID,map.get(MagicString.ZOKUSEI_ID));
                                zokuseiMap.put(MagicString.ZOKUSEI_COL,map.get(MagicString.ZOKUSEI_COL));
                                zokuseiMap.put("zokusei_nm",stringObjectEntry.getValue());
                                zokuseiList.add(zokuseiMap);
                            }
                        }
                    }
                }
                if(!zokuseiList.isEmpty()){
                    zokuseiMstMapper.setValBatch(zokuseiList, finalCompanyCd,prodMstClass);
                }

                cacheUtil.put(taskId+MagicString.STATUS_STR, MagicString.TASK_STATUS_SUCCESS);
                cacheUtil.put(taskId+MagicString.DATA_STR, count + MagicString.MSG_UPLOAD_SUCCESS);
            } catch (Exception e) {
                logger.error("", e);
                logAspect.setTryErrorLog(e,new Object[]{commonPartsData, finalCompanyCd,classCd});
                cacheUtil.put(taskId+",exception", MagicString.MSG_ABNORMALITY_DATA);
                cacheUtil.put(taskId+MagicString.STATUS_STR, MagicString.TASK_STATUS_EXCEPTION);
            }
        });

        JSONObject json = new JSONObject();
        json.put(MagicString.TASKID, taskId);
        return ResultMaps.result(ResultEnum.SUCCESS, json);
    }

    /**
     * JANデータ同期
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncJanData() {
        String syncCompanyList = sysConfigMapper.selectSycConfig("sync_company_list");
        String[] companyList = syncCompanyList.split(",");
        List<CommoditySyncSet> commoditySyncSetList;
        String prodMstClass;
        String tableNameHeader;
        String tableNameInfo;
        String tableNameHeaderWK;
        String tableNameInfoWK;
        List<String> janAttrList;
        String column;
        for (String companyCd : companyList) {
            mstCommodityService.syncCommodityMaster(companyCd);
            commoditySyncSetList = mstCommodityService.getCommodityList(companyCd);
            for (CommoditySyncSet commoditySyncSet : commoditySyncSetList) {
                prodMstClass = commoditySyncSet.getProdMstClass();
                tableNameHeader = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, companyCd, prodMstClass);
                tableNameInfo = MessageFormat.format(MagicString.PROD_JAN_INFO, companyCd, prodMstClass);
                tableNameHeaderWK = MessageFormat.format(MagicString.WK_PROD_JAN_ATTR_HEADER_SYS, companyCd, prodMstClass);
                String tableNameKaisouHeader = MessageFormat.format(MagicString.WK_PROD_JAN_KAISOU_HEADER_SYS, companyCd, prodMstClass);
                tableNameInfoWK = MessageFormat.format(MagicString.WK_PROD_JAN_INFO, companyCd, prodMstClass);

                int i = mstBranchMapper.checkTableExist(tableNameInfoWK.split("\\.")[1], companyCd);
                if(i<1){
                    //if jan_info not exist, delete from master_syohin
                    String mstSyohin = MessageFormat.format(MagicString.MASTER_SYOHIN, companyCd);
                    mstBranchMapper.deleteNotExistMst(prodMstClass, mstSyohin);
                    continue;
                }

                syncJanKaisou(companyCd, prodMstClass);

                mstJanMapper.syncJanHeader(tableNameHeader,tableNameHeaderWK);
                janAttrList = mstJanMapper.getJanAttrColWK(tableNameHeaderWK, tableNameKaisouHeader);
                column =janAttrList.stream().collect(Collectors.joining(","));
                deleteMultipleJan(companyCd, prodMstClass, tableNameInfoWK);
                mstJanMapper.syncJanData(tableNameInfo, tableNameInfoWK, column);
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS.getCode(),"同期完了しました");
    }

    @Override
    public JanInfoVO getJanListResult(DownFlagVO downFlagVO, HttpServletResponse response) throws IOException {
        if (MagicString.TASK_STATUS_SUCCESS.equals(cacheUtil.get(downFlagVO.getTaskID()+MagicString.STATUS_STR))) {
            if (Objects.equals(cacheUtil.get(downFlagVO.getTaskID()+MagicString.FLAG_STR), 1)) {
                response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
                String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(String.format("商品明細-%s.xlsx",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern(MagicString.DATE_FORMATER_SS))), "utf-8"));
                response.setHeader("Content-Disposition", format);
                ServletOutputStream outputStream = response.getOutputStream();

                Object o = cacheUtil.get(downFlagVO.getTaskID() + MagicString.FILEPATH_STR);
                if(o!=null){
                    ApplicationHome h = new ApplicationHome(this.getClass());
                    File jarF = h.getSource();
                    String fileName = o.toString();
                    String path = jarF.getParentFile().toString();
                    String filePath = Joiner.on(File.separator).join(Lists.newArrayList(path, fileName));

                    try(FileInputStream fis = new FileInputStream(filePath)){
                        byte[] byteBuffer = new byte[1024];
                        int len = 0;
                        while ((len = fis.read(byteBuffer))!=-1){
                            outputStream.write(byteBuffer, 0, len);
                        }
                    } catch (Exception e){
                        logger.error("",e);
                    }finally {
                        Files.deleteIfExists(new File(filePath).toPath());
                        cacheUtil.remove(downFlagVO.getTaskID()+MagicString.STATUS_STR);
                        cacheUtil.remove(downFlagVO.getTaskID()+MagicString.FLAG_STR);
                        cacheUtil.remove(downFlagVO.getTaskID()+MagicString.FILEPATH_STR);
                    }
                }

                outputStream.flush();
                return null;
            }else{
                JanInfoVO janInfoVO = (JanInfoVO) cacheUtil.get(downFlagVO.getTaskID() + MagicString.RETURN_STR);
                cacheUtil.remove(downFlagVO.getTaskID()+MagicString.STATUS_STR);
                cacheUtil.remove(downFlagVO.getTaskID()+MagicString.FLAG_STR);
                cacheUtil.remove(downFlagVO.getTaskID()+MagicString.RETURN_STR);

                return janInfoVO;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getUploadJanDataResult(String taskId) {
        Object o = cacheUtil.get(taskId + MagicString.STATUS_STR);
        if (Objects.equals(o.toString(), MagicString.TASK_STATUS_SUCCESS)) {
            String data = cacheUtil.get(taskId + MagicString.DATA_STR).toString();
            cacheUtil.remove(taskId+MagicString.DATA_STR);
            cacheUtil.remove(taskId+MagicString.STATUS_STR);
            Map<String, Object> result = ResultMaps.result(ResultEnum.SUCCESS.getCode(), data);
            result.put("data", data);
            return result;
        }else if(Objects.equals(o.toString(), MagicString.TASK_STATUS_PROCESSING)){
            JSONObject returnJson = new JSONObject();
            returnJson.put(MagicString.STATUS, "9");
            returnJson.put(MagicString.TASKID, taskId);
            return ResultMaps.result(ResultEnum.SUCCESS, returnJson);
        }

        Object msg = cacheUtil.get(taskId + ",exception");
        cacheUtil.remove(taskId+MagicString.DATA_STR);
        cacheUtil.remove(taskId+",exception");

        return ResultMaps.result(ResultEnum.FAILURE.getCode(), String.valueOf(msg));
    }

    /**
     * JAN階層同期
     *
     * @return
     */
    public void syncJanKaisou(String companyCd, String prodMstClass) {
        String tableNameHeader = MessageFormat.format(MagicString.PROD_JAN_KAISOU_HEADER_SYS, companyCd, prodMstClass);
        String tableNameKaisou = MessageFormat.format(MagicString.PROD_JAN_KAISOU, companyCd, prodMstClass);
        String tableNameHeaderWK = MessageFormat.format(MagicString.WK_PROD_JAN_KAISOU_HEADER_SYS, companyCd, prodMstClass);
        String tableNameKaisouWK = MessageFormat.format(MagicString.WK_PROD_JAN_KAISOU, companyCd, prodMstClass);
        mstJanMapper.deleteKaisou(tableNameHeader);
        mstJanMapper.insertKaisou(tableNameHeader, tableNameHeaderWK);
        mstJanMapper.deleteKaisou(tableNameKaisou);
        mstJanMapper.insertKaisou(tableNameKaisou, tableNameKaisouWK);
    }

    /**
     * 重複Janを削除
     *
     * @return
     */
    public void deleteMultipleJan(String companyCd, String prodMstClass, String tableNameInfoWK) {
        String tableNameHeaderWK = MessageFormat.format(MagicString.WK_PROD_JAN_KAISOU_HEADER_SYS, companyCd, prodMstClass);
        List<String> janKaisouCol = mstJanMapper.getJanKaisouColWK(tableNameHeaderWK);
        mstJanMapper.deleteMultipleJan(janKaisouCol, tableNameInfoWK);
    }
}
