package com.trechina.planocycle.service;


import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface PriorityOrderRestrictSetService {
    Map<String,Object> setPriorityOrderRestrict(@RequestBody PriorityOrderRestrictSet priorityOrderRestrictSet);
}
