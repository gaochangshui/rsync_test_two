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

    /**
     * @param companyCd
     * @param filename
     * @param datacd
     * @param productPowerNo
     * @param dataNm
     * @return
     */
    @GetMapping("/getAttrFileSaveForCgi")
    public Map<String, Object> getAttrFileSaveForCgi(String companyCd, String filename, String datacd,
                                                     Integer productPowerNo, String dataNm) {
        return commodityScoreDataService.getAttrFileSaveForCgi(companyCd, filename, datacd, productPowerNo, dataNm);
    }


    /**
     * 編集時に最終テーブルのdataを取得する
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("getDBCommodityScoreData")
    public Map<String, Object> getDBCommodityScoreData(String companyCd, Integer productPowerCd) {
        return commodityScoreDataService.getDBCommodityScoreData(companyCd, productPowerCd);
    }

    /**
     * 商品力ポイントテーブルに関連付けられた優先順位テーブルの個数を取得する
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("")
    public Map<String, Object> get(String companyCd, Integer productPowerCd) {
        return null;
    }

}
