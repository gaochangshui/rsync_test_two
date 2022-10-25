package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.ParamConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("getCompanyConfig")
    public Map<String,Object> getCompanyConfig(@RequestBody Map<String,Object> map)  {

        return paramConfigService.getCompanyConfig(map);
    }

    @GetMapping("setCompanyConfig")
    public Map<String,Object> setCompanyConfig(Map<String,Object> map)  {

        return paramConfigService.setCompanyConfig(map);
    }

    /**
     * エンタープライズ管理ページエンタープライズリストの取得
     *
     * @return
     */
    @GetMapping("getCompanyList")
    public Map<String,Object> getCompanyList()  {

        return paramConfigService.getCompanyList();
    }
}
