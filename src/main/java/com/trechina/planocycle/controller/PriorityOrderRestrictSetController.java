package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOderAttrSet;
import com.trechina.planocycle.service.PriorityOrderRestrictSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderRestrict")

public class PriorityOrderRestrictSetController {
@Autowired
    PriorityOrderRestrictSetService priorityOrderRestrictSetService;

    /**
     * テーブル/セグメント対応属性の追加削除
     * @param priorityOderAttrSet
     * @return
     */
    @PostMapping("setPriorityOrderRestrict")
    public Map<String,Object> setPriorityOrderRestrict(@RequestBody PriorityOderAttrSet priorityOderAttrSet){

        return priorityOrderRestrictSetService.setPriorityOrderRestrict(priorityOderAttrSet);
    }

    /**
     *各ステージ/セグメントに対応するプロパティの取得
     * @param companyCd
     * @return
     */
    @GetMapping("/getAttrDisplay")
    public Map<String,Object> getAttrDisplay(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return priorityOrderRestrictSetService.getAttrDisplay(companyCd,priorityOrderCd);
    }

}
