package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.BasicAllPatternMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("planoCycle/BasicAllPatternMst")
public class BasicAllPatternMstController {
    @Autowired
    private BasicAllPatternMstService basicAllPatternMstService;

    /**
     * 自動計算
     * @return
     */

    @PostMapping("/autoCalculation")
    public Map<String,Object> autoCalculation(@RequestBody PriorityAllSaveDto priorityAllSaveDto){
        // 自動計算する前に選択された基本パターン、全パターンを一時テーブルに保存
        if (basicAllPatternMstService.saveWKAllPatternData(priorityAllSaveDto) == 0) {
            // 一時テーブルデータにより自動計算を行う
            return basicAllPatternMstService.autoCalculation(priorityAllSaveDto);
        } else {
            return ResultMaps.result(ResultEnum.FAILURE, "保存で失敗しました。");
        }
    }
}
