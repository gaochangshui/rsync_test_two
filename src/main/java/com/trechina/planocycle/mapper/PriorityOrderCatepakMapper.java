package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderCatepak;
import com.trechina.planocycle.entity.vo.PriorityOrderCatePakVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PriorityOrderCatepakMapper {
    int deleteByPrimaryKey(String companyCd, Integer priorityOrderCd);

    int insert(PriorityOrderCatepak record);

    List<PriorityOrderCatePakVO> selectByPrimaryKey(String companyCd, Integer priorityOrderCd);

    Integer selectColName(String companyCd, Integer productPowerNo);

    void updateBranchNum(Integer id, Integer branchNum);
}
