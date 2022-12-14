package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/sysConfig")
public class SysConfigController {
    @Autowired
    private SysConfigService sysConfigService;

    @PostMapping("/getShowJanSku")
    public Map<String, Object> getShowJanSku(@RequestBody EnterpriseAxisDto enterpriseAxisDto){
        return sysConfigService.getShowJanSku(enterpriseAxisDto);
    }


    @GetMapping("/getCompanySettings")
    public Map<String, Object> getCompanySettings(String companyCd){
        return sysConfigService.getCompanySettings(companyCd);
    }
}
