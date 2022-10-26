package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface GroupCompanyMapper {
    void deleteCompany(String groupCd);

    void insertCompany(String groupCd,String groupName, List<Map<String, String>> companyMapList, String aud, LocalDateTime date);

    List<Map<String, String>> selectCompanyName(String companyList);

    String selectGroupCdNumber();
}
