package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
* @author 10218504
* @createDate 2022-06-23 12:36:25
* @Entity com.trechina.planocycle.entity.po.BasicPatternRestrictRelation
*/
@Mapper
public interface BasicPatternRestrictRelationMapper {

    int deleteByPrimaryKey(Integer priorityOrderCd, String companyCd);

    int insert(BasicPatternRestrictRelation record);

    int insertSelective(BasicPatternRestrictRelation record);

    BasicPatternRestrictRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BasicPatternRestrictRelation record);

    int updateByPrimaryKey(BasicPatternRestrictRelation record);

    int insertBatch(List<Map<String, Object>> lists);

}
