package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.ProductPowerVO;
import com.trechina.planocycle.service.ProductPowerMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/ProductPowerMst")
public class ProductPowerMstController {
    @Autowired
    private ProductPowerMstService powerMstService;
    /**
     * 企業cdによる商品力点数表一覧の取得
     * @param companyCd
     * @return
     */
    @GetMapping("/getTableName")
    public Map<String,Object> getTableName(String companyCd){
        return powerMstService.getTableName(companyCd);
    }

    /**
     *mst基本情報の取得
     * @param companyCd
     * @return
     */
    @GetMapping("/getProductPowerTable")
    public Map<String,Object> getProductPowerTable(String companyCd){
        return powerMstService.getProductPowerTable(companyCd);
    }

    /**
     * 商品力点数表一覧データの取得
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("getProductPowerInfo")
    public Map<String,Object> getProductPowerInfo(String companyCd ,Integer productPowerCd,Integer priorityOrderCd){
        return powerMstService.getProductPowerInfo(companyCd,productPowerCd,priorityOrderCd);
    }


    /**
     * 商品力点数表データを取得excel download
     * @return
     */
    @PostMapping("downloadProductPowerInfo")
    public void downloadProductPowerInfo(@RequestBody ProductPowerVO productPowerVO, HttpServletResponse response){
        powerMstService.downloadProductPowerInfo(productPowerVO.getCompanyCd(),productPowerVO.getProductPowerNo(),response);
    }
}
