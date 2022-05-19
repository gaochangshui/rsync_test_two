package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/sysConfig")
public class SysConfigController {
    @Autowired
    private SysConfigService sysConfigService;

    @PostMapping("/getShowJanSku")
    public Map<String, Object> getShowJanSku(@RequestBody Map<String ,Object>map){
        return sysConfigService.getShowJanSku(map);
    }
}
