package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.PriorityOrderMstVO;
import com.trechina.planocycle.service.PriorityOrderMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
@RestController
@RequestMapping("/planoCycle/PriorityOrderMst")
public class PriorityOrderMstController {
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Value("${ptsDownPath}")
    String ptsDownPath;

    /**
     * 優先順位テーブルリストの取得
     * @param companyCd
     * @return
     */
    @GetMapping("/getPriorityOrderList")
    public Map<String,Object> getPriorityOrderList(String companyCd){
        return priorityOrderMstService.getPriorityOrderList(companyCd);
    }



    /**
     * 登録者がいる企業に優先順位表があるかどうかを調べる
     * @return
     */
    @GetMapping("/getPriorityOrderExistsFlg")
    public Map<String,Object> getPriorityOrderExistsFlg(String companyCd){
        return priorityOrderMstService.getPriorityOrderExistsFlg(companyCd);
    }



    ///**
    // * 優先順位表cdに基づいて商品力点数表cdを取得する
    // * @param priorityOrderCd
    // * @return
    // */
    //@GetMapping("/getProductPowerCdForPriority")
    //public Map<String,Object> getProductPowerCdForPriority(Integer priorityOrderCd){
    //    return priorityOrderMstService.getProductPowerCdForPriority(priorityOrderCd);
    //}


    /**
     * S自動計算-STEP 1
     * @param companyCd
     * @param patternCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/preCalculation")
    public Map<String, Object> preCalculation(String companyCd, Long patternCd,Integer priorityOrderCd ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderMstService.preCalculation(companyCd, patternCd,priorityOrderCd);
    }

    /**
     * 表示自動計算実行ステータス
     * @param taskId
     * @return
     */
    @GetMapping("/autoTaskId")
    public Map<String, Object> autoTaskId(String taskId){
        return priorityOrderMstService.autoTaskId(taskId);
    }
    /**
     * 編集時にすべての情報を表示
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("getPriorityOrderAll")
    public Map<String,Object> getPriorityOrderAll(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
       return priorityOrderMstService.getPriorityOrderAll(companyCd,priorityOrderCd);
    }


    /**
     * 編集時にptsの名前が存在するかどうかを確認
     * @return
     */
    @PostMapping("/checkOrderName")
    public Map<String,Object> checkOrderName(@RequestBody PriorityOrderMstVO priorityOrderMstVO){
        return priorityOrderMstService.checkOrderName(priorityOrderMstVO);
    }

    /**
     * 基本パターン削除
     * @param priorityOrderMstVO
     * @return
     */
    @DeleteMapping("/deletePriorityOrderAll")
    public Map<String,Object> deletePriorityOrderAll(@RequestBody PriorityOrderMstVO priorityOrderMstVO){
        return priorityOrderMstService.deletePriorityOrderAll(priorityOrderMstVO);
    }

    /**
     * 各種mst展示
     * @param companyCd
     * @param priorityOrderCd
     * @param flag
     * @return

     */
    @GetMapping("/getVariousMst")
    public Map<String,Object> getVariousMst(String companyCd,Integer priorityOrderCd,Integer flag) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return priorityOrderMstService.getVariousMst(companyCd,priorityOrderCd,flag);
    }

}
