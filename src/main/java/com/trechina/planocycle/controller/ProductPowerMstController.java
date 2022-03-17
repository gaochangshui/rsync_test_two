package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.ProductPowerMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/ProductPowerMst")
public class ProductPowerMstController {
    @Autowired
    private ProductPowerMstService powerMstService;
    /**
     * 根据企业cd获取商品力点数表一览
     * @param companyCd
     * @return
     */
    @GetMapping("/getTableName")
    public Map<String,Object> getTableName(String companyCd){
        return powerMstService.getTableName(companyCd);
    }

    @GetMapping("/getProductPowerTable")
    public Map<String,Object> getProductPowerTable(String companyCd){
        return powerMstService.getProductPowerTable(companyCd);
    }

    /**
     * 获取商品力点数表一览数据
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("getProductPowerInfo")
    public Map<String,Object> getProductPowerInfo(String companyCd ,Integer productPowerCd,Integer priorityOrderCd){
        return powerMstService.getProductPowerInfo(companyCd,productPowerCd,priorityOrderCd);
    }
}
