package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrValueDto;
import com.trechina.planocycle.entity.dto.PriorityOrderJanCgiDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import com.trechina.planocycle.utils.cgiUtils;
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
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PriorityOrderMstServiceImpl implements PriorityOrderMstService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${skuPerPattan}")
    Long skuCountPerPattan;
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private PriorityOrderPatternMapper priorityOrderPatternMapper;
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private ShelfPatternService shelfPatternService;
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private PriorityOrderJanNewService priorityOrderJanNewService;
    @Autowired
    private PriorityOrderJanCardService priorityOrderJanCardService;
    @Autowired
    private PriorityOrderJanAttributeMapper priorityOrderJanAttributeMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private WorkPriorityOrderRestrictSetMapper workPriorityOrderRestrictSetMapper;
    @Autowired
    private WorkPriorityOrderRestrictResultMapper workPriorityOrderRestrictResultMapper;
    @Autowired
    private WorkPriorityOrderRestrictRelationMapper workPriorityOrderRestrictRelationMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private WorkPriorityOrderSpaceMapper workPriorityOrderSpaceMapper;
    @Autowired
    private WorkPriorityOrderSortMapper workPriorityOrderSortMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private PriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private PriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    private PriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private PriorityOrderRestrictRelationMapper priorityOrderRestrictRelationMapper;
    @Autowired
    private PriorityOrderRestrictResultMapper priorityOrderRestrictResultMapper;
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private PriorityOrderResultDataMapper priorityOrderResultDataMapper;
    @Autowired
    private PriorityOrderSpaceMapper priorityOrderSpaceMapper;
    @Autowired
    private PriorityOrderSortRankMapper priorityOrderSortRankMapper;
    @Autowired
    private PriorityOrderSortMapper priorityOrderSortMapper;
    @Autowired
    private PriorityOrderShelfDataMapper priorityOrderShelfDataMapper;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private PriorityOrderShelfDataService priorityOrderShelfDataService;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private VehicleNumCache vehicleNumCache;
    @Autowired
    private BasicPatternRestrictResultMapper basicPatternRestrictResultMapper;
    @Autowired
    private BasicPatternRestrictRelationMapper basicPatternRestrictRelationMapper;
    @Autowired
    private BasicPatternAttrMapper basicPatternAttrMapper;
    @Autowired
    private BasicPatternRestrictResultDataMapper basicPatternRestrictResultDataMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;

    /**
     * 優先順位テーブルリストの取得
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        logger.info("つかむ取優先順位表参数：{}", companyCd);
        List<PriorityOrderMst> priorityOrderMstList = priorityOrderMstMapper.selectByPrimaryKey(companyCd);
        logger.info("つかむ取優先順位表返回値：{}", priorityOrderMstList);
        return ResultMaps.result(ResultEnum.SUCCESS, priorityOrderMstList);
    }

    @Override
    public Map<String, Object> getPriorityOrderListInfo(String companyCd,Integer priorityOrderCd) {
        priorityOrderJanCardMapper.getExistOtherMst(companyCd,priorityOrderCd);
        return null;
    }

    /**
     * この企業に優先順位表があるかどうかを取得します。
     *
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderExistsFlg(String companyCd) {
        int result = priorityOrderMstMapper.selectPriorityOrderCount(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }



    /**
     * 優先順位表cdに基づいて商品力点数表cdを取得する
     *
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getProductPowerCdForPriority(Integer priorityOrderCd) {
        logger.info("根据優先順位表cdつかむ取商品力点数表cd的参数{}", priorityOrderCd);
        Map<String, Object> productPowerCd = priorityOrderMstMapper.selectProductPowerCd(priorityOrderCd);
        logger.info("根据優先順位表cdつかむ取商品力点数表cd的返回値{}", priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS, productPowerCd);
    }


    /**
     * Productpowercdクエリに関連付けられた優先順位テーブルcd
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public String selPriorityOrderCdForProdCd(String companyCd, Integer productPowerCd) {
        return priorityOrderMstMapper.selectPriorityOrderCdForProdCd(companyCd, productPowerCd);
    }

    /**
     * 優先順位表マスターテーブル情報削除
     *
     * @param primaryKeyVO
     * @return
     */
    private Integer delPriorityOrderMst(PriorityOrderPrimaryKeyVO primaryKeyVO) {
        return priorityOrderMstMapper.deleteByPrimaryKey(primaryKeyVO.getCompanyCd(), primaryKeyVO.getPriorityOrderCd());
    }

    /**
     * S自動計算-STEP 1
     * @param companyCd
     * @param patternCd
     * @param priorityOrderCd
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> preCalculation(String companyCd, Long patternCd, Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int isUnset = 0;
        String authorCd = (String) session.getAttribute("aud");
        String getZokusei = "getZokusei";
        String setZokusei = "setZokusei";

        // ワークシートを空にする
        workPriorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        workPriorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        // 1.patternCdによるptsのステージ詳細の検索
        List<ShelfPtsDataTanamst> tanamstList = shelfPtsDataTanamstMapper.selectByPatternCd(patternCd);

        // 2.制約条件の取得
        List<WorkPriorityOrderRestrictSet> workRestrictSetList = workPriorityOrderRestrictSetMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);

        // 3.ステージ全体、セグメント制約条件をステージセグメントに置く
        List<WorkPriorityOrderRestrictSet> resultList = new ArrayList<>();
        WorkPriorityOrderRestrictSet restrictSet = null;
        WorkPriorityOrderRestrictSet halfRestrictSet1 = null;
        WorkPriorityOrderRestrictSet halfRestrictSet2 = null;
        // 整台制約
        Optional<WorkPriorityOrderRestrictSet> fullTaiSetOptional;
        WorkPriorityOrderRestrictSet fullTaiSet = null;
        // セグメント制約
        Optional<WorkPriorityOrderRestrictSet> fullTanaSetOptional;
        WorkPriorityOrderRestrictSet fullTanaSet = null;
        // はんだんせいやく
        String zokusei = null;
        // 3.1 ハーフセグメント設定のステージセグメントを検出
        List<WorkPriorityOrderRestrictSet> halfRestrictSetList = workRestrictSetList.stream()
                .filter(obj -> obj.getTanaType() == 1 || obj.getTanaType() == 2).collect(Collectors.toList());
        // 3.2 ループセグメントデータ
        Class<?> clazz = WorkPriorityOrderRestrictSet.class;
        for (ShelfPtsDataTanamst tanamst : tanamstList) {
            restrictSet = new WorkPriorityOrderRestrictSet();
            restrictSet.setCompanyCd(companyCd);
            restrictSet.setAuthorCd(authorCd);
            restrictSet.setTaiCd(tanamst.getTaiCd());
            restrictSet.setTanaCd(tanamst.getTanaCd());
            restrictSet.setTanaType((short) 0);
            // 3.2.1 ぜんだいせいやく
            fullTaiSetOptional = workRestrictSetList.stream()
                    .filter(obj -> obj.getTaiCd().equals(tanamst.getTaiCd()) && obj.getTanaCd().equals(0)).findFirst();
            if (fullTaiSetOptional.isPresent()) {
                fullTaiSet = fullTaiSetOptional.get();
                // [1,10]
                for (int i = 1; i <= 10; i++) {
                    zokusei = (String) clazz.getMethod(getZokusei + i).invoke(fullTaiSet);
                    // 属性が空でない場合は上書きします
                    if (zokusei != null) {
                        clazz.getMethod(setZokusei + i, String.class).invoke(restrictSet, zokusei);
                    }
                }
            }
            // 3.2.2 セグメント制約
            fullTanaSetOptional = workRestrictSetList.stream()
                    .filter(obj -> obj.getTaiCd().equals(tanamst.getTaiCd())
                            && obj.getTanaCd().equals(tanamst.getTanaCd())
                            && obj.getTanaType() == 0)
                    .findFirst();
            if (fullTanaSetOptional.isPresent()) {
                fullTanaSet = fullTanaSetOptional.get();
                // [1,10]
                for (int i = 1; i <= 10; i++) {
                    zokusei = (String) clazz.getMethod(getZokusei + i).invoke(fullTanaSet);
                    // 属性が空でない場合は上書きします
                    if (zokusei != null) {
                        clazz.getMethod(setZokusei + i, String.class).invoke(restrictSet, zokusei);
                    }
                }
            }
            // 3.2.3 はんだんせいやくがある

            if (halfRestrictSetList.isEmpty()) {
                resultList.add(restrictSet);
            } else {
                List<WorkPriorityOrderRestrictSet> halfSetList = halfRestrictSetList.stream()
                        .filter(obj -> obj.getTaiCd().equals(tanamst.getTaiCd()) && obj.getTanaCd().equals(tanamst.getTanaCd()))
                        .collect(Collectors.toList());
                if (halfSetList.isEmpty()) {
                    resultList.add(restrictSet);
                } else {
                    // ループによって2つのセグメントが作成されないのは、1つのセグメントのみが設定されている可能性があるからです。
                    halfRestrictSet1 = new WorkPriorityOrderRestrictSet();
                    halfRestrictSet2 = new WorkPriorityOrderRestrictSet();
                    BeanUtils.copyProperties(restrictSet, halfRestrictSet1);
                    BeanUtils.copyProperties(restrictSet, halfRestrictSet2);
                    halfRestrictSet1.setTanaType((short) 1);
                    halfRestrictSet2.setTanaType((short) 2);
                    //
                    for (int i = 0; i < halfSetList.size(); i++) {
                        int tanaType = halfSetList.get(i).getTanaType();
                        for (int j = 1; j <= 10; j++) {
                            zokusei = (String) clazz.getMethod(getZokusei + j).invoke(halfSetList.get(i));
                            // 属性が空でない場合は上書きします
                            if (zokusei != null) {
                                if (tanaType == 1) {
                                    clazz.getMethod(setZokusei + j, String.class).invoke(halfRestrictSet1, zokusei);
                                } else {
                                    clazz.getMethod(setZokusei + j, String.class).invoke(halfRestrictSet2, zokusei);
                                }
                            }
                        }

                    }
                    resultList.add(halfRestrictSet1);
                    resultList.add(halfRestrictSet2);
                }
            }
        }
        // ステージセグメントに条件が設定されているか否かを判断する
        List<WorkPriorityOrderRestrictSet> unsetList = resultList.stream()
                .filter(obj -> Boolean.TRUE.equals(obj.checkCondition()))
                .collect(Collectors.toList());
        if (!unsetList.isEmpty()) {
            logger.error("{}台段没有設定制約条件", unsetList.size());
            isUnset = 1;
        }

        // 一意の条件の再取得
        List<WorkPriorityOrderRestrictSet> distinctList = resultList.stream().filter(distinctByKey(obj -> Stream.of(obj.getZokusei1(), obj.getZokusei2(), obj.getZokusei3(), obj.getZokusei4()
                , obj.getZokusei5(), obj.getZokusei6(), obj.getZokusei7(), obj.getZokusei8(), obj.getZokusei9(), obj.getZokusei10()).toArray())).collect(Collectors.toList());
        if (!distinctList.isEmpty()) {
            List<WorkPriorityOrderRestrictResult> orderResultDataList = new ArrayList<>();
            WorkPriorityOrderRestrictResult orderResult = null;
            WorkPriorityOrderRestrictSet orderRestrictSet = null;
            for (int i = 0; i < distinctList.size(); i++) {
                orderRestrictSet = distinctList.get(i);
                orderResult = new WorkPriorityOrderRestrictResult();
                orderResult.setCompanyCd(companyCd);
                orderResult.setAuthorCd(authorCd);
                orderResult.setPriorityOrderCd(priorityOrderCd);
                orderResult.setRestrictCd((long) i + 1);
                orderResult.setZokusei1(orderRestrictSet.getZokusei1());
                orderResult.setZokusei2(orderRestrictSet.getZokusei2());
                orderResult.setZokusei3(orderRestrictSet.getZokusei3());
                orderResult.setZokusei4(orderRestrictSet.getZokusei4());
                orderResult.setZokusei5(orderRestrictSet.getZokusei5());
                orderResult.setZokusei6(orderRestrictSet.getZokusei6());
                orderResult.setZokusei7(orderRestrictSet.getZokusei7());
                orderResult.setZokusei8(orderRestrictSet.getZokusei8());
                orderResult.setZokusei9(orderRestrictSet.getZokusei9());
                orderResult.setZokusei10(orderRestrictSet.getZokusei10());
                orderResult.setTanaCnt((long) 0);
                orderResultDataList.add(orderResult);
            }


            // ステージセグメントと条件を関連付ける
            List<WorkPriorityOrderRestrictRelation> orderRestrictRelationList = new ArrayList<>();
            WorkPriorityOrderRestrictRelation orderRestrictRelation = null;
            Optional<WorkPriorityOrderRestrictResult> resultOptional;
            WorkPriorityOrderRestrictResult restrictResult = null;
            for (WorkPriorityOrderRestrictSet set : resultList) {
                orderRestrictRelation = new WorkPriorityOrderRestrictRelation();
                orderRestrictRelation.setPriorityOrderCd(priorityOrderCd);
                orderRestrictRelation.setCompanyCd(companyCd);
                orderRestrictRelation.setAuthorCd(authorCd);
                orderRestrictRelation.setTaiCd(set.getTaiCd());
                orderRestrictRelation.setTanaCd(set.getTanaCd());
                orderRestrictRelation.setTanaType(set.getTanaType());
                // 制約条件リストから該当する制約を検索
                String condition = set.getCondition();
                resultOptional = orderResultDataList.stream().filter(obj -> condition.equals(obj.getCondition())).findFirst();
                if (resultOptional.isPresent()) {
                    restrictResult = resultOptional.get();
                    orderRestrictRelation.setRestrictCd(restrictResult.getRestrictCd());
                    // 棚数積算
                    long tanaCnt = restrictResult.getTanaCnt() + 1;
                    restrictResult.setTanaCnt(tanaCnt);
                    //棚ごとに13品目を固定し、3倍に拡大して商品を取り、全パタンで直接使用する。
                    restrictResult.setSkuCnt(tanaCnt * skuCountPerPattan * 3);
                }

                orderRestrictRelationList.add(orderRestrictRelation);
            }

            workPriorityOrderRestrictResultMapper.insertAll(orderResultDataList);
            workPriorityOrderRestrictRelationMapper.insertAll(orderRestrictRelationList);
        }
        return ResultMaps.result(ResultEnum.SUCCESS, isUnset);
    }

    /**
     * オブジェクトの重量除去に使用
     *
     * @param keyExtractor デウェイトが必要なプロパティ
     * @param <T>
     * @return
     */
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        //既存のオブジェクトまたは属性を記録
        ConcurrentSkipListMap<Object, Boolean> skipListMap = new ConcurrentSkipListMap<>();
        //だからシーケンス化は性能を消耗するがもっと良い方法はない。
        return t -> skipListMap.putIfAbsent(JSON.toJSONString(keyExtractor.apply(t)), Boolean.TRUE) == null;
    }

    /**
     * 自動計算
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> autoCalculation(String companyCd, Integer priorityOrderCd,Integer partition) {


    //    String authorCd = session.getAttribute("aud").toString();
    //    String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
    //    String uuid = UUID.randomUUID().toString();
    //    executor.execute(() -> {
    //    //商品力点数表cdを取得する
    //    Integer productPowerCd = productPowerMstMapper.getProductPowerCd(companyCd, authorCd, priorityOrderCd);
    //    Integer patternCd = productPowerMstMapper.getpatternCd(companyCd, authorCd, priorityOrderCd);
    //    Integer minFaceNum = 1;
    //    //仕切り板の厚さと仕切り板を使用して保存するかどうか
    //    priorityOrderMstMapper.setPartition(companyCd,priorityOrderCd,authorCd,partition);
    //    //まず社員番号に従ってワークシートのデータを削除します
    //    workPriorityOrderResultDataMapper.delResultData(companyCd, authorCd, priorityOrderCd);
    //    //制約条件の取得
    //    List<WorkPriorityOrderRestrictResult> resultList = workPriorityOrderRestrictResultMapper.getResultList(companyCd, authorCd, priorityOrderCd);
    //    // 1.制約条件で該当商品を探す
    //    for (WorkPriorityOrderRestrictResult workPriorityOrderRestrictResult : resultList) {
    //        List<ProductPowerDataDto> newList = new ArrayList<>();
    //        workPriorityOrderRestrictResult.setPriorityOrderCd(priorityOrderCd);
    //        List<ProductPowerDataDto> productPowerData = workPriorityOrderRestrictResultMapper.getProductPowerData(workPriorityOrderRestrictResult, companyCd, productPowerCd, authorCd);
    //
    //        for (ProductPowerDataDto productPowerDatum : productPowerData) {
    //
    //            if (productPowerDatum.getJanNew() != null) {
    //                productPowerDatum.setJan(productPowerDatum.getJanNew());
    //            }
    //            if (productPowerDatum.getRankNewResult()!=null){
    //                productPowerDatum.setRankResult(productPowerDatum.getRankNewResult());
    //            }
    //        }
    //        for (ProductPowerDataDto productPowerDatum : productPowerData) {
    //            newList.add(productPowerDatum);
    //            if (newList.size() % 1000 == 0 && !newList.isEmpty()) {
    //                workPriorityOrderResultDataMapper.setResultDataList(newList, workPriorityOrderRestrictResult.getRestrictCd(), companyCd, authorCd, priorityOrderCd);
    //                newList.clear();
    //
    //            }
    //        }
    //        if (!newList.isEmpty()) {
    //
    //            workPriorityOrderResultDataMapper.setResultDataList(newList, workPriorityOrderRestrictResult.getRestrictCd(), companyCd, authorCd, priorityOrderCd);
    //        }
    //
    //    }
    //
    //    String resultDataList = workPriorityOrderResultDataMapper.getResultDataList(companyCd, authorCd, priorityOrderCd);
    //    if (resultDataList == null) {
    //        //return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
    //        vehicleNumCache.put("janNotExist"+uuid,1);
    //    }
    //    String[] array = resultDataList.split(",");
    //    //cgiを呼び出す
    //
    //    Map<String, Object> data = getFaceKeisanForCgi(array, companyCd, patternCd, authorCd,tokenInfo);
    //    if (data.get("data") != null && data.get("data") != "") {
    //        String[] strResult = data.get("data").toString().split("@");
    //        String[] strSplit = null;
    //        List<WorkPriorityOrderResultData> list = new ArrayList<>();
    //        WorkPriorityOrderResultData orderResultData = null;
    //        for (int i = 0; i < strResult.length; i++) {
    //            orderResultData = new WorkPriorityOrderResultData();
    //            strSplit = strResult[i].split(" ");
    //            orderResultData.setSalesCnt(Double.valueOf(strSplit[1]));
    //            orderResultData.setJanCd(strSplit[0]);
    //
    //
    //            list.add(orderResultData);
    //        }
    //        workPriorityOrderResultDataMapper.update(list, companyCd, authorCd, priorityOrderCd);
    //
    //
    //        //古いptsの平均値、最大値最小値を取得
    //        FaceNumDataDto faceNum = productPowerMstMapper.getFaceNum(patternCd);
    //        minFaceNum = faceNum.getFaceMinNum();
    //        DecimalFormat df = new DecimalFormat("#.00");
    //        //salescntAvgを取得し、小数点を2桁保持
    //        Double salesCntAvg = productPowerMstMapper.getSalesCntAvg(companyCd, authorCd, priorityOrderCd);
    //        String format = df.format(salesCntAvg);
    //        salesCntAvg = Double.valueOf(format);
    //
    //        List<WorkPriorityOrderResultData> resultDatas = workPriorityOrderResultDataMapper.getResultDatas(companyCd, authorCd, priorityOrderCd);
    //
    //        Double d = null;
    //        for (WorkPriorityOrderResultData resultData : resultDatas) {
    //            resultData.setPriorityOrderCd(priorityOrderCd);
    //            if (resultData.getSalesCnt() != null) {
    //                d = resultData.getSalesCnt() * faceNum.getFaceAvgNum() / salesCntAvg;
    //
    //                if (d > faceNum.getFaceMaxNum()) {
    //                    resultData.setFace(Long.valueOf(faceNum.getFaceMaxNum()));
    //                } else if (d < faceNum.getFaceMinNum()) {
    //                    resultData.setFace(Long.valueOf(faceNum.getFaceMinNum()));
    //                } else {
    //                    resultData.setFace(d.longValue());
    //                }
    //
    //            } else {
    //                resultData.setFace(Long.valueOf(faceNum.getFaceMinNum()));
    //            }
    //
    //        }
    //        workPriorityOrderResultDataMapper.updateFace(resultDatas, companyCd, authorCd);
    //    }
    //    //属性別に並べ替える
    //    this.getReorder(companyCd, priorityOrderCd,productPowerCd,authorCd);
    //    //商品を並べる
    //    WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
    //    Long shelfPatternCd = priorityOrderMst.getShelfPatternCd();
    //
    //    if (shelfPatternCd == null) {
    //        //logger.info("shelfPatternCd:{}不存在", shelfPatternCd);
    //        //return ResultMaps.result(ResultEnum.FAILURE);
    //        vehicleNumCache.put("PatternCdNotExist"+uuid,1);
    //    }
    //
    //    List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations = workPriorityOrderRestrictRelationMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
    //    List<PriorityOrderResultDataDto> workPriorityOrderResultData = workPriorityOrderResultDataMapper.getResultJans(companyCd, authorCd, priorityOrderCd);
    //    List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(shelfPatternCd.intValue());
    //
    //    Short partitionFlag = Optional.ofNullable(priorityOrderMst.getPartitionFlag()).orElse((short) 0);
    //    Short partitionVal = Optional.ofNullable(priorityOrderMst.getPartitionVal()).orElse((short) 2);
    //
    //    Map<String, List<PriorityOrderResultDataDto>> finalSetJanResultData =
    //            commonMstService.commSetJan(partitionFlag, partitionVal, taiData,
    //                    workPriorityOrderResultData, workPriorityOrderRestrictRelations, minFaceNum);
    //
    //    for (Map.Entry<String, List<PriorityOrderResultDataDto>> entry : finalSetJanResultData.entrySet()) {
    //        String taiTanaKey = entry.getKey();
    //        List<PriorityOrderResultDataDto> resultDataDtos = finalSetJanResultData.get(taiTanaKey);
    //        workPriorityOrderResultDataMapper.updateTaiTanaBatch(companyCd, priorityOrderCd, authorCd, resultDataDtos);
    //    }
    //
    //    //ptsを一時テーブルに保存
    //    shelfPtsService.saveWorkPtsData(companyCd, authorCd, priorityOrderCd);
    //        vehicleNumCache.put(uuid,1);
    //});
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


    /**
     * 表示自動計算実行ステータス
     * @param taskId
     * @return
     */
    @Override
    public Map<String, Object> autoTaskId(String taskId) {
        if (vehicleNumCache.get("janNotExist"+taskId)!=null){
            vehicleNumCache.remove("janNotExist"+taskId);
            return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
        }
        if (vehicleNumCache.get("PatternCdNotExist"+taskId)!=null){
            vehicleNumCache.remove("PatternCdNotExist"+taskId);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        if (vehicleNumCache.get(taskId) != null){
            vehicleNumCache.remove(taskId);
            return ResultMaps.result(ResultEnum.SUCCESS,"success");
        }
        return ResultMaps.result(ResultEnum.SUCCESS,"9");

    }

    /**
     * rankソートの再計算
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getReorder(String companyCd, Integer priorityOrderCd,Integer productPowerCd,String authorCd) {


        //String colNmforMst = priorityOrderMstAttrSortMapper.getColNmforMst(companyCd, authorCd, priorityOrderCd);
        //if (colNmforMst == null || colNmforMst.equals("")) {
        //    return ResultMaps.result(ResultEnum.SUCCESS);
        //}
        //String[] split = colNmforMst.split(",");
        //List<PriorityOrderJanNewDto> reorder = null;
        //List<WorkPriorityOrderResultData> reorder1 = null;
        //if (split.length == 1) {
        //
        //    reorder1 = workPriorityOrderResultDataMapper.getReorder(companyCd, authorCd,productPowerCd, priorityOrderCd, split[0], null);
        //
        //} else {
        //
        //    reorder1 = workPriorityOrderResultDataMapper.getReorder(companyCd, authorCd,productPowerCd, priorityOrderCd, split[0], split[1]);
        //
        //
        //}
        //int j = 1;
        //for (WorkPriorityOrderResultData workPriorityOrderResultData : reorder1) {
        //    workPriorityOrderResultData.setResultRank(j++);
        //
        //}
        //
        //workPriorityOrderResultDataMapper.setSortRank(reorder1, companyCd, authorCd, priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * rankソートの再計算
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getNewReorder(String companyCd, Integer priorityOrderCd, String authorCd) {
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);

        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);

        List<String> colNmforMst = priorityOrderMstAttrSortMapper.getColNmforMst(companyCd, authorCd, priorityOrderCd,commonTableName);


        List<WorkPriorityOrderResultData> reorder = null;
        if (colNmforMst.isEmpty()) {
            reorder = workPriorityOrderResultDataMapper.getProductReorder(companyCd,authorCd,workPriorityOrderMst.getProductPowerCd(),priorityOrderCd);
        }else if (colNmforMst.size() == 1) {
            reorder = workPriorityOrderResultDataMapper.getReorder(companyCd, authorCd,workPriorityOrderMst.getProductPowerCd(), priorityOrderCd,commonTableName, colNmforMst.get(0), null);
        } else if (colNmforMst.size() == 2){
            reorder = workPriorityOrderResultDataMapper.getReorder(companyCd, authorCd,workPriorityOrderMst.getProductPowerCd(), priorityOrderCd,commonTableName, colNmforMst.get(0), colNmforMst.get(1));
        }

        workPriorityOrderResultDataMapper.setSortRank(reorder, companyCd, authorCd, priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 新規作成時に対応するテンポラリ・テーブルのすべての情報をクリア
     *
     * @param companyCd
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public  void  deleteWorkTable(String companyCd, Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();

        workPriorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //RestrictRelationテーブルをクリア
        workPriorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //RestrictResultテーブルをクリア
        workPriorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //RestrictSetテーブルをクリア
        workPriorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //ResultDataテーブルをクリア
        workPriorityOrderResultDataMapper.delResultData(companyCd, authorCd, priorityOrderCd);
        //スペーステーブルをクリア
        workPriorityOrderSpaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //Sortテーブルをクリア
        workPriorityOrderSortMapper.delete(companyCd, authorCd, priorityOrderCd);
        //janNewテーブルをクリア
        priorityOrderJanNewMapper.workDelete(companyCd, authorCd, priorityOrderCd);
        //クリアjan_replace
        priorityOrderJanReplaceMapper.workDelete(companyCd, authorCd, priorityOrderCd);
        //クリアワーク_priority_order_Cutテーブル
        priorityOrderJanCardMapper.workDelete(companyCd, priorityOrderCd, authorCd);
        //ptsCdの取得
        Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
        //クリアワーク_priority_order_pts_data
        shelfPtsDataMapper.deletePtsData(id);
        //クリアワーク_priority_order_pts_data_taimst
        shelfPtsDataMapper.deletePtsTaimst(id);
        //クリアワーク_priority_order_pts_data_tanamst
        shelfPtsDataMapper.deletePtsTanamst(id);
        //クリアワーク_priority_order_pts_data_version
        shelfPtsDataMapper.deletePtsVersion(id);
        //クリアワーク_priority_order_pts_data_jandata
        shelfPtsDataMapper.deletePtsDataJandata(id);



    }

    @Override
    public Map<String, Object> getFaceKeisanForCgi(String[] array, String companyCd, Integer shelfPatternNo, String authorCd,String tokenInfo) {
        PriorityOrderJanCgiDto priorityOrderJanCgiDto = new PriorityOrderJanCgiDto();
        priorityOrderJanCgiDto.setDataArray(array);
        String uuid = UUID.randomUUID().toString();
        priorityOrderJanCgiDto.setGuid(uuid);
        priorityOrderJanCgiDto.setMode("idposaverage_data");
        priorityOrderJanCgiDto.setCompany(companyCd);
        priorityOrderJanCgiDto.setShelfPatternNo(shelfPatternNo);
        priorityOrderJanCgiDto.setUsercd(authorCd);
        logger.info("計算給FaceKeisancgi的参数{}", priorityOrderJanCgiDto);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");

        Map<String, Object> resultCgi = null;
        //再帰的にcgiを呼び出して、まずtaskidに行きます
        String result = cgiUtil.postCgi(path, priorityOrderJanCgiDto, tokenInfo);
        logger.info("taskId返回：{}", result);
        String queryPath = resourceBundle.getString("TaskQuery");
        //taskIdを持って、再度cgiに運転状態/データの取得を要求する
        resultCgi = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
        logger.info("保存優先順位表結果：{}", resultCgi);
        return resultCgi;
    }


    /**
     * taskIdを持って、再度cgiに運転状態/データの取得を要求する
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveAllWorkPriorityOrder(PriorityOrderMstVO primaryKeyVO) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = primaryKeyVO.getCompanyCd();
        Integer priorityOrderCd = primaryKeyVO.getPriorityOrderCd();
        String priorityOrderName = primaryKeyVO.getPriorityOrderName();

        try {
            //OrderMstを保存し、元のデータを削除
            priorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderMstMapper.insertBySelect(companyCd, authorCd, priorityOrderCd, priorityOrderName);

            //OrderRestrictRelationを保存し、元のデータを削除
            priorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictRelationMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //OrderRestrictResultを保存し、元のデータを削除
            priorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictResultMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //OrderRestrictSetを保存し、元のデータを削除
            priorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictSetMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //ResultDataの保存、元のデータの削除
            priorityOrderResultDataMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderResultDataMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //スペースの保存、元のデータの削除
            priorityOrderSpaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderSpaceMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //cutを保存し、元のデータを削除
            priorityOrderJanCardMapper.deleteByAuthorCd(companyCd, priorityOrderCd, authorCd);
            priorityOrderJanCardMapper.insertBySelect(companyCd, priorityOrderCd, authorCd);

            //jan_を保存新、元データの削除
            priorityOrderJanNewMapper.deleteByAuthorCd(companyCd, priorityOrderCd, authorCd);
            priorityOrderJanNewMapper.insertBySelect(companyCd, priorityOrderCd, authorCd);

            //jan_を保存replace,元のデータを削除する
            priorityOrderJanReplaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderJanReplaceMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //sortを保存し、元のデータを削除
            priorityOrderSortMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderSortMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //ptsデータの保存
            shelfPtsService.saveFinalPtsData(companyCd, authorCd, priorityOrderCd);

            //work_basic_pattern_restrict_relation保存rank、元のデータを削除
            basicPatternRestrictRelationMapper.deleteFinal(companyCd, authorCd, priorityOrderCd);
            basicPatternRestrictRelationMapper.setFinalForWork(companyCd, authorCd, priorityOrderCd);

            //work_basic_pattern_attr_compose保存＃ホゾン＃
            basicPatternAttrMapper.deleteFinal(companyCd, authorCd, priorityOrderCd);
            basicPatternAttrMapper.setFinalForWork(companyCd, authorCd, priorityOrderCd);
            //work_basic_pattern_restrict_result保存＃ホゾン＃
            basicPatternRestrictResultMapper.deleteFinal(companyCd, authorCd, priorityOrderCd);
            basicPatternRestrictResultMapper.setFinalForWork(companyCd, authorCd, priorityOrderCd);
            //work_basic_pattern_restrict_result_data 保存＃ホゾン＃
            basicPatternRestrictResultDataMapper.deleteFinal(companyCd, authorCd, priorityOrderCd);
            basicPatternRestrictResultDataMapper.setFinalForWork(companyCd, authorCd, priorityOrderCd);

        } catch (Exception exception) {
            logger.error("保存臨時表数据到實際表報錯", exception);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 編集時にすべての情報を表示
     * @param companyCd
     * @param priorityOrderCd
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> getPriorityOrderAll(String companyCd, Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Integer id = shelfPtsDataMapper.getNewId(companyCd, priorityOrderCd);
        String aud = session.getAttribute("aud").toString();
        priorityOrderMstService.deleteWorkTable(companyCd, priorityOrderCd);
        priorityOrderJanCardMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        priorityOrderJanReplaceMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        priorityOrderJanNewMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderMstMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderRestrictRelationMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderRestrictResultMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderRestrictSetMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderResultDataMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderSortMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);
        workPriorityOrderSpaceMapper.setWorkForFinal(companyCd, priorityOrderCd, aud);

        //ptsIdの取得

        shelfPtsDataMapper.insertWorkPtsData(companyCd, aud, priorityOrderCd);
        shelfPtsDataMapper.insertWorkPtsTaiData(companyCd, aud, id);
        shelfPtsDataMapper.insertWorkPtsTanaData(companyCd, aud, id);
        shelfPtsDataMapper.insertWorkPtsVersionData(companyCd, aud, id);
        shelfPtsDataMapper.insertWorkPtsJanData(companyCd, aud, id);
        Map<String, Object> map = new HashMap<>();
        //プライマリ・テーブル情報
        WorkPriorityOrderMstEditVo workPriorityOrderMst = workPriorityOrderMstMapper.getWorkPriorityOrderMst(companyCd, priorityOrderCd, aud);
        Integer shelfCd = workPriorityOrderMstMapper.getShelfName(workPriorityOrderMst.getShelfPatternCd().intValue());
        workPriorityOrderMst.setShelfCd(shelfCd);


        //スペース情報
        List<PriorityOrderAttrVO> workPriorityOrderSpace = priorityOrderSpaceMapper.workPriorityOrderSpace(companyCd, aud, priorityOrderCd);
        //setテーブル情報
        List<PriorityOrderRestrictSet> workPriorityOrderRestrictSet = priorityOrderRestrictSetMapper.getPriorityOrderRestrict(companyCd, aud, priorityOrderCd);
        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues();
        Class clazz = PriorityOrderRestrictSet.class;
        for (int i = 1; i <= 10; i++) {
            Method getMethod = clazz.getMethod("get" + "Zokusei" + i);
            Method setMethod = clazz.getMethod("set" + "ZokuseiName" + i, String.class);
            for (PriorityOrderRestrictSet priorityOrderRestrictSet : workPriorityOrderRestrictSet) {
                for (PriorityOrderAttrValueDto attrValue : attrValues) {
                    if (getMethod.invoke(priorityOrderRestrictSet) != null && getMethod.invoke(priorityOrderRestrictSet).equals(attrValue.getVal()) && attrValue.getZokuseiId() == i) {
                        setMethod.invoke(priorityOrderRestrictSet, attrValue.getNm());
                    }
                }
            }
        }

        //商品力点数表情報
        Map<String, Object> taiNumTanaNum = shelfPtsService.getTaiNumTanaNum(workPriorityOrderMst.getShelfPatternCd().intValue(), priorityOrderCd);
        //陳列順情報の取得
        List<WorkPriorityOrderSortVo> workPriorityOrderSort = shelfPtsDataMapper.getDisplays(companyCd, aud, priorityOrderCd);
        //基本スタンド別情報の取得
        List<PriorityOrderPlatformShedDto> platformShedData = priorityOrderShelfDataMapper.getPlatformShedData(companyCd, aud, priorityOrderCd);
        //基本制約別情報の取得
        Map<String, Object> restrictData = priorityOrderShelfDataService.getRestrictData(companyCd, priorityOrderCd);
        //pts詳細の取得
        Map<String, Object> ptsDetailData = shelfPtsService.getPtsDetailData(workPriorityOrderMst.getShelfPatternCd().intValue(), companyCd, priorityOrderCd);
        //商品力情報
        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, workPriorityOrderMst.getProductPowerCd());
        Integer skuNum = productPowerMstMapper.getSkuNum(companyCd, workPriorityOrderMst.getProductPowerCd());
        productPowerInfo.setSku(skuNum);
        //商品の詳細
        List<JanMstPlanocycleVo> janNewInfo = priorityOrderJanNewMapper.getJanNewInfo(companyCd);
        map.put("workPriorityOrderMst",workPriorityOrderMst);
        map.put("workPriorityOrderSpace",workPriorityOrderSpace);
        map.put("workPriorityOrderRestrictSet",workPriorityOrderRestrictSet);
        map.put("taiNumTanaNum",taiNumTanaNum.get("data"));
        map.put("workPriorityOrderSort",workPriorityOrderSort);
        map.put("platformShedData",platformShedData);
        map.put("restrictData",restrictData.get("data"));
        map.put("ptsDetailData",ptsDetailData.get("data"));
        map.put("productPowerInfo",productPowerInfo);
        map.put("janNewInfo",janNewInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }

    /**
     * 編集時にptsの名前が存在するかどうかを確認
     * @param priorityOrderMstVO
     * @return
     */
    @Override
    public Map<String, Object> checkOrderName(PriorityOrderMstVO priorityOrderMstVO) {
        String priorityOrderName = priorityOrderMstVO.getPriorityOrderName();
        Integer priorityOrderCd = priorityOrderMstVO.getPriorityOrderCd();
        String companyCd = priorityOrderMstVO.getCompanyCd();
        String authorCd = session.getAttribute("aud").toString();

        Integer orderNameCount = priorityOrderMstMapper.selectByOrderName(priorityOrderName, companyCd, authorCd);
        int isEdit = priorityOrderMstMapper.selectByPriorityOrderCd(priorityOrderCd);

        if (isEdit > 0) {
            //編集→名前の更新
            priorityOrderMstMapper.updateOrderName(priorityOrderCd, priorityOrderName);
            return ResultMaps.result(ResultEnum.SUCCESS);
        }

        //新しいルール
        if(orderNameCount>0){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 基本パターン削除
     * @param priorityOrderMstVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> deletePriorityOrderAll(PriorityOrderMstVO priorityOrderMstVO) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = priorityOrderMstVO.getCompanyCd();
        Integer priorityOrderCd = priorityOrderMstVO.getPriorityOrderCd();
        //mstテーブルの削除
        priorityOrderMstMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除relation
        priorityOrderRestrictRelationMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除result表
        priorityOrderRestrictResultMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除set表
        priorityOrderRestrictSetMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除data表
        priorityOrderResultDataMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除sort表
        priorityOrderSortMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除rank表
        priorityOrderSortRankMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除space表
        priorityOrderSpaceMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 各種mst展示
     * @param companyCd
     * @param priorityOrderCd
     * @param flag
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Override
    public Map<String, Object> getVariousMst(String companyCd, Integer priorityOrderCd, Integer flag) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String authorCd = session.getAttribute("aud").toString();
        //商品力点数表情報
        WorkPriorityOrderMstEditVo workPriorityOrderMst = workPriorityOrderMstMapper.getWorkPriorityOrderMst(companyCd, priorityOrderCd, authorCd);
        WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd,priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderMst.getCommonPartsData(), companyCd);
        if (flag == 0) {
            //取得janNew情報
            Map<String, Object> priorityOrderJanNew = priorityOrderJanNewService.getPriorityOrderJanNew(companyCd, priorityOrderCd, workPriorityOrderMst.getProductPowerCd());
            return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanNew.get("data"));
        }
        if (flag == 2) {
            ////取得janCut情報
            List<PriorityOrderJanCardVO> priorityOrderJanCut = priorityOrderJanCardMapper.selectJanCard(companyCd, priorityOrderCd,commonTableName);
            return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanCut);
        }
        if (flag == 1) {
            //取得jan変情報
            List<PriorityOrderJanReplaceVO> priorityOrderJanReplace = priorityOrderJanReplaceMapper.selectJanInfo(companyCd, priorityOrderCd,commonTableName);
            return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanReplace);
        }
        return ResultMaps.result(ResultEnum.FAILURE);
    }


}
