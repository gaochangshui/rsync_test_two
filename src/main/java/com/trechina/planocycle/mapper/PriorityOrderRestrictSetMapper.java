package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderAttrValueDto;
import com.trechina.planocycle.entity.po.PriorityOderAttrSet;
import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderRestrictSetMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,
            @Param("taiCd") Integer taiCd, @Param("tanaCd") Integer tanaCd,
            @Param("tanaType") Integer tanaType);

    int insert(PriorityOrderRestrictSet record);

    int insertSelective(PriorityOrderRestrictSet record);

    PriorityOrderRestrictSet selectByPrimaryKey(@Param("companyCd") String companyCd,
            @Param("priorityOrderCd") Integer priorityOrderCd, @Param("taiCd") Integer taiCd,
            @Param("tanaCd") Integer tanaCd, @Param("tanaType") Integer tanaType);

    int updateByPrimaryKeySelective(PriorityOrderRestrictSet record);

    int updateByPrimaryKey(PriorityOrderRestrictSet record);

    int setPriorityOrderRestrict(@Param("item") PriorityOderAttrSet priorityOderAttrSet, @Param("authorCd") String authorCd);

    List<PriorityOrderRestrictSet> getPriorityOrderRestrict(@Param("companyCd")String companyCd,
                                       @Param("authorCd")String authorCd, @Param("priorityOrderCd")Integer priorityOrderCd);

    List<Map<String, Object>> getDynamicPriorityOrderRestrict(@Param("companyCd")String companyCd,
                                                       @Param("authorCd")String authorCd, @Param("priorityOrderCd")Integer priorityOrderCd,
                                                       @Param("zokuseiList")List<String> zokuseiList);
    //名前の前に大分類nameがついている
    List<PriorityOrderAttrValueDto> getAttrValues();

    List<PriorityOrderAttrValueDto> getDynamicAttrValues(String companyCd, String classCd);

    List<PriorityOrderAttrValueDto> getAttrValues1();

    int delete(@Param("item") PriorityOderAttrSet priorityOderAttrSet, @Param("authorCd") String authorCd);

    int insertBySelect(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int logicDeleteByPriorityOrderCd(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int deleteByAuthorCd(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);
}
