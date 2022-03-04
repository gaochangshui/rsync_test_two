package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import com.trechina.planocycle.service.PriorityOrderRestrictSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderRestrict")

public class PriorityOrderRestrictSetController {
@Autowired
    PriorityOrderRestrictSetService priorityOrderRestrictSetService;
    @PostMapping("setPriorityOrderRestrict")
    public Map<String,Object> setPriorityOrderRestrict(@RequestBody PriorityOrderRestrictSet priorityOrderRestrictSet){

        return priorityOrderRestrictSetService.setPriorityOrderRestrict(priorityOrderRestrictSet);
    }

}
