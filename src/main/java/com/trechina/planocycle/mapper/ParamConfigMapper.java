package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParamConfigMapper {
    List<ParamConfigDto> getParamConfig(Integer showItemCheck);

    List<ParamConfigVO> selectParamConfig();

    List<ParamConfigVO> selectParamConfigByCd(@Param("cdList") List<String> cdList);
}
