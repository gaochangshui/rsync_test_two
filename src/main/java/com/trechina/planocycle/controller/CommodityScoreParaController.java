package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.CommodityScorePara;
import com.trechina.planocycle.entity.po.ProductPowerReserveMst;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;
import com.trechina.planocycle.service.CommodityScoreParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/CommodityScorePara")
public class CommodityScoreParaController {
    @Autowired
    private CommodityScoreParaService commodityScoreParaService;
    /**
     * 获取表示项目的参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    //TODO:表示项目设定：需要返回 顧客セグメント
    @GetMapping("/getCommodityScorePara")
    public Map<String,Object> getCommodityScorePara(String conpanyCd, Integer productPowerCd){
        return commodityScoreParaService.getCommodityScorePara(conpanyCd,productPowerCd);
    }

    /**
     * 保存s1期间、表示项目、预备项目、weight所有参数,删除预备项目
     * @param commodityScorePara
     * @return
     */
    //TODO:表示项目设定：需要添加 顧客セグメント
    @PostMapping("/setCommodityScorePara")
    public Map<String,Object> setCommodityScorePara(@RequestBody CommodityScorePara commodityScorePara){
        return commodityScoreParaService.setCommodityScorePare(commodityScorePara);
    }

    /**
     * 获取weight参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getCommodityScoreWeight")
    public Map<String,Object> getCommodityScoreWeight(String conpanyCd, Integer productPowerCd) {
        return commodityScoreParaService.getCommodityScoreWeight(conpanyCd,productPowerCd);
    }

    /**
     * 获取表示项目的预备项目参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getCommodityScorePrePara")
    public Map<String,Object> getCommodityScorePrePara(String conpanyCd, Integer productPowerCd){
        return commodityScoreParaService.getCommodityScorePrePara(conpanyCd,productPowerCd);
    }

    /**
     * 删除商品力点数表所有信息
     * @param primaryKeyVO
     * @return
     */
    @DeleteMapping("/delCommodityScoreAllInfo")
    public Map<String,Object> delCommodityScoreAllInfo(@RequestBody ProductPowerPrimaryKeyVO primaryKeyVO){
        return commodityScoreParaService.delCommodityScoreAllInfo(primaryKeyVO);
    }

    /**
     * 调用cgi删除预备项目
     * @param productPowerReserveMst
     * @return
     */
    @PostMapping("/delYoBi")
    public Map<String,Object> delYoBi(@RequestBody ProductPowerReserveMst productPowerReserveMst){
        return commodityScoreParaService.delYoBi(productPowerReserveMst);
    }

}
