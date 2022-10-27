package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.Company;
import com.trechina.planocycle.entity.po.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface CompanyConfigMapper {

    Map<String,Object> getMstkigyocore(String companyCd);

    void setCompany(String companyCd, String companyName);
    void setCompanyConfig();

    List<Map<String,Object>> getCompanyConfig(String companyCd);

    void setSyncCompany(String syncCompanyList);

    void setGroupCompany(String companyCd, String companyName, String groupCd, String groupName);

    List<Company> getCompanyList();

    List<Group> getGroupList();
}
