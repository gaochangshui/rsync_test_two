package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderCatepakAttribute;
import com.trechina.planocycle.entity.vo.PriorityOrderCatePakVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassicPriorityOrderCatepakAttributeMapper {
    int deleteByPrimaryKey(String companyCd, Integer priorityOrderCd);

    int deleteFinalByPrimaryKey(String companyCd, Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderCatepakAttribute> record);

    List<Integer> selectForTempTable( @Param("lists") List<String> colValueList,Integer priorityOrderCd,Integer rank);

    int insertFinalData(String companyCd, Integer priorityOrderCd);

    int setWorkForFinal(String companyCd, Integer priorityOrderCd,Integer newPriorityOrderCd);

    List<PriorityOrderCatePakVO> selectFinalByPrimaryKey(List<Integer> rankAttrList, String companyCd, Integer priorityOrderCd);
}
