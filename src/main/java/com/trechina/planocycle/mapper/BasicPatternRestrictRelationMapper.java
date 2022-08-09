package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;
import com.trechina.planocycle.entity.vo.BasicPatternRestrictRelationVo;
import com.trechina.planocycle.entity.vo.PtsTanaVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    List<Map<String, Object>> selectByPrimaryKey(Integer priorityOrderCd, String companyCd,
                                                 List<String> zokuseiList, String classCd);

    List<Map<String, Object>> selectByPriorityOrderCd(Integer priorityOrderCd);
    int updateByPrimaryKeySelective(BasicPatternRestrictRelation record);

    int updateByPrimaryKey(BasicPatternRestrictRelation record);

    int insertBatch(List<Map<String, Object>> lists);


    int update(@Param("item") BasicPatternRestrictRelation record,@Param("authorCd")String authorCd);

    void deleteFinal(String companyCd, String authorCd, Integer priorityOrderCd);

    void setFinalForWork(String companyCd, String authorCd, Integer priorityOrderCd);

    void deleteTana(Integer taiCd, String companyCd, Integer priorityOrderCd);

    void deleteTanas(Integer taiCd,Integer tanaCd, String companyCd, Integer priorityOrderCd);

    void setTaiInfo(List<PtsTanaVo> ptsTanaVoList,String companyCd, Integer priorityOrderCd,String authorCd);

    List<BasicPatternRestrictRelationVo> getTanaAttrList(@Param("item") BasicPatternRestrictRelation basicPatternRestrictRelation);

    void deleteForTanaPosition(@Param("item") BasicPatternRestrictRelation basicPatternRestrictRelation);

    void updateTanaPosition(@Param("list") List<BasicPatternRestrictRelationVo> tanaAttrList,@Param("authorCd")String authorCd);

    void setWorkForFinal(String companyCd, Integer priorityOrderCd);

    Integer selectUnusedTaiTana(Integer priorityOrderCd);

    Integer getTanaGroup(@Param("item") BasicPatternRestrictRelation basicPatternRestrictRelation);

    void updateAreaPosition(List<Map<String, Object>> list, Integer priorityOrderCd);


}
