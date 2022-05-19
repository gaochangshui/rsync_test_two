package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/sysConfig")
public class SysConfigController {
    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/getShowJanSku")
    public Map<String, Object> getShowJanSku(String companyCd,String commonPartsData){
        return sysConfigService.getShowJanSku(companyCd,commonPartsData);
    }
}
