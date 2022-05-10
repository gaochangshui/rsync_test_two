package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.vo.ParamConfigVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface  ParamConfigMapper {
    List<ParamConfigVO> selectParamConfig();
}
