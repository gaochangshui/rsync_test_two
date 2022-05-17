package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanCard;
import com.trechina.planocycle.entity.po.PriorityOrderJanCard;
import com.trechina.planocycle.service.ClassicPriorityOrderJanCardService;
import com.trechina.planocycle.service.PriorityOrderJanCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/priority/PriorityOrderJanCard")
public class ClassicPriorityOrderJanCardController {

    @Autowired
    private ClassicPriorityOrderJanCardService priorityOrderJanCardService;

    /**
     * 获取card商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanCard")
    public Map<String,Object> getPriorityOrderJanCard (String companyCd,Integer priorityOrderCd) {
        return priorityOrderJanCardService.getPriorityOrderJanCard(companyCd,priorityOrderCd);
    }

    /**
     * 保存card商品list
     * @param priorityOrderJanCard
     * @return
     */
    @PostMapping("/setPriorityOrderJanCard")
    public Map<String,Object> setPriorityOrderJanCard(@RequestBody List<ClassicPriorityOrderJanCard> priorityOrderJanCard){
        return priorityOrderJanCardService.setPriorityOrderJanCard(priorityOrderJanCard);
    }

}
