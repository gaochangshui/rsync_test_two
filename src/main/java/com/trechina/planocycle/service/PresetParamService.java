package com.trechina.planocycle.service;

import java.util.Map;

public interface PresetParamService {
    Map<String, Object> setPresetParam(String presetParam);

    Map<String, Object> getPresetParam();
}
