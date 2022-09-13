package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.ShowDataVO;
import com.trechina.planocycle.service.CommodityScoreDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/CommodityScoreData")
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
    public Map<String, Object> getCommodityScoreData(@RequestBody Map<String,Object> map) throws InterruptedException {
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
     * commonはプロジェクトインタフェースの取得を示します。
     * @param showDataVO
     * @return
     */
    @PostMapping("/getShowCommodityScoreData")
    public Map<String, Object> getCommodityScoreDataFromDB(@RequestBody ShowDataVO showDataVO) {
        return commodityScoreDataService.getCommodityScoreDataFromDB(showDataVO.getProductPowerCd(), showDataVO.getCompanyCd(),
                showDataVO.getPosCd(), showDataVO.getPrepareCd(), showDataVO.getIntageCd(), showDataVO.getCustomerCd());
    }


    /**
     * 商品属性value
     */
    @GetMapping("/getJanAttrValueList")
    public Map<String, Object> getJanAttrValueList(String attrList) {
        return commodityScoreDataService.getJanAttrValueList(attrList);
    }

}
