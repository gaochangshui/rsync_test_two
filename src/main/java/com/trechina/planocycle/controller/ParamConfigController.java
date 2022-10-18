package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.ParamConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/ParamConfig")
public class ParamConfigController {
    @Autowired
    private ParamConfigService paramConfigService;

    @GetMapping("getParamConfigList")
    public Map<String,Object> getParamConfigList(@RequestParam(required = false) Integer showItemCheck)  {

        return paramConfigService.getParamConfigList(showItemCheck);
    }


}
