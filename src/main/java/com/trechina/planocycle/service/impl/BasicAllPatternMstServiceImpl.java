package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private PriorityOrderRestrictResultMapper priorityOrderRestrictResultMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
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
    private PriorityOrderMstService priorityOrderMstService;
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
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
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
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Map<String, Object> autoCalculation(PriorityAllSaveDto priorityAllSaveDto) {
        String uuid = UUID.randomUUID().toString();
        String authorCd = session.getAttribute("aud").toString();
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");

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
                basicPatternCd = priorityAllMstMapper.getPatternCdBYPriorityCd(companyCd, priorityOrderCd);
                info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, basicPatternCd,authorCd);
                // 全パターンのList
                List<PriorityAllPatternListVO> checkedInfo = info.stream().filter(vo->vo.getCheckFlag()==1).collect(Collectors.toList());
                int isReOrder = priorityOrderSortMapper.selectSort(companyCd, priorityOrderCd);
                for(PriorityAllPatternListVO pattern : checkedInfo) {
                    autoDetect(companyCd,priorityAllCd,pattern.getShelfPatternCd(),priorityOrderCd,authorCd);

                    makeWKResultDataList(pattern, priorityAllCd, companyCd, authorCd,priorityOrderCd);

                    this.getNewReorder(companyCd,priorityOrderCd,authorCd,priorityAllCd,pattern.getShelfPatternCd());
                    ////古いptsの平均値、最大値最小値を取得
                    FaceNumDataDto faceNum = productPowerMstMapper.getFaceNum(pattern.getShelfPatternCd());
                    Integer minFaceNum = faceNum.getFaceMinNum();

                    priorityAllPtsService.saveWorkPtsData(companyCd, authorCd, priorityAllCd, pattern.getShelfPatternCd());

                    /**
                     * 商品を置く
                     */
                    Map<String, Object> setJanResultMap = this.allPatternCommSetJan(pattern.getShelfPatternCd(),
                            companyCd, priorityOrderCd, priorityAllCd, authorCd, minFaceNum);

                    if (setJanResultMap!=null && MapUtils.getInteger(setJanResultMap, "code").equals(ResultEnum.HEIGHT_NOT_ENOUGH.getCode())) {
                        vehicleNumCache.put("setJanHeightError"+uuid,setJanResultMap.get("data"));
                    }else{
                        //ptsを一時テーブルに保存
                        Object tmpData = MapUtils.getObject(setJanResultMap, "data");
                        List<WorkPriorityOrderResultDataDto> workData = new Gson().fromJson(new Gson().toJson(tmpData), new TypeToken<List<WorkPriorityOrderResultDataDto>>() {
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
                                              String authorCd, Integer minFaceNum){
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
        int isReOrder = priorityOrderSortMapper.selectSort(companyCd, priorityOrderCd);
        List<Integer> attrList = priorityOrderMstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        List<Map<String, Object>> sizeAndIrisu = janClassifyMapper.getSizeAndIrisu(commonTableName.getProAttrTable());
        List<PriorityOrderResultDataDto> janResult = jandataMapper.selectJanByPatternCdByAll(authorCd, companyCd, patternCd,
                priorityAllCd,priorityOrderCd, sizeAndIrisu, isReOrder, commonTableName.getProInfoTable());

        return commonMstService.commSetJanForShelf(patternCd, companyCd, priorityOrderCd, minFaceNum, zokuseiMsts, allCdList,
                restrictResult, attrList, authorCd, commonTableName,
                partitionVal, null, tanaWidCheck, tanaList, relationMap,janResult,sizeAndIrisu, isReOrder);
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
            return 1;
        }
        return 0;
    }

    @Override
    public void autoDetect(String companyCd,Integer priorityAllCd,Integer shelfPatternCd,Integer priorityOrderCd,String aud) {
        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrListFinal(companyCd, priorityOrderCd);
        PriorityOrderAttrDto priorityOrderAttrDto = priorityOrderMstMapper.getCommonPartsData(companyCd, priorityOrderCd);
        String commonPartsData = priorityOrderAttrDto.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        String zokuseiIds = Joiner.on(",").join(attrList);

        List<Integer> cdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(),
                commonTableName.getProdMstClass(), zokuseiIds);

        List<ShelfPtsDataTanamst> tanamsts = shelfPtsDataTanamst.selectByPatternCd((long) shelfPatternCd);

        List<Map<String, Object>> sizeAndIrisu = janClassifyMapper.getSizeAndIrisu(commonTableName.getProAttrTable());
        Map<String, String> sizeAndIrisuMap = sizeAndIrisu.stream().collect(Collectors.toMap(map -> MapUtils.getString(map, "attr"), map -> MapUtils.getString(map, "attrVal")));
        List<Map<String, Object>> classifyList = janInfoMapper.selectJanClassify(commonTableName.getProInfoTable(), shelfPatternCd,
                zokuseiMsts, cdList, sizeAndIrisuMap);


        Map<String, BasicPatternRestrictResult> classify = basicPatternMstService.getJanInfoClassify(classifyList, companyCd,
                zokuseiIds, aud, (long) priorityAllCd);

        workPriorityAllRestrictMapper.deleteBasicPatternResult(companyCd,priorityAllCd,aud,shelfPatternCd);
        long restrictCd = 1;
        for (Map.Entry<String, BasicPatternRestrictResult> entry : classify.entrySet()) {
            BasicPatternRestrictResult value = entry.getValue();
            value.setRestrictCd(restrictCd);
            classify.put(entry.getKey(), value);
            restrictCd++;
        }
        List<BasicPatternRestrictResult> basicPatternRestrictResults = new ArrayList<>(classify.values());
        BasicPatternRestrictResult result = new BasicPatternRestrictResult();
        result.setRestrictCd(MagicString.NO_RESTRICT_CD);
        result.setAuthorCd(aud);
        result.setCompanyCd(companyCd);
        result.setPriorityOrderCd((long)priorityOrderCd);
        basicPatternRestrictResults.add(result);
        workPriorityAllRestrictMapper.setBasicPatternResult(basicPatternRestrictResults,shelfPatternCd);

        ArrayList<String> zokuseiList = Lists.newArrayList(zokuseiIds.split(","));

        workPriorityAllRestrictRelationMapper.deleteBasicPatternRelation(companyCd,priorityAllCd,aud,shelfPatternCd);
        for (ShelfPtsDataTanamst tanamst : tanamsts) {
            final int[] index = {1};
            Integer taiCd = tanamst.getTaiCd();
            Integer tanaCd = tanamst.getTanaCd();
            Integer tanaWidth = tanamst.getTanaWidth();

            List<Map<String, Object>> jans = classifyList.stream().filter(map -> MapUtils.getInteger(map, MagicString.TAI_CD).equals(taiCd) &&
                    MapUtils.getInteger(map, MagicString.TANA_CD).equals(tanaCd)).collect(Collectors.toList());

            logger.info("taiCd:{},tanaCd:{}, jans:{}", taiCd, tanaCd,jans);
            double areaWidth = 0;
            String lastKey = "";
            int janCount = 0;
            //Traverse all groups. If it is different from the previous group, record it. If it is the same, the area will be accumulated
            List<Map<String, Object>> newJans = new ArrayList<>();
            for (int i = 0; i < jans.size(); i++) {
                Map<String, Object> janMap = jans.get(i);
                double width = MapUtils.getDouble(janMap, "width");
                StringBuilder key = new StringBuilder();
                for (String zokusei : zokuseiList) {
                    if(key.length()>0){
                        key.append(",");
                    }
                    key.append(MapUtils.getString(janMap, zokusei));
                }

                if(lastKey.equals(key.toString()) && (i+1)==jans.size()){
                    areaWidth += width;
                    janCount++;
                }

                if(!"".equals(lastKey) && (!lastKey.equals(key.toString()) || (i+1)==jans.size())){
                    double percent = BigDecimal.valueOf(areaWidth).divide(BigDecimal.valueOf(tanaWidth), 2, RoundingMode.CEILING)
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
                if(!lastKey.equals(key.toString()) && (i+1)==jans.size()){
                    areaWidth = width;
                    int percent = BigDecimal.valueOf(areaWidth).divide(BigDecimal.valueOf(tanaWidth), 2, BigDecimal.ROUND_UP)
                            .multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_UP).intValue();
                    Map<String, Object> map = new GsonBuilder().create().fromJson(JSONObject.toJSONString(janMap),
                            new TypeToken<Map<String, Object>>(){}.getType());
                    map.put(MagicString.RESTRICT_CD, classify.get(key.toString()).getRestrictCd());
                    map.put("area", percent);
                    map.put("janCount", janCount);
                    map.put("priorityOrderCd", priorityOrderCd);
                    map.put("companyCd", companyCd);
                    map.put("authorCd", aud);
                    newJans.add(map);
                }

                lastKey = key.toString();
            }

            newJans.stream().forEach(map->{
                map.put("tanaPosition", index[0]);
                index[0]++;
            });
            if(!newJans.isEmpty()){
                workPriorityAllRestrictRelationMapper.setBasicPatternRelation(newJans,shelfPatternCd);
            }
        }


    }

    /**
     * 基本パターンの制約により各パターンの制約一覧を作成
     * @param pattern
     * @param basicRestrictList
     * @param priorityAllCd
     * @param companyCd
     * @param authorCd
     * @param basicTannaNum
     * @return
     */
    private List<PriorityAllRestrictDto> makeWKRestrictList(PriorityAllPatternListVO pattern
            , List<WorkPriorityAllRestrictResult> basicRestrictList, Integer priorityAllCd
            , String companyCd, String  authorCd, BigDecimal basicTannaNum) {
        List<PriorityAllRestrictDto> allRestrictDtoList = new ArrayList<>();

        //基本パターン的face数拡大三倍取商品（基本パターン已同期修改）
        Long skuCountPer = skuCountPerPattan*3;

        // チェックされたパターン
        BigDecimal inX = new BigDecimal(pattern.getTanaCnt()).divide(basicTannaNum, 2, BigDecimal.ROUND_DOWN);
        logger.info("基本パターン：{}, 全パターン：{},系数：{}", pattern.getTanaCnt(), basicTannaNum, inX.doubleValue());
        BigDecimal allTanaNum = new BigDecimal(0);
        // 基本パターンの制約List
        for(WorkPriorityAllRestrictResult basicSet : basicRestrictList) {
            PriorityAllRestrictDto allSet = new PriorityAllRestrictDto();
            allSet.setPriorityAllCd(priorityAllCd);
            allSet.setCompanyCd(companyCd);
            allSet.setAuthorCd(authorCd);
            allSet.setPatternCd(pattern.getShelfPatternCd());
            allSet.setRestrictCd(basicSet.getRestrictCd());
            allSet.setZokusei1(basicSet.getZokusei1());
            allSet.setZokusei2(basicSet.getZokusei2());
            allSet.setZokusei3(basicSet.getZokusei3());
            allSet.setZokusei4(basicSet.getZokusei4());
            allSet.setZokusei5(basicSet.getZokusei5());
            allSet.setZokusei6(basicSet.getZokusei6());
            allSet.setZokusei7(basicSet.getZokusei7());
            allSet.setZokusei8(basicSet.getZokusei8());
            allSet.setZokusei9(basicSet.getZokusei9());
            allSet.setZokusei10(basicSet.getZokusei10());

            //      係数<1 の場合「基本より小さい場合
            //          tana_cnt が１及び以下の分は維持
            //          tana_cnt が１より大きい分は係数*tana_cnt、小数点は切り捨て
            BigDecimal basicTanaCnt = basicSet.getTanaCnt();
            Integer tmpTanaCnt = inX.multiply(basicTanaCnt).intValue();
            allSet.setBasicTanaCnt(basicTanaCnt);

            if(basicTanaCnt.compareTo(BigDecimal.valueOf(0.5))==0) {
                allSet.setTanaCnt(BigDecimal.valueOf(0.5));
                logger.info("拡/縮后：{}", 0.5);
            } else {
                allSet.setTanaCnt(new BigDecimal(tmpTanaCnt));
                logger.info("拡/縮后：{}", tmpTanaCnt);
            }

            allSet.setSkuCnt(allSet.getTanaCnt().multiply(new BigDecimal(skuCountPer)).intValue());
            // 全パターン制約に追加
            allRestrictDtoList.add(allSet);
            allTanaNum = allTanaNum.add(allSet.getTanaCnt());
        }
        // 残棚がある場合
        if (new BigDecimal(pattern.getTanaCnt()).compareTo(allTanaNum) > 0 ) {
            //　棚数の小順
            allRestrictDtoList.sort((a, b) -> b.getBasicTanaCnt().compareTo(a.getBasicTanaCnt()));

            // 棚数が多い制約から棚追加
            for(PriorityAllRestrictDto allRestrict : allRestrictDtoList) {
                BigDecimal remainTanaCnt = new BigDecimal(pattern.getTanaCnt()).subtract(allTanaNum);
                if(remainTanaCnt.compareTo(BigDecimal.ZERO)!=0){
                    //残りのtana数がなく、そのまま終了
                    logger.info("基本{}, 拡縮后{}", allRestrict.getBasicTanaCnt(), allRestrict.getTanaCnt());
                    if (remainTanaCnt.compareTo(new BigDecimal(1)) < 0) {
                        allRestrict.setTanaCnt(allRestrict.getTanaCnt().add(BigDecimal.valueOf(0.5)));
                        allRestrict.setSkuCnt(allRestrict.getSkuCnt() + BigDecimal.valueOf(skuCountPer/2).setScale(0, RoundingMode.CEILING).intValue());
                        break;
                    } else {
                        allRestrict.setTanaCnt(allRestrict.getTanaCnt().add(new BigDecimal(1)));
                        allRestrict.setSkuCnt(allRestrict.getSkuCnt() + skuCountPer.intValue());
                        allTanaNum = allTanaNum.add(new BigDecimal(1));
                    }
                }
            }
        }
        // 制約IDにより並び替え
        allRestrictDtoList.sort(Comparator.comparingInt(a -> a.getRestrictCd().intValue()));

        return allRestrictDtoList;
    }

    private List<WorkPriorityAllRestrictRelation> makeWKRelationList(List<PriorityAllRestrictDto> allRestrictDtoList
            , List<ShelfPtsDataTanamst> tanaList, Integer priorityAllCd, String companyCd
            , String  authorCd, Integer patternCd){
        List<WorkPriorityAllRestrictRelation> allRelationsList = new ArrayList<>();
        int iRestrict = 0;
        BigDecimal iTanaCnt = allRestrictDtoList.get(0).getTanaCnt();
        for (ShelfPtsDataTanamst tanaInfo : tanaList) {
            WorkPriorityAllRestrictRelation relation = new WorkPriorityAllRestrictRelation();
            WorkPriorityAllRestrictRelation relation2 = null;
            relation.setPriorityAllCd(priorityAllCd);
            relation.setCompanyCd(companyCd);
            relation.setAuthorCd(authorCd);
            relation.setPatternCd(patternCd);
            relation.setTaiCd(tanaInfo.getTaiCd());
            relation.setTanaCd(tanaInfo.getTanaCd());
            relation.setRestrictCd(allRestrictDtoList.get(iRestrict).getRestrictCd());
            if (iTanaCnt.compareTo(BigDecimal.valueOf(0.5)) == 0) {
                // 該当制約の面積が半棚の場合
                relation.setTanaType((short) 1);
                // 該当棚の残り半分を次の制約を設定する
                relation2 = new WorkPriorityAllRestrictRelation();
                relation2.setPriorityAllCd(priorityAllCd);
                relation2.setCompanyCd(companyCd);
                relation2.setAuthorCd(authorCd);
                relation2.setPatternCd(patternCd);
                relation2.setTaiCd(tanaInfo.getTaiCd());
                relation2.setTanaCd(tanaInfo.getTanaCd());
                relation2.setTanaType((short) 2);
                iRestrict++;
                relation2.setRestrictCd(allRestrictDtoList.get(iRestrict).getRestrictCd());
                // 次の制約の棚Cntは半分を減らす
                iTanaCnt = allRestrictDtoList.get(iRestrict).getTanaCnt().subtract(BigDecimal.valueOf(0.5));
                if (iTanaCnt.compareTo(new BigDecimal(0)) == 0 && iRestrict < allRestrictDtoList.size() - 1){
                    // まだ終わってないから次へ
                    iRestrict++;
                    iTanaCnt = allRestrictDtoList.get(iRestrict).getTanaCnt();
                }
            } else if (iTanaCnt.compareTo(new BigDecimal(1)) == 0) {
                // 残り棚数１の場合、該当制約設定完了次の制約へ
                relation.setTanaType((short)0);
                if (iRestrict < allRestrictDtoList.size() - 1) {
                    iRestrict++;
                    iTanaCnt = allRestrictDtoList.get(iRestrict).getTanaCnt();
                }
            } else {
                // 残棚数が１以上の場合、この制約の棚Cnt-１で次の棚に行く
                relation.setTanaType((short)0);
                iTanaCnt = iTanaCnt.subtract(new BigDecimal(1));
            }
            // Listに追加
            allRelationsList.add(relation);
            if (relation2 != null) {
                allRelationsList.add(relation2);
            }
        }
        return allRelationsList;
    }

    /**
     * 基本パターンの制約により各パターンの制約一覧を作成
     * @param pattern
     * @param priorityAllCd
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     * @return
     */
    private int makeWKRestrictList(PriorityAllPatternListVO pattern
            , Integer priorityAllCd
            , String companyCd, String  authorCd,Integer priorityOrderCd) {
        workPriorityAllRestrictMapper.deleteBasicPatternResult(companyCd,priorityAllCd,authorCd,pattern.getShelfPatternCd());
        // 全パターンRelationテーブル更新
        return 1;
    }

    /**
     *
     * @param pattern
     * @param priorityAllCd
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     * @return
     */
    private int makeWKRelationList(PriorityAllPatternListVO pattern
            , Integer priorityAllCd
            , String companyCd, String  authorCd,Integer priorityOrderCd){
        workPriorityAllRestrictRelationMapper.deleteBasicPatternRelation(companyCd,priorityAllCd,authorCd,pattern.getShelfPatternCd());
        return 1;

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
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);

        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);

        List<String> colNmforMst = priorityOrderMstAttrSortMapper.getColNmforMst(companyCd, authorCd, priorityOrderCd,commonTableName);

        if (colNmforMst.isEmpty()){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        List<WorkPriorityOrderResultData> reorder = null;
        if (colNmforMst.size() == 1) {
            reorder = workPriorityAllResultDataMapper.getReorder(companyCd, authorCd,workPriorityOrderMst.getProductPowerCd(), priorityAllCd,commonTableName, colNmforMst.get(0), patternCd,null);
        } else if (colNmforMst.size() == 2){
            reorder = workPriorityAllResultDataMapper.getReorder(companyCd, authorCd,workPriorityOrderMst.getProductPowerCd(), priorityAllCd,commonTableName, colNmforMst.get(0),patternCd, colNmforMst.get(1));
        }

        workPriorityAllResultDataMapper.setSortRank(reorder, companyCd, authorCd, priorityOrderCd,priorityAllCd,patternCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }



    public List<Map<String, Object>> getPtsGroup(String companyCd,Integer priorityOrderCd,Integer ptsCd,String authorCd,Integer priorityAllCd,Integer patternCd) {

        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String, Object>> restrictResult = priorityAllRestrictMapper.selectRestrictResult(priorityAllCd, patternCd, authorCd);

        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<Map<String, Object>> zokuseiList = basicPatternRestrictResultMapper.selectAllPatternResultData(ptsCd, zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol);
        for (int i = 0; i < zokuseiList.size(); i++) {
            Map<String, Object> zokusei = zokuseiList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
                int equalsCount = 0;
                for (Integer integer : attrList) {
                    String restrictKey = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + integer, "");
                    String zokuseiKey = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + integer, "");

                    if(restrictKey!=null && restrictKey.equals(zokuseiKey)){
                        equalsCount++;
                    }
                }

                if(equalsCount == attrList.size()){
                    int restrictCd = MapUtils.getInteger(restrict, "restrict_cd");
                    zokusei.put(MagicString.RESTRICT_CD, restrictCd);
                }
            }

            zokuseiList.set(i, zokusei);
        }
        zokuseiList = zokuseiList.stream().filter(map->MapUtils.getInteger(map, MagicString.RESTRICT_CD)!=null).collect(Collectors.toList());
        return zokuseiList;
    }
}
