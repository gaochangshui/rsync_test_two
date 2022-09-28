package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.PriorityAllPatternListVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.exception.BusinessException;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicAllPatternMstService;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.service.PriorityAllPtsService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasicAllPatternMstServiceImpl implements BasicAllPatternMstService {
    @Autowired
    private HttpSession session;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private WorkPriorityAllResultDataMapper workPriorityAllResultDataMapper;
    @Autowired
    private PriorityAllMstMapper priorityAllMstMapper;
    @Autowired
    private ShelfPtsDataJandataMapper jandataMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private WorkPriorityAllRestrictRelationMapper workPriorityAllRestrictRelationMapper;
    @Value("${skuPerPattan}")
    private Long skuCountPerPattan;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private WorkPriorityAllRestrictMapper workPriorityAllRestrictMapper;
    @Autowired
    private PriorityAllPtsService priorityAllPtsService;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private VehicleNumCache vehicleNumCache;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private PriorityAllPtsMapper priorityAllPtsMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;

    @Autowired
    private WorkPriorityAllRestrictRelationMapper restrictRelationMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private BasicPatternRestrictResultMapper basicPatternRestrictResultMapper;
    @Autowired
    private LogAspect logAspect;
    @Autowired
    private WorkPriorityAllRestrictMapper priorityAllRestrictMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamst;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private JanInfoMapper janInfoMapper;
    @Autowired
    private PriorityOrderSortMapper priorityOrderSortMapper;
    @Autowired
    private ApplicationContext applicationContext;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Map<String, Object> autoCalculation(PriorityAllSaveDto priorityAllSaveDto) {
        String uuid = UUID.randomUUID().toString();
        String authorCd = session.getAttribute("aud").toString();

        executor.execute(()->{
            Integer basicPatternCd;

            String companyCd = priorityAllSaveDto.getCompanyCd();
            Integer priorityAllCd = priorityAllSaveDto.getPriorityAllCd();
            Integer priorityOrderCd = priorityAllSaveDto.getPriorityOrderCd();
            // 同一棚名称の棚パータンListを取得
            List<PriorityAllPatternListVO> info;
            // 基本パターンの制約List
            // 全パターンの制約List

            // 全パターンのRelationList

            try {
                PriorityOrderMstDto priorityOrderMst = priorityAllMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
                basicPatternCd = Integer.parseInt(priorityOrderMst.getShelfPatternCd());
                Integer productPowerCd = priorityOrderMst.getProductPowerCd();
                info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, basicPatternCd);
                // 全パターンのList
                List<PriorityAllPatternListVO> checkedInfo = info.stream().filter(vo->vo.getCheckFlag()==1).collect(Collectors.toList());
                int isReOrder = priorityOrderSortMapper.selectSort(companyCd, priorityOrderCd);
                BasicAllPatternMstService basicAllPatternMstService = applicationContext.getBean(BasicAllPatternMstService.class);
                for(PriorityAllPatternListVO pattern : checkedInfo) {
                    basicAllPatternMstService.autoDetect(companyCd,priorityAllCd,pattern.getShelfPatternCd(),priorityOrderCd,authorCd);

                    makeWKResultDataList(pattern, priorityAllCd, companyCd, authorCd,priorityOrderCd);

                    this.getNewReorder(companyCd,priorityOrderCd,authorCd,priorityAllCd,pattern.getShelfPatternCd());
                 


                    priorityAllPtsService.saveWorkPtsData(companyCd, authorCd, priorityAllCd, pattern.getShelfPatternCd());

                    /**
                     * 商品を置く
                     */
                    Map<String, Object> setJanResultMap = this.allPatternCommSetJan(pattern.getShelfPatternCd(),
                            companyCd, priorityOrderCd, priorityAllCd, authorCd, productPowerCd);

                    if (setJanResultMap!=null && MapUtils.getInteger(setJanResultMap, "code").equals(ResultEnum.HEIGHT_NOT_ENOUGH.getCode())) {
                        vehicleNumCache.put("setJanHeightError"+uuid,setJanResultMap.get("data"));
                    }else{
                        //ptsを一時テーブルに保存
                        Object tmpData = MapUtils.getObject(setJanResultMap, "data");
                        List<PriorityOrderResultDataDto> workData = new Gson().fromJson(new Gson().toJson(tmpData), new TypeToken<List<PriorityOrderResultDataDto>>() {
                        }.getType());
                        priorityAllPtsService.saveWorkPtsJanData(companyCd, authorCd, priorityAllCd, pattern.getShelfPatternCd(), workData, isReOrder);
                        vehicleNumCache.put(uuid,1);
                    }
                }

            } catch(Exception ex) {
                logger.error("", ex);
                vehicleNumCache.put("IO"+uuid,1);
                vehicleNumCache.put("IOError"+uuid,JSON.toJSONString(ex));
                logAspect.setTryErrorLog(ex,new Object[]{priorityAllSaveDto});
                throw new BusinessException("自動計算失敗");
            }finally {
                vehicleNumCache.put(uuid,1);
            }
        });
        return ResultMaps.result(ResultEnum.SUCCESS, uuid);
    }

    private Map<String, Object> allPatternCommSetJan(Integer patternCd, String companyCd, Integer priorityOrderCd,Integer priorityAllCd,
                                              String authorCd, Integer productPowerCd) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        PriorityOrderMst priorityOrderMst = priorityOrderMstMapper.selectOrderMstByPriorityOrderCd(priorityOrderCd);
        //all pattern don't check 棚幅チェック and 高さスペース
        Integer tanaWidCheck = 0;

        Short partitionFlag = Optional.ofNullable(priorityOrderMst.getPartitionFlag()).orElse((short) 0);
        Short partitionVal = Optional.ofNullable(priorityOrderMst.getPartitionVal()).orElse((short) 2);
        if(partitionFlag.equals((short)0)){
            partitionVal = 0;
        }

        List<PriorityOrderMstAttrSort> priorityOrderMstAttrSorts = priorityOrderMstAttrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        String zokuseiIds = priorityOrderMstAttrSorts.stream().map(PriorityOrderMstAttrSort::getValue).collect(Collectors.joining(","));
        String commonPartsData = priorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(),
                commonTableName.getProdMstClass(), zokuseiIds);
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());

        List<Map<String, Object>> relationMap = priorityAllRestrictMapper.selectByPriorityAllCd(priorityAllCd, patternCd,authorCd);
        List<Map<String, Object>> tanaList = priorityAllPtsMapper.selectTanaMstByPatternCd(priorityAllCd, patternCd);
        List<Map<String, Object>> restrictResult = restrictRelationMapper.selectRelation(priorityAllCd, patternCd);
        //zokuseiId convert to work_basic_pattern_restrict_result's zokuseiId
        for (PriorityOrderMstAttrSort mstAttrSort : priorityOrderMstAttrSorts) {
            restrictResult.forEach(map->{
                String beforeZokuseiId = MagicString.ZOKUSEI_PREFIX + (mstAttrSort.getSort() + 1);
                String val = MapUtils.getString(map, beforeZokuseiId);
                map.remove(beforeZokuseiId);
                map.put(MagicString.ZOKUSEI_PREFIX + (mstAttrSort.getValue()), val);
            });
        }
        int isReOrder = priorityOrderSortMapper.selectSort(companyCd, priorityOrderCd);
        List<Integer> attrList = priorityOrderMstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());

        String proMstHeaderTb = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
        String proMstTb = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);

        List<Map<String, Object>> sizeAndIrisu = janClassifyMapper.getSizeAndIrisu(proMstHeaderTb);
        List<PriorityOrderResultDataDto> janResult = jandataMapper.selectJanByPatternCdByAll(authorCd, companyCd, patternCd,
                priorityAllCd,priorityOrderCd, sizeAndIrisu, isReOrder, commonTableName.getProInfoTable(), proMstTb);

        List<String> colNmforMst = priorityOrderMstAttrSortMapper.getColNmforMst(companyCd, authorCd, priorityOrderCd,commonTableName);

        return commonMstService.commSetJanForShelf(patternCd, companyCd, priorityOrderCd, zokuseiMsts, allCdList,
                restrictResult, attrList, authorCd, commonTableName,
                partitionVal, null, tanaWidCheck, tanaList, relationMap,janResult,sizeAndIrisu, isReOrder, productPowerCd, colNmforMst);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer saveWKAllPatternData(PriorityAllSaveDto priorityAllSaveDto) {
        String authorCd = session.getAttribute("aud").toString();
        // mstテーブル
        try {
            priorityAllMstMapper.deleteWKTableMst(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
            // shelfsテーブル
            priorityAllMstMapper.deleteWKTableShelfs(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
            workPriorityAllRestrictMapper.deleteWKTableRestrict(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd());
            workPriorityAllRestrictRelationMapper.deleteWKTableRelation(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
            priorityAllMstMapper.insertWKTableMst(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd, priorityAllSaveDto.getPriorityOrderCd());
            priorityAllMstMapper.insertWKTableShelfs(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd, priorityAllSaveDto.getPatterns());
            priorityAllMstMapper.delWKTablePtsTai(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
            priorityAllMstMapper.delWKTablePtsTana(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
            priorityAllMstMapper.delWKTablePtsJans(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
            priorityAllMstMapper.delWKTablePtsData(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
            priorityAllMstMapper.delWKTablePtsVersion(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
        } catch (Exception e) {
            logger.error("全patternの保存に失敗しました:{}",e.getMessage());
            logAspect.setTryErrorLog(e,new Object[]{priorityAllSaveDto});
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 1;
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoDetect(String companyCd,Integer priorityAllCd,Integer shelfPatternCd,Integer priorityOrderCd,String aud) {
        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrListFinal(companyCd, priorityOrderCd);
        PriorityOrderAttrDto priorityOrderAttrDto = priorityOrderMstMapper.getCommonPartsData(companyCd, priorityOrderCd);
        String commonPartsData = priorityOrderAttrDto.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        String zokuseiIds = Joiner.on(",").join(attrList);

        List<Integer> cdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectFinalZokuseiByCd(commonTableName.getProdIsCore(),
                commonTableName.getProdMstClass(), zokuseiIds, priorityOrderCd);

        List<ShelfPtsDataTanamst> tanamsts = shelfPtsDataTanamst.selectByPatternCd((long) shelfPatternCd);

        String proMstTb = MessageFormat.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
        String proInfoTb = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);

        List<Map<String, Object>> sizeAndIrisu = janClassifyMapper.getSizeAndIrisu(proMstTb);
        Map<String, String> sizeAndIrisuMap = sizeAndIrisu.stream().collect(Collectors.toMap(map -> MapUtils.getString(map, "attr"), map -> MapUtils.getString(map, "attrVal")));
        List<Map<String, Object>> classifyList = janInfoMapper.selectJanClassify(commonTableName.getProInfoTable(), shelfPatternCd,
                zokuseiMsts, cdList, sizeAndIrisuMap, proInfoTb);

        classifyList = basicPatternMstService.updateJanSizeByMap(classifyList);
        classifyList.forEach(item-> item.put(MagicString.WIDTH, MapUtils.getLong(item,MagicString.WIDTH, MagicString.DEFAULT_WIDTH)*MapUtils.getInteger(item, "faceCount",1)));

        Map<String, BasicPatternRestrictResult> classify = basicPatternMstService.getJanInfoClassify(classifyList, companyCd,
                zokuseiMsts, aud, (long) priorityAllCd);

        workPriorityAllRestrictMapper.deleteBasicPatternResult(companyCd,priorityAllCd,aud,shelfPatternCd);
        classify = this.generateRestrictCd(classify);
        List<BasicPatternRestrictResult> basicPatternRestrictResults = new ArrayList<>(classify.values());
        BasicPatternRestrictResult result = new BasicPatternRestrictResult();
        result.setRestrictCd(MagicString.NO_RESTRICT_CD);
        result.setAuthorCd(aud);
        result.setCompanyCd(companyCd);
        result.setPriorityOrderCd((long)priorityOrderCd);
        basicPatternRestrictResults.add(result);
        workPriorityAllRestrictMapper.setBasicPatternResult(basicPatternRestrictResults,shelfPatternCd);

        List<Integer> zokuseiList = zokuseiMsts.stream().map(ZokuseiMst::getZokuseiId).collect(Collectors.toList());

        workPriorityAllRestrictRelationMapper.deleteBasicPatternRelation(companyCd,priorityAllCd,aud,shelfPatternCd);
        for (ShelfPtsDataTanamst tanamst : tanamsts) {
            final int[] index = {1};
            Integer taiCd = tanamst.getTaiCd();
            Integer tanaCd = tanamst.getTanaCd();
            Integer tanaWidth = tanamst.getTanaWidth();

            List<Map<String, Object>> jans = classifyList.stream().filter(map -> commonMstService.taiTanaEquals(MapUtils.getInteger(map, MagicString.TAI_CD),
                    taiCd, MapUtils.getInteger(map, MagicString.TANA_CD), tanaCd)).collect(Collectors.toList());

            logger.info("taiCd:{},tanaCd:{}, jans:{}", taiCd, tanaCd,jans);
            double areaWidth = 0;
            String lastKey = "";
            int janCount = 0;
            //Traverse all groups. If it is different from the previous group, record it. If it is the same, the area will be accumulated
            List<Map<String, Object>> newJans = new ArrayList<>();
            for (int i = 0; i < jans.size(); i++) {
                Map<String, Object> janMap = jans.get(i);
                double width = MapUtils.getDouble(janMap, MagicString.WIDTH);
                String key = getClassifyKey(zokuseiList, janMap);

                if(isLastAndEqualsToLastKey(key, lastKey, i, jans.size())){
                    areaWidth += width;
                    janCount++;
                }

                if(!"".equals(lastKey) && (!lastKey.equals(key) || (i+1)==jans.size())){
                    double percent = BigDecimal.valueOf(areaWidth).divide(BigDecimal.valueOf(tanaWidth), 5, RoundingMode.CEILING)
                            .multiply(BigDecimal.valueOf(100)).doubleValue();
                    Map<String, Object> map = new GsonBuilder().create().fromJson(JSON.toJSONString(janMap),
                            new TypeToken<Map<String, Object>>(){}.getType());
                    map.put(MagicString.RESTRICT_CD, classify.get(lastKey).getRestrictCd());
                    map.put("area", percent);
                    map.put("priorityOrderCd", priorityAllCd);
                    map.put("janCount", janCount);
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
                    int percent = BigDecimal.valueOf(areaWidth).divide(BigDecimal.valueOf(tanaWidth), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
                    Map<String, Object> map = new GsonBuilder().create().fromJson(JSON.toJSONString(janMap),
                            new TypeToken<Map<String, Object>>(){}.getType());
                    map.put(MagicString.RESTRICT_CD, classify.get(key).getRestrictCd());
                    map.put("area", percent);
                    map.put("janCount", janCount);
                    map.put("priorityOrderCd", priorityAllCd);
                    map.put("companyCd", companyCd);
                    map.put("authorCd", aud);
                    newJans.add(map);
                }

                lastKey = key;
            }

            newJans.forEach(map->{
                map.put("tanaPosition", index[0]);
                index[0]++;
            });
            workPriorityAllRestrictRelationMapper.setBasicPatternRelation(newJans,shelfPatternCd);
        }


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

    private String getClassifyKey(List<Integer> zokuseiList, Map<String, Object> janMap){
        StringBuilder key = new StringBuilder();
        for (Integer zokusei : zokuseiList) {
            if(key.length()>0){
                key.append(",");
            }
            key.append(MapUtils.getString(janMap, zokusei+""));
        }
        return key.toString();
    }

    private boolean isLastAndNotEqualsToLastKey(String key, String lastKey, int currentIndex, int size){
        return !lastKey.equals(key) && (currentIndex+1)==size;
    }

    private boolean isLastAndEqualsToLastKey(String key, String lastKey, int currentIndex, int size){
        return lastKey.equals(key) && (currentIndex+1)==size;
    }

    private int makeWKResultDataList(PriorityAllPatternListVO pattern, Integer priorityAllCd, String companyCd, String authorCd, Integer priorityOrderCd) {
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(pattern.getShelfPatternCd());
        List<Map<String, Object>> ptsGroup = this.getPtsGroup(companyCd, priorityOrderCd, ptsCd,authorCd,priorityAllCd,pattern.getShelfPatternCd());
        workPriorityAllResultDataMapper.deleteWKTableResultData(companyCd,priorityAllCd,authorCd,pattern.getShelfPatternCd());
        return  workPriorityAllResultDataMapper.insertWKTableResultData(companyCd, priorityAllCd, authorCd,pattern.getShelfPatternCd(),ptsGroup);


    }
    /**
     * sort順
     * @param companyCd
     * @param priorityOrderCd
     * @param authorCd
     * @return
     */
    public Map<String, Object> getNewReorder(String companyCd, Integer priorityOrderCd, String authorCd,Integer priorityAllCd,Integer patternCd) {
        PriorityOrderMst priorityOrderMst = priorityOrderMstMapper.selectOrderMstByPriorityOrderCd(priorityOrderCd);

        String commonPartsData = priorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);

        List<String> colNmforMst = priorityOrderMstAttrSortMapper.getColNmforMst(companyCd, authorCd, priorityOrderCd,commonTableName);

        if (colNmforMst.isEmpty()){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        List<WorkPriorityOrderResultData> reorder = null;
        if (colNmforMst.size() == 1) {
            reorder = workPriorityAllResultDataMapper.getReorder(companyCd, authorCd,priorityOrderMst.getProductPowerCd(), priorityAllCd,commonTableName, colNmforMst.get(0), patternCd,null);
        } else if (colNmforMst.size() == 2){
            reorder = workPriorityAllResultDataMapper.getReorder(companyCd, authorCd,priorityOrderMst.getProductPowerCd(), priorityAllCd,commonTableName, colNmforMst.get(0),patternCd, colNmforMst.get(1));
        }

        workPriorityAllResultDataMapper.setSortRank(reorder, companyCd, authorCd, priorityOrderCd,priorityAllCd,patternCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }



    public List<Map<String, Object>> getPtsGroup(String companyCd,Integer priorityOrderCd,Integer ptsCd,String authorCd,Integer priorityAllCd,Integer patternCd) {

        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKeyForFinal(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        PriorityOrderMst priorityOrderMst = priorityOrderMstMapper.selectOrderMstByPriorityOrderCd(priorityOrderCd);
        String commonPartsData = priorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String, Object>> restrictResult = priorityAllRestrictMapper.selectRestrictResult(priorityAllCd, patternCd, authorCd);
        List<Map<String, Object>> newRestrictResult = new ArrayList<>();
        //zokuseiId convert to work_basic_pattern_restrict_result's zokuseiId
        //1,3,5->1,2,3

        for (Map<String, Object> map : restrictResult) {
            Map<String, Object> newHashMap = new HashMap<>();
            newHashMap.put("shelf_pattern_cd", map.get("shelf_pattern_cd"));
            newHashMap.put(MagicString.RESTRICT_CD_UNDERLINE, map.get(MagicString.RESTRICT_CD_UNDERLINE));
            newHashMap.put("author_cd", map.get("author_cd"));
            newHashMap.put("priority_all_cd", map.get("priority_all_cd"));
            newHashMap.put("company_cd", map.get("company_cd"));

            for (PriorityOrderMstAttrSort mstAttrSort : mstAttrSorts) {
                String beforeZokuseiId = MagicString.ZOKUSEI_PREFIX + (mstAttrSort.getSort() + 1);
                String val = MapUtils.getString(map, beforeZokuseiId);
                newHashMap.put(MagicString.ZOKUSEI_PREFIX + (mstAttrSort.getValue()), val);
            }

            newRestrictResult.add(newHashMap);
        }

        String proMstInfo = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<Map<String, Object>> zokuseiList = basicPatternRestrictResultMapper.selectAllPatternResultData(ptsCd, zokuseiMsts, allCdList,
                commonTableName.getProInfoTable(),zokuseiCol, proMstInfo);
        for (int i = 0; i < zokuseiList.size(); i++) {
            Map<String, Object> zokusei = zokuseiList.get(i);
            for (Map<String, Object> restrict : newRestrictResult) {
                if(this.groupEquals(attrList, restrict, zokusei)){
                    int restrictCd = MapUtils.getInteger(restrict, MagicString.RESTRICT_CD_UNDERLINE);
                    zokusei.put(MagicString.RESTRICT_CD, restrictCd);
                }
            }

            zokuseiList.set(i, zokusei);
        }
        zokuseiList = zokuseiList.stream().filter(map->MapUtils.getInteger(map, MagicString.RESTRICT_CD)!=null).collect(Collectors.toList());
        return zokuseiList;
    }

    private boolean groupEquals(List<Integer> attrList, Map<String, Object> restrict, Map<String, Object> zokusei){
        int equalsCount = 0;
        for (Integer integer : attrList) {
            String restrictKey = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + integer, MagicString.DEFAULT_VALUE);
            String zokuseiKey = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + integer, MagicString.DEFAULT_VALUE);

            if(Objects.equals(restrictKey, zokuseiKey)){
                equalsCount++;
            }
        }

        return equalsCount == attrList.size();
    }
}
