package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.PriorityOrderMst;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.PriorityAllMstService;
import com.trechina.planocycle.service.PriorityOrderMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PriorityAllMstServiceImpl  implements PriorityAllMstService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 全パータン計算
     * TODO:2200866
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> autoCalculation(String companyCd, Integer priorityOrderCd) {
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

        return null;
    }

    @Override
    public Map<String, Object> savePriorityAll(String companyCd, Integer priorityOrderCd) {
        return null;
    }
}
