package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;
import com.trechina.planocycle.service.CommodityScoreParaService;
import com.trechina.planocycle.service.FilesOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/CommodityScorePara")
public class CommodityScoreParaController {
    @Autowired
    private CommodityScoreParaService commodityScoreParaService;
    @Autowired
    private FilesOperationService filesOperationService;


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
     * @param productPowerCd
     * @param dataName
     * @param valueCd
     * @return
     */
    @PostMapping("/yobi")
    public Map<String, Object> updateYobi(@RequestParam(value = "file",required = false) MultipartFile multipartFile, @RequestParam("companyCd") String companyCd, @RequestParam("dataCd") Integer productPowerCd, @RequestParam("dataName")String dataName, @RequestParam("valueCd")Integer valueCd) {
        List<String[]> csvData = filesOperationService.uploadCsvToList(multipartFile);
        return commodityScoreParaService.saveYoBi(csvData, companyCd, productPowerCd,dataName,valueCd);
    }


    /**
     * 計算rank
     */
    @PostMapping("rankCalculate")
    public Map<String,Object> rankCalculate(@RequestBody Map<String,Object> map) {

        return commodityScoreParaService.rankCalculate(map);
    }

    /**
     *
     */
    @GetMapping("rankCalculateForTaskId")
    public Map<String,Object> rankCalculateForTaskId(String taskID) {

        return commodityScoreParaService.rankCalculateForTaskId(taskID);
    }

    /**
     * プロジェクト削除の準備
     */

    @DeleteMapping("deleteReserve")
    public Map<String,Object> deleteReserve(@RequestBody JSONObject jsonObject)  {

        return commodityScoreParaService.deleteReserve(jsonObject);
    }


}
