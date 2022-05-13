package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.ProductPowerGroupDataForCgiDto;
import com.trechina.planocycle.entity.vo.ShowDataVO;
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
     * @param
     * @return
     */

    @PostMapping("/getCommodityScoreData")
    public Map<String, Object> getCommodityScoreData(@RequestBody Map<String,Object> map) {
        return commodityScoreDataService.getCommodityScoreData(map);
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

    @PostMapping("/getShowCommodityScoreData")
    public Map<String, Object> getCommodityScoreDataFromDB(@RequestBody ShowDataVO showDataVO) {
        return commodityScoreDataService.getCommodityScoreDataFromDB(showDataVO.getProductPowerCd(), showDataVO.getCompanyCd(),
                showDataVO.getPosCd(), showDataVO.getPrepareCd(), showDataVO.getIntageCd(), showDataVO.getCustomerCd());
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
