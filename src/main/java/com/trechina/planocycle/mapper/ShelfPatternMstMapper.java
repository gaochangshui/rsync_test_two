package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.po.ShelfPatternMst;
import com.trechina.planocycle.entity.vo.ShelfPatternNameVO;
import com.trechina.planocycle.entity.vo.ShelfPatternTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelfPatternMstMapper {
    int deleteByPrimaryKey(@Param("conpanyCd") String conpanyCd, @Param("shelfPatternCd") Integer shelfPatternCd, @Param("area") Integer area);

    int insert(ShelfPatternMst record);

    List<ShelfPatternMst> selectByPrimaryKey(@Param("conpanyCd") String conpanyCd);

    List<ShelfPatternNameVO> selectPatternName(@Param("conpanyCd") String conpanyCd);

    List<ShelfPatternTreeVO> selectPatternNameBranch(@Param("conpanyCd") String conpanyCd);

    void deleteByShelfName(Integer id);

    String selectByShePatternNoNm(@Param("item") String shelfPatternNo);

    Integer selectDistinctName(ShelfPatternDto shelfPatternDto);

    void updateByPtsForShelfPdCd(Integer id);

    void deleteShelfPdCdHistory(Integer id);

    List<Integer> getpatternIdOfPtsKey(String ptsKey);
}
