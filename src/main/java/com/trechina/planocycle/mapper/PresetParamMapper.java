package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PresetParamMapper {
    int insertPresetParam(String authorCd, String[] presetParam);

    int deleteByAuthorCd(String authorCd);
}
