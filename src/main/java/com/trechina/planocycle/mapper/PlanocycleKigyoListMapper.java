package com.trechina.planocycle.mapper;


import com.trechina.planocycle.entity.dto.CompanyListDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlanocycleKigyoListMapper {

    List<CompanyListDto> getCompanyList(List<String> companyCd);

    String getGroupInfo(String companyCd);
}
