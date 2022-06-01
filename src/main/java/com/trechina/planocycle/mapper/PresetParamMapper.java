package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PresetParamMapper {
    int insertPresetParam(String authorCd, String[] presetParam);

    int deleteByAuthorCd(String authorCd);

    List<String> getPresetParam(String authorCd);
}
