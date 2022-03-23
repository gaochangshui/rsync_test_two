package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.PriorityAllRestrictDto;
import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.ShelfPtsDataTanamst;
import com.trechina.planocycle.entity.po.WorkPriorityAllRestrictRelation;
import com.trechina.planocycle.entity.po.WorkPriorityAllRestrictResult;
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
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private PriorityOrderRestrictResultMapper priorityOrderRestrictResultMapper;
    @Autowired
    private WorkPriorityAllRestrictRelationMapper workPriorityAllRestrictRelationMapper;
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
            workPriorityAllRestrictRelationMapper.deleteWKTableRelation(companyCd, priorityAllCd, authorCd);
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
        Map<String, Object> ptsInfoTemp = shelfPtsService.getTaiNumTanaNum(patternCd,priorityOrderCd);
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
        workPriorityAllRestrictRelationMapper.deleteWKTableRelation(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
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
        BigDecimal basicTannaNum = new BigDecimal(0);
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = priorityAllSaveDto.getCompanyCd();
        Integer priorityAllCd = priorityAllSaveDto.getPriorityAllCd();
        Integer priorityOrderCd = priorityAllSaveDto.getPriorityOrderCd();
        // 同一棚名称の棚パータンListを取得
        List<PriorityAllPatternListVO> info = new ArrayList<>();
        // 基本パターンの制約List
        List<WorkPriorityAllRestrictResult> basicRestrictList = new ArrayList<>();
        // 全パターンの制約List
        List<PriorityAllRestrictDto> allRestrictDtoList = new ArrayList<>();
        // 全パターンのRelationList
        List<WorkPriorityAllRestrictRelation> allRelationsList = new ArrayList<>();

        try {
            basicPatternCd = priorityAllMstMapper.getPatternCdBYPriorityCd(companyCd, priorityOrderCd);
            basicTannaNum = new BigDecimal(shelfPtsDataMapper.getTanaNum(basicPatternCd));
            info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, basicPatternCd);
            basicRestrictList = priorityOrderRestrictResultMapper.getPriorityOrderRestrictAll(companyCd, priorityOrderCd);
            // 全パターンのList
            for(PriorityAllPatternListVO pattern : info) {
                if (pattern.getCheckFlag() == 1) {

                    // パターンのPTS台/棚List
                    List<ShelfPtsDataTanamst> tanaList = shelfPtsDataTanamstMapper.selectByPatternCd(pattern.getShelfPatternCd().longValue());

                    // 全パターン制約一覧作成 workPriorityAllRestrict
                    allRestrictDtoList = makeWKRestrictList(tanaList, pattern, basicRestrictList, priorityAllCd, companyCd, authorCd, basicTannaNum);
                    // 全パターンのRelation一覧作成
                    allRelationsList = makeWKRelationList(allRestrictDtoList, tanaList, priorityAllCd, companyCd, authorCd,pattern.getShelfPatternCd());


                    // 全パターンRelationテーブル更新
                    workPriorityAllRestrictRelationMapper.insertWKTableRelation(allRelationsList);
                    // 全パターン制約テーブルに保存
                    workPriorityAllRestrictMapper.insertWKTableRestrict(allRestrictDtoList);
                }
            }
        } catch(Exception ex) {
            return ResultMaps.result(ResultEnum.FAILURE, ex.getMessage());
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

    /**
     * 基本パターンの制約により各パターンの制約一覧を作成
     * @param tanaList
     * @param pattern
     * @param basicRestrictList
     * @param priorityAllCd
     * @param companyCd
     * @param authorCd
     * @param basicTannaNum
     * @return
     */
    private List<PriorityAllRestrictDto> makeWKRestrictList(List<ShelfPtsDataTanamst> tanaList, PriorityAllPatternListVO pattern
            , List<WorkPriorityAllRestrictResult> basicRestrictList, Integer priorityAllCd
            , String companyCd, String  authorCd, BigDecimal basicTannaNum) {
        List<PriorityAllRestrictDto> allRestrictDtoList = new ArrayList<>();

        // チェックされたパターン
        BigDecimal inX = new BigDecimal(pattern.getTanaCnt()).divide(basicTannaNum, 2, BigDecimal.ROUND_DOWN);
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
            Integer tmpTanaCnt = inX.multiply(basicSet.getTanaCnt()).intValue();
            if(tmpTanaCnt < 1) {
                allSet.setTanaCnt(new BigDecimal(0.5));
            } else {
                allSet.setTanaCnt(new BigDecimal(tmpTanaCnt));
            }

            allSet.setSkuCnt(allSet.getTanaCnt().multiply(new BigDecimal(13)).intValue());
            // 全パターン制約に追加
            allRestrictDtoList.add(allSet);
            allTanaNum = allTanaNum.add(allSet.getTanaCnt());
        }
        // 残棚がある場合
        if (new BigDecimal(pattern.getTanaCnt()).compareTo(allTanaNum) > 0 ) {
            //　棚数の小順
            Collections.sort(allRestrictDtoList ,(a, b)->{
                return b.getTanaCnt().compareTo(a.getTanaCnt());
            });
            // 棚数が多い制約から棚追加
            for(PriorityAllRestrictDto allRestrict : allRestrictDtoList) {
                if (new BigDecimal(pattern.getTanaCnt()).subtract(allTanaNum).compareTo(new BigDecimal(1)) < 0) {
                    allRestrict.setTanaCnt(allRestrict.getTanaCnt().add(new BigDecimal(0.5)));
                    allRestrict.setSkuCnt(allRestrict.getSkuCnt() + 7);
                    break;
                } else {
                    allRestrict.setTanaCnt(allRestrict.getTanaCnt().add(new BigDecimal(1)));
                    allRestrict.setSkuCnt(allRestrict.getSkuCnt() + 13);
                    allTanaNum = allTanaNum.add(new BigDecimal(1));
                }
            }
        }
        // 制約IDにより並び替え
        Collections.sort(allRestrictDtoList ,(a, b)->{
            return a.getRestrictCd().intValue() - b.getRestrictCd().intValue();
        });

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
            if (iTanaCnt.compareTo(new BigDecimal(0.5)) == 0) {
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
                iTanaCnt = allRestrictDtoList.get(iRestrict).getTanaCnt().subtract(new BigDecimal(0.5));
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
