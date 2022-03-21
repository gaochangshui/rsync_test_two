package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.PriorityAllRestrictDto;
import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictResult;
import com.trechina.planocycle.entity.vo.PriorityAllPatternListVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.PriorityAllMstService;
import com.trechina.planocycle.service.ShelfPtsService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

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
    private PriorityOrderRestrictResultMapper priorityOrderRestrictResultMapper;
    @Autowired
    private WorkPriorityAllRestrictMapper workPriorityAllRestrictMapper;

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
            priorityAllMstMapper.deleteWKTableRelation(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableResult(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsTai(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsTana(companyCd, priorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsJans(companyCd, priorityAllCd, authorCd);

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
            }
            return ResultMaps.result(ResultEnum.SUCCESS, "新規作成成功しました。");
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
        logger.info("基本パターンList："+resultInfo);
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

        // 基本パターンに紐付け棚パターンCDをもらう
        Integer patternCd = priorityAllMstMapper.getPatternCdBYPriorityCd(companyCd, priorityOrderCd);
        // 棚パターンのPTS基本情報をもらう
        Map<String, Object> ptsInfoTemp = shelfPtsService.getTaiNumTanaNum(patternCd);
        if ((Integer)ptsInfoTemp.get("code") != 101) {
            return ResultMaps.result(ResultEnum.FAILURE, "該当基本パターンに紐付け棚パターンが見つけていませんでした。");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("tanaInfo", ptsInfoTemp.get("data"));

        // 同一棚名称の棚パータンListを取得
        List<PriorityAllPatternListVO> info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, patternCd);
        result.put("ptsInfo", info);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    /**
     * 自動計算する前にデータを一時テーブルに保存
     *
     * @param priorityAllSaveDto@return
     * TODO:0318 2200866
     */
    @Override
    public Integer saveWKAllPatternData(PriorityAllSaveDto priorityAllSaveDto) {
        String authorCd = session.getAttribute("aud").toString();
        // mstテーブル
        priorityAllMstMapper.deleteWKTableMst(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
        // shelfsテーブル
        priorityAllMstMapper.deleteWKTableShelfs(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
        workPriorityAllRestrictMapper.deleteWKTableRestrict(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd());
        priorityAllMstMapper.insertWKTableMst(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd, priorityAllSaveDto.getPriorityOrderCd());
        priorityAllMstMapper.insertWKTableShelfs(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd, priorityAllSaveDto.getPatterns());

        return 0;
    }

    /**
     * 全パータン計算
     * TODO:2200866
     * @return
     * TODO:0319 21 23 2200866
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
        Integer basicPatternCd = 0;
        Integer basicTannaNum = 0;
        Integer allTanaNum = 0;
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = priorityAllSaveDto.getCompanyCd();
        Integer priorityAllCd = priorityAllSaveDto.getPriorityAllCd();
        Integer priorityOrderCd = priorityAllSaveDto.getPriorityOrderCd();
        // 同一棚名称の棚パータンListを取得
        List<PriorityAllPatternListVO> info = new ArrayList<>();
        // 基本パターンの制約List
        List<WorkPriorityOrderRestrictResult> basicRestrictList = new ArrayList<>();
        // 全パターンの制約List
        List<PriorityAllRestrictDto> allRestrictDtoList = new ArrayList<>();
        try {
            basicPatternCd = priorityAllMstMapper.getPatternCdBYPriorityCd(companyCd, priorityOrderCd);
            basicTannaNum = shelfPtsDataMapper.getTanaNum(basicPatternCd);
            info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, basicPatternCd);
            basicRestrictList = priorityOrderRestrictResultMapper.getPriorityOrderRestrictAll(companyCd, priorityOrderCd);
            BigDecimal inX = new BigDecimal(1.00);   // 係数
            // 全パターンのList
            for(PriorityAllPatternListVO pattern : info) {
                if (pattern.getCheckFlag() == 1) {
                    allRestrictDtoList = new ArrayList<>();
                    // チェックされたパターン
                    inX = new BigDecimal(pattern.getTanaCnt()).divide(new BigDecimal(basicTannaNum), 2, BigDecimal.ROUND_DOWN);
                    allTanaNum = 0;
                    // 基本パターンの制約List
                    for(WorkPriorityOrderRestrictResult basicSet : basicRestrictList) {
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
                        Integer tmpTanaCnt = inX.multiply(new BigDecimal(basicSet.getTanaCnt())).intValue();
                        if(tmpTanaCnt <= 1) {
                            allSet.setTanaCnt(1);
                        } else {
                            allSet.setTanaCnt(tmpTanaCnt);
                        }

                        allSet.setSkuCnt(allSet.getTanaCnt() * 13);
                        // 全パターン制約に追加
                        allRestrictDtoList.add(allSet);
                        allTanaNum = allTanaNum + allSet.getTanaCnt();
                    }
                    // 残棚がある場合
                    if (pattern.getTanaCnt() > allTanaNum) {
                        //　棚数の小順
                        Collections.sort(allRestrictDtoList ,(a, b)->{
                            return b.getTanaCnt() - a.getTanaCnt();
                        });
                        // 棚数が多い制約から棚追加
                        for(PriorityAllRestrictDto allRestrict : allRestrictDtoList) {
                            allRestrict.setTanaCnt(allRestrict.getTanaCnt() + 1);
                            allRestrict.setSkuCnt(allRestrict.getSkuCnt() + 13);

                            allTanaNum = allTanaNum + 1;
                            if (pattern.getTanaCnt() == allTanaNum) {
                                break;
                            }
                        }
                    }
                    // テーブルに保存
                    workPriorityAllRestrictMapper.insertWKTableRestrict(allRestrictDtoList);
                }
            }
        } catch(Exception ex) {
            ResultMaps.result(ResultEnum.SUCCESS, ex.getMessage());
        }

        return ResultMaps.result(ResultEnum.SUCCESS, "計算成功しました。");
    }

    /**
     * 保存
     * @param companyCd
     * @param priorityOrderCd
     * @return
     * TODO:0321 liuxinyu
     */
    @Override
    public Map<String, Object> savePriorityAll(String companyCd, Integer priorityOrderCd) {
        return null;
    }

    /**
     * pts詳細
     * @param companyCd
     * @param priorityAllCd
     * @param patternCd
     * @return
     * TODO 0321 Chenke
     */
    @Override
    public Map<String, Object> getPriorityPtsInfo(String companyCd, Integer priorityAllCd, Integer patternCd) {
        return null;
    }

}
