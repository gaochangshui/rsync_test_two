package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanReplaceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassicPriorityOrderJanReplaceMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderJanReplace> record);

    List<ClassicPriorityOrderJanReplaceVO> selectJanInfo(String companyCd, Integer priorityOrderCd, String tableName, String janCdCol,
                                                         String janNameCol);

    String selectJanDistinct(String proInfoTable);

    int selectJanDistinctByJan(String proInfoTable, String jan);

    int setFinalForWork(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<String> existJanNew(List<String> janList, @Param("companyCd") String companyCd);

    List<String> existJanOld(List<String> janList, @Param("companyCd") String companyCd);
}
