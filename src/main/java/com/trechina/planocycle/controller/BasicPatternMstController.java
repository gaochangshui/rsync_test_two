package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import com.trechina.planocycle.service.BasicPatternMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("planoCycle/BasicPatternMst")
public class BasicPatternMstController {
    @Autowired
    private BasicPatternMstService basicPatternMstService;

    /**
     * じどうけんさ
     * @param basePatternAutoDetectVO
     * @return
     */
    @PostMapping("/autoDetect")
    public Map<String, Object> autoDetect(@RequestBody BasicPatternAutoDetectVO basePatternAutoDetectVO){
        return basicPatternMstService.autoDetect(basePatternAutoDetectVO);
    }

    /**
     * group展示
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getAttrDisplay")
    public Map<String,Object> getAttrDisplay(String companyCd,Integer priorityOrderCd) {
        return basicPatternMstService.getAttrDisplay(companyCd,priorityOrderCd);
    }

    /**
     * group保存
     * @param basicPatternRestrictRelation
     * @return
     */
    @PostMapping("/setAttrDisplay")
    public Map<String,Object> setAttrDisplay(@RequestBody BasicPatternRestrictRelation basicPatternRestrictRelation) {
        return basicPatternMstService.setAttrDisplay(basicPatternRestrictRelation);
    }

    /**
     * グループ化されていない端末があるかどうかを確認する
     * @param companyCd
     * @param patternCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/preCalculation")
    public Map<String, Object> preCalculation(String companyCd, Long patternCd,Integer priorityOrderCd ) {
        return basicPatternMstService.preCalculation(companyCd, patternCd,priorityOrderCd);
    }

    /**
     * 自動計算
     * @return
     */
    @GetMapping("/autoCalculation")
    public Map<String,Object> autoCalculation(String companyCd,Integer priorityOrderCd,Integer partition,
                                              Integer heightSpace, Integer tanaWidthCheck){
        return basicPatternMstService.autoCalculation(companyCd,priorityOrderCd,partition, heightSpace, tanaWidthCheck);
    }

    /**
     * 表示自動計算実行ステータス
     * @param taskId
     * @return
     */
    @GetMapping("/autoTaskId")
    public Map<String, Object> autoTaskId(String taskId){
        return basicPatternMstService.autoTaskId(taskId);
    }


}
