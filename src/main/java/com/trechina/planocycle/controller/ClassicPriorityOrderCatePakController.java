package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.service.ClassicPriorityOrderCatePakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/priority/PriorityOrderCatePak")
public class ClassicPriorityOrderCatePakController {
    @Autowired
    private ClassicPriorityOrderCatePakService priorityOrderCatePakService;

    /**
     * つかむ取カテパケ拡縮
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


    /**
     * 分類によって商品の力点数表を除いて同類の商品を抽出する
     * @param
     * @return
     */
    @PostMapping("getCatePakSimilarity")
    public Map<String, Object> getCatePakSimilarity(@RequestBody Map<String, Object> map) {
        return priorityOrderCatePakService.getCatePakSimilarity(map);
    }
}
