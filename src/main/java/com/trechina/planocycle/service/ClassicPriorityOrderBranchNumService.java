package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.StarReadingTableDto;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.entity.vo.CommodityBranchPrimaryKeyVO;

import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderBranchNumService {


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
     * 必須リスト
     * @param companyCd
     * @return
     */
    Map<String, Object> getPriorityOrderMustList(String companyCd, Integer priorityOrderCd);
    /**
     * リスト不可
     * @param companyCd
     * @return
     */
    Map<String, Object> getPriorityOrderNotList(String companyCd, Integer priorityOrderCd);
    /**
     * 詳細が必要です
     * @param companyCd
     * @param priorityOrderCd
     * @param jan
     * @return
     */
    Map<String, Object> getCommodityMustBranchList(String companyCd, Integer priorityOrderCd, String jan);
    /**
     * 詳細不可
     * @param companyCd
     * @param priorityOrderCd
     * @param jan
     * @return
     */
    Map<String, Object> getCommodityNotBranchList(String companyCd, Integer priorityOrderCd, String jan);

    /**
     * 詳細保存不可
     * @param companyCd,priorityOrderCd,jan,commodityList
     * @return
     */
    Map<String, Object> saveCommodityNotBranchList(String companyCd, Integer priorityOrderCd, String jan, String commodityList);
    /**
     * 詳細を保存する必要があります
     * @param companyCd,priorityOrderCd,jan,commodityList
     * @return
     */
    Map<String, Object> saveCommodityMustBranchList(String companyCd, Integer priorityOrderCd, String jan, String commodityList);
    /**
     * 詳細を削除する必要があります
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    Map<String, Object> delCommodityMustBranch(CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO);
    /**
     * 詳細削除不可
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    Map<String, Object> delCommodityNotBranch(CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO);

    Map<String, Object> getStarReadingTable(StarReadingTableDto starReadingTableDto);

    Map<String, Object> getStarReadingParam(StarReadingTableDto starReadingTableDto);
}
