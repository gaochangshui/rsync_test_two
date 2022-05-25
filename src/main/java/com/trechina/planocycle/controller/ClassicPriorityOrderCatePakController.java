package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.service.ClassicPriorityOrderCatePakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priority/PriorityOrderCatePak")
public class ClassicPriorityOrderCatePakController {
    @Autowired
    private ClassicPriorityOrderCatePakService priorityOrderCatePakService;

    /**
     * 获取カテパケ拡縮
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderCatePak")
    public Map<String,Object> getPriorityOrderCatePak(String companyCd, Integer priorityOrderCd){
        return priorityOrderCatePakService.getPriorityOrderCatePak(companyCd,priorityOrderCd);
    }

    /**
     * 保存カテパケ拡縮
     * @param jsonArray
     * @return
     */
    @PostMapping("/setPriorityOrderCatePak")
    public Map<String,Object> setPriorityOrderCatePak(@RequestBody JSONArray jsonArray){
        return priorityOrderCatePakService.setPriorityOrderCatePak(jsonArray);
    }
}
