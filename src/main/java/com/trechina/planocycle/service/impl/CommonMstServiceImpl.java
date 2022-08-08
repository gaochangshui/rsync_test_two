package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.CommonPartsDto;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommonMstServiceImpl implements CommonMstService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AreasMapper areasMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ProductPowerParamMstMapper productPowerParamMstMapper;
    @Autowired
    private ClassicPriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ShelfPtsDataJandataMapper jandataMapper;
    @Autowired
    private BasicPatternRestrictRelationMapper restrictRelationMapper;

    @Autowired
    private WorkPriorityOrderJanCutMapper janCutMapper;
    @Autowired
    private WorkPriorityOrderJanNewMapper janNewMapper;
    @Autowired
    private BasicPatternRestrictResultMapper basicRestrictResultMapper;
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanNewService priorityOrderJanNewService;
    @Autowired
    private JanClassifyMapper janClassifyMapper;

    //@Override
    //public Map<String, Object> getAreaInfo(String companyCd) {
    //    List<Areas> areasList = areasMapper.select(companyCd);
    //    return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    //}
    //@Override
    //public Map<String, Object> getAreaForShelfName(Integer shelfNameCd) {
    //    List<Areas> areasList = areasMapper.selectForShelfName(shelfNameCd);
    //    return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    //}

    @Override
    public Map<String, List<PriorityOrderResultDataDto>> commSetJan(Short partitionFlag, Short partitionVal,
        List<PtsTaiVo> taiData, List<PriorityOrderResultDataDto> workPriorityOrderResultData,
        List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations, Integer minFace) {
        /**
         * tai_によるとtanaは割り当てられた商品リストを分類して保管します
         */
        Map<String, List<PriorityOrderResultDataDto>> finalSetJanResultData = new HashMap<>();

        if (partitionFlag == 0) {
            //仕切りがない場合
            partitionVal = 0;
        }

        /**
         * 制約条件に従ってグループ分けして配置する
         */
        Map<Long, List<WorkPriorityOrderRestrictRelation>> relationByGroup = workPriorityOrderRestrictRelations
                .stream().collect(Collectors.groupingBy(WorkPriorityOrderRestrictRelation::getRestrictCd, LinkedHashMap::new, Collectors.toList()));

        List<WorkPriorityOrderRestrictRelation> relationValue = null;
        List<PriorityOrderResultDataDto> relationSorted = null;
        PriorityOrderResultDataDto priorityOrderResultData = null;
        List<PriorityOrderResultDataDto> resultData = null;
        List<String> adoptedJan = new ArrayList<>();

        for (Map.Entry<Long, List<WorkPriorityOrderRestrictRelation>> relationEntry : relationByGroup.entrySet()) {
            Long relationCd = relationEntry.getKey();
            //商品がどこに置かれたかを記録する-同じ制約の商品が異なる台、段
            int setResultDataIndex = 0;

            //現在の制約条件に合致する商品はrankでソートする
            //sortrankがnullの場合skurankのみでソート
            relationSorted = workPriorityOrderResultData
                    .stream().filter(data -> relationCd.equals(data.getRestrictCd()))
                    .sorted(Comparator.comparing(PriorityOrderResultDataDto::getSortRank, Comparator.nullsFirst(Long::compareTo))
                            .thenComparingLong(PriorityOrderResultDataDto::getSkuRank)
                            .thenComparingLong(PriorityOrderResultDataDto::getNewRank)).collect(Collectors.toList());

            relationValue = relationEntry.getValue();
            for (WorkPriorityOrderRestrictRelation workPriorityOrderRestrictRelation : relationValue) {
                Integer taiCd = workPriorityOrderRestrictRelation.getTaiCd();
                Integer tanaCd = workPriorityOrderRestrictRelation.getTanaCd();
                short tanaType = workPriorityOrderRestrictRelation.getTanaType();

                //分類key、同一類の商品はface数を減らす処理を行う
                String taiTanaKey = taiCd + "_" + tanaCd + "_" + tanaType;

                Optional<PtsTaiVo> taiInfo = taiData.stream().filter(ptsTaiVo -> taiCd.equals(ptsTaiVo.getTaiCd())).findFirst();

                if (!taiInfo.isPresent()) {
                    logger.info("{}台信息不存在", taiCd);
                    continue;
                }

                Integer taiWidth = taiInfo.get().getTaiWidth();
                //テーブルまたはセグメントの幅、使用済みの幅
                double width = taiWidth;
                double usedWidth = 0;

                if (tanaType != 0) {
                    //セグメントの幅は、特定の位置セグメントの幅に応じて配置されます。
                    width = taiWidth / 2.0;
                }

                //商品を置く
                for (int i = setResultDataIndex; i < relationSorted.size(); i++) {
                    priorityOrderResultData = relationSorted.get(i);

                    //if jan is adopted, it will not be set
                    if(adoptedJan.contains(priorityOrderResultData.getJanCd())){
                        continue;
                    }

                    Long faceSku = Optional.ofNullable(priorityOrderResultData.getFaceSku()).orElse(1L);
                    Long janWidth = Optional.ofNullable(priorityOrderResultData.getPlanoWidth()).orElse(0L);
                    Long face = priorityOrderResultData.getFace();

                    //商品幅nullまたは0の場合はデフォルト幅67 mm、faceSku>1の必要にfaceSkuを乗じます
                    if (janWidth == 0) {
                        janWidth = 67 * faceSku;
                    }
                    priorityOrderResultData.setWidth(janWidth);

                    long janTotalWidth = janWidth * face + partitionVal;
                    if (janTotalWidth + usedWidth <= width) {
                        //face数に応じて並べて離すことができます
                        setResultDataIndex = i + 1;

                        priorityOrderResultData.setFaceFact(face);
                        priorityOrderResultData.setTaiCd(taiCd);
                        priorityOrderResultData.setTanaCd(tanaCd);
                        priorityOrderResultData.setAdoptFlag(1);
                        priorityOrderResultData.setTanaType((int) tanaType);

                        adoptedJan.add(priorityOrderResultData.getJanCd());

                        resultData = finalSetJanResultData.getOrDefault(taiTanaKey, new ArrayList<>());
                        resultData.add(priorityOrderResultData);
                        finalSetJanResultData.put(taiTanaKey, resultData);

                        usedWidth += janTotalWidth;
                    } else {
                        resultData = finalSetJanResultData.getOrDefault(taiTanaKey, new ArrayList<>());
                        if(this.isSetJanByCutFace(resultData, width, usedWidth, partitionVal, minFace, priorityOrderResultData)){
                            priorityOrderResultData.setTaiCd(taiCd);
                            priorityOrderResultData.setTanaType((int) tanaType);
                            priorityOrderResultData.setAdoptFlag(1);
                            priorityOrderResultData.setTanaCd(tanaCd);

                            adoptedJan.add(priorityOrderResultData.getJanCd());

                            setResultDataIndex = i + 1;
                            resultData.add(priorityOrderResultData);
                            finalSetJanResultData.put(taiTanaKey, resultData);
                        }

                        //face数に応じて並べても離せず、そのまま置かず、その位置の並べ替えを終了します
                        break;
                    }
                }
            }
        }

        return finalSetJanResultData;
    }

    /**
     * set jans for shelf
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> commSetJanForShelf(Integer patternCd, String companyCd, Integer priorityOrderCd,
                                                  Integer minFace, List<ZokuseiMst> zokuseiMsts, List<Integer> allCdList,
                                                  List<Map<String, Object>> restrictResult, List<Integer> attrList, String aud,
                                                  GetCommonPartsDataDto commonTableName, Short partitionVal, Short topPartitionVal,
                                                  Integer tanaWidthCheck, List<Map<String, Object>> tanaList, List<Map<String, Object>> relationMap,
                                                  List<PriorityOrderResultDataDto> janResult, List<Map<String, Object>> sizeAndIrisu,
                                                  int isReOrder) {
        List<Map<String, Object>> cutList = janCutMapper.selectJanCut(priorityOrderCd);

        Integer currentTaiCd=1;
        for (int i = 0; i < tanaList.size(); i++) {
            Map<String, Object> tana = tanaList.get(i);

            Integer taiCd = MapUtils.getInteger(tana, MagicString.TAI_CD);

            if(i==(tanaList.size()-1)){
                tana.put("height", MapUtils.getInteger(tana, "taiHeight")-MapUtils.getInteger(tana, "tanaHeight"));
                continue;
            }

            Map<String, Object> nextTana = tanaList.get(i + 1);
            Integer nextTaiCd = MapUtils.getInteger(nextTana, MagicString.TAI_CD);
            if(!Objects.equals(currentTaiCd, nextTaiCd)){
                tana.put("height", MapUtils.getInteger(tana, "taiHeight")-MapUtils.getInteger(tana, "tanaHeight"));
            }else{
                tana.put("height", MapUtils.getInteger(nextTana, "tanaHeight")-MapUtils.getInteger(tana, "tanaHeight")-MapUtils.getInteger(nextTana, "tanaThickness"));
            }
            currentTaiCd = taiCd;
        }

        //jan group relation restrict_cd
        //TODO
        List<Map<String, Object>> newList = janNewMapper.selectJanNew(priorityOrderCd, allCdList, zokuseiMsts, commonTableName.getProInfoTable(),
                sizeAndIrisu);
        for (int i = 0; i < newList.size(); i++) {
            Map<String, Object> zokusei = newList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
                if (Objects.equals(MapUtils.getLong(restrict, "restrict_cd"), MagicString.NO_RESTRICT_CD)) {
                    continue;
                }
                int equalsCount = 0;
                for (Integer integer : attrList) {
                    String restrictKey = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + integer);
                    String zokuseiKey = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + integer);

                    if(restrictKey.equals(zokuseiKey)){
                        equalsCount++;
                    }
                }

                if(equalsCount == attrList.size()){
                    int restrictCd = MapUtils.getInteger(restrict, "restrict_cd");
                    zokusei.put("restrictCd", restrictCd);
                }
            }

            newList.set(i, zokusei);
        }

        newList = newList.stream().filter(map->MapUtils.getInteger(map, MagicString.RESTRICT_CD)!=null).collect(Collectors.toList());

        Map<Long, List<PriorityOrderResultDataDto>> janResultByRestrictCd = janResult.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd, LinkedHashMap::new, Collectors.toList()));
        Map<String, List<Map<String, Object>>> relationGroupTaiTana = relationMap.stream()
                .filter(map->!MapUtils.getString(map, MagicString.RESTRICT_CD).equals(MagicString.NO_RESTRICT_CD+""))
                .collect(Collectors.groupingBy(map -> MapUtils.getInteger(map, MagicString.TAI_CD)+ "_" + MapUtils.getInteger(map, MagicString.TANA_CD),
                        LinkedHashMap::new, Collectors.toList()));

        List<PriorityOrderResultDataDto> adoptJan = new ArrayList<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : relationGroupTaiTana.entrySet()) {
            String groupKey = entry.getKey();
            String taiCd = groupKey.split("_")[0];
            String tanaCd = groupKey.split("_")[1];

            List<Map<String, Object>> tanaMst = tanaList.stream().filter(map -> MapUtils.getString(map, MagicString.TAI_CD).equals(taiCd) &&
                    MapUtils.getString(map, MagicString.TANA_CD).equals(tanaCd)).collect(Collectors.toList());
            if (tanaMst.isEmpty()) {
                continue;
            }

            Map<String, Object> mst = tanaMst.get(0);
            Integer tanaWidth = MapUtils.getInteger(mst, "tanaWidth");
            Integer tanaHeight = MapUtils.getInteger(mst, "height");

            List<Map<String, Object>> relationList = entry.getValue();
            for (Map<String, Object> relation : relationList) {
                String restrictCd = MapUtils.getString(relation, MagicString.RESTRICT_CD);
                List<PriorityOrderResultDataDto> jans = janResultByRestrictCd.get(Long.valueOf(restrictCd));
                Map<String, Object> resultMap = null;
                if(jans!=null){
                    resultMap = this.doSetJan(cutList, newList, partitionVal, topPartitionVal, tanaWidthCheck, minFace, adoptJan, jans, relation, tanaWidth, tanaHeight, taiCd, tanaCd);
                }
                if(resultMap!=null){
                    return resultMap;
                }
            }
        }

        Map<String, List<Map<String, Object>>> noRelationGroupTaiTana = relationMap.stream()
                .filter(map->MapUtils.getString(map, MagicString.RESTRICT_CD).equals(MagicString.NO_RESTRICT_CD+""))
                .collect(Collectors.groupingBy(map -> MapUtils.getInteger(map, MagicString.TAI_CD)+ "_" + MapUtils.getInteger(map, MagicString.TANA_CD),
                        LinkedHashMap::new, Collectors.toList()));
        if(!noRelationGroupTaiTana.isEmpty()){
            List<PriorityOrderResultDataDto> notAdoptJan = new ArrayList<>();
            for (List<PriorityOrderResultDataDto> value : janResultByRestrictCd.values()) {
                notAdoptJan.addAll(value.stream().filter(dto->!Objects.equals(dto.getAdoptFlag(), 1)).collect(Collectors.toList()));
            }

            if(!notAdoptJan.isEmpty()){
                for (Map.Entry<String, List<Map<String, Object>>> entry : noRelationGroupTaiTana.entrySet()) {
                    String groupKey = entry.getKey();
                    String taiCd = groupKey.split("_")[0];
                    String tanaCd = groupKey.split("_")[1];

                    List<Map<String, Object>> tanaMst = tanaList.stream().filter(map -> MapUtils.getString(map, MagicString.TAI_CD).equals(taiCd) &&
                            MapUtils.getString(map, MagicString.TANA_CD).equals(tanaCd)).collect(Collectors.toList());
                    if (tanaMst.isEmpty()) {
                        continue;
                    }

                    Map<String, Object> mst = tanaMst.get(0);
                    Integer tanaWidth = MapUtils.getInteger(mst, "tanaWidth");
                    Integer tanaHeight = MapUtils.getInteger(mst, "height");

                    notAdoptJan = notAdoptJan.stream().sorted(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank)).collect(Collectors.toList());
                    for (Map<String, Object> relation : entry.getValue()) {
                        Map<String, Object> resultMap = this.doSetJan(cutList, newList, partitionVal, topPartitionVal, tanaWidthCheck,
                                minFace, adoptJan, notAdoptJan, relation,tanaWidth, tanaHeight, taiCd, tanaCd);
                        if(resultMap!=null){
                            return resultMap;
                        }
                    }
                }
            }
        }

        return ResultMaps.result(ResultEnum.SUCCESS, adoptJan);
    }

    private Map<String, Object> doSetJan(List<Map<String, Object>> cutList, List<Map<String, Object>> newList, Short partitionVal,
                                         Short topPartitionVal, Integer tanaWidthCheck,
                          Integer minFace, List<PriorityOrderResultDataDto> adoptJan, List<PriorityOrderResultDataDto> jans,
                          Map<String, Object> relation, Integer tanaWidth, Integer tanaHeight, String taiCd, String tanaCd){
        String restrictCd = MapUtils.getString(relation, MagicString.RESTRICT_CD);
        double area = MapUtils.getDouble(relation, "area");
        double groupArea = BigDecimal.valueOf(tanaWidth * area / 100.0).setScale(3, RoundingMode.CEILING).doubleValue();
        Long usedArea = 0L;

        Short partitionValue = partitionVal;
        if(!Objects.equals(tanaWidthCheck, 1)){
            partitionValue = 0;
        }

        for (PriorityOrderResultDataDto jan : jans) {
            if(Objects.equals(jan.getAdoptFlag(), 1) || Objects.equals(jan.getCutFlag(), 1)){
                continue;
            }
            PriorityOrderResultDataDto newJanDto = new PriorityOrderResultDataDto();
            List<Map<String, Object>> cutJan = cutList.stream().filter(map -> MapUtils.getString(map, "jan").equals(jan.getJanCd())).collect(Collectors.toList());

            Long width = Optional.ofNullable(jan.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH);
            Long face = jan.getFace();
            Long janWidth = width + partitionValue;

            if(!cutJan.isEmpty()){
                //The new regulation takes precedence over cut
                List<Map<String, Object>> newJanList = newList.stream().filter(map -> restrictCd.equals(MapUtils.getString(map, MagicString.RESTRICT_CD))
                    && !"1".equals(MapUtils.getString(map, "adoptFlag"))).collect(Collectors.toList());
                if(newJanList.isEmpty()){
                    continue;
                }
                int count = Math.min(cutJan.size(), newJanList.size());
                for (int i = 0; i < count; i++) {
                    if(i >= newJanList.size()){
                        continue;
                    }
                    Map<String, Object> newJan = newJanList.get(i);
                    newJanDto.setJanCd(MapUtils.getString(newJan, "jan"));
                    newJanDto.setFace(jan.getFace());
                    newJanDto.setTaiCd(MapUtils.getInteger(newJan, "taiCd"));
                    newJanDto.setTanaCd(MapUtils.getInteger(newJan, "tanaCd"));
                    newJanDto.setFaceFact(jan.getFace());
                    newJanDto.setRank(-1L);
                    newJanDto.setSkuRank(MapUtils.getLong(newJan, "sku_rank"));
                    newJanDto.setRestrictCd(MapUtils.getLong(newJan, MagicString.RESTRICT_CD));
                    newJanDto.setWidth(MapUtils.getLong(newJan, MagicString.WIDTH_NAME));
                    newJanDto.setHeight(MapUtils.getLong(newJan, MagicString.HEIGHT_NAME));
                    newJanDto.setIrisu(MapUtils.getString(newJan, MagicString.IRISU_NAME));

                    if(!Objects.equals(tanaWidthCheck, 1)){
                        jan.setCutFlag(1);
                        newList.forEach(map -> {
                            if(MapUtils.getString(map,"jan").equals(MapUtils.getString(newJan,"jan"))
                                    && MapUtils.getString(map,MagicString.RESTRICT_CD).equals(MapUtils.getString(newJan,MagicString.RESTRICT_CD))){
                                newJan.put("adoptFlag", "1");
                            }
                        });
                    }else{
                        if(janWidth*face + usedArea + partitionValue <= groupArea) {
                            //pre calculation used area
                            jan.setCutFlag(1);
                            newList.forEach(map -> {
                                if(MapUtils.getString(map,"jan").equals(MapUtils.getString(newJan,"jan"))
                                        && MapUtils.getString(map,MagicString.RESTRICT_CD).equals(MapUtils.getString(newJan,MagicString.RESTRICT_CD))){
                                    newJan.put("adoptFlag", "1");
                                }
                            });
                        }
                    }
                }
            }else{
                BeanUtils.copyProperties(jan, newJanDto);
                newJanDto.setFaceFact(jan.getFace());
            }

            if(topPartitionVal!=null){
                Long janHeight = Optional.ofNullable(jan.getHeight()).orElse(MagicString.DEFAULT_HEIGHT) + topPartitionVal;
                if(janHeight+topPartitionVal>tanaHeight){
                    Map<String, Object> errInfo = new HashMap<>();
                    errInfo.put("taiCd", Integer.parseInt(taiCd));
                    errInfo.put("tanaCd", Integer.parseInt(tanaCd));
                    errInfo.put("janHeight", janHeight);
                    errInfo.put("jan", jan.getJanCd());
                    errInfo.put("restrictCd", jan.getRestrictCd());
                    return ImmutableMap.of("code",ResultEnum.HEIGHT_NOT_ENOUGH.getCode(),"data", errInfo);
                }
            }

            if(janWidth*face + usedArea <= groupArea){
                usedArea += janWidth*face + partitionValue;
                newJanDto.setTaiCd(Integer.parseInt(taiCd));
                newJanDto.setTanaCd(Integer.parseInt(tanaCd));
                adoptJan.add(newJanDto);
                jan.setAdoptFlag(1);
            }else{
                if(tanaWidthCheck!=null && Objects.equals(tanaWidthCheck, 1)){
                    boolean setJanByCutFace = isSetJanByCutFace(adoptJan, groupArea, usedArea, partitionVal, minFace, newJanDto);
                    if(setJanByCutFace){
                        newJanDto.setTaiCd(Integer.parseInt(taiCd));
                        newJanDto.setTanaCd(Integer.parseInt(tanaCd));
                        adoptJan.add(newJanDto);
                        jan.setAdoptFlag(1);
                        usedArea+=Optional.ofNullable(newJanDto.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH)*newJanDto.getFaceFact()+partitionVal;
                    }
                }
            }
        }

        return null;
    }

    /**
     * cut face数で置けるかどうか
     * ろんり：最初のステップ：先に置く商品に対してface数-1を行います
     *          1.face数*商品幅が置けば置いて、実際に保管しているface数をメモします
     *          2.手放せない
     *      ステップ2：同一棚区分(taiCd_tanaCd_tanaType)における商品リストの逆順についてcut face判定を行う
     *          1.入数irisu=1の商品がなければcutを行わず、そのまま終了
     *          2.入数irisu=1がある場合は、商品リストを逆順に巡回し、face数-1を行います
     *              3.face数*幅が置けば置いて、実際に保管しているface数をメモします
     *              4.置くことができなくて、終わって、この位置は放しません
     * @param resultDataDtoList スタンド別商品リスト
     * @param targetResultData 置くべき商品
     * @param width 現在の棚区分の幅
     * @param usedWidth 現在の棚区分の使用済み幅
     * @param minFace 最小face数
     * @param partitionVal スペーサ幅
     * @return true放す，false放さない
     */
    private boolean isSetJanByCutFace(List<PriorityOrderResultDataDto> resultDataDtoList, double width, double usedWidth, long partitionVal,
                                      long minFace, PriorityOrderResultDataDto targetResultData){
        Long face = targetResultData.getFace();
        Long janWidth = Optional.ofNullable(targetResultData.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH);

        //使用可能な幅が残ります(仕切りがある場合を考慮する必要があります)
        double remainderWidth = width - usedWidth;
        //カットでターゲット商品のface数を落とせますか？
        boolean isSetByCut = false;

        //まず置く商品を1つ1つ下げていくかどうかを見てみましょう
        for (Long i = face - 1; i >= minFace; i--) {
            if((janWidth * i + partitionVal) <= remainderWidth){
                //置くことができる
                isSetByCut = true;
                //実際のface数を保存
                targetResultData.setFaceFact(i);
                break;
            }
        }

        if(!isSetByCut){
            //カットで対象商品のface数を落とすことはできません
            //入数=1のもののみを処理し、1に等しくないものはcutを行わない
            List<PriorityOrderResultDataDto> resultDataDtoByIrisu = resultDataDtoList.stream()
                    .filter(data-> 1 == Long.parseLong(Optional.ofNullable(data.getPlanoIrisu()).orElse("1"))).collect(Collectors.toList());

            if(resultDataDtoByIrisu.isEmpty()){
                //条件を満たさないとcutできない
                return false;
            }

            //最小faceで放置すると、どのくらいの幅が必要か(仕切りがある場合を考慮する必要がある)+残りの無駄な幅
            long needWidth = Optional.ofNullable(targetResultData.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH) * minFace + partitionVal;
            PriorityOrderResultDataDto currentResultData = null;
            for (int i = resultDataDtoByIrisu.size()-1; i >= 0 ; i--) {
                currentResultData = resultDataDtoByIrisu.get(i);
                Long faceFact = currentResultData.getFaceFact();

                //最小face以下ではcutできません
                //現在の商品は1つのface数を減らして、目標の商品を置くことができますか
                //Cut 1つのfaceの幅に残りの幅を加えてターゲットの上に置くのに必要な幅を満たすことができるかどうか
                if(faceFact > minFace && Optional.ofNullable(currentResultData.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH) + remainderWidth >= needWidth){
                    currentResultData.setFaceFact(faceFact -  1);
                    targetResultData.setFaceFact(minFace);
                    isSetByCut = true;
                    break;
                }
            }
        }

        return isSetByCut;
    }

    /**
     * まず台遍歴してから棚を遍歴し、同じ棚の商品は左から右の順に1からラベルを付け、棚が変わり、ラベルを付け直す
     *
     * @param workPriorityOrderResultData
     * @return
     */
    @Override
    public List<WorkPriorityOrderResultDataDto> calculateTanaPosition(List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData, int isReOrder) {
        //ハウス位置のresultdataデータ
        List<WorkPriorityOrderResultDataDto> positionResultData = new ArrayList<>(workPriorityOrderResultData.size());
        //テーブルに基づいてグループを分けて巡回する
        Map<Integer, List<WorkPriorityOrderResultDataDto>> workPriorityOrderResultDataByTai = workPriorityOrderResultData.stream()
                .collect(Collectors.groupingBy(WorkPriorityOrderResultDataDto::getTaiCd, LinkedHashMap::new, Collectors.toList()));
        Map<Integer, List<WorkPriorityOrderResultDataDto>> workPriorityOrderResultDataByTana = null;
        List<WorkPriorityOrderResultDataDto> workPriorityOrderResultDataDtos = null;

        for (Map.Entry<Integer, List<WorkPriorityOrderResultDataDto>> entrySet : workPriorityOrderResultDataByTai.entrySet()) {
            Integer taiCd = entrySet.getKey();

            workPriorityOrderResultDataDtos = workPriorityOrderResultDataByTai.get(taiCd);
            //小屋によってグループを分けて遍歴する
            workPriorityOrderResultDataByTana = workPriorityOrderResultDataDtos.stream()
                    .collect(Collectors.groupingBy(WorkPriorityOrderResultDataDto::getTanaCd, LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<Integer, List<WorkPriorityOrderResultDataDto>> entry : workPriorityOrderResultDataByTana.entrySet()) {
                //同じ棚で、番号が1から加算され、次の棚で再び1から加算されます。
                Integer tantaPositionCd=0;
                Integer tanaCd = entry.getKey();
                List<WorkPriorityOrderResultDataDto> resultDataByTanaCdList = workPriorityOrderResultDataByTana.get(tanaCd);

                if(isReOrder>0){
                    resultDataByTanaCdList = resultDataByTanaCdList.stream().sorted(Comparator.comparing(WorkPriorityOrderResultDataDto::getSkuRank)).collect(Collectors.toList());
                }

                for (WorkPriorityOrderResultDataDto currentDataDto : resultDataByTanaCdList) {
                    currentDataDto.setTanaPositionCd(++tantaPositionCd);
                    currentDataDto.setFaceMen(1);
                    currentDataDto.setFaceKaiten(0);
                    currentDataDto.setTumiagesu(1);
                    currentDataDto.setFaceDisplayflg(0);
                    currentDataDto.setFacePosition(1);
                    currentDataDto.setDepthDisplayNum(1);
                    positionResultData.add(currentDataDto);
                }
            }
        }
        return positionResultData;
    }

    /**
     * get bussiness
     */
    @Override
    public CommonPartsDto getCommonPartsData(Integer productPowerCd, String companyCd){
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        ProductPowerParam param = productPowerParamMstMapper.getParam(companyCd, productPowerCd);
        String commonPartsData = param.getCommonPartsData();
        JSONObject jsonObject = JSON.parseObject(commonPartsData);

        CommonPartsDto commonPartsDto = new CommonPartsDto();
        commonPartsDto.setProdMstClass(jsonObject.getString("prodMstClass"));
        commonPartsDto.setCoreCompany(coreCompany);
        commonPartsDto.setProdIsCore(jsonObject.getString("prodIsCore"));

        return commonPartsDto;
    }

    @Override
    public CommonPartsDto getPriorityCommonPartsData(Integer priorityOrderCd, String companyCd){
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        String commonPartsData = priorityOrderMstMapper.getCommonPartsData(companyCd, priorityOrderCd);
        JSONObject jsonObject = JSON.parseObject(commonPartsData);

        CommonPartsDto commonPartsDto = new CommonPartsDto();
        commonPartsDto.setProdMstClass(jsonObject.getString("prodMstClass"));
        commonPartsDto.setCoreCompany(coreCompany);
        commonPartsDto.setProdIsCore(jsonObject.getString("prodIsCore"));

        return commonPartsDto;
    }
}
