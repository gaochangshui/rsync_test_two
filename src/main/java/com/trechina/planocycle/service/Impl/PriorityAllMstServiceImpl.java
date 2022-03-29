package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.PriorityAllPatternListVO;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriorityAllMstServiceImpl  implements PriorityAllMstService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    HttpSession session;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private PriorityAllMstMapper priorityAllMstMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private PriorityOrderRestrictResultMapper priorityOrderRestrictResultMapper;
    @Autowired
    private WorkPriorityAllRestrictRelationMapper workPriorityAllRestrictRelationMapper;
    @Autowired
    private WorkPriorityAllRestrictMapper workPriorityAllRestrictMapper;
    @Autowired
    private WorkPriorityAllResultDataMapper workPriorityAllResultDataMapper;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private PriorityAllPtsService priorityAllPtsService;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private PriorityAllNumGeneratorMapper priorityAllNumGeneratorMapper;
    @Value("${skuPerPattan}")
    private Long skuCountPerPattan;

    /**
     * 新規作成＆編集の処理
     *
     * @param jsonObject@return
     */
    @Override
    public Map<String, Object> addPriorityAllData(JSONObject jsonObject) {
        try{

            String authorCd = session.getAttribute("aud").toString();
            String companyCd = jsonObject.get("companyCd").toString();
            Integer priorityAllCd = (Integer) jsonObject.get("priorityAllCd");
            //「companyCd、priorityAllCd、Author_cd」によりWKテーブルをクリア
            priorityAllMstMapper.deleteWKTableMst(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableShelfs(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableRestrict(companyCd, priorityAllCd, authorCd);
            workPriorityAllRestrictRelationMapper.deleteWKTableRelation(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableResult(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsTai(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsTana(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsJans(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsData(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsRelation(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsVersion(companyCd, priorityAllCd, authorCd);


            if (priorityAllCd != 0) {
                // データコピー
                priorityAllMstMapper.copyWKTableMst(companyCd, priorityAllCd, authorCd);
                priorityAllMstMapper.copyWKTableShelfs(companyCd, priorityAllCd, authorCd);
                priorityAllMstMapper.copyWKTableRestrict(companyCd, priorityAllCd, authorCd);
                priorityAllMstMapper.copyWKTableRelation(companyCd, priorityAllCd, authorCd);
                priorityAllMstMapper.copyWKTableResult(companyCd, priorityAllCd, authorCd);
                priorityAllMstMapper.copyWKTablePtsTai(companyCd, priorityAllCd, authorCd);
                priorityAllMstMapper.copyWKTablePtsTana(companyCd, priorityAllCd, authorCd);
                priorityAllMstMapper.copyWKTablePtsJans(companyCd, priorityAllCd, authorCd);
                Integer priorityOrderCd = priorityAllMstMapper.getPriorityOrderCd(priorityAllCd, companyCd, authorCd);
                Map<String, Object> allPatternData = getAllPatternData(companyCd, priorityAllCd, priorityOrderCd);
                Map <String ,Object> map = new HashMap<>();
                map.put("priorityOrderCd",priorityOrderCd);
                map.put("allPatternData",allPatternData.get("data"));
                return ResultMaps.result(ResultEnum.SUCCESS,map);
            }
            return ResultMaps.result(ResultEnum.SUCCESS, null);
        } catch (Exception ex) {
            return ResultMaps.result(ResultEnum.FAILURE, "新規作成失敗しました。");
        }
    }

    /**
     * 基本パターン一覧のList api①
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        String aud = session.getAttribute("aud").toString();
        List<TableNameDto> resultInfo = priorityOrderMstMapper.getTableNameByCompanyCd(companyCd, aud);
        logger.info("基本パターンList：{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * 選択された基本パターンにより基本パターンの連携棚パターン、そして同じ棚名称したの棚パターンListを取得するapi②
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getAllPatternData(String companyCd, Integer priorityAllCd, Integer priorityOrderCd) {
        String aud = session.getAttribute("aud").toString();
        // 基本パターンに紐付け棚パターンCDをもらう
        Integer patternCd = priorityAllMstMapper.getPatternCdBYPriorityCd(companyCd, priorityOrderCd);
        priorityAllMstMapper.deleteWKTableShelfs(companyCd, priorityAllCd, aud);
        // 棚パターンのPTS基本情報をもらう
        Map<String, Object> ptsInfoTemp = shelfPtsService.getTaiNumTanaNum(patternCd,priorityOrderCd);
        if ((Integer)ptsInfoTemp.get("code") != 101) {
            return ResultMaps.result(ResultEnum.FAILURE, "該当基本パターンに紐付け棚パターンが見つけていませんでした。");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("tanaInfo", ptsInfoTemp.get("data"));

        // 同一棚名称の棚パータンListを取得

        List<PriorityAllPatternListVO> info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, patternCd,aud);
        result.put("ptsInfo", info);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    /**
     * 自動計算する前にデータを一時テーブルに保存
     *
     * @param priorityAllSaveDto@return
     */
    @Override
    public Integer saveWKAllPatternData(PriorityAllSaveDto priorityAllSaveDto) {
        String authorCd = session.getAttribute("aud").toString();
        // mstテーブル
        priorityAllMstMapper.deleteWKTableMst(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
        // shelfsテーブル
        priorityAllMstMapper.deleteWKTableShelfs(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
        workPriorityAllRestrictMapper.deleteWKTableRestrict(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd());
        workPriorityAllRestrictRelationMapper.deleteWKTableRelation(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
        priorityAllMstMapper.insertWKTableMst(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd, priorityAllSaveDto.getPriorityOrderCd());
        priorityAllMstMapper.insertWKTableShelfs(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd, priorityAllSaveDto.getPatterns());

        return 0;
    }

    /**
     * 全パータン計算
     * @return
     */
    @Override
    public Map<String, Object> autoCalculation(PriorityAllSaveDto priorityAllSaveDto) {
        // 基本パータンの制約により各棚パターンの制約を作成する
        // １．基本台数、基本棚数により新棚の制約を割合で保存　「work_priority_all_restrict」
        //      係数>=１の場合「基本より大きい場合
        //          0.5の制約はそのまま維持
        //          それ以外の制約は係数*tana_cnt、小数点は切り捨て
        //      係数<1 の場合「基本より小さい場合
        //          tana_cnt が１及び以下の分は維持
        //          tana_cnt が１より大きい分は係数*tana_cnt、小数点は切り捨て
        //    1.1 補足、上記計算済み後、残り棚数を分配
        //      残り棚数 = 対象パターンの全棚数-制約別のtana_cnt合計 小数点は切り捨てだからマイナスは存在しない
        //          制約別のtana_cntの降順で制約別tana_cnt＋１
        //          残り棚数が0の場合、終了 ❊ループが終了し、残棚数が残す場合は存在しないはず
        // 2. 制約により台、棚関係テーブル作成     「work_priority_all_restrict_relation」
        //      基本パータンの面積設定のロジックを使う
        // 3. 商品結果テーブル作成　「work_priority_all_result_data」
        //      基本パターンのデータをすべてコピー
        //      3.1　各棚パターン別で推薦F数計算、保存
        //          基本パータンのCGI、計算Serviceを使う
        //      3.2　各棚パターン別に棚に置く商品確定
        //          基本パターンのServiceを使う
        // 4. 各棚パターンのPTSテーブル作成
        //          「work_priority_all_pts_tai」
        //          「work_priority_all_pts_tana」
        //          「work_priority_all_pts_jans」
        // 5. 作成済み、画面に返す「自動計算完了しました」
        // 6. 保存 「priority_all_mst」とかの主テーブルに保存
        // 7. PTS出力

        // 基本パータンの制約により各棚パターンの制約を作成する
        // １．基本台数、基本棚数により新棚の制約を割合で保存　「work_priority_all_restrict」
        //      係数>=１の場合「基本より大きい場合
        //          0.5の制約はそのまま維持
        //          それ以外の制約は係数*tana_cnt、小数点は切り捨て
        //      係数<1 の場合「基本より小さい場合
        //          tana_cnt が１及び以下の分は維持
        //          tana_cnt が１より大きい分は係数*tana_cnt、小数点は切り捨て
        //    1.1 補足、上記計算済み後、残り棚数を分配
        //      残り棚数 = 対象パターンの全棚数-制約別のtana_cnt合計 小数点は切り捨てだからマイナスは存在しない
        //          制約別のtana_cntの降順で制約別tana_cnt＋１
        //          残り棚数が0の場合、終了 ❊ループが終了し、残棚数が残す場合は存在しないはず
        // 基本パターンに紐付け棚パターンCDをもらう
        Integer basicPatternCd;
        BigDecimal basicTannaNum;
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = priorityAllSaveDto.getCompanyCd();
        Integer priorityAllCd = priorityAllSaveDto.getPriorityAllCd();
        Integer priorityOrderCd = priorityAllSaveDto.getPriorityOrderCd();
        // 同一棚名称の棚パータンListを取得
        List<PriorityAllPatternListVO> info;
        // 基本パターンの制約List
        List<WorkPriorityAllRestrictResult> basicRestrictList;
        // 全パターンの制約List
        List<PriorityAllRestrictDto> allRestrictDtoList;
        // 全パターンのRelationList
        List<WorkPriorityAllRestrictRelation> allRelationsList;

        workPriorityAllResultDataMapper.deleteWKTableResultData(companyCd, priorityAllCd, authorCd);

        try {
            basicPatternCd = priorityAllMstMapper.getPatternCdBYPriorityCd(companyCd, priorityOrderCd);
            basicTannaNum = new BigDecimal(shelfPtsDataMapper.getTanaNum(basicPatternCd));
            info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, basicPatternCd,authorCd);
            basicRestrictList = priorityOrderRestrictResultMapper.getPriorityOrderRestrictAll(companyCd, priorityOrderCd);
            // 全パターンのList
            List<PriorityAllPatternListVO> checkedInfo = info.stream().filter(vo->vo.getCheckFlag()==1).collect(Collectors.toList());
            for(PriorityAllPatternListVO pattern : checkedInfo) {
                // パターンのPTS台/棚List
                List<ShelfPtsDataTanamst> tanaList = shelfPtsDataTanamstMapper.selectByPatternCd(pattern.getShelfPatternCd().longValue());

                // 全パターン制約一覧作成 workPriorityAllRestrict
                allRestrictDtoList = makeWKRestrictList(pattern, basicRestrictList, priorityAllCd, companyCd, authorCd, basicTannaNum);
                // 全パターンのRelation一覧作成
                allRelationsList = makeWKRelationList(allRestrictDtoList, tanaList, priorityAllCd, companyCd, authorCd,pattern.getShelfPatternCd());

                // 全パターンRelationテーブル更新
                workPriorityAllRestrictRelationMapper.insertWKTableRelation(allRelationsList);
                // 全パターン制約テーブルに保存
                workPriorityAllRestrictMapper.insertWKTableRestrict(allRestrictDtoList);


                //从基本パターンresult_data中查询出来存到work_all_priority_result_data中(face、台棚放置数据不存)
                workPriorityAllResultDataMapper.insertWKTableResultData(companyCd, priorityAllCd, priorityOrderCd, authorCd, pattern.getShelfPatternCd());
                //获取pattern对应的jan
                String resultDataList = workPriorityAllResultDataMapper.getJans(pattern.getShelfPatternCd(), companyCd, priorityAllCd,authorCd);
                if (resultDataList == null) {
                    return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
                }
                String[] array = resultDataList.split(",");
                //调用smt计算推荐face数
                Map<String, Object> Data = priorityOrderMstService.getFaceKeisanForCgi(array, companyCd,  pattern.getShelfPatternCd(), authorCd);
                if (Data.get("data") != null && Data.get("data") != "") {
                    String[] strResult = Data.get("data").toString().split("@");
                    String[] strSplit = null;
                    List<PriorityAllResultDataDto> list = new ArrayList<>();
                    PriorityAllResultDataDto orderResultData;
                    for (int i = 0; i < strResult.length; i++) {
                        orderResultData = new PriorityAllResultDataDto();
                        strSplit = strResult[i].split(" ");
                        orderResultData.setSalesCnt(Double.valueOf(strSplit[1]));
                        orderResultData.setJanCd(strSplit[0]);


                        list.add(orderResultData);
                    }
                    workPriorityAllResultDataMapper.update(list,companyCd,authorCd,priorityAllCd,pattern.getShelfPatternCd());
                }
                List<PriorityAllResultDataDto> resultDatas = workPriorityAllResultDataMapper.getResultDatas(companyCd, authorCd, priorityAllCd, pattern.getShelfPatternCd());
                //获取旧pts的平均值，最大值最小值
                FaceNumDataDto faceNum = productPowerMstMapper.getFaceNum(pattern.getShelfPatternCd());
                Integer minFaceNum = faceNum.getFaceMinNum();
                if (Data.get("data") != null && Data.get("data") != "") {

                    DecimalFormat df = new DecimalFormat("#.00");
                    //获取salesCntAvg并保留两位小数
                    Double avgSalesCunt = workPriorityAllResultDataMapper.getAvgSalesCunt(companyCd, authorCd, priorityAllCd, pattern.getShelfPatternCd());
                    String format = df.format(avgSalesCunt);
                    avgSalesCunt = Double.valueOf(format);
                    Double d = null;
                    for (PriorityAllResultDataDto resultData : resultDatas) {
                        resultData.setPriorityAllCd(priorityAllCd);
                        resultData.setCompanyCd(companyCd);
                        resultData.setShelfPatternCd(pattern.getShelfPatternCd());
                        if (resultData.getSalesCnt() != null) {
                            d = resultData.getSalesCnt() * faceNum.getFaceAvgNum() / avgSalesCunt;

                            if (d > faceNum.getFaceMaxNum()) {
                                resultData.setFaceNum(Integer.valueOf(faceNum.getFaceMaxNum()));
                            } else if (d < faceNum.getFaceMinNum()) {
                                resultData.setFaceNum(Integer.valueOf(faceNum.getFaceMinNum()));
                            } else {
                                resultData.setFaceNum(d.intValue());
                            }

                        } else {
                            resultData.setFaceNum(Integer.valueOf(faceNum.getFaceMinNum()));
                        }

                    }
                }else {
                    for (PriorityAllResultDataDto resultData : resultDatas) {
                        resultData.setPriorityAllCd(priorityAllCd);
                        resultData.setCompanyCd(companyCd);
                        resultData.setShelfPatternCd(pattern.getShelfPatternCd());
                        resultData.setFaceNum(minFaceNum);
                    }
                }
                workPriorityAllResultDataMapper.updateFace(resultDatas);
                this.setJan(companyCd, authorCd, priorityAllCd, priorityOrderCd, pattern.getShelfPatternCd(), minFaceNum);
                //保存pts到临时表里
                priorityAllPtsService.saveWorkPtsData(companyCd, authorCd, priorityAllCd, pattern.getShelfPatternCd());
            }
        } catch(Exception ex) {
            logger.error("", ex);
            return ResultMaps.result(ResultEnum.FAILURE, ex.getMessage());
        }

        return ResultMaps.result(ResultEnum.SUCCESS, "計算成功しました。");
    }



    @Override
    public boolean setJan(String companyCd, String authorCd, Integer priorityAllCd, Integer priorityOrderCd, Integer shelfPatternCd, Integer minFace) {
        PriorityOrderMst priorityOrderMst = priorityOrderMstMapper.selectOrderMstByPriorityOrderCd(priorityOrderCd);

        List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations = workPriorityAllRestrictRelationMapper.selectByAuthorCd(companyCd, priorityAllCd, authorCd, shelfPatternCd);
        List<PriorityOrderResultDataDto> workPriorityOrderResultData = workPriorityAllResultDataMapper.getResultJans(companyCd, priorityAllCd,authorCd,shelfPatternCd);
        List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(shelfPatternCd);

        Short partitionFlag = Optional.ofNullable(priorityOrderMst.getPartitionFlag()).orElse((short) 0);
        Short partitionVal = Optional.ofNullable(priorityOrderMst.getPartitionVal()).orElse((short) 2);

        Map<String, List<PriorityOrderResultDataDto>> finalSetJanResultData =
                commonMstService.commSetJan(partitionFlag, partitionVal, taiData,
                        workPriorityOrderResultData, workPriorityOrderRestrictRelations, minFace);

        for (Map.Entry<String, List<PriorityOrderResultDataDto>> entry : finalSetJanResultData.entrySet()) {
            List<PriorityOrderResultDataDto> resultDataDtos = entry.getValue();
            workPriorityAllResultDataMapper.updateTaiTanaBatch(companyCd, priorityAllCd, shelfPatternCd, authorCd, resultDataDtos);
        }

        return true;
    }
    /**
     * 保存
     * @param companyCd
     * @param priorityAllCd
     * @return
     * TODO:0321 liuxinyu
     */
    @Override
    public Map<String, Object> savePriorityAll(String companyCd, Integer priorityAllCd,String priorityAllName) {
        String aud = session.getAttribute("aud").toString();
        Integer cd = priorityAllMstMapper.selectPriorityAllName(priorityAllName, companyCd);
        if (cd != null && !cd.equals(priorityAllCd) ){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        ProductPowerNumGenerator p = new ProductPowerNumGenerator();
        if (priorityAllCd != 0){

            priorityAllMstMapper.deleteFinalTableMst(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTableShelfs(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTableRestrict(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTableResult(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTablePtsTai(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTablePtsTana(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTablePtsJans(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTablePtsData(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTablePtsRelation(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteFinalTablePtsVersion(companyCd,priorityAllCd,aud);

            priorityAllMstMapper.setFinalTableMst(companyCd,priorityAllCd,aud,priorityAllName);
            priorityAllMstMapper.setFinalTableShelfs(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.setFinalTableRestrict(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.setFinalTableRelation(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.setFinalTableResult(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.setFinalTablePtsTai(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.setFinalTablePtsTana(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.setFinalTablePtsJans(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.setFinalTablePtsData(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.setFinalTablePtsVersion(companyCd,priorityAllCd,aud);

        }else {

            p.setUsercd(session.getAttribute("aud").toString());
            int id = priorityAllNumGeneratorMapper.insert(p);

            logger.info("全pattern表自动取号："+p.getId());
            priorityAllMstMapper.setNewFinalTableMst(companyCd,p.getId(),aud,priorityAllName);
            priorityAllMstMapper.setNewFinalTableShelfs(companyCd,p.getId(),aud);
            priorityAllMstMapper.setNewFinalTableRestrict(companyCd,p.getId(),aud);
            priorityAllMstMapper.setNewFinalTableRelation(companyCd,p.getId(),aud);
            priorityAllMstMapper.setNewFinalTableResult(companyCd,p.getId(),aud);
            priorityAllMstMapper.setNewFinalTablePtsTai(companyCd,p.getId(),aud);
            priorityAllMstMapper.setNewFinalTablePtsTana(companyCd,p.getId(),aud);
            priorityAllMstMapper.setNewFinalTablePtsJans(companyCd,p.getId(),aud);
            priorityAllMstMapper.setNewFinalTablePtsData(companyCd,p.getId(),aud);
            priorityAllMstMapper.setNewFinalTablePtsVersion(companyCd,p.getId(),aud);
            return ResultMaps.result(ResultEnum.SUCCESS,p.getId());
        }



        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * pts詳細
     * @param companyCd
     * @param priorityAllCd
     * @param patternCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityPtsInfo(String companyCd, Integer priorityAllCd, Integer patternCd) {
        return priorityAllPtsService.getPtsDetailData(patternCd,companyCd,priorityAllCd);
    }

    @Override
    public Map<String, Object> deletePriorityAll( PriorityAllSaveDto priorityAllSaveDto) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = priorityAllSaveDto.getCompanyCd();
        Integer priorityAllCd = priorityAllSaveDto.getPriorityAllCd();
        priorityAllMstMapper.deleteMst(companyCd,priorityAllCd,aud);
        return null;
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

        //基本パターン的face数扩大三倍取商品（基本パターン已同步修改）
        skuCountPerPattan*=3;

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
                logger.info("扩/缩后：{}", 0.5);
            } else {
                allSet.setTanaCnt(new BigDecimal(tmpTanaCnt));
                logger.info("扩/缩后：{}", tmpTanaCnt);
            }

            allSet.setSkuCnt(allSet.getTanaCnt().multiply(new BigDecimal(skuCountPerPattan)).intValue());
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
                    //没有剩余的tana数，直接结束
                    logger.info("基本{}, 扩缩后{}", allRestrict.getBasicTanaCnt(), allRestrict.getTanaCnt());
                    if (remainTanaCnt.compareTo(new BigDecimal(1)) < 0) {
                        allRestrict.setTanaCnt(allRestrict.getTanaCnt().add(BigDecimal.valueOf(0.5)));
                        allRestrict.setSkuCnt(allRestrict.getSkuCnt() + BigDecimal.valueOf(skuCountPerPattan/2).setScale(0, RoundingMode.CEILING).intValue());
                        break;
                    } else {
                        allRestrict.setTanaCnt(allRestrict.getTanaCnt().add(new BigDecimal(1)));
                        allRestrict.setSkuCnt(allRestrict.getSkuCnt() + skuCountPerPattan.intValue());
                        allTanaNum = allTanaNum.add(new BigDecimal(1));
                    }
                }
            }
        }
        // 制約IDにより並び替え
        allRestrictDtoList.sort(Comparator.comparingInt(a -> a.getRestrictCd().intValue()));

        return allRestrictDtoList;
    }

    /**
     * Relation一覧を作成
     * @param allRestrictDtoList
     * @param tanaList
     * @param priorityAllCd
     * @param companyCd
     * @param authorCd
     * @param patternCd
     * @return
     */
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
}
