package com.trechina.planocycle.service;


import com.trechina.planocycle.entity.po.PriorityOderAttrSet;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface PriorityOrderRestrictSetService {
    /**
     * テーブル/セグメント対応属性の追加削除
     * @param priorityOderAttrSet
     * @return
     */
    Map<String,Object> setPriorityOrderRestrict( PriorityOderAttrSet priorityOderAttrSet);

    /**
     * 各ステージ/セグメントに対応するプロパティの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    Map<String,Object> getAttrDisplay(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
