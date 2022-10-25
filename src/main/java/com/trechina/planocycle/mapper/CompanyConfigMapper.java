package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.MstKiGyoCore;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;
@Mapper
public interface CompanyConfigMapper {

    MstKiGyoCore getMstkigyocore(String companyCd);

    void setCompany(String companyCd, String companyName);
    void setCompanyConfig();

    Map<String,Object> getCompanyConfig(String companyCd,String classCd);

    void setSyncCompany(String syncCompanyList);

    void setGroupCompany(String companyCd, String companyName, String groupCd, String groupName);
}
