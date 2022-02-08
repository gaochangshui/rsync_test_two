package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public interface PriorityOrderBranchNumService {
    /**
     * 获取smart处理之后的必须+不可商品的结果集，并保存
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderBranchNum(String companyCd,Integer priorityOrderCd,String shelfPatternCd);

    /**
     * 获取必须商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderCommodityMust(String companyCd,Integer priorityOrderCd);

    /**
     * 获取不可商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderCommodityNot(String companyCd,Integer priorityOrderCd);

    /**
     * 保存必须商品list，并传参给smart
     * @param priorityOrderCommodityMust
     * @return
     */
    Map<String,Object> setPriorityOrderCommodityMust(List<PriorityOrderCommodityMust> priorityOrderCommodityMust);

    /**
     * 保存不可商品list，并传参给smart
     * @param priorityOrderCommodityNot
     * @return
     */
    Map<String,Object> setPriorityOrderCommodityNot(List<PriorityOrderCommodityNot> priorityOrderCommodityNot);

    /**
     * 删除必须商品
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderCommodityMustInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 删除不可商品
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderCommodityNotInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 删除必须不可的中间表
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delPriorityOrderBranchNumInfo(String companyCd,Integer priorityOrderCd);
}
