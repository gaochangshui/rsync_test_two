package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import com.trechina.planocycle.service.BasicPatternMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
@RequestMapping("planoCycle/BasicPatternMst")
public class BasicPatternMstController {
    @Autowired
    private BasicPatternMstService basicPatternMstService;

    @PostMapping("/autoDetect")
    public Map<String, Object> autoDetect(@RequestBody BasicPatternAutoDetectVO basePatternAutoDetectVO){
        return basicPatternMstService.autoDetect(basePatternAutoDetectVO);
    }

    @GetMapping("/getAttrDisplay")
    public Map<String,Object> getAttrDisplay(String companyCd,Integer priorityOrderCd) {
        return basicPatternMstService.getAttrDisplay(companyCd,priorityOrderCd);
    }
}
