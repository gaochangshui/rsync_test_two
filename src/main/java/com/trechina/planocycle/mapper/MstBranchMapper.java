package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.BranchList;
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

    Integer getBranchExist(String branchInfoTableName, List<String> branchCdList);


    int syncTenData(@Param("tableName") String tableNameInfo, @Param("tableNameWK") String tableNameInfoWK, String column);

    List<LinkedHashMap<String, Object>> getTenHeader(String tableNameHeaderInfo);

    List<String> getMasterTenClass(String tableName);
    void setBranchInfo(@Param("list") List<BranchList> branchList, @Param("branchInfoTableName") String branchInfoTableName);

    void deleteBranch(String branchInfoTableName,List<String> groupCd);
    void deleteBranch1(String branchInfoTableName);

    void deleteNotExistMst(String classCd, String tableName);

    int checkSchemaExist(String schemaCd);

    int checkTableExist(String tableName, String schema);

    void creatTenData(String tableNameInfo, String tableNameInfoWK,String tableNameInfoPkey);

    String selectTableNameExist(String companyCd,String classCd);
    String selectMasterTenExist(String companyCd);

    void creatMasterTen(String masterTenTb, String masterTenTbWk,String masterTenTbPkey);
}
