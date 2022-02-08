package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderJanNew")
public class PriorityOrderJanNewController {
    @Autowired
    private PriorityOrderJanNewService priorityOrderJanNewService;

    /**
     * 获取新规商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanNew")
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd,Integer productPowerNo) {
        return priorityOrderJanNewService.getPriorityOrderJanNew(companyCd,priorityOrderCd,productPowerNo);
    }

    /**
     * 保存新规商品list
     * @param jsonArray
     * @return
     */
    @PostMapping("/setPriorityOrderJanNew")
    public Map<String,Object> setPriorityOrderJanNew(@RequestBody JSONArray jsonArray){
        return priorityOrderJanNewService.setPriorityOrderJanNew(jsonArray);
    }
}
