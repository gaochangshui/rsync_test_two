package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.ProductPowerReserveMst;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;
import com.trechina.planocycle.entity.vo.RankCalculateVo;
import com.trechina.planocycle.service.CommodityScoreParaService;
import com.trechina.planocycle.service.FilesOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/CommodityScorePara")
public class CommodityScoreParaController {
    @Autowired
    private CommodityScoreParaService commodityScoreParaService;
    @Autowired
    private FilesOperationService filesOperationService;

    /**
     * アイテムを表すパラメータの取得
     *
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getCommodityScorePara")
    public Map<String, Object> getCommodityScorePara(String conpanyCd, Integer productPowerCd) {
        return commodityScoreParaService.getCommodityScorePara(conpanyCd, productPowerCd);
    }

    /**
     * s 1期間、表示項目、予備項目、weightのすべてのパラメータを保存し、予備項目、基本posデータを削除する
     *
     * @param
     * @return
     */
    @PostMapping("/setCommodityScorePara")
    public Map<String,Object> setCommodityScorePara(@RequestBody ProductPowerParam productPowerParam){
        return commodityScoreParaService.setCommodityScorePare(productPowerParam);
    }



    /**
     * 商品力点表のすべての情報を削除
     *
     * @param primaryKeyVO
     * @return
     */
    @DeleteMapping("/delCommodityScoreAllInfo")
    public Map<String, Object> delCommodityScoreAllInfo(@RequestBody ProductPowerPrimaryKeyVO primaryKeyVO) {
        return commodityScoreParaService.delCommodityScoreAllInfo(primaryKeyVO);
    }

    /**
     * プロジェクトのアップロードをデータベースに保存する
     * @param multipartFile
     * @param companyCd
     * @param dataCd
     * @param dataName
     * @param valueCd
     * @return
     */
    @PostMapping("/yobi")
    public Map<String, Object> updateYobi(@RequestParam(value = "file",required = false) MultipartFile multipartFile, @RequestParam("companyCd") String companyCd, @RequestParam("dataCd") String dataCd, @RequestParam("dataName")String dataName, @RequestParam("valueCd")Integer valueCd) {
        List<String[]> csvData = filesOperationService.uploadCsvToList(multipartFile);
        return commodityScoreParaService.saveYoBi(csvData, companyCd, dataCd,dataName,valueCd);
    }

    /**
     * cgiを呼び出して予備項目を削除
     *
     * @param productPowerReserveMst
     * @return
     */
    @PostMapping("/delYoBi")
    public Map<String, Object> delYoBi(@RequestBody ProductPowerReserveMst productPowerReserveMst) {
        return commodityScoreParaService.delYoBi(productPowerReserveMst);
    }

    /**
     * 計算rank
     */
    @PostMapping("rankCalculate")
    public Map<String,Object> rankCalculate(@RequestBody RankCalculateVo rankCalculateVo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return commodityScoreParaService.rankCalculate(rankCalculateVo);
    }


    /**
     * プロジェクト削除の準備
     */

    @DeleteMapping("deleteReserve")
    public Map<String,Object> deleteReserve(@RequestBody JSONObject jsonObject)  {

        return commodityScoreParaService.deleteReserve(jsonObject);
    }


}
