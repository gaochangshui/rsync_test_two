package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PresetAttribute;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PresetAttributeMapper {
    int insertPresetAttribute(String authorCd, String[] presetAttribute);

    int deleteByAuthorCd(String authorCd);

    List<PresetAttribute> getPresetAttribute(String authorCd);
}
