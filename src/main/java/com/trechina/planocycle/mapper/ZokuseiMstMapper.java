package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.Zokusei;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ZokuseiMstMapper {
    int insertBatch(String companyCd, String classCd, @Param("lists") List<Zokusei> lists);

    List<Map<String, Object>> selectHeader(String tableName);

    List<String> selectAllAttrTable(String schema);

    List<String> selectAllKaisouTable(String schema);

    void insertZokuseiData(String company, String classCd, Integer zokuseiId, Integer col, List<Integer> list);

    Integer selectExist(String company, String classCd, Integer col);

    void insertZokuseiData1(String company, String classCd, Integer zokuseiId, Integer col);

    void delete(String companyCd, String classCd);

    void deleteData(String companyCd, String classCd);

    List<Zokusei> selectByZokuseiId(String companyCd, Integer priorityOrderCd, String zokuseiId, String classCd);

    List<Map<String,Object>> getZokuseiCol(List<Integer> attrList ,String companyCd, String classCd);

    List<Map<String,Object>> getzokuseiName(@Param("companyCd") String companyCd,@Param("classCd")String classCd);


    void updateZokuseiMstData(List<Map<String, Object>> map, String companyCd, String classCd);

    void updateZokuseiMst(List<Map<String, Object>> map, String companyCd, String classCd);

    Integer getZokuseiIdForCol(String colName, String companyCd, String classCd);

    void delZokuseiMstForId(String classCd, String companyCd, Integer zokuseiId);

    void delZokuseiMstDataForId(String classCd, String companyCd, Integer zokuseiId);

    List<Map<String,Object>> getZokuseiIdAndCol(String companyCd, String classCd);

    void setVal(LinkedHashMap<String, Object> maps,String companyCd,String classCd);

    List<Map<String,Object>> getJanSizeCol(String proAttrTable);
}
