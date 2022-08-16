package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderCatepak;
import com.trechina.planocycle.entity.vo.PriorityOrderCatePakVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderCatepakMapper {
    int deleteByPrimaryKey(String companyCd, Integer priorityOrderCd);

    int deleteFinalByPrimaryKey(String companyCd, Integer priorityOrderCd);

    int insert(PriorityOrderCatepak record);

    List<PriorityOrderCatePakVO> selectByPrimaryKey(String companyCd, Integer priorityOrderCd);

    void updateBranchNum(Integer id, Integer branchNum);

    int insertFinalData(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("newPriorityOrderCd")Integer newPriorityOrderCd);

    List<Map<String, Object>> getCatePakSimilarity(Integer priorityOrderCd, Map<String, Object> maps);
}
