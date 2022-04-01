package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityOrderDataForCgiDto;
import com.trechina.planocycle.service.PriorityOrderDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderData")
public class PriorityOrderDataController {

    @Autowired
    private PriorityOrderDataService priorityOrderDataService;

    /**
     * 優先順位表データの取得
     * @param priorityOrderDataForCgiDto
     * @return
     */
    @PostMapping("/getPriorityOrderData")
    public Map<String,Object> getPriorityOrderData(@RequestBody PriorityOrderDataForCgiDto priorityOrderDataForCgiDto){
        return priorityOrderDataService.getPriorityOrderData(priorityOrderDataForCgiDto);
    }

    /**
     * rank属性ソート+優先順位表反応ボタン抽出データ
     * @param colNameList
     * @return
     */
    @GetMapping("/getPriorityOrderDataUpd")
    public Map<String,Object> getPriorityOrderDataUpd(@RequestParam List<String> colNameList,Integer priorityOrderCd){
        return priorityOrderDataService.getPriorityOrderDataUpd(colNameList,priorityOrderCd);
    }

    /**
     * 属性列名の取得
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getAttrName")
    public List<Map<String,Object>> getAttrName(Integer productPowerCd){
        return priorityOrderDataService.getAttrName(productPowerCd);
    }


}
