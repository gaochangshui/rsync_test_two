package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface  ParamConfigMapper {
    List<ParamConfigVO> selectParamConfig(String type);
    List<ParamConfigDto> getParamConfig();
    //获取所有的列名
    List<ParamConfigVO> getParamConfigAll();
}
