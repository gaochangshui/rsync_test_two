package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;

import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderJanProposalService {
    /**
     * 获取jan变提案list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanProposal(String companyCd,Integer priorityOrderCd,Integer productPowerNo,String shelfPatternNo);

    /**
     * 修改jan变提案list的flag
     * @param priorityOrderJanProposal
     * @return
     */
    Map<String,Object> setPriorityOrderJanProposal(List<PriorityOrderJanProposal> priorityOrderJanProposal);

    /**
     * 保存cgi返回的jan提案list
     * @param jsonArray
     * @return
     */
    Map<String,Object> savePriorityOrderJanProposal(JSONArray jsonArray,String companyCd,Integer priorityOrderCd);

    /**
     * 删除jan变提案list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderJanProposalInfo(String companyCd,Integer priorityOrderCd);
}
