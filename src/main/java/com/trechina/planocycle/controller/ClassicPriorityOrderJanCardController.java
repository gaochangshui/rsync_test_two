package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanCard;
import com.trechina.planocycle.service.ClassicPriorityOrderJanCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/priority/PriorityOrderJanCard")
public class ClassicPriorityOrderJanCardController {

    @Autowired
    private ClassicPriorityOrderJanCardService priorityOrderJanCardService;

    /**
     * card商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanCard")
    public Map<String,Object> getPriorityOrderJanCard (String companyCd,Integer priorityOrderCd) {
        return priorityOrderJanCardService.getPriorityOrderJanCard(companyCd,priorityOrderCd);
    }

    /**
     * card商品listの保存
     * @param priorityOrderJanCard
     * @return
     */
    @PostMapping("/setPriorityOrderJanCard")
    public Map<String,Object> setPriorityOrderJanCard(@RequestBody List<ClassicPriorityOrderJanCard> priorityOrderJanCard){
        return priorityOrderJanCardService.setPriorityOrderJanCard(priorityOrderJanCard);
    }

}
