package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.service.PriorityOrderBranchNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderBranchNum")
public class PriorityOrderBranchNumController {
    @Autowired
    private PriorityOrderBranchNumService priorityOrderBranchNumService;
    /**
     * smart処理後の必須+不可商品の結果セットを取得し、保存する
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderBranchNum")
    public Map<String,Object> getPriorityOrderBranchNum(String companyCd,Integer priorityOrderCd,String shelfPatternCd){
        return  priorityOrderBranchNumService.getPriorityOrderBranchNum(companyCd,priorityOrderCd,shelfPatternCd);
    }

    /**
     * 必須商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderCommodityMust")
    public Map<String,Object> getPriorityOrderCommodityMust(String companyCd,Integer priorityOrderCd){
        return priorityOrderBranchNumService.getPriorityOrderCommodityMust(companyCd,priorityOrderCd);
    }

    /**
     * 不可商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderCommodityNot")
    public Map<String,Object> getPriorityOrderCommodityNot(String companyCd,Integer priorityOrderCd){
        return priorityOrderBranchNumService.getPriorityOrderCommodityNot(companyCd,priorityOrderCd);
    }

    /**
     * 必須商品リストの保存
     * @param priorityOrderCommodityMust
     * @return
     */
    @PostMapping("/setPriorityOrderCommodityMust")
    public Map<String,Object> setPriorityOrderCommodityMust(@RequestBody List<PriorityOrderCommodityMust> priorityOrderCommodityMust) {
        return priorityOrderBranchNumService.setPriorityOrderCommodityMust(priorityOrderCommodityMust);
    }

    /**
     * 保存不可商品リスト
     * @param priorityOrderCommodityNot
     * @return
     */
    @PostMapping("/setPriorityOrderCommodityNot")
    public Map<String,Object> setPriorityOrderCommodityNot(@RequestBody List<PriorityOrderCommodityNot> priorityOrderCommodityNot) {
        return priorityOrderBranchNumService.setPriorityOrderCommodityNot(priorityOrderCommodityNot);
    }
}
