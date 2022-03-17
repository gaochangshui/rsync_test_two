package com.trechina.planocycle.service;


import com.trechina.planocycle.entity.po.PriorityOderAttrSet;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface PriorityOrderRestrictSetService {
    //
    Map<String,Object> setPriorityOrderRestrict( PriorityOderAttrSet priorityOderAttrSet);


    Map<String,Object> getAttrDisplay(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
