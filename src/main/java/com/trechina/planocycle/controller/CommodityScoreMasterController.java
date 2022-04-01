package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.ProductCdAndNameDto;
import com.trechina.planocycle.entity.po.ProductPowerParamMst;
import com.trechina.planocycle.service.CommodityScoreMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/ShelfPatternMst")
public class CommodityScoreMasterController {
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    /**
     * 企業情報の取得
     * @return
     */
    @GetMapping("/getEnterpriseInfo")
    public Map<String,Object> getEnterpriseInfo(){
        return commodityScoreMasterService.getEnterpriseInfo();
    }
    /**
     * 企業cd関連商品力点数リスト取得
     * @param conpanyCd
     * @return
     */
    @GetMapping("/getCommodityListInfo")
    public Map<String,Object> getCommodityListInfo(String conpanyCd) { return commodityScoreMasterService.getCommodityListInfo(conpanyCd);}
    /**
     * 商品力点数のパラメータを取得する
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getCommodityParam")
    public Map<String,Object> getCommodityParam(String conpanyCd, Integer productPowerCd){
        return commodityScoreMasterService.getCommodityParam(conpanyCd,productPowerCd);
    }

    /**
     * 商品力点数表のテンプレート名を保存する
     * @param productPowerName
     * @return
     */
    @PutMapping ("/setCommodityList")
    public Map<String,Object> setCommodityList(@RequestBody ProductCdAndNameDto productPowerName) {
        return commodityScoreMasterService.setCommodityList(productPowerName);
    }
    /**
     * 商品力点数のパラメータを保存
     * @param productPowerParamMst
     * @return
     */
    @PutMapping("/setCommodityParam")
    public Map<String,Object> setCommodityParam(@RequestBody ProductPowerParamMst productPowerParamMst) {
        return commodityScoreMasterService.setCommodityParam(productPowerParamMst);
    }

    /**
     * Chanel情報の取得
     * @return
     */
    @GetMapping("/getChanelInfo")
    public Map<String,Object> getChanelInfo() {
        return commodityScoreMasterService.getChanelInfo();
    }

    /**
     * 取得都道府県
     * @return
     */
    @GetMapping("/getPrefectureInfo")
    public Map<String,Object> getPrefectureInfo() {
        return commodityScoreMasterService.getPrefectureInfo();
    }

    /**
     * パラメータが変更された場合は、このテンプレートのパラメータを削除します。
     * @param productPowerParamMst
     * @return
     */
    @PostMapping("/delCommodityParam")
    public Map<String,Object> delCommodityParam(@RequestBody ProductPowerParamMst productPowerParamMst){
        return commodityScoreMasterService.delCommodityParam(productPowerParamMst);
    }

    /**
     * 編集はすべてのパラメータを返します
     * @param companyCd
     * @param productPowerNo
     * @return
     */

    @GetMapping("getAllDataOrParam")
    public Map<String,Object> getAllDataOrParam(String companyCd,Integer productPowerNo){
        return commodityScoreMasterService.getAllDataOrParam(companyCd,productPowerNo);
    }
}
