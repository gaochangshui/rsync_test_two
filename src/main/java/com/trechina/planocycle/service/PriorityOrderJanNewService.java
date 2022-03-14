package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderJanNew;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface PriorityOrderJanNewService {
    /**
     * 获取新规janlist
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanNew(String companyCd,Integer priorityOrderCd, Integer productPowerNo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     *保存新规商品list
     * @param
     * @return
     */
    Map<String,Object> setPriorityOrderJanNew(List<PriorityOrderJanNew> priorityOrderJanNew);

    /**
     * 删除新规商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delriorityOrderJanNewInfo(String companyCd,Integer priorityOrderCd);
}
