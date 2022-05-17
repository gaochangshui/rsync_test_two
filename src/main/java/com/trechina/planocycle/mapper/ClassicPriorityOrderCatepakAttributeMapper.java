package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderCatepakAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassicPriorityOrderCatepakAttributeMapper {
    int deleteByPrimaryKey(String companyCd, Integer priorityOrderCd);

    int deleteFinalByPrimaryKey(String companyCd, Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderCatepakAttribute> record);

    String selectForTempTable( @Param("lists") List<String> colValueList
            ,@Param("tableName") String tableName);

    int insertFinalData(String companyCd, Integer priorityOrderCd);

    int setWorkForFinal(String companyCd, Integer priorityOrderCd);
}
