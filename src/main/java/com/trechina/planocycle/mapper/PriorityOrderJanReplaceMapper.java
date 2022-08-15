package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.entity.vo.PriorityOrderJanReplaceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderJanReplaceMapper {
    int workDelete(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderJanReplace> record, @Param("authorCd") String authorCd);

    List<PriorityOrderJanReplaceVO> selectJanInfo(String companyCd, Integer priorityOrderCd, GetCommonPartsDataDto commonTableName);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd") String authorCd
    ,@Param("newPriorityOrderCd")Integer newPriorityOrderCd);

    String selectJanDistinct();

    int insertBySelect(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteByAuthorCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String, String>> selectJanReplace(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
}
