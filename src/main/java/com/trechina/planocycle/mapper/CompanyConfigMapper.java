package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface CompanyConfigMapper {

    Map<String,Object> getMstkigyocore(String companyCd);

    void setCompany(CompanyList companyList);
    void setCompanyConfig( List<SkuNameConfig> resultProd);

    List<Map<String,Object>> getCompanyConfig(String companyCd);


    void setGroupCompany(String companyCd, String companyName, String groupCd, String groupName);

    List<Company> getCompanyList();

    List<Group> getGroupList();

    void setStoreMstClass(String companyCd, List<String> storeMstClass);
    void delStore(String companyCd);

    void setProdClass(List<CompanyAttrConfig> prodClass);

    void delProdClass(String companyCd);

    void delcompanyConfig(String companyCd);

    List<Map<String,Object>> getCompanyConfigForCompany(String companyCd);

    List<String> getStoreList(String companyCd);

    List<Map<String, Object>> getProdList(String companyCd);

    Map<String, Object> getCompanyList1(String companyCd);

    List<Company> getInUseCompanyList();

    List<Map<String, Object>> selectAttrTargetColumn(List<String> targetColumn, Map<String, Object> condition);

    Map<String, Object> getKokyakuShow(String isCompanyCd);

    void setMstKigyocore(String companyCd);

    void delMstKigyocore(String companyCd);

    String getCompanyProdClass(String isCompanyCd);

    String getCompanyStoreClass(String isCompanyCd);
}
