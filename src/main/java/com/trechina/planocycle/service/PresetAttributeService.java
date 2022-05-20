package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PresetAttribute;

import java.util.Map;

public interface PresetAttributeService {
    Map<String, Object> getPresetAttribute();

    Map<String, Object> setPresetAttribute(PresetAttribute presetAttribute);
}
