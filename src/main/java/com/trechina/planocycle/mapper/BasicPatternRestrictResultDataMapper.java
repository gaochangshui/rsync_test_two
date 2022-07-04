package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.BasicPatternRestrictResultData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
* @createDate 2022-06-24 14:35:41
* @Entity com.trechina.planocycle.entity.po.BasicPatternRestrictResultData
*/
@Mapper
public interface BasicPatternRestrictResultDataMapper {

    int deleteByPrimaryKey(Integer priorityOrderCd);

    int insert(BasicPatternRestrictResultData record);

    int insertSelective(BasicPatternRestrictResultData record);

    BasicPatternRestrictResultData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BasicPatternRestrictResultData record);

    int updateByPrimaryKey(BasicPatternRestrictResultData record);

    void insertBatch(List<Integer> attrList, List<Map<String, Object>> zokuseiList, Integer priorityOrderCd, String companyCd, String authorCd);

    void deleteFinal(String companyCd, String authorCd, Integer priorityOrderCd);

    void setFinalForWork(String companyCd, String authorCd, Integer priorityOrderCd);

    void setWorkForFinal(String companyCd, Integer priorityOrderCd);
}
