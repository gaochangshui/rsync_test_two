package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.BasicAllPatternMstService;
import com.trechina.planocycle.service.PriorityAllMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityAllMst")
public class PriorityAllMstController {
    @Autowired
    private HttpSession session;

    @Autowired
    private PriorityAllMstService priorityAllMstService;

    @Autowired
    private BasicAllPatternMstService basicAllPatternMstService;


    @PostMapping("/addPriorityAllData")
    public Map<String, Object> addPriorityAllData(@RequestBody JSONObject jsonObject) {
        return priorityAllMstService.addPriorityAllData(jsonObject);
    }

    /**
     * 基本パターンのList　api①
     * @param companyCd
     * @return
     */
    @GetMapping("/getPriorityOrderList")
    public Map<String, Object> getPriorityOrderList(String companyCd){
        return priorityAllMstService.getPriorityOrderList(companyCd);
    }

    /**
     * 選択された基本パターンにより基本パターンの連携棚パターン、そして同じ棚名称したの棚パターンListを取得する　api②
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getAllPatternData")
    public Map<String, Object> getAllPatternData(String companyCd, Integer priorityAllCd, Integer priorityOrderCd){
        return priorityAllMstService.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd);
    }

    /**
     * 自動計算Main api③
     * @param priorityAllSaveDto
     * @return
     */
    @PostMapping("/autoCalculation")
    public Map<String, Object> autoCalculation(@RequestBody PriorityAllSaveDto priorityAllSaveDto){
        // 自動計算する前に選択された基本パターン、全パターンを一時テーブルに保存
        if (basicAllPatternMstService.saveWKAllPatternData(priorityAllSaveDto) == 0) {
            // 一時テーブルデータにより自動計算を行う
            return basicAllPatternMstService.autoCalculation(priorityAllSaveDto);
        } else {
            return ResultMaps.result(ResultEnum.FAILURE, "保存で失敗しました。");
        }
    }

    /**
     * 表示自動計算実行ステータス
     * @param taskId
     * @return
     */
    @GetMapping("/returnAutoCalculationState")
    public Map<String, Object> returnAutoCalculationState(String taskId){
        return priorityAllMstService.returnAutoCalculationState(taskId);
    }



    /**
     * PTS詳細 api④
     * @param companyCd
     * @param priorityAllCd
     * @param patternCd
     * @return
     */
    @GetMapping("/getPriorityPtsInfo")
    public Map<String, Object> getPriorityPtsInfo(String companyCd, Integer priorityAllCd, Integer patternCd){
        return priorityAllMstService.getPriorityPtsInfo(companyCd, priorityAllCd, patternCd);
    }


    /**
     * 保存＆更新 api⑦
     * @param priorityAllSaveDto
     * @return
     */
    @PostMapping("/savePriorityAllData")
    public Map<String, Object> savePriorityAllData(@RequestBody PriorityAllSaveDto priorityAllSaveDto){
        return priorityAllMstService.savePriorityAll(priorityAllSaveDto);
    }


    /**
     * 削除 api⑦
     * @param priorityAllSaveDto
     * @return
     */
    @DeleteMapping("/deletePriorityAll")
    public Map<String, Object> deletePriorityAll(@RequestBody  PriorityAllSaveDto priorityAllSaveDto){
        return priorityAllMstService.deletePriorityAll(priorityAllSaveDto);
    }



}
