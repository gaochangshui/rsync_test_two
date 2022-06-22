package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("/planocycle/BasicPatternMst")
public class BasicPatternMstController {

    @Autowired
    private BasicPatternMstService basicPatternMstService;

    @PostMapping("/autoDetect")
    public Map<String, Object> autoDetect(@RequestBody BasicPatternAutoDetectVO basePatternAutoDetectVO){
        basicPatternMstService.autoDetect(basePatternAutoDetectVO);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
}
