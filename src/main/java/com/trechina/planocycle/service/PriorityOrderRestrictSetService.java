package com.trechina.planocycle.service;


import com.trechina.planocycle.entity.po.PriorityOderAttrSet;

import java.util.Map;

public interface PriorityOrderRestrictSetService {
    //
    Map<String,Object> setPriorityOrderRestrict( PriorityOderAttrSet priorityOderAttrSet);


    Map<String,Object> getAttrDisplay(String companyCd);
}
