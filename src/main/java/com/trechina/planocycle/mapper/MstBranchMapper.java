package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.BranchList;
import com.trechina.planocycle.entity.po.CommoditySyncSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface MstBranchMapper {

    List<Map<String,Object>> getCommuneBranchInfo(String branchInfoTableName,String companyCd,List<String> groupCompany);

    List<Map<String,Object>> getCompanyBranchInfo(String branchInfoTableName, String companyCd, List<String> groupCompany);

    Integer getBranchSize(String branchInfoTableName,String companyCd);

    Integer getBranchExist(String branchInfoTableName, String branchStr);

    void setBranchInfo(@Param("item") BranchList branchList, @Param("branchInfoTableName") String branchInfoTableName);

    void syncTenData(@Param("tableName") String tableNameInfo, @Param("tableNameWK") String tableNameInfoWK, String column);

    List<LinkedHashMap<String, Object>> getTenHeader(String tableNameHeaderInfo);

    List<String> getMasterTenClass(String tableName);
}
