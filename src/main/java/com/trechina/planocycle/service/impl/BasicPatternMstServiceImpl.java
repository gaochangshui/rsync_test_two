package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.ProductPowerDataDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import com.trechina.planocycle.entity.vo.BasicPatternRestrictRelationVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.service.PriorityOrderMstService;
import com.trechina.planocycle.service.ShelfPtsService;
import com.trechina.planocycle.utils.CommonUtil;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import com.trechina.planocycle.utils.cgiUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class BasicPatternMstServiceImpl implements BasicPatternMstService {
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    @Autowired
    private JanInfoMapper janInfoMapper;
    @Autowired
    private HttpSession session;
    @Autowired
    private ShelfPtsDataJandataMapper jandataMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamst;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private WorkPriorityOrderRestrictRelationMapper workPriorityOrderRestrictRelationMapper;
    @Autowired
    private BasicPatternRestrictResultMapper restrictResultMapper;
    @Autowired
    private BasicPatternRestrictRelationMapper restrictRelationMapper;
    @Autowired
    private BasicPatternAttrMapper basicMapperMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    private WorkPriorityOrderRestrictResultMapper workPriorityOrderRestrictResultMapper;
    @Autowired
    private VehicleNumCache vehicleNumCache;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private BasicPatternRestrictResultDataMapper restrictResultDataMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private PriorityOrderPtsDataMapper priorityOrderPtsDataMapper;
    @Autowired
    private PriorityOrderSortMapper priorityOrderSortMapper;
    @Autowired
    private BasicPatternJanPlacementMapper basicPatternJanPlacementMapper;
    @Autowired
    private MstBranchMapper mstBranchMapper;
    @Autowired
    private cgiUtils cgiUtil;
    @Value("${smartUrlPath}")
    public String smartPath;
    @Autowired
    private LogAspect logAspect;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> autoDetect(BasicPatternAutoDetectVO autoDetectVO) {
        Integer shelfPatternCd = autoDetectVO.getShelfPatternCd();
        Integer priorityOrderCd = autoDetectVO.getPriorityOrderCd();
        String companyCd = autoDetectVO.getCompanyCd();
        String commonPartsData = autoDetectVO.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = getCommonTableName(commonPartsData, companyCd);
        String classCd = commonTableName.getProdMstClass();
        String zokuseiIds = autoDetectVO.getAttrList();
        String aud = session.getAttribute("aud").toString();

        WorkPriorityOrderMst priorityOrderMst = new WorkPriorityOrderMst();
        BeanUtils.copyProperties(autoDetectVO, priorityOrderMst);
        priorityOrderMst.setAuthorCd(aud);
        priorityOrderMst.setShelfPatternCd((long)shelfPatternCd);

        shelfPtsDataMapper.deleteDisplay(companyCd,priorityOrderCd);
        shelfPtsDataMapper.deletePtsJandataByPriorityOrderCd(priorityOrderCd);
        workPriorityOrderMstMapper.insert(priorityOrderMst);
        List<Integer> list = Arrays.asList(zokuseiIds.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());

        List<Integer> cdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        priorityOrderMstAttrSortMapper.deleteAttrList(companyCd,priorityOrderCd);
        priorityOrderMstAttrSortMapper.setAttrList(companyCd,priorityOrderCd,list);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokuseiByCd(commonTableName.getProdIsCore(),
                classCd, zokuseiIds, priorityOrderCd);
        List<ShelfPtsDataTanamst> tanamsts = shelfPtsDataTanamst.selectByPatternCd((long) shelfPatternCd);

        String proHeaderTb = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
        String proInfoTb = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);

        List<Map<String, Object>> sizeAndIrisu = janClassifyMapper.getSizeAndIrisu(proHeaderTb);
        Map<String, String> sizeAndIrisuMap = sizeAndIrisu.stream().collect(Collectors.toMap(map -> MapUtils.getString(map, "attr"), map -> MapUtils.getString(map, "attrVal")));
        List<Map<String, Object>> classifyList = janInfoMapper.selectJanClassify(commonTableName.getProInfoTable(), shelfPatternCd,
                zokuseiMsts, cdList, sizeAndIrisuMap, proInfoTb);
        classifyList = this.updateJanSizeByMap(classifyList);
        classifyList.forEach(item-> item.put(MagicString.WIDTH, MapUtils.getLong(item,MagicString.WIDTH, MagicString.DEFAULT_WIDTH)*MapUtils.getInteger(item, "faceCount", 1)));

        basicMapperMapper.delete(priorityOrderCd, companyCd);

        Map<String, BasicPatternRestrictResult> classify = getJanInfoClassify(classifyList, companyCd,
                zokuseiMsts, aud, (long) autoDetectVO.getPriorityOrderCd());

        restrictResultMapper.deleteByPriorityCd(autoDetectVO.getPriorityOrderCd(), companyCd);
        classify = this.generateRestrictCd(classify);
        List<BasicPatternRestrictResult> basicPatternRestrictResults = new ArrayList<>(classify.values());
        BasicPatternRestrictResult result = new BasicPatternRestrictResult();
        result.setRestrictCd(MagicString.NO_RESTRICT_CD);
        result.setAuthorCd(aud);
        result.setCompanyCd(companyCd);
        result.setPriorityOrderCd((long)priorityOrderCd);
        basicPatternRestrictResults.add(result);
        restrictResultMapper.insertBatch(basicPatternRestrictResults);
        basicMapperMapper.insertBatch(basicPatternRestrictResults);

        List<Integer> zokuseiList = zokuseiMsts.stream().map(ZokuseiMst::getZokuseiId).collect(Collectors.toList());

        restrictRelationMapper.deleteByPrimaryKey(priorityOrderCd, companyCd);
        for (ShelfPtsDataTanamst tanamst : tanamsts) {
            final int[] index = {1};
            Integer taiCd = tanamst.getTaiCd();
            Integer tanaCd = tanamst.getTanaCd();
            Integer tanaWidth = tanamst.getTanaWidth();

            List<Map<String, Object>> jans = classifyList.stream().filter(map -> commonMstService.taiTanaEquals(MapUtils.getInteger(map, MagicString.TAI_CD), taiCd,
                    MapUtils.getInteger(map, MagicString.TANA_CD),tanaCd)).collect(Collectors.toList());

            logger.info("taiCd:{},tanaCd:{}, jans:{}", taiCd, tanaCd,jans);
            double areaWidth = 0;
            String lastKey = "";
            int janCount = 0;

            //Traverse all groups. If it is different from the previous group, record it. If it is the same, the area will be accumulated
            List<Map<String, Object>> newJans = new ArrayList<>();
            for (int i = 0; i < jans.size(); i++) {
                Map<String, Object> janMap = jans.get(i);
                double width = MapUtils.getDouble(janMap, MagicString.WIDTH);
                String key = commonMstService.getClassifyKey(zokuseiList, janMap);

                if(isLastAndEqualsToLastKey(key, lastKey, i, jans.size())){
                    areaWidth += width;
                    janCount++;
                }

                if(!"".equals(lastKey) && (!lastKey.equals(key) || (i+1)==jans.size())){
                    double percent = BigDecimal.valueOf(areaWidth).divide(BigDecimal.valueOf(tanaWidth), 5, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue();
                    Map<String, Object> map = new GsonBuilder().create().fromJson(JSON.toJSONString(janMap),
                            new TypeToken<Map<String, Object>>(){}.getType());
                    map.put(MagicString.RESTRICT_CD, classify.get(lastKey).getRestrictCd());
                    map.put("area", percent);
                    map.put("janCount", janCount);
                    map.put("priorityOrderCd", priorityOrderCd);
                    map.put("companyCd", companyCd);
                    map.put("authorCd", aud);
                    newJans.add(map);
                    areaWidth=width;
                    janCount=1;
                }else{
                    areaWidth += width;
                    janCount++;
                }

                //If the last grouping is independent, it should be handled separately
                if(isLastAndNotEqualsToLastKey(key, lastKey, i, jans.size())){
                    areaWidth = width;
                    janCount = 1;
                    int percent = BigDecimal.valueOf(areaWidth).divide(BigDecimal.valueOf(tanaWidth), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
                    Map<String, Object> map = new GsonBuilder().create().fromJson(JSON.toJSONString(janMap),
                            new TypeToken<Map<String, Object>>(){}.getType());
                    map.put(MagicString.RESTRICT_CD, classify.get(key).getRestrictCd());
                    map.put("area", percent);
                    map.put("janCount", janCount);
                    map.put("priorityOrderCd", priorityOrderCd);
                    map.put("companyCd", companyCd);
                    map.put("authorCd", aud);
                    newJans.add(map);
                }
                
                lastKey = key;
            }

            newJans.forEach(map->{
                map.put(MagicString.TANA_POSITION, index[0]);
                index[0]++;
            });

            restrictRelationMapper.insertBatch(newJans);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    private boolean isLastAndNotEqualsToLastKey(String key, String lastKey, int currentIndex, int size){
        return !lastKey.equals(key) && (currentIndex+1)==size;
    }

    private boolean isLastAndEqualsToLastKey(String key, String lastKey, int currentIndex, int size){
        return lastKey.equals(key) && (currentIndex+1)==size;
    }

    private Map<String, BasicPatternRestrictResult> generateRestrictCd(Map<String, BasicPatternRestrictResult> classify){
        long restrictCd = 1;
        for (Map.Entry<String, BasicPatternRestrictResult> entry : classify.entrySet()) {
            BasicPatternRestrictResult value = entry.getValue();
            value.setRestrictCd(restrictCd);
            classify.put(entry.getKey(), value);
            restrictCd++;
        }

        return classify;
    }


    @Override
    public Map<String, BasicPatternRestrictResult> getJanInfoClassify(List<Map<String, Object>> classifyList,
                                                                      String companyCd, List<ZokuseiMst> zokuseiMst, String authorCd, Long priorityOrderCd){
        Map<String, BasicPatternRestrictResult> classify = new HashMap<>();

        List<Integer> zokuseiList = zokuseiMst.stream().map(ZokuseiMst::getZokuseiId).collect(Collectors.toList());
        classifyList.forEach(map->{
            String classifyId = getClassifyId(zokuseiList, map);

            if (!classify.containsKey(classifyId)) {
                BasicPatternRestrictResult result = new BasicPatternRestrictResult();
                try {
                    for (Integer zokusei : zokuseiList) {
                        Method method = result.getClass().getMethod("setZokusei" + zokusei, String.class);
                        if(MapUtils.getString(map, "mstExist").equals("0")){
                            method.invoke(result, "_");
                        }else{
                            String val = MapUtils.getString(map, zokusei+"", MagicString.DEFAULT_VALUE);
                            method.invoke(result, CommonUtil.defaultIfEmpty(val,MagicString.DEFAULT_VALUE));
                        }
                    }
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    logger.error("",e);
                }
                result.setCompanyCd(companyCd);
                result.setAuthorCd(authorCd);
                result.setPriorityOrderCd(priorityOrderCd);

                classify.put(classifyId, result);
            }
        });

        return classify;
    }

    private String getClassifyId(List<Integer> zokuseiList, Map<String, Object> map){
        StringBuilder classifyId = new StringBuilder();
        for (Integer id : zokuseiList) {
            if(classifyId.length()>0){
                classifyId.append(",");
            }
            if(MapUtils.getString(map, "mstExist").equals("0")){
                classifyId.append("_");
            }else{
                String val = MapUtils.getString(map, id + "", MagicString.DEFAULT_VALUE);
                classifyId.append(Strings.isNullOrEmpty(val)?MagicString.DEFAULT_VALUE:val);
            }
        }

        return classifyId.toString();
    }

    @Override
    public List<PriorityOrderResultDataDto> updateJanSize(List<PriorityOrderResultDataDto> priorityOrderResultDataDtoList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long planoWidth = 0 ;
        long planoHeight = 0 ;
        long planoDepth = 0 ;
        List<Map<String, Object>> janPlacementList = basicPatternJanPlacementMapper.getJanPlacementList();
        Class<PriorityOrderResultDataDto> clazz = PriorityOrderResultDataDto.class;
        for (PriorityOrderResultDataDto priorityOrderResultDataDto : priorityOrderResultDataDtoList) {
            for (Map<String, Object> map : janPlacementList) {
                if (priorityOrderResultDataDto.getFaceKaiten().intValue() == MapUtils.getInteger(map,MagicString.FACE_KAITEN) &&
                        priorityOrderResultDataDto.getFaceMen().intValue() == MapUtils.getInteger(map,MagicString.FACE_MEN)
                ){
                    Method getPlanoWidth = clazz.getMethod(MagicString.PLANO_PREFIX + map.get(MagicString.PLANO_WIDTH).toString().substring(0, 1).toUpperCase() + map.get(MagicString.PLANO_WIDTH).toString().substring(1));
                    Method getPlanoHeight = clazz.getMethod(MagicString.PLANO_PREFIX + map.get(MagicString.PLANO_HEIGHT).toString().substring(0, 1).toUpperCase() + map.get(MagicString.PLANO_HEIGHT).toString().substring(1));
                    Method getPlanoDepth = clazz.getMethod(MagicString.PLANO_PREFIX + map.get(MagicString.PLANO_DEPTH).toString().substring(0, 1).toUpperCase() + map.get(MagicString.PLANO_DEPTH).toString().substring(1));
                    planoWidth = (long)getPlanoWidth.invoke(priorityOrderResultDataDto);
                    planoHeight = (long)getPlanoHeight.invoke(priorityOrderResultDataDto);
                    planoDepth = (long)getPlanoDepth.invoke(priorityOrderResultDataDto);

                    priorityOrderResultDataDto.setPlanoWidth(planoWidth);
                    priorityOrderResultDataDto.setPlanoHeight(planoHeight);
                    priorityOrderResultDataDto.setPlanoDepth(planoDepth);
                }
            }
        }

        return priorityOrderResultDataDtoList;
    }

    @Override
    public List<Map<String, Object>> updateJanSizeByMap(List<Map<String, Object>> priorityOrderResultDataDtoList) {
        List<Map<String, Object>> janPlacementList = basicPatternJanPlacementMapper.getJanPlacementList();

        priorityOrderResultDataDtoList.forEach(priorityOrderResultDataDto->{
            for (Map<String, Object> map : janPlacementList) {
                if (Objects.equals(MapUtils.getInteger(priorityOrderResultDataDto, MagicString.FACE_KAITEN, 0), MapUtils.getInteger(map,MagicString.FACE_KAITEN)) &&
                        Objects.equals(MapUtils.getInteger(priorityOrderResultDataDto, MagicString.FACE_MEN, 1), MapUtils.getInteger(map,MagicString.FACE_MEN))
                ){
                    priorityOrderResultDataDto.put(MagicString.WIDTH, priorityOrderResultDataDto.get(MagicString.PLANO+
                            map.getOrDefault(MagicString.PLANO_WIDTH, MagicString.WIDTH).toString().substring(0,1).toUpperCase()+map.getOrDefault(MagicString.PLANO_WIDTH, MagicString.WIDTH).toString().substring(1)));
                    priorityOrderResultDataDto.put(MagicString.HEIGHT, priorityOrderResultDataDto.get(MagicString.PLANO+
                            map.getOrDefault(MagicString.PLANO_HEIGHT, MagicString.HEIGHT).toString().substring(0,1).toUpperCase()+map.getOrDefault(MagicString.PLANO_HEIGHT, MagicString.HEIGHT).toString().substring(1)));
                    priorityOrderResultDataDto.put(MagicString.DEPTH, priorityOrderResultDataDto.get(MagicString.PLANO+
                            map.getOrDefault(MagicString.PLANO_DEPTH, MagicString.DEPTH).toString().substring(0,1).toUpperCase()+map.getOrDefault(MagicString.PLANO_DEPTH, MagicString.DEPTH).toString().substring(1)));
                }
            }
        });

        return priorityOrderResultDataDtoList;
    }

    @Override
    public Map<String, Object> cancelTask(String taskId) {
        String taskKey = MessageFormat.format(MagicString.TASK_KEY_FUTURE, taskId);
        String cancelKey = MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskId);
        Object o = vehicleNumCache.get(taskKey);
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        if(o!=null){
            Future<?> future = (Future) o;
            future.cancel(true);
            vehicleNumCache.remove(taskKey);
            if(future.isCancelled() || future.isDone()){
                String cgiKey = MessageFormat.format(MagicString.TASK_KEY_CGI, taskId);
                if(vehicleNumCache.get(cgiKey)!=null){
                    Future<?> f = (Future) vehicleNumCache.get(taskKey+"2");
                    f.cancel(true);
                    vehicleNumCache.remove(taskKey);
                    String[] cgiTasks = vehicleNumCache.get(cgiKey).toString().split(",");
                    for (String cgiTask : cgiTasks) {
                        Map<String,Object> posMap = new HashMap<>();
                        posMap.put("taskid",cgiTask);
                        String s = cgiUtil.postCgi(MagicString.CGI_KILL_PROCESS, posMap, tokenInfo, smartPath);
                        logger.info("taskId:{}, cancel result:{}",cgiTask, s);
                    }
                }

                vehicleNumCache.put(cancelKey, "1");
                return ResultMaps.result(ResultEnum.SUCCESS);
            }

            logger.warn("taskId:{} is interrupt error", taskId);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> getCoreCompany() {
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int i = mstBranchMapper.checkSchemaExist(coreCompany);
        if(i>0){
            return ResultMaps.result(ResultEnum.SUCCESS, 1);
        }
        return ResultMaps.result(ResultEnum.SUCCESS, 0);
    }

    public GetCommonPartsDataDto getCommonTableName(String commonPartsData, String companyCd ) {

        JSONObject jsonObject = JSON.parseObject(commonPartsData);

        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        GetCommonPartsDataDto getCommonPartsDataDto = new GetCommonPartsDataDto();

        if(jsonObject.containsKey("prodMstClass") && jsonObject.containsKey("prodIsCore")){
            String prodMstClass = jsonObject.get("prodMstClass").toString();
            String prodIsCore = jsonObject.get("prodIsCore").toString();
            String isCompanyCd = null;

            getCommonPartsDataDto.setProdMstClass(prodMstClass);

            if ("1".equals(prodIsCore)) {
                isCompanyCd = coreCompany;
            } else {
                isCompanyCd = companyCd;
            }

            getCommonPartsDataDto.setProdIsCore(isCompanyCd);

            getCommonPartsDataDto.setProKaisouTable(MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass));
            getCommonPartsDataDto.setProKaisouInfoTable(MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou", isCompanyCd, prodMstClass));
            getCommonPartsDataDto.setProAttrTable(MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", isCompanyCd, prodMstClass));
            getCommonPartsDataDto.setProInfoTable(MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass));
        }


        if (jsonObject.containsKey("storeIsCore") && jsonObject.containsKey("storeMstClass")) {
            String storeIsCore = jsonObject.get("storeIsCore").toString();
            String storeMstClass = jsonObject.get("storeMstClass").toString();
            String isCompanyCd = null;

            if ("1".equals(storeIsCore)) {
                isCompanyCd = coreCompany;
            } else {
                isCompanyCd = companyCd;
            }

            getCommonPartsDataDto.setStoreIsCore(isCompanyCd);
            getCommonPartsDataDto.setStoreMstClass(storeMstClass);
            getCommonPartsDataDto.setStoreInfoTable(MessageFormat.format("\"{0}\".ten_{1}_ten_info", isCompanyCd, storeMstClass));
        }

        return getCommonPartsDataDto;
    }

    @Override
    public Map<String, Object> getAttrDisplay(String companyCd, Integer priorityOrderCd) {

        String aud = session.getAttribute("aud").toString();
        WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, aud,priorityOrderCd);
        GetCommonPartsDataDto commonTableName = getCommonTableName(priorityOrderMst.getCommonPartsData(), companyCd);
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());

        List<Map<String, Object>> basicPatternRestrictRelationDto = restrictRelationMapper.selectByPrimaryKey(priorityOrderCd,
                commonTableName.getProdIsCore(), attrCol,commonTableName.getProdMstClass());
        logger.info("...{}",basicPatternRestrictRelationDto);
        Map<String, List<Map<String, Object>>> relationByTaiTana = basicPatternRestrictRelationDto.stream()
                .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.TAI_CD) + "," + MapUtils.getString(map, MagicString.TANA_CD)));

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : relationByTaiTana.entrySet()) {
            String key = entry.getKey();

            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put(MagicString.TAI_CD, Integer.valueOf(key.split(",")[0]));
            resultMap.put(MagicString.TANA_CD, Integer.valueOf(key.split(",")[1]));

            List<Map<String, Object>> groups = new ArrayList<>();

            for (Map<String, Object> itemMap : entry.getValue()) {
                StringBuilder groupCd = new StringBuilder();
                StringBuilder groupName = new StringBuilder();
                for (Map<String,Object> zokuseiId : attrCol) {
                    if (itemMap.containsKey(zokuseiId.get(MagicString.ZOUKUSEI_COLCD))) {
                        String attrCd = MapUtils.getString(itemMap, zokuseiId.get(MagicString.ZOUKUSEI_COLCD).toString());
                        if(itemMap.containsKey("json") && itemMap.get("json")!=null){
                            itemMap.put(zokuseiId.get(MagicString.ZOUKUSEI_COLNAME).toString()
                                    , JSON.parseObject(itemMap.get("json").toString()).get(attrCd)==null?"":JSON.parseObject(itemMap.get("json").toString()).get(attrCd));
                        }else{
                            itemMap.put(zokuseiId.get(MagicString.ZOUKUSEI_COLNAME).toString(), "_");
                        }
                    }
                    itemMap.putIfAbsent(zokuseiId.get(MagicString.ZOUKUSEI_COLNAME).toString(), "");

                    Integer isExist = MapUtils.getInteger(zokuseiId, "is_exist", 0);
                    String colname = MapUtils.getString(itemMap, zokuseiId.get(MagicString.ZOUKUSEI_COLNAME).toString());
                    if(isExist==0 && Strings.isNullOrEmpty(MapUtils.getString(itemMap, zokuseiId.get(MagicString.ZOUKUSEI_COLNAME).toString()))){
                        colname = "_";
                    }

                    if(isExist==1 && Strings.isNullOrEmpty(MapUtils.getString(itemMap, zokuseiId.get(MagicString.ZOUKUSEI_COLNAME).toString()))){
                        colname = MagicString.DEFAULT_VALUE;
                    }

                    if (groupCd.toString().equals("")){
                        groupCd.append(itemMap.get(zokuseiId.get(MagicString.ZOUKUSEI_COLCD).toString()));
                        groupName.append(colname);
                    }else {
                        groupCd.append("->").append(itemMap.get(zokuseiId.get(MagicString.ZOUKUSEI_COLCD).toString()));
                        groupName.append("->").append(colname);
                    }

                }
                itemMap.put("groupCd",groupCd.toString());
                itemMap.put("groupName",groupName.toString());
                itemMap.remove("json");
                itemMap.put(MagicString.TANA_POSITION, MapUtils.getString(itemMap, "areaPosition"));
                itemMap.put(MagicString.RESTRICT_CD, MapUtils.getInteger(itemMap, MagicString.RESTRICT_CD, MagicString.NO_RESTRICT_CD.intValue()));
                groups.add(itemMap);
                resultMap.put("groups", groups);
            }
            resultList.add(resultMap);
        }
        List<Map<String, Object>> tana = restrictRelationMapper.getTana(priorityOrderCd,resultList);
        tana.forEach(map->{
            map.put("groups",new ArrayList<>());
            resultList.add(map);
        });

        restrictRelationMapper.updateAreaPosition(resultList, priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS, resultList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> autoCalculation(String companyCd, Integer priorityOrderCd, Integer partition, Integer heightSpace,
                                               Integer tanaWidthCheck) {
        String authorCd = session.getAttribute("aud").toString();
        String uuid = UUID.randomUUID().toString();
        Integer finalHeightSpace = heightSpace;
        Map<String, Object> requestMap = logAspect.errInfo();
        Future<?> future = executor.submit(() -> {
            try{
                //商品力点数表cdを取得する
                Integer patternCd = productPowerMstMapper.getpatternCd(companyCd, authorCd, priorityOrderCd);
                List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
                List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
                WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
                String commonPartsData = workPriorityOrderMst.getCommonPartsData();
                GetCommonPartsDataDto commonTableName = getCommonTableName(commonPartsData, companyCd);
                List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
                List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
                List<Map<String, Object>> restrictResult = restrictResultMapper.selectByPrimaryKey(priorityOrderCd);
                //zokuseiId convert to work_basic_pattern_restrict_result's zokuseiId
                List<Map<String, Object>> newRestrictResult = new ArrayList<>();
                restrictResult.forEach(map->{
                    Map<String, Object> newMap = new HashMap<>();
                    newMap.put(MagicString.RESTRICT_CD_UNDERLINE, MapUtils.getLong(map,MagicString.RESTRICT_CD_UNDERLINE));
                    newMap.put("author_cd", MapUtils.getString(map,"author_cd"));
                    newMap.put("priority_order_cd", MapUtils.getInteger(map,"priority_order_cd"));
                    mstAttrSorts.forEach(mstAttrSort->{
                        String beforeZokuseiId = "zokusei" + (mstAttrSort.getSort() + 1);
                        String val = MapUtils.getString(map, beforeZokuseiId, "");
                        newMap.put("zokusei" + (mstAttrSort.getValue()), val);
                    });
                    newRestrictResult.add(newMap);
                });

                this.setPtsJandataByRestrictCd(priorityOrderCd, patternCd,companyCd, authorCd, attrList, zokuseiMsts,
                        commonTableName, allCdList,newRestrictResult, uuid);
                Integer productPowerCd = productPowerMstMapper.getProductPowerCd(companyCd, authorCd, priorityOrderCd);

                //仕切り板の厚さと仕切り板を使用して保存するかどうか
                priorityOrderMstMapper.setPartition(companyCd,priorityOrderCd,authorCd,partition, finalHeightSpace, tanaWidthCheck);
                //まず社員番号に従ってワークシートのデータを削除します
                workPriorityOrderResultDataMapper.delResultData(companyCd, authorCd, priorityOrderCd);

                if(this.interruptThread(uuid, "1")){
                    return;
                }
                //制約条件の取得
                List<ProductPowerDataDto> productPowerData = workPriorityOrderRestrictResultMapper.getProductPowerData( companyCd
                        ,priorityOrderCd, productPowerCd, authorCd,patternCd);
                    if (!productPowerData.isEmpty()) {
                        workPriorityOrderResultDataMapper.setResultDataList(productPowerData, companyCd, authorCd, priorityOrderCd);
                    }

                String resultDataList = workPriorityOrderResultDataMapper.getResultDataList(companyCd, authorCd, priorityOrderCd);
                if (resultDataList == null) {
                    vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_JAN_NOT_EXIST, uuid),1);
                }

                if(this.interruptThread(uuid, "2")){
                    return;
                }

                //属性別に並べ替える
                priorityOrderMstService.getNewReorder(companyCd, priorityOrderCd, authorCd);
                //商品を並べる
                WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
                Long shelfPatternCd = priorityOrderMst.getShelfPatternCd();

                if (shelfPatternCd == null) {
                    vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_PATTERN_NOT_EXIST, uuid),1);
                }

                Short partitionFlag = Optional.ofNullable(priorityOrderMst.getPartitionFlag()).orElse((short) 0);
                Short partitionVal = Optional.ofNullable(priorityOrderMst.getPartitionVal()).orElse((short) 2);
                if(partitionFlag.equals((short)0)){
                    partitionVal = 0;
                }
                Short topPartitionVal = null;
                if (finalHeightSpace!=null) {
                    topPartitionVal = finalHeightSpace.shortValue();
                }

                List<Map<String, Object>> originRelationMap = restrictRelationMapper.selectByPriorityOrderCd(priorityOrderCd);
                List<Map<String, Object>> relationMap = scaleTaiTanaWidth(originRelationMap, tanaWidthCheck);

                List<Map<String, Object>> tanaList = priorityOrderPtsDataMapper.selectTanaMstByPatternCd(patternCd, priorityOrderCd);
                List<String> colNmforMst = priorityOrderMstAttrSortMapper.getColNmforMst(companyCd, authorCd, priorityOrderCd,commonTableName);
                int isReOrder = colNmforMst.size();

                if (this.interruptThread(uuid, "5")) {
                    return;
                }

                String proMstHeaderTb = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
                String proMstTb = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
                List<Map<String, Object>> sizeAndIrisu = janClassifyMapper.getSizeAndIrisu(proMstHeaderTb);
                List<PriorityOrderResultDataDto> janResult = jandataMapper.selectJanByPatternCd(authorCd, companyCd, patternCd, priorityOrderCd,
                        sizeAndIrisu, isReOrder, commonTableName.getProInfoTable(), proMstTb);
                Map<String, Object> resultMap = commonMstService.commSetJanForShelf(patternCd, companyCd, priorityOrderCd,
                        zokuseiMsts, allCdList,
                        newRestrictResult, attrList, authorCd, commonTableName, partitionVal, topPartitionVal, tanaWidthCheck,
                        tanaList, relationMap, janResult, sizeAndIrisu, isReOrder, productPowerCd, colNmforMst);

                if (resultMap!=null && MapUtils.getInteger(resultMap, "code").equals(ResultEnum.HEIGHT_NOT_ENOUGH.getCode())) {
                    vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_SET_JAN_HEIGHT_ERROR, uuid),resultMap.get("data"));
                }else{
                    //ptsを一時テーブルに保存
                    Object tmpData = MapUtils.getObject(resultMap, "data");
                    List<PriorityOrderResultDataDto> workData = new Gson().fromJson(new Gson().toJson(tmpData), new TypeToken<List<PriorityOrderResultDataDto>>() {
                    }.getType());
                    shelfPtsService.basicSaveWorkPtsData(companyCd, authorCd, priorityOrderCd, workData, isReOrder);
                    vehicleNumCache.put(uuid,1);
                    //recalculation area
                    List<Map<String, Object>> areaList = commonMstService.recalculationArea(workData, tanaList);
                    restrictRelationMapper.deleteArea(priorityOrderCd);
                    restrictRelationMapper.updateArea(areaList, priorityOrderCd, companyCd, authorCd);
                }
                logger.info("taskId:{} auto calculate end", uuid);
            }catch (Exception e){
                logger.error("auto calculation is error", e);
                vehicleNumCache.put(uuid,2);
                vehicleNumCache.put(uuid+"-error",JSON.toJSONString(e));
                logAspect.errInfoForEmail(requestMap, e, new Object[]{companyCd, priorityOrderCd, partition, heightSpace,
                        tanaWidthCheck});
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        });
        vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_FUTURE, uuid),future);
            return ResultMaps.result(ResultEnum.SUCCESS,uuid);
    }

    private boolean interruptThread(String taskId, String step){
        if(Thread.currentThread().isInterrupted()){
            logger.info("taskId:{} interrupted, step:{}", taskId, step);
            Thread.currentThread().interrupt();
            return true;
        }

        return false;
    }

    private List<Map<String, Object>> scaleTaiTanaWidth(List<Map<String, Object>> relationMap, Integer tanaWidthCheck){
        List<Map<String, Object>> newRelationMap = new ArrayList<>();
        Map<String, List<Map<String, Object>>> relationMapByTana = relationMap.stream().collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.TAI_CD) + "_" + MapUtils.getString(map, MagicString.TANA_CD)));
        for (Map.Entry<String, List<Map<String, Object>>> entry : relationMapByTana.entrySet()) {
            String taiTana = entry.getKey();
            double area = relationMapByTana.get(taiTana).stream().collect(Collectors.summarizingDouble(map -> MapUtils.getDouble(map, "area"))).getSum();
            // <100%
            List<Map<String, Object>> relationMapList = relationMapByTana.get(taiTana);

            if(Double.compare(area, 100.0)<0 || (Objects.equals(tanaWidthCheck, 1) && Double.compare(area, 100.0)>0)){
                relationMapList.forEach(r->{
                    BigDecimal newAreaPercent = BigDecimal.valueOf(MapUtils.getDouble(r, "area")).divide(BigDecimal.valueOf(area), 4, RoundingMode.CEILING).multiply(BigDecimal.valueOf(100));
                    r.put("area", newAreaPercent);
                });
            }

            newRelationMap.addAll(relationMapList);
        }
        newRelationMap = newRelationMap.stream().sorted(Comparator.comparing(map->MapUtils.getInteger(map, MagicString.TANA_POSITION)))
                .sorted(Comparator.comparing(map->MapUtils.getInteger(map, MagicString.TANA_CD)))
                .sorted(Comparator.comparing(map->MapUtils.getInteger(map, MagicString.TAI_CD))).collect(Collectors.toList());

        return newRelationMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setAttrDisplay( BasicPatternRestrictRelation basicPatternRestrictRelation) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = basicPatternRestrictRelation.getCompanyCd();
        Long priorityOrderCd = basicPatternRestrictRelation.getPriorityOrderCd();
        shelfPtsDataMapper.deletePtsJandataByPriorityOrderCd(priorityOrderCd.intValue());
        if (basicPatternRestrictRelation.getRestrictCd()== null){
            Map<String, Object> tanaInfo = restrictRelationMapper.getTanaInfo(basicPatternRestrictRelation);
            Double areaNew = Double.parseDouble(tanaInfo.get("area").toString()) - basicPatternRestrictRelation.getArea();
            if (areaNew>1){
                basicPatternRestrictRelation.setArea(areaNew.longValue());
                basicPatternRestrictRelation.setRestrictCd(Long.parseLong(tanaInfo.get("restrict_cd").toString()));
            }else {
                basicPatternRestrictRelation.setRestrictCd(9999L);
            }
        }
        restrictRelationMapper.deleteForTanaPosition(basicPatternRestrictRelation);
        restrictRelationMapper.update(basicPatternRestrictRelation,authorCd);

            List<BasicPatternRestrictRelationVo> tanaAttrList = restrictRelationMapper.getTanaAttrList(basicPatternRestrictRelation);
            tanaAttrList = tanaAttrList.stream().filter(item ->item.getRestrictCd()!=9999).collect(Collectors.toList());
        DoubleSummaryStatistics collect = tanaAttrList.stream().collect(Collectors.summarizingDouble(BasicPatternRestrictRelationVo::getArea));
        Integer taiCd = Integer.valueOf(basicPatternRestrictRelation.getTaiCd().toString());
            Integer tanaCd = Integer.valueOf(basicPatternRestrictRelation.getTanaCd().toString());
            int i = 1;
                for (BasicPatternRestrictRelationVo patternRestrictRelation : tanaAttrList) {
                    patternRestrictRelation.setAreaPosition(i++);
                }
                if (collect.getSum() < 99L){
                    BasicPatternRestrictRelationVo basicPatternRestrictRelation1 = new BasicPatternRestrictRelationVo();
                    basicPatternRestrictRelation1.setArea( (100-collect.getSum()));
                    basicPatternRestrictRelation1.setTaiCd(taiCd);
                    basicPatternRestrictRelation1.setTanaCd(tanaCd);
                    basicPatternRestrictRelation1.setRestrictCd(9999L);
                    basicPatternRestrictRelation1.setAuthorCd(authorCd);
                    basicPatternRestrictRelation1.setAreaPosition(i);
                    basicPatternRestrictRelation1.setTanaPosition(9999);
                    basicPatternRestrictRelation1.setCompanyCd(companyCd);
                    basicPatternRestrictRelation1.setPriorityOrderCd(priorityOrderCd);
                   tanaAttrList.add(basicPatternRestrictRelation1);
                }

            restrictRelationMapper.deleteTanas(taiCd,tanaCd,companyCd,priorityOrderCd.intValue());
            restrictRelationMapper.updateTanaPosition(tanaAttrList,authorCd);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> autoTaskId(String taskId) {
        final Map<String, Object>[] resultMap = new Map[]{null};
        LocalDateTime now = LocalDateTime.now();
        String cacheKey = "";
        while (true) {
            Map<String, Object> reMap = this.exceptionWrapper(taskId);
            if(!reMap.isEmpty()){
                return reMap;
            }

            cacheKey = MessageFormat.format(MagicString.TASK_KEY_SET_JAN_HEIGHT_ERROR, taskId);
            if (vehicleNumCache.get(cacheKey)!=null){
                Object o = vehicleNumCache.get(cacheKey);
                vehicleNumCache.remove(cacheKey);
                resultMap[0] = ResultMaps.result(ResultEnum.HEIGHT_NOT_ENOUGH, o);
                return resultMap[0];
            }

            if (vehicleNumCache.get(taskId) != null){
                if(Objects.equals(vehicleNumCache.get(taskId), 2)){
                    vehicleNumCache.remove(taskId);
                    cacheKey = MessageFormat.format(MagicString.TASK_KEY_ERROR, taskId);
                    String error = vehicleNumCache.get(cacheKey).toString();
                    vehicleNumCache.remove(cacheKey);
                    resultMap[0] = ResultMaps.error(ResultEnum.FAILURE, error);
                }else{
                    vehicleNumCache.remove(taskId);
                    resultMap[0] = ResultMaps.result(ResultEnum.SUCCESS,"success");
                }
                return resultMap[0];
            }

            cacheKey = MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskId);
            if (Objects.equals(vehicleNumCache.get(cacheKey), "1")){
                resultMap[0] = ResultMaps.result(ResultEnum.SUCCESS);
                return resultMap[0];
            }

            if(Duration.between(now, LocalDateTime.now()).getSeconds() >MagicString.TASK_TIME_OUT_LONG){
                return ResultMaps.result(ResultEnum.SUCCESS,"9");
            }
        }
    }

    private Map<String, Object> exceptionWrapper(String taskId){
        String cacheKey = MessageFormat.format(MagicString.TASK_KEY_JAN_NOT_EXIST, taskId);
        if (vehicleNumCache.get(cacheKey)!=null){
            vehicleNumCache.remove(cacheKey);
            return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
        }
        cacheKey = MessageFormat.format(MagicString.TASK_KEY_PATTERN_NOT_EXIST, taskId);
        if (vehicleNumCache.get(cacheKey)!=null){
            vehicleNumCache.remove(cacheKey);
            return ResultMaps.error(ResultEnum.FAILURE, "PatternCdNotExist");
        }

        return ImmutableMap.of();
    }

    @Override
    public Map<String, Object> preCalculation(String companyCd, Long patternCd, Integer priorityOrderCd) {
        Integer unused = restrictRelationMapper.selectUnusedTaiTana(priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS, unused>0?1:0);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setPtsJandataByRestrictCd(Integer priorityOrderCd, Integer patternCd, String companyCd, String authorCd, List<Integer> attrList,
                                           List<ZokuseiMst> zokuseiMsts, GetCommonPartsDataDto commonTableName, List<Integer> allCdList,
                                           List<Map<String, Object>> restrictResult, String uuid){
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(patternCd);
        String proInfoTable = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
        List<Map<String, Object>> zokuseiList = restrictResultMapper.selectJanZokusei(priorityOrderCd, ptsCd, zokuseiMsts, allCdList,
                commonTableName.getProInfoTable(), proInfoTable);

        if (this.interruptThread(uuid, "6")) {
            return;
        }

        for (int i = 0; i < zokuseiList.size(); i++) {
            Map<String, Object> zokusei = zokuseiList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
                if(commonMstService.zokuseiEquals(attrList, restrict, zokusei)){
                    int restrictCd = MapUtils.getInteger(restrict, MagicString.RESTRICT_CD_UNDERLINE);
                    zokusei.put(MagicString.RESTRICT_CD, restrictCd);
                }
            }

            zokuseiList.set(i, zokusei);
        }
        restrictResultDataMapper.deleteByPrimaryKey(priorityOrderCd);
        zokuseiList = zokuseiList.stream().filter(map->map.get(MagicString.RESTRICT_CD)!=null).collect(Collectors.toList());

        if(!zokuseiList.isEmpty()){
            restrictResultDataMapper.insertBatch(attrList, zokuseiList, priorityOrderCd, companyCd, authorCd);
        }

        if (this.interruptThread(uuid, "7")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
