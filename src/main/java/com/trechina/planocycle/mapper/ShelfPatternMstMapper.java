package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.po.ShelfPatternMst;
import com.trechina.planocycle.entity.vo.ShelfNamePatternVo;
import com.trechina.planocycle.entity.vo.ShelfPatternNameVO;
import com.trechina.planocycle.entity.vo.ShelfPatternTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface ShelfPatternMstMapper {
    int deleteByPrimaryKey(@Param("conpanyCd") String conpanyCd, @Param("shelfPatternCd") Integer shelfPatternCd, @Param("area") Integer area);

    int insert(ShelfPatternMst record);

    int update(ShelfPatternMst record);

    List<ShelfPatternMst> selectByPrimaryKey(@Param("conpanyCd") String conpanyCd);

    List<ShelfPatternNameVO> selectPatternName(@Param("conpanyCd") String conpanyCd);

    List<ShelfPatternTreeVO> selectPatternNameBranch(@Param("conpanyCd") String conpanyCd);

    void deleteByShelfName(@Param("shelfPatternCd") Integer id,@Param("authorCd") String authorCd);

    String selectByShePatternNoNm(@Param("item") String shelfPatternNo);

    Integer selectDistinctName(ShelfPatternDto shelfPatternDto);

    void updateByPtsForShelfPdCd(@Param("id") Integer id,@Param("authorCd") String authorCd);

    void deleteShelfPdCdHistory(@Param("id") Integer id,@Param("authorCd") String authorCd);

    List<Integer> getpatternIdOfPtsKey(String ptsKey);

    List<Integer> getShelfPattern(@Param("companyCd") String companyCd, @Param("shelfNameCd") Integer shelfNameCd);

    List<ShelfNamePatternVo> getShelfPatternForArea(@Param("companyCd") String companyCd);

    int setPatternList( List<ShelfPatternDto> shelfPatternDto, String companyCd, String authorCd);

    List<String> getPatternName(@Param("list")List<ShelfPatternDto> shelfPatternDto,@Param("companyCd") String companyCd);

    List<Map<String, Object>> selectPatternCommonPartsData(Integer priorityOrderCd);

    List<ShelfPatternNameVO> getPatternForStorel(String storeIsCore,String companyCd,Map<String,Object> map);

    List<Integer> getpatternIdOfFilename(String fileName, String companyCd);

    List<String> isCompany(List<String> shelfPatternList);

    List<Integer> getShelfPts(List<String> storeCd, String companyCd);


    Set<String> getExistSpecialUse(List<String> list);

    List<String> getCommonPartsData(String companyCd);
}
