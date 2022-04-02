package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.SalesReportService;
import com.trechina.planocycle.service.ShelfNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/SalesReport")
public class SalesReportController {

    @Autowired
    private SalesReportService salesReportService;
    @Autowired
    private ShelfNameService shelfNameService;

    /**
     * 棚名と棚patternのツリー構造の取得
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPatternMaster")
    public Map<String,Object> getShelfPatternMaster(String companyCd){
        return shelfNameService.getShelfPatternMaster(companyCd);
    }

    /**
     * 棚別実績データの取得
     * @param taskID
     * @return
     */
    @GetMapping("/getShelfReportInfo")
    public Map<String,Object> getShelfReportInfo(String taskID){
        return salesReportService.getShelfReportInfo(taskID);
    }

    /**
     * 商品別実績データの取得
     * @param taskID
     * @return
     */
    @GetMapping("/getProductReportInfo")
    public Map<String,Object> getProductReportInfo(String taskID){
        return salesReportService.getProductReportInfo(taskID);
    }

    /**
     * 単一janの他のデータの取得
     * @param taskID
     * @param startDay
     * @param endDay
     * @return
     */
    @GetMapping("/getJanReportDetailInfo")
    public Map<String,Object> getJanReportDetailInfo(String taskID,String startDay,String endDay){
        return salesReportService.getJanReportDetailInfo(taskID,startDay,endDay);
    }
}
