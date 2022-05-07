package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.dto.ProductPowerGroupDataForCgiDto;
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
     * 商品力点数表の基本データの取得
     *
     * @param taskID
     * @return
     */

    @GetMapping("/getCommodityScoreData")
    public Map<String, Object> getCommodityScoreData(String taskID, String companyCd) {
        return commodityScoreDataService.getCommodityScoreData(taskID, companyCd);
    }

    /**
     * 商品力点数表の基本データの取得
     *
     * @param productPowerDataForCgiDto
     * @return
     */

    @PostMapping("/getCommodityScoreTaskId")
    public Map<String, Object> getCommodityScoreTaskId(@RequestBody ProductPowerDataForCgiDto productPowerDataForCgiDto) {
        return commodityScoreDataService.getCommodityScoreTaskId(productPowerDataForCgiDto);
    }

    /**
     * 商品力点数表Groupデータの取得
     *
     * @param taskID
     * @return
     */

    @GetMapping("/getCommodityScoreGroupData")
    public Map<String, Object> getCommodityScoreGroupData(String taskID, String companyCd) {
        return commodityScoreDataService.getCommodityScoreGroupData(taskID, companyCd);
    }

    /**
     * 商品力点数表Groupデータの取得
     *
     * @param productPowerDataForCgiDto
     * @return
     */

    @PostMapping("/getCommodityScoreGroupTaskId")
    public Map<String, Object> getCommodityScoreGroupTaskId(@RequestBody ProductPowerGroupDataForCgiDto productPowerDataForCgiDto) {
        return commodityScoreDataService.getCommodityScoreGroupTaskId(productPowerDataForCgiDto);
    }



}
