package com.trechina.planocycle.service;


import com.trechina.planocycle.entity.po.PriorityOderAttrSet;

import java.util.Map;

public interface PriorityOrderRestrictSetService {
    /**
     * テーブル/セグメント対応属性の追加削除
     * @param priorityOderAttrSet
     * @return
     */
    Map<String,Object> setPriorityOrderRestrict( PriorityOderAttrSet priorityOderAttrSet);

}
