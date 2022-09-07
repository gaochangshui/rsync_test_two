package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.po.JanHeaderAttr;
import com.trechina.planocycle.entity.po.JanInfoList;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
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
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
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
    public Map<String, Object> getJanList(DownFlagVO downFlagVO) throws Exception {
        JanInfoVO janInfoVO = new JanInfoVO();
        if (cacheUtil.get(downFlagVO.getTaskID()) == null) {
            return ResultMaps.result(ResultEnum.FAILURE, "taskId not exists");
        }

        if(MagicString.TASK_STATUS_PROCESSING.equals(cacheUtil.get(downFlagVO.getTaskID()+",status"))){
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", "9");
            returnJson.put("taskId", downFlagVO.getTaskID());
            return ResultMaps.result(ResultEnum.SUCCESS, returnJson);
        }else if(MagicString.TASK_STATUS_EXCEPTION.equals(cacheUtil.get(downFlagVO.getTaskID()+",status"))){
            return ResultMaps.result(ResultEnum.FAILURE);
        }else if(MagicString.TASK_STATUS_SUCCESS.equals(cacheUtil.get(downFlagVO.getTaskID()+",status"))){
            JSONObject json = new JSONObject();
            json.put("taskId", downFlagVO.getTaskID());
            return ResultMaps.result(ResultEnum.SUCCESS, json);
        }

        cacheUtil.put(downFlagVO.getTaskID()+",status", MagicString.TASK_STATUS_PROCESSING);
        cacheUtil.put(downFlagVO.getTaskID()+",flag", downFlagVO.getDownFlag());
        Future futureTask = executor.submit(()->{
            try {
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
                    if(optional.isPresent()){
                        janHeaderSort.add(optional.get());
                    }
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
                     String format = MessageFormat.format("attachment;filename={0};", UriUtils.encode(fileName, "utf-8"));

                     for (LinkedHashMap<String, Object> map : janInfoVO.getJanDataList()) {
                         excelData.add(map.values().stream().map(Object::toString).toArray(String[]::new));
                     }

                     String filePath = ExcelUtils.generateNormalExcelToFile(excelData, fileName);
                     cacheUtil.put(downFlagVO.getTaskID() + ",status", MagicString.TASK_STATUS_SUCCESS);
                     cacheUtil.put(downFlagVO.getTaskID() + ",filepath", filePath);
                 }else{
                     cacheUtil.put(downFlagVO.getTaskID() + ",status", MagicString.TASK_STATUS_SUCCESS);
                     cacheUtil.put(downFlagVO.getTaskID() + ",returnVal", janInfoVO);
                }
            }catch (Exception e){
                logger.error("", e);
                cacheUtil.put(downFlagVO.getTaskID()+",status", MagicString.TASK_STATUS_EXCEPTION);
                cacheUtil.remove(downFlagVO.getTaskID()+",status");
            }
        });

        try {
            futureTask.get(MagicString.TASK_TIME_OUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", "9");
            returnJson.put("taskId", downFlagVO.getTaskID());
            return ResultMaps.result(ResultEnum.SUCCESS, returnJson);
        } catch(InterruptedException e ){
            Thread.currentThread().interrupt();
            logger.error("", e);
            cacheUtil.remove(downFlagVO.getTaskID()+",status");
            return ResultMaps.result(ResultEnum.FAILURE);
        } catch (ExecutionException e){
            logger.error("", e);
            cacheUtil.remove(downFlagVO.getTaskID()+",status");
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        JSONObject json = new JSONObject();
        json.put("taskId", downFlagVO.getTaskID());
        return ResultMaps.result(ResultEnum.SUCCESS, json);
    }

    @Override
    public Map<String, Object> getJanListInfo(JanInfoList janInfoList) {
        String companyCd = "1000";

        if ("0".equals(janInfoList.getCommonPartsData().getProdIsCore())) {
            companyCd = janInfoList.getCompanyCd();
        }
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd,
                janInfoList.getCommonPartsData().getProdMstClass());
        String tableNameKaisou = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", companyCd,
                janInfoList.getCommonPartsData().getProdMstClass());
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,
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
        if (janInfoMap == null || janInfoMap.get("sync").equals("") || janInfoMap.get("sync").equals("1")){
            janInfoMap.put("sync",true);
        }else {
            janInfoMap.put("sync",false);
        }
        Map<String,Object> janInfo = new HashMap<>();

       List janClass = new ArrayList();

        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janKaisouList) {
            Map<String,Object> janKaisouInfo = new HashMap<>();
            janKaisouInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janKaisouInfo.put("id",janInfoList1!=null?janInfoList1.get((Integer.parseInt(stringObjectLinkedHashMap.get("3").toString())-1)+""):"");
            janKaisouInfo.put("title",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janKaisouInfo.put("value","zokusei"+stringObjectLinkedHashMap.get("3"));

            janClass.add(janKaisouInfo);

        }
        janInfo.put("janClass",janClass);
        List janAttr = new ArrayList();

        List group1 = new ArrayList();
        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janAttrGroup1) {
            Map<String,Object> janAttrInfo = new HashMap<>();
            janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janAttrInfo.put("title",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put("id",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put("value",stringObjectLinkedHashMap.get("1"));
            if (stringObjectLinkedHashMap.get("11").equals("6")) {
                janAttrInfo.put("isDelete",1);
            }else {
                janAttrInfo.put("isDelete",0);
            }
            if (stringObjectLinkedHashMap.get("13").equals("0")) {
                janAttrInfo.put("type","number");
            }else {
                janAttrInfo.put("type","string");
            }

            group1.add(janAttrInfo);


        }
        janAttr.add(group1);
        List group2 = new ArrayList();
        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janAttrGroup3) {
            Map<String,Object> janAttrInfo = new HashMap<>();
            janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janAttrInfo.put("title",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put("id",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put("value",stringObjectLinkedHashMap.get("1"));
            if (stringObjectLinkedHashMap.get("13").equals("0")) {
                janAttrInfo.put("type","number");
            }else {
                janAttrInfo.put("type","string");
            }

            group2.add(janAttrInfo);


        }
        janAttr.add(group2);
        janInfo.put("janAttr",janAttr);
        janInfoMap.put("janInfo",janInfo);

        List janBulk = new ArrayList();
        janBulk.add(null);
        List janBulk1 = new ArrayList();

        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janAttrGroup2) {
            Map<String,Object> janAttrInfo = new HashMap<>();
            janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janAttrInfo.put("value",stringObjectLinkedHashMap.get("1"));
            janAttrInfo.put("title",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");
            janAttrInfo.put("id",janInfoList1!=null?janInfoList1.getOrDefault(stringObjectLinkedHashMap.get("3"),""):"");

            if (stringObjectLinkedHashMap.get("13").equals("0")) {
                janAttrInfo.put("type","number");
            }else {
                janAttrInfo.put("type","string");
            }
            janBulk1.add(janAttrInfo);
        }
        janBulk.add(janBulk1);
        janInfoMap.put("janBulk",janBulk);
        return ResultMaps.result(ResultEnum.SUCCESS,janInfoMap);
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
    public Map<String, Object> setJanListInfo(Map<String, Object> map) {
       map=  map.entrySet().stream().filter(newMap->!"".equals(newMap.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String,Object> commonPartsDto = (Map<String,Object>) map.get(MagicString.COMMON_PARTS_DATA);

        String companyCd = "1000";
        String authorCd = session.getAttribute("aud").toString();
        if ("0".equals(commonPartsDto.get(MagicString.PROD_IS_CORE))) {
            companyCd = map.get("companyCd").toString();
        }
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd,
                commonPartsDto.get(MagicString.PROD_MST_CLASS));

        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,
                commonPartsDto.get(MagicString.PROD_MST_CLASS));

        LinkedHashMap <String,Object> setInfoMap = new LinkedHashMap<>();
        LinkedHashMap <String,Object> getKaisouNameMap = new LinkedHashMap<>();
        String jan = map.get(MagicString.JAN).toString();
        List<String> list = new ArrayList();
        for (String s : map.keySet()) {
            if (s.contains("zokusei")){
                Integer zokuseiId = Integer.parseInt(s.replace("zokusei", ""))-1;
                setInfoMap.put(zokuseiId+"",map.get(s));
                if (map.get(s) != null && !map.get(s).equals("")){
                    list.add(s.replace("zokusei", ""));
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

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        List<LinkedHashMap<String,Object>> janAttrList = mstJanMapper.getJanAttrList(tableNameAttr);
        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janAttrList) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                if (stringObjectLinkedHashMap.get("1").equals(stringObjectEntry.getKey())){
                    setInfoMap.put(stringObjectLinkedHashMap.get("3").toString(),stringObjectEntry.getValue());
                }
            }
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
        LinkedHashMap<String,Object> janInfoData= setInfoMap.entrySet().stream().filter(infoMap->!janInfoCol.contains(infoMap.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(k1,k2)->k1,LinkedHashMap ::new));
        LinkedHashMap<String,Object> janSpecialData= setInfoMap.entrySet().stream().filter(Special->janInfoCol.contains(Special.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(k1,k2)->k1,LinkedHashMap ::new));
        if (!janInfoData.isEmpty()) {
            mstJanMapper.setJanInfo(janInfoData, jan, janInfoTableName);
        }

        if (!janSpecialData.isEmpty()) {
            mstJanMapper.setJanSpecial(janSpecialData, jan);
        }

        List<Map<String, Object>> zokuseiIdAndCol = zokuseiMstMapper.getZokuseiIdAndCol(companyCd, commonPartsDto.get(MagicString.PROD_MST_CLASS).toString());
        LinkedHashMap<String,Object> maps = new LinkedHashMap<>();
        for (Map<String, Object> objectMap : zokuseiIdAndCol) {
            for (Map.Entry<String, Object> stringObjectEntry : setInfoMap.entrySet()) {
                if (objectMap.get("zokusei_col").equals(stringObjectEntry.getKey())){
                    maps.put(objectMap.get("zokusei_id").toString(),stringObjectEntry.getValue());
                }
            }
        }
        zokuseiMstMapper.setVal(maps,companyCd,commonPartsDto.get(MagicString.PROD_MST_CLASS).toString());

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * データ一括取込
     * @param file
     * @param fileName
     * @param classCd
     * @return
     */
    @Override
    public Map<String, Object> uploadJanData(MultipartFile file, String fileName, String classCd,
                                             String commonPartsData, String companyCd) {
        Pattern numberPattern = Pattern.compile("[0-9]+");
        int count;
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
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd, prodMstClass);
        String tableNameKaisou = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", companyCd, prodMstClass);
        String tableNameInfo = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd, prodMstClass);
        String tableNameKaisouData = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou", companyCd, prodMstClass);
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
        Map<String, String> headerNameIndex = new HashMap<>();
        for (JanHeaderAttr headerAttr : janHeader) {
            headerNameIndex.put(headerAttr.getAttrVal(), headerAttr.getSort());
        }
        List<String> columnHeader = new ArrayList<>();
        for (String column : header) {
            columnHeader.add(headerNameIndex.get(column));
        }
        String infoHeader = "\"" + String.join("\",\"", columnHeader) + "\"";
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
            String authorCd = session.getAttribute("aud").toString();
            List<Map<String, Object>> zokuseiIdAndCol = zokuseiMstMapper.getZokuseiIdAndCol(companyCd, prodMstClass);
            List<String> janInfoCol = mstJanMapper.getJanInfoCol();

            List<LinkedHashMap<String, Object>> janInfoData = janData.stream()
                    .map(map -> map.entrySet().stream().filter(infoMap -> !janInfoCol.contains(infoMap.getKey()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new)))
                    .collect(Collectors.toList());

            List<LinkedHashMap<String, Object>> janSpecialData = janData.stream()
                    .map(map -> map.entrySet().stream().filter(infoMap -> janInfoCol.contains(infoMap.getKey()) || "1".equals(infoMap.getKey()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new)))
                    .collect(Collectors.toList());
                count = mstJanMapper.insertJanList(tableNameInfo, janInfoData, dateStr, authorCd);
                count = mstJanMapper.insertJanSpecialList(janSpecialData);

            Set zokuseiList = new HashSet();
            for (LinkedHashMap<String, Object> janDatum : janData) {
                for (Map.Entry<String, Object> stringObjectEntry : janDatum.entrySet()) {
                    for (Map<String, Object> map : zokuseiIdAndCol) {
                        if (stringObjectEntry.getKey().equals(map.get("zokusei_col"))) {
                            Map <String,Object> zokuseiMap = new HashMap<>();
                            zokuseiMap.put("zokusei_id",map.get("zokusei_id"));
                            zokuseiMap.put("zokusei_col",map.get("zokusei_col"));
                            zokuseiMap.put("zokusei_nm",stringObjectEntry.getValue());
                            zokuseiList.add(zokuseiMap);
                        }
                    }
                }
            }
            if(!zokuseiList.isEmpty()){
                zokuseiMstMapper.setValBatch(zokuseiList,companyCd,prodMstClass);
            }

        } catch (Exception e) {
            logAspect.setTryErrorLog(e,new Object[]{commonPartsData,companyCd,classCd});
            return ResultMaps.result(ResultEnum.FAILURE.getCode(), MagicString.MSG_ABNORMALITY_DATA);
        }
        return ResultMaps.result(ResultEnum.SUCCESS.getCode(), count + MagicString.MSG_UPLOAD_SUCCESS);
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
                syncJanKaisou(companyCd, prodMstClass);
                tableNameHeader = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, companyCd, prodMstClass);
                tableNameInfo = MessageFormat.format(MagicString.PROD_JAN_INFO, companyCd, prodMstClass);
                tableNameHeaderWK = MessageFormat.format(MagicString.WK_PROD_JAN_ATTR_HEADER_SYS, companyCd, prodMstClass);
                String tableNameKaisouHeader = MessageFormat.format(MagicString.WK_PROD_JAN_KAISOU_HEADER_SYS, companyCd, prodMstClass);
                tableNameInfoWK = MessageFormat.format(MagicString.WK_PROD_JAN_INFO, companyCd, prodMstClass);
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
        if (MagicString.TASK_STATUS_SUCCESS.equals(cacheUtil.get(downFlagVO.getTaskID()+",status"))) {
            if (Objects.equals(cacheUtil.get(downFlagVO.getTaskID()+",flag"), 1)) {
                response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
                String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(String.format("商品明細-%s.xlsx",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern(MagicString.DATE_FORMATER_SS))), "utf-8"));
                response.setHeader("Content-Disposition", format);
                ServletOutputStream outputStream = response.getOutputStream();
                
                Object o = cacheUtil.get(downFlagVO.getTaskID() + ",filepath");
                if(o!=null){
                    String filePath = o.toString();
                    try( FileInputStream fis = new FileInputStream(filePath);
                         FileChannel chIn = fis.getChannel();
                         WritableByteChannel chOut = Channels.newChannel(outputStream)){
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        while(chIn.read(byteBuffer)!=-1){
                            byteBuffer.flip();
                            chOut.write(byteBuffer);
                            byteBuffer.clear();
                        }
                    } finally {
                        Files.deleteIfExists(new File(filePath).toPath());
                        cacheUtil.remove(downFlagVO.getTaskID()+",status");
                        cacheUtil.remove(downFlagVO.getTaskID()+",flag");
                        cacheUtil.remove(downFlagVO.getTaskID()+",filepath");
                    }
                }
                outputStream.flush();
            }else{
                JanInfoVO janInfoVO = (JanInfoVO) cacheUtil.get(downFlagVO.getTaskID() + ",returnVal");
                cacheUtil.remove(downFlagVO.getTaskID()+",status");
                cacheUtil.remove(downFlagVO.getTaskID()+",flag");
                cacheUtil.remove(downFlagVO.getTaskID()+",returnVal");

                return janInfoVO;
            }
        }

        return null;
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
