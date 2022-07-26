package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOrderJanCard;
import com.trechina.planocycle.service.PriorityOrderJanCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/PriorityOrderJanCard")
public class PriorityOrderJanCardController {

    @Autowired
    private PriorityOrderJanCardService priorityOrderJanCardService;

    /**
     * card商品リストを取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanCard")
    public Map<String,Object> getPriorityOrderJanCard (String companyCd,Integer priorityOrderCd) {
        return priorityOrderJanCardService.getPriorityOrderJanCard(companyCd,priorityOrderCd);
    }

    /**
     * card商品リストの保存
     * @param priorityOrderJanCard
     * @return
     */
    @PostMapping("/setPriorityOrderJanCard")
    public Map<String,Object> setPriorityOrderJanCard(@RequestBody List<PriorityOrderJanCard> priorityOrderJanCard){
        return priorityOrderJanCardService.setPriorityOrderJanCard(priorityOrderJanCard);
    }

}
