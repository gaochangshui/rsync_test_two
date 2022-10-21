package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.MstKiGyoCore;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyConfigMapper {

    MstKiGyoCore getMstkigyocore(String companyCd);

    void setCompany(String companyCd, String companyName);
    void setCompanyConfig();
}
