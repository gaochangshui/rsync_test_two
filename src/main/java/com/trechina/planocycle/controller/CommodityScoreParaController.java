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
     * 获取表示项目的参数
     *
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    //TODO:表示项目设定：需要返回 顧客セグメント
    @GetMapping("/getCommodityScorePara")
    public Map<String, Object> getCommodityScorePara(String conpanyCd, Integer productPowerCd) {
        return commodityScoreParaService.getCommodityScorePara(conpanyCd, productPowerCd);
    }

    /**
     * 保存s1期间、表示项目、预备项目、weight所有参数,删除预备项目,基本pos数据
     *
     * @param
     * @return
     */
    //TODO:表示项目设定：需要添加 顧客セグメント
    @PostMapping("/setCommodityScorePara")
    public Map<String,Object> setCommodityScorePara(@RequestBody ProductPowerParam productPowerParam){
        return commodityScoreParaService.setCommodityScorePare(productPowerParam);
    }

    /**
     * 获取weight参数
     *
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getCommodityScoreWeight")
    public Map<String, Object> getCommodityScoreWeight(String conpanyCd, Integer productPowerCd) {
        return commodityScoreParaService.getCommodityScoreWeight(conpanyCd, productPowerCd);
    }

    /**
     * 获取表示项目的预备项目参数
     *
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getCommodityScorePrePara")
    public Map<String, Object> getCommodityScorePrePara(String conpanyCd, Integer productPowerCd) {
        return commodityScoreParaService.getCommodityScorePrePara(conpanyCd, productPowerCd);
    }

    /**
     * 删除商品力点数表所有信息
     *
     * @param primaryKeyVO
     * @return
     */
    @DeleteMapping("/delCommodityScoreAllInfo")
    public Map<String, Object> delCommodityScoreAllInfo(@RequestBody ProductPowerPrimaryKeyVO primaryKeyVO) {
        return commodityScoreParaService.delCommodityScoreAllInfo(primaryKeyVO);
    }

    /**
     * 预备项目上传保存到数据库
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
     * 调用cgi删除预备项目
     *
     * @param productPowerReserveMst
     * @return
     */
    @PostMapping("/delYoBi")
    public Map<String, Object> delYoBi(@RequestBody ProductPowerReserveMst productPowerReserveMst) {
        return commodityScoreParaService.delYoBi(productPowerReserveMst);
    }

    /**
     * 计算rank
     */
    @PostMapping("rankCalculate")
    public Map<String,Object> rankCalculate(@RequestBody RankCalculateVo rankCalculateVo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return commodityScoreParaService.rankCalculate(rankCalculateVo);
    }


    /**
     * 预备项目删除
     */

    @DeleteMapping("deleteReserve")
    public Map<String,Object> deleteReserve(@RequestBody JSONObject jsonObject)  {

        return commodityScoreParaService.deleteReserve(jsonObject);
    }


}
