package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PresetParam;

import java.util.Map;

public interface PresetParamService {
    Map<String, Object> setPresetParam(PresetParam presetParam);

    Map<String, Object> getPresetParam();

    Map<String, Object> setPresetParamForProduct(PresetParam presetParam);

    Map<String, Object> getPresetParamForProduct();

}
