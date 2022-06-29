package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.CommonPartsDto;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.Areas;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommonMstService;
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
    private PriorityOrderPtsDataMapper priorityOrderPtsDataMapper;
    @Autowired
    private BasicPatternRestrictResultMapper basicRestrictResultMapper;
    @Autowired
    private HttpSession session;

    @Override
    public Map<String, Object> getAreaInfo(String companyCd) {
        List<Areas> areasList = areasMapper.select(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    }
    @Override
    public Map<String, Object> getAreaForShelfName(Integer shelfNameCd) {
        List<Areas> areasList = areasMapper.selectForShelfName(shelfNameCd);
        return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    }

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
                    Long janWidth = Optional.ofNullable(priorityOrderResultData.getJanWidth()).orElse(0L);
                    Long face = priorityOrderResultData.getFace();

                    //商品幅nullまたは0の場合はデフォルト幅67 mm、faceSku>1の必要にfaceSkuを乗じます
                    if (janWidth == 0) {
                        janWidth = 67 * faceSku;
                    }
                    priorityOrderResultData.setJanWidth(janWidth);

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
                                                  GetCommonPartsDataDto commonTableName) {
        List<PriorityOrderResultDataDto> janResult = jandataMapper.selectJanByPatternCd(aud, companyCd, patternCd, priorityOrderCd);
        List<Map<String, Object>> relationMap = restrictRelationMapper.selectByPriorityOrderCd(priorityOrderCd);
        List<Map<String, Object>> tanaList = priorityOrderPtsDataMapper.selectTanaMstByPatternCd(patternCd, priorityOrderCd);
        List<Map<String, Object>> cutList = janCutMapper.selectJanCut(priorityOrderCd);

        //jan group relation restrict_cd
        List<Map<String, Object>> newList = janNewMapper.selectJanNew(priorityOrderCd, allCdList, zokuseiMsts, commonTableName.getProInfoTable());
        for (int i = 0; i < newList.size(); i++) {
            Map<String, Object> zokusei = newList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
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

        Integer partitionVal = 0;
        Integer topPartitionVal = 0;

        Map<Long, List<PriorityOrderResultDataDto>> janResultByRestrictCd = janResult.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd));
        Map<String, List<Map<String, Object>>> relationGroupTaiTana = relationMap.stream().collect(Collectors.groupingBy(map -> MapUtils.getInteger(map, MagicString.TAI_CD)
                + "_" + MapUtils.getInteger(map, MagicString.TANA_CD), LinkedHashMap::new, Collectors.toList()));

        List<PriorityOrderResultDataDto> adoptJan = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : relationGroupTaiTana.entrySet()) {
            String groupKey = entry.getKey();
            String taiCd = groupKey.split("_")[0];
            String tanaCd = groupKey.split("_")[1];

            List<Map<String, Object>> tanaMst = tanaList.stream().filter(map -> MapUtils.getString(map, MagicString.TAI_CD).equals(taiCd) &&
                    MapUtils.getString(map, MagicString.TANA_CD).equals(tanaCd)).collect(Collectors.toList());
            if(tanaMst.isEmpty()){
                continue;
            }

            Map<String, Object> mst = tanaMst.get(0);
            Integer tanaWidth = MapUtils.getInteger(mst, "tanaWidth");
            Integer tanaHeight = MapUtils.getInteger(mst, "tanaHeight");

            List<Map<String, Object>> relationList = entry.getValue();
            for (Map<String, Object> relation : relationList) {
                String restrictCd = MapUtils.getString(relation, MagicString.RESTRICT_CD);
                Integer area = MapUtils.getInteger(relation, "area");
                Integer groupArea = BigDecimal.valueOf(tanaWidth * area / 100.0).intValue();
                Long usedArea = 0L;

                List<PriorityOrderResultDataDto> jans = janResultByRestrictCd.get(restrictCd);
                for (PriorityOrderResultDataDto jan : jans) {
                    PriorityOrderResultDataDto newJanDto = new PriorityOrderResultDataDto();
                    List<Map<String, Object>> cutJan = cutList.stream().filter(map -> MapUtils.getString(map, "jan").equals(jan.getJanCd())).collect(Collectors.toList());
                    if(!cutJan.isEmpty()){
                        List<Map<String, Object>> newJanList = newList.stream().filter(map -> MapUtils.getString(map, MagicString.RESTRICT_CD).equals(restrictCd)).collect(Collectors.toList());
                        if(newJanList.isEmpty()){
                            continue;
                        }

                        Map<String, Object> newJan = newJanList.get(0);
                        newJanDto.setJanCd(MapUtils.getString(newJan, "jan"));
                        newJanDto.setFace(MapUtils.getLong(newJan, "face"));
                        newJanDto.setRestrictCd(MapUtils.getLong(newJan, MagicString.RESTRICT_CD));
                        newJanDto.setJanWidth(MapUtils.getLong(newJan, "width"));
                        newJanDto.setJanHeight(MapUtils.getLong(newJan, "height"));
                    }else{
                        BeanUtils.copyProperties(jan, newJanDto);
                    }

                    Long width = jan.getJanWidth();
                    Long face = jan.getFace();
                    Long janWidth = width + partitionVal;
                    Long janHeight = jan.getJanHeight() + topPartitionVal;

                    if(janHeight>tanaHeight){
                        return ImmutableMap.of(ResultEnum.FAILURE.name(), "height not enough");
                    }

                    if(janWidth*face + usedArea <= groupArea){
                        usedArea += janWidth*face;
                    }else{
                        boolean setJanByCutFace = isSetJanByCutFace(adoptJan, area, usedArea, partitionVal, minFace, newJanDto);
                        if(setJanByCutFace){
                            adoptJan.add(newJanDto);
                            usedArea+=newJanDto.getJanWidth()*newJanDto.getFaceFact()+partitionVal;
                        }
                    }
                }
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS, adoptJan);
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
        Long janWidth = targetResultData.getJanWidth();

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
                    .filter(data-> 1 == data.getIrisu()).collect(Collectors.toList());

            if(resultDataDtoByIrisu.isEmpty()){
                //条件を満たさないとcutできない
                return false;
            }

            //最小faceで放置すると、どのくらいの幅が必要か(仕切りがある場合を考慮する必要がある)+残りの無駄な幅
            long needWidth = targetResultData.getJanWidth() * minFace + partitionVal;
            PriorityOrderResultDataDto currentResultData = null;
            for (int i = resultDataDtoByIrisu.size()-1; i >= 0 ; i--) {
                currentResultData = resultDataDtoByIrisu.get(i);
                Long faceFact = currentResultData.getFaceFact();

                //最小face以下ではcutできません
                //現在の商品は1つのface数を減らして、目標の商品を置くことができますか
                //Cut 1つのfaceの幅に残りの幅を加えてターゲットの上に置くのに必要な幅を満たすことができるかどうか
                if(faceFact > minFace && currentResultData.getJanWidth() + remainderWidth >= needWidth){
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
    public List<WorkPriorityOrderResultDataDto> calculateTanaPosition(List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData) {
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

                for (WorkPriorityOrderResultDataDto currentDataDto : workPriorityOrderResultDataByTana.get(tanaCd)) {
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
