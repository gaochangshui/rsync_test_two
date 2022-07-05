package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriorityOrderCompanyConfigMapper {

    int saveUserCompany(String companyCd,String authorCd);

    int deleteUserCompany(String authorCd);

    String getUserCompany(String authorCd);
}
