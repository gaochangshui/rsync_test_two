package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;

import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderJanProposalService {
    /**
     * jan変提案listの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanProposal(String companyCd,Integer priorityOrderCd,Integer productPowerNo,String shelfPatternNo);

    /**
     * jan変提案listのflagを修正する
     * @param priorityOrderJanProposal
     * @return
     */
    Map<String,Object> setPriorityOrderJanProposal(List<PriorityOrderJanProposal> priorityOrderJanProposal);

    /**
     * cgiが返すjan提案リストを保存する
     * @param jsonArray
     * @return
     */
    Map<String,Object> savePriorityOrderJanProposal(JSONArray jsonArray,String companyCd,Integer priorityOrderCd);

    /**
     * jan変提案listを削除する
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderJanProposalInfo(String companyCd,Integer priorityOrderCd);
}
