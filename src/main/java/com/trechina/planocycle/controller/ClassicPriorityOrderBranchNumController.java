package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.service.ClassicPriorityOrderBranchNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priority/PriorityOrderBranchNum")
public class ClassicPriorityOrderBranchNumController {
    @Autowired
    private ClassicPriorityOrderBranchNumService priorityOrderBranchNumService;
    /**
     * 获取smart处理之后的必须+不可商品的结果集，并保存
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderBranchNum")
    public Map<String,Object> getPriorityOrderBranchNum(String companyCd,Integer priorityOrderCd,String shelfPatternCd){
        return  priorityOrderBranchNumService.getPriorityOrderBranchNum(companyCd,priorityOrderCd,shelfPatternCd);
    }

    /**
     * 获取必须商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderCommodityMust")
    public Map<String,Object> getPriorityOrderCommodityMust(String companyCd,Integer priorityOrderCd){
        return priorityOrderBranchNumService.getPriorityOrderCommodityMust(companyCd,priorityOrderCd);
    }

    /**
     * 获取不可商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderCommodityNot")
    public Map<String,Object> getPriorityOrderCommodityNot(String companyCd,Integer priorityOrderCd){
        return priorityOrderBranchNumService.getPriorityOrderCommodityNot(companyCd,priorityOrderCd);
    }

    /**
     * 保存必须商品list
     * @param priorityOrderCommodityMust
     * @return
     */
    @PostMapping("/setPriorityOrderCommodityMust")
    public Map<String,Object> setPriorityOrderCommodityMust(@RequestBody List<PriorityOrderCommodityMust> priorityOrderCommodityMust) {
        return priorityOrderBranchNumService.setPriorityOrderCommodityMust(priorityOrderCommodityMust);
    }

    /**
     * 保存不可商品list
     * @param priorityOrderCommodityNot
     * @return
     */
    @PostMapping("/setPriorityOrderCommodityNot")
    public Map<String,Object> setPriorityOrderCommodityNot(@RequestBody List<PriorityOrderCommodityNot> priorityOrderCommodityNot) {
        return priorityOrderBranchNumService.setPriorityOrderCommodityNot(priorityOrderCommodityNot);
    }
}
