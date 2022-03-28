package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.IDGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/IDGenerator")
public class IDGeneratorController {
    @Autowired
    private IDGeneratorService idGeneratorService;

    /**
     * 优先顺位表自动取号
     * @return
     */
    @GetMapping("/getPriorityOrderID")
    public Map<String,Object> getPriorityOrderID(){
        return idGeneratorService.priorityOrderNumGenerator();
    }

    /**
     * 商品力点数表自动取号
     * @return
     */
    @GetMapping("")
    public Map<String,Object> getProductPowerID(){
        return idGeneratorService.productPowerNumGenerator();
    }


    @GetMapping("/getPriorityAllID")
    public Map<String,Object> PriorityAllID(){
        return idGeneratorService.priorityAllID();
    }
}
