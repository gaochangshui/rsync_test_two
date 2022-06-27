package com.trechina.planocycle.controller;


import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.service.impl.BasicPatternAttrServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("planoCycle/BasicPatternAttr")
public class BasicPatternAttrController {
    @Autowired
    private BasicPatternAttrServiceImpl basicPatternAttrService;
    /**
     *つかむ取属性1と属性2
     */

    @PostMapping("/getAttribute")
    public Map<String,Object> getAttribute(@RequestBody PriorityOrderAttrDto priorityOrderAttrDto){
        return basicPatternAttrService.getAttribute(priorityOrderAttrDto);
    }

}
