package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerDataMapper {
    //临时表
    int dropTempData(String tablename);

    int updateTempData( @Param("tablename") String tablename);

    int insert(@Param("keyNameList") List<List<String>> keyNameList,@Param("tableName")String tableName);



    //最终表

}
