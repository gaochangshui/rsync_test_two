package com.trechina.planocycle.controller;

import com.google.gson.Gson;
import com.trechina.planocycle.entity.vo.ShowDataVO;
import com.trechina.planocycle.service.CommodityScoreDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/CommodityScoreData")
public class CommodityScoreDataController {
    @Autowired
    private CommodityScoreDataService commodityScoreDataService;
    @Autowired
    private HttpSession session;

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
    public Map<String, Object> getCommodityScoreTaskId(@RequestBody Map<String,Object> map)  {
        //smtデータソースを教える
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get("company").toString();
        Integer productPowerCd = Integer.valueOf(map.get("productPowerNo").toString());
        //param
        String customerConditionStr = map.get("customerCondition").toString();
        String prodAttrData = new Gson().toJson(map.get("prodAttrData"));
        String singleJan = new Gson().toJson(map.get("singleJan"));
        String level = new Gson().toJson(map.get("level"));

        commodityScoreDataService.setProductParam(map,productPowerCd,companyCd,authorCd,customerConditionStr,prodAttrData,singleJan,level);
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
