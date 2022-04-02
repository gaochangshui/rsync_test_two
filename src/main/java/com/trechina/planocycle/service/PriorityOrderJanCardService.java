package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderJanCard;

import java.util.List;
import java.util.Map;

public interface PriorityOrderJanCardService {
    /**
     * 获取card商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanCard(String companyCd,Integer priorityOrderCd);

    /**
     * 保存card商品list
     * @param priorityOrderJanCard
     * @return
     */
    Map<String,Object> setPriorityOrderJanCard(List<PriorityOrderJanCard> priorityOrderJanCard);


}
