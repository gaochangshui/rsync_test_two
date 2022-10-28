package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ParamConfigMapper {
    List<ParamConfigDto> getParamConfig(Integer showItemCheck);

    List<ParamConfigVO> selectParamConfig();

    List<ParamConfigDto> selectAllParamConfig();

    List<ParamConfigVO> selectParamConfigByCd(@Param("cdList") List<String> cdList);

    void updateParamConfig(@Param("list") List<ParamConfigDto> paramConfigVO);

    List<String> getParamRatio();

    List<Map<String, Object>> selectProdClassMst(String companyCd);

    List<Map<String, Object>> selectTenClassMst(String companyCd);

    Map<String, Object> selectCompanyMst(String companyCd);
}
