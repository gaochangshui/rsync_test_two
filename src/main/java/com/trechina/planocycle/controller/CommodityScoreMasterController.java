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
     * 获取企业信息
     * @return
     */
    @GetMapping("/getEnterpriseInfo")
    public Map<String,Object> getEnterpriseInfo(){
        return commodityScoreMasterService.getEnterpriseInfo();
    }
    /**
     * 获取企业cd关联的商品力点数List
     * @param conpanyCd
     * @return
     */
    @GetMapping("/getCommodityListInfo")
    public Map<String,Object> getCommodityListInfo(String conpanyCd) { return commodityScoreMasterService.getCommodityListInfo(conpanyCd);}
    /**
     * 获取商品力点数的参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getCommodityParam")
    public Map<String,Object> getCommodityParam(String conpanyCd, Integer productPowerCd){
        return commodityScoreMasterService.getCommodityParam(conpanyCd,productPowerCd);
    }

    /**
     * 保存商品力点数表的模板名
     * @param productPowerName
     * @return
     */
    @PutMapping ("/setCommodityList")
    public Map<String,Object> setCommodityList(@RequestBody ProductCdAndNameDto productPowerName) {
        return commodityScoreMasterService.setCommodityList(productPowerName);
    }
    /**
     * 保存商品力点数的参数
     * @param productPowerParamMst
     * @return
     */
    @PutMapping("/setCommodityParam")
    public Map<String,Object> setCommodityParam(@RequestBody ProductPowerParamMst productPowerParamMst) {
        return commodityScoreMasterService.setCommodityParam(productPowerParamMst);
    }

    /**
     * 获取Chanel信息
     * @return
     */
    @GetMapping("/getChanelInfo")
    public Map<String,Object> getChanelInfo() {
        return commodityScoreMasterService.getChanelInfo();
    }

    /**
     * 获取都道府県
     * @return
     */
    @GetMapping("/getPrefectureInfo")
    public Map<String,Object> getPrefectureInfo() {
        return commodityScoreMasterService.getPrefectureInfo();
    }

    /**
     * 如果参数有改动，就删除这个模板的参数
     * @param productPowerParamMst
     * @return
     */
    @PostMapping("/delCommodityParam")
    public Map<String,Object> delCommodityParam(@RequestBody ProductPowerParamMst productPowerParamMst){
        return commodityScoreMasterService.delCommodityParam(productPowerParamMst);
    }

    /**
     * 编辑返回所有参数
     * @param companyCd
     * @param productPowerNo
     * @return
     */

    @GetMapping("getAllDataOrParam")
    public Map<String,Object> getAllDataOrParam(String companyCd,Integer productPowerNo){
        return commodityScoreMasterService.getAllDataOrParam(companyCd,productPowerNo);
    }
}
