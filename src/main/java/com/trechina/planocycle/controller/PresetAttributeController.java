package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PresetAttribute;
import com.trechina.planocycle.entity.po.PresetParam;
import com.trechina.planocycle.service.PresetAttributeService;
import com.trechina.planocycle.service.PresetParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/PresetAttribute")
public class PresetAttributeController {
    @Autowired
    private PresetAttributeService presetAttributeService;
    @PostMapping("/setPresetAttribute")
    public Map<String, Object> setPresetParam(@RequestBody PresetAttribute presetAttribute){
        return presetAttributeService.setPresetAttribute(presetAttribute);
    }

    @GetMapping("/getPresetAttribute")
    public Map<String, Object> getPresetAttribute(){
        return presetAttributeService.getPresetAttribute();
    }
}
