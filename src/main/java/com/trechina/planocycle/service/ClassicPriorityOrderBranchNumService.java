package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;

import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderBranchNumService {
    /**
     * smart処理後の必須+不可商品の結菓セットを取得し、保存
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderBranchNum(String companyCd,Integer priorityOrderCd,String shelfPatternCd);

    /**
     * 必須商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderCommodityMust(String companyCd,Integer priorityOrderCd);

    /**
     * 不可商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderCommodityNot(String companyCd,Integer priorityOrderCd);

    /**
     * 保存必須商品リスト
     * @param priorityOrderCommodityMust
     * @return
     */
    Map<String,Object> setPriorityOrderCommodityMust(List<PriorityOrderCommodityMust> priorityOrderCommodityMust);

    /**
     * 不可商品リストの保存
     * @param priorityOrderCommodityNot
     * @return
     */
    Map<String,Object> setPriorityOrderCommodityNot(List<PriorityOrderCommodityNot> priorityOrderCommodityNot);

    /**
     * 必須商品を削除する
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderCommodityMustInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 不可商品を削除する
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderCommodityNotInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 必須の中間テーブルを削除
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderBranchNumInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 不可商品が他のmstと重複しているかどうかをチェック
     * @param priorityOrderCommodityNot
     * @return
     */
    Map<String,Object> checkIsJanCommodityNot(List<PriorityOrderCommodityNot> priorityOrderCommodityNot);

    List<String> checkIsJanCommodityMust(List<PriorityOrderCommodityMust> priorityOrderCommodityMust);
}
