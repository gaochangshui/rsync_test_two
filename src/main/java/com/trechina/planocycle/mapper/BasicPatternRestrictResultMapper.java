package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.BasicPatternRestrictResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 10218504
* @createDate 2022-06-23 10:46:59
* @Entity generator.domain.BasicPatternRestrictResult
*/
@Mapper
public interface BasicPatternRestrictResultMapper {

    int deleteByPrimaryKey(Long id);

    int deleteByPriorityCd(Integer priorityOrderCd, String companyCd);

    int insert(BasicPatternRestrictResult record);

    int insertBatch(List<BasicPatternRestrictResult> lists);

    int insertSelective(BasicPatternRestrictResult record);

    BasicPatternRestrictResult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BasicPatternRestrictResult record);

    int updateByPrimaryKey(BasicPatternRestrictResult record);

}
