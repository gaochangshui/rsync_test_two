package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PresetParam;
import com.trechina.planocycle.service.PresetParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PresetParam")
public class PresetParamController {
    @Autowired
    private PresetParamService presetParamService;
    @PostMapping("/setPresetParam")
    public Map<String, Object> setPresetParam(@RequestBody PresetParam presetParam){
        return presetParamService.setPresetParam(presetParam);
    }

    @GetMapping("/getPresetParam")
    public Map<String, Object> getPresetParam(){
        return presetParamService.getPresetParam();
    }
}
