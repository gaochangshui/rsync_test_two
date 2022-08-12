package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.BasicPatternJanPlacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("planoCycleApi/BasicPatternJanPlacement")
public class BasicPatternJanPlacementController {
    @Autowired
    private BasicPatternJanPlacementService basicPatternJanPlacementService;
    @GetMapping("getJanPlacement")
    Map<String,Object> getJanPlacement(){

        return basicPatternJanPlacementService.getJanPlacement();
    }
}
