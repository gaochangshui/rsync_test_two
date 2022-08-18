package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.DownloadDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderJanAttributeMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderJanAttribute> record);

    List<String> getAttribute(@Param("priorityOrderCd")Integer priorityOrderCd);

    List<PriorityOrderJanAttribute> selectAttributeByJan(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,
                                      @Param("janList") List<DownloadDto> janList);

    int setFinalForWork(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("newPriorityOrderCd")Integer newPriorityOrderCd);

    void deleteByJan(String company, Integer priorityOrderCd, List<DownloadDto> lists);

    List<Map<String, Object>> getAttr(String companyCd, Integer priorityOrderCd);
}
