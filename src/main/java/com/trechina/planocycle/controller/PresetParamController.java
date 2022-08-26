package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PresetParam;
import com.trechina.planocycle.service.PresetParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/PresetParam")
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


    @PostMapping("/setPresetParamForProduct")
    public Map<String, Object> setPresetParamForProduct(@RequestBody PresetParam presetParam){
        return presetParamService.setPresetParamForProduct(presetParam);
    }


    @GetMapping("/getPresetParamForProduct")
    public Map<String, Object> getPresetParamForProduct(){
        return presetParamService.getPresetParamForProduct();
    }

}
