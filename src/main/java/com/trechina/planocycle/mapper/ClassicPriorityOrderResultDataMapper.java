package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderResultDataMapper {
    int setFinalForWork(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd") String authorCd
            , @Param("tablename")String tablename, @Param("tableCol")List<String> tableCol);

    int deleteFinal(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);


    List<Map<String, Object>> selectFinalData(Integer priorityOrderCd, String companyCd,List<String> list);

}
