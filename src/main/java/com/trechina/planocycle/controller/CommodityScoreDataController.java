package com.trechina.planocycle.controller;

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
    public Map<String, Object> getCommodityScoreData(String taskID, String companyCd,String commonPartsData) {
        return commodityScoreDataService.getCommodityScoreData(taskID, companyCd,commonPartsData);
    }

    /**
     * 商品力点数表の基本データの取得
     *
     * @param
     * @return
     */

    @PostMapping("/getCommodityScoreTaskId")
    public Map<String, Object> getCommodityScoreTaskId(@RequestBody Map<String,Object> map) {
        return commodityScoreDataService.getCommodityScoreTaskId(map);
    }

    /**
     * 商品力点数表Groupデータの取得
     *
     * @param taskID
     * @return
     */

    @GetMapping("/getCommodityScoreGroupData")
    @Deprecated
    public Map<String, Object> getCommodityScoreGroupData(String taskID, String companyCd,String commonPartsData) {
        return commodityScoreDataService.getCommodityScoreGroupData(taskID, companyCd,commonPartsData);
    }

    @GetMapping("/getShowCommodityScoreData")
    public Map<String, Object> getCommodityScoreDataFromDB(Integer productPowerCd, String companyCd,String[] posCd, String[] prepareCd, String[] intageCd,
                                                           String[] customerCd) {
        return commodityScoreDataService.getCommodityScoreDataFromDB(productPowerCd, companyCd, posCd, prepareCd, intageCd, customerCd);
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
