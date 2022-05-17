package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;
import com.trechina.planocycle.service.ClassicPriorityOrderJanProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/priority/PriorityOrderJanProposal")
public class ClassicPriorityOrderJanProposalController {

    @Autowired
    private ClassicPriorityOrderJanProposalService priorityOrderJanProposalService;

    /**
     * 获取jan变提案list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanProposal")
    public Map<String,Object> getPriorityOrderJanProposal(String companyCd, Integer priorityOrderCd,Integer productPowerNo,String shelfPatternNo){
        return priorityOrderJanProposalService.getPriorityOrderJanProposal(companyCd,priorityOrderCd,productPowerNo,shelfPatternNo);
    }

    /**
     * 修改jan变提案list的flag
     * @param priorityOrderJanProposal
     * @return
     */
    @PostMapping("/setPriorityOrderJanProposal")
    public Map<String,Object> setPriorityOrderJanProposal(@RequestBody List<PriorityOrderJanProposal> priorityOrderJanProposal){
        return priorityOrderJanProposalService.setPriorityOrderJanProposal(priorityOrderJanProposal);
    }
}
