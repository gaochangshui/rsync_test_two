package com.trechina.planocycle.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JanInfoMapper {
    List<String> getSchemaOrTableName();
    int setFinalForWork(@Param("finalTable")String finalTable,@Param("wkTable")String wkTable);
    void dropFinal(@Param("tableName") String tableName);

}
