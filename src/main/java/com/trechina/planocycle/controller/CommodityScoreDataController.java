package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.service.CommodityScoreDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/CommodityScoreData")
public class CommodityScoreDataController {
    @Autowired
    private CommodityScoreDataService commodityScoreDataService;

    /**
     * 获取商品力点数表数据
     * @param taskID
     * @return
     */
    @GetMapping("/getCommodityScoreData")
    public Map<String,Object> getCommodityScoreData(String taskID) {
        return commodityScoreDataService.getCommodityScoreData(taskID);
    }

    @PostMapping("/getCommodityScoreTaskId")
    public Map<String,Object> getCommodityScoreTaskId(@RequestBody ProductPowerDataForCgiDto productPowerDataForCgiDto){
        return commodityScoreDataService.getCommodityScoreTaskId(productPowerDataForCgiDto);
    }

    /**
     *
     * @param companyCd
     * @param filename
     * @param datacd
     * @param productPowerNo
     * @param dataNm
     * @return
     */
    @GetMapping("/getAttrFileSaveForCgi")
    public Map<String,Object> getAttrFileSaveForCgi(String companyCd,String filename,String datacd,
                                                    Integer productPowerNo,String dataNm){
        return commodityScoreDataService.getAttrFileSaveForCgi(companyCd,filename,datacd,productPowerNo,dataNm);
    }

}
