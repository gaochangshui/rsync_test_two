package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ClaasicPriorityOrderAttributeClassifyMapper {

    List<PriorityOrderAttributeClassify> classifyList(@Param("tai") String tai, @Param("tana") String tana);

     int insert (@Param("list")List<PriorityOrderAttributeClassify> list);

     int delete (@Param("priorityOrderCd")Integer priorityOrderCd);

     Integer getAttrNum(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    List<PriorityOrderAttributeClassify> getClassifyList(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    Set<String> selectDiffJanTaiTana(String companyCd, Integer priorityOrderCd);

    int deleteFinal(@Param("priorityOrderCd")Integer priorityOrderCd);

    int insertFinal (@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int insertWork (@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    void checkIsJanNew(String tableName, String[] jans);
}
