package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.service.ClassicPriorityOrderJanNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priority/PriorityOrderJanNew")
public class ClassicPriorityOrderJanNewController {
    @Autowired
    private ClassicPriorityOrderJanNewService priorityOrderJanNewService;

    /**
     * 新規商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanNew")
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanNewService.getPriorityOrderJanNew(companyCd,priorityOrderCd);
    }

    /**
     * 新規商品リストの保存
     * @param jsonArray
     * @return
     */
    @PostMapping("/setPriorityOrderJanNew")
    public Map<String,Object> setPriorityOrderJanNew(@RequestBody JSONArray jsonArray){
        return priorityOrderJanNewService.setPriorityOrderJanNew(jsonArray);
    }
}
