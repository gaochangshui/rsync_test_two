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
     * 获取商品力点数表基本数据
     * @param taskID
     * @return
     */
    //TODO:获取商品力点数表基本数据
    @GetMapping("/getCommodityScoreData")
    public Map<String,Object> getCommodityScoreData(String taskID) {
        return commodityScoreDataService.getCommodityScoreData(taskID);
    }

    /**
     * 获取商品力点数表基本数据
     * @param productPowerDataForCgiDto
     * @return
     */
    //TODO:获取商品力点数表基本数据
    @PostMapping("/getCommodityScoreTaskId")
    public Map<String,Object> getCommodityScoreTaskId(@RequestBody ProductPowerDataForCgiDto productPowerDataForCgiDto){
        return commodityScoreDataService.getCommodityScoreTaskId(productPowerDataForCgiDto);
    }

    /**
     * 获取商品力点数表Group数据
     * @param taskID
     * @return
     */
    //TODO:获取商品力点数表Group数据
    @GetMapping("/getCommodityScoreGroupData")
    public Map<String,Object> getCommodityScoreGroupData(String taskID) {
        return commodityScoreDataService.getCommodityScoreGroupData(taskID);
    }

    /**
     * 获取商品力点数表Group数据
     * @param productPowerDataForCgiDto
     * @return
     */
    //TODO:获取商品力点数表Group数据
    @PostMapping("/getCommodityScoreGroupTaskId")
    public Map<String,Object> getCommodityScoreGroupTaskId(@RequestBody ProductPowerDataForCgiDto productPowerDataForCgiDto){
        return commodityScoreDataService.getCommodityScoreGroupTaskId(productPowerDataForCgiDto);
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

    /**
     * 商品力点数表编辑
     * @param productPowerDataForCgiDto
     * @return
     */
    @PutMapping("/updateCommodityScoreData")
    public Map<String,Object> updateCommodityScoreData(@RequestBody ProductPowerDataForCgiDto productPowerDataForCgiDto){
        return commodityScoreDataService.updateCommodityScoreData(productPowerDataForCgiDto);
    }

    /**
     * 编辑时获取最终表里的data
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("getDBCommodityScoreData")
    public Map<String,Object> getDBCommodityScoreData(String companyCd ,Integer productPowerCd){
        return commodityScoreDataService.getDBCommodityScoreData(companyCd,productPowerCd);
    }

    /**
     * 获取商品力点数表关联的优先顺位表的个数
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("")
    public Map<String,Object> get(String companyCd ,Integer productPowerCd){
        return null;
    }

}
