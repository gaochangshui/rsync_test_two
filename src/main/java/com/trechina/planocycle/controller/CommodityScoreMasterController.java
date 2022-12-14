package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.ProductCdAndNameDto;
import com.trechina.planocycle.service.CommodityScoreMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/ShelfPatternMst")
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
     * 商品力点数表のテンプレート名を保存する
     * @param productPowerName
     * @return
     */
    @PostMapping ("/setCommodityList")
    public Map<String,Object> setCommodityList(@RequestBody ProductCdAndNameDto productPowerName) {
        return commodityScoreMasterService.setCommodityList(productPowerName);
    }



    /**
     * 編集はすべてのパラメータを返します
     * @param companyCd
     * @param productPowerNo
     * @return
     */

    @GetMapping("getAllDataOrParam")
    public Map<String,Object> getAllDataOrParam(String companyCd,Integer productPowerNo,@RequestParam(required = false) Integer isCover){
        return commodityScoreMasterService.getAllDataOrParam(companyCd,productPowerNo,isCover);
    }

    /**
     * つかむ取都道府県&Chanel情報
     * @return
     */
    @GetMapping("/getPrefectureAndChanelInfo")
    public Map<String,Object> getPrefectureAndChanelInfo() {
        return commodityScoreMasterService.getPrefectureAndChanelInfo();
    }

}
