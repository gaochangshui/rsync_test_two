package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface  ParamConfigMapper {
    List<ParamConfigDto> getParamConfig();
    //つかむ取所有的列名
    List<ParamConfigVO> getParamConfigAll();
    List<ParamConfigVO> selectParamConfig();

    List<ParamConfigVO> selectParamConfigByCd(@Param("cdList") List<String> cdList);
}
