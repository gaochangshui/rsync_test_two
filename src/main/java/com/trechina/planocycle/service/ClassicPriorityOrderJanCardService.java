package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanCard;

import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderJanCardService {
    /**
     * card商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanCard(String companyCd,Integer priorityOrderCd);

    /**
     * card商品listの保存
     * @param priorityOrderJanCard
     * @return
     */
    Map<String,Object> setPriorityOrderJanCard(List<ClassicPriorityOrderJanCard> priorityOrderJanCard);

    /**
     * card商品listの削除
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderJanCardInfo(String companyCd,Integer priorityOrderCd);

    Map<String,String> checkIsJanCut(List<ClassicPriorityOrderJanCard> priorityOrderJanCard);
}
