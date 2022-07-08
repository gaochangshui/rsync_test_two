package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PresetAttribute;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface PresetAttributeMapper {
    int insertPresetAttribute(String authorCd, PresetAttribute presetAttribute);

    int deleteByAuthorCd(String authorCd);

    PresetAttribute getPresetAttribute(String authorCd);
}
