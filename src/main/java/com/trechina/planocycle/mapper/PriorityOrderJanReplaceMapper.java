package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.entity.vo.PriorityOrderJanReplaceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderJanReplaceMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int workDelete(@Param("companyCd") String companyCd,@Param("authorCd")String authorCd ,@Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderJanReplace> record,@Param("authorCd")String authorCd);

    List<PriorityOrderJanReplaceVO> selectJanInfo(String companyCd, Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);
    String selectJanDistinct();
}
