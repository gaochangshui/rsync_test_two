package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.IDGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/IDGenerator")
public class IDGeneratorController {
    @Autowired
    private IDGeneratorService idGeneratorService;

    /**
     * 基本pattern自動番号付け
     * @return
     */
    @GetMapping("/getPriorityOrderID")
    public Map<String,Object> getPriorityOrderID(){
        return idGeneratorService.priorityOrderNumGenerator();
    }

    /**
     * ゆうせんじゅんいひょう自動番号付け
     * @return
     */
    @GetMapping("/getClassicPriorityOrderID")
    public Map<String,Object> getClassicPriorityOrderID(){
        return idGeneratorService.classPriorityOrderNumGenerator();
    }
    /**
     * 商品力点数表自動番号取り
     * @return
     */
    @GetMapping("")
    public Map<String,Object> getProductPowerID(){
        return idGeneratorService.productPowerNumGenerator();
    }

    /**
     * 全pattern自動取號
     * @return
     */
    @GetMapping("/getPriorityAllID")
    public Map<String,Object> priorityAllID(){
        return idGeneratorService.priorityAllID();
    }

}
