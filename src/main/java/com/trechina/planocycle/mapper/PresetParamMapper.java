package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PresetAttribute;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PresetParamMapper {
    int insertPresetParam(String authorCd, String[] presetParam);

    int deleteByAuthorCd(String authorCd);

    List<String> getPresetParam(String authorCd);

    void insertProductPresetParam(String authorCd, PresetAttribute presetAttribute);

    PresetAttribute getProductPresetParam(String authorCd,PresetAttribute presetAttribute);

    void deleteProductPresetParam(String authorCd,PresetAttribute presetAttribute);
}
