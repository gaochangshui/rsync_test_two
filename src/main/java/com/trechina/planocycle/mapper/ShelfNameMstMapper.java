package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfNameDto;
import com.trechina.planocycle.entity.po.ShelfNameMst;
import com.trechina.planocycle.entity.vo.ShelfNameDataVO;
import com.trechina.planocycle.entity.vo.ShelfNameUpdVO;
import com.trechina.planocycle.entity.vo.ShelfNameVO;
import com.trechina.planocycle.entity.vo.ShelfPatternTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelfNameMstMapper {

    int insert(ShelfNameMst record);

    /**
     * 获取棚名称信息
     * @param conpanyCd
     * @return
     */
    List<ShelfNameDataVO> selectShelfNameInfo(@Param("conpanyCd") String conpanyCd);

    /**
     * 获取棚名称 Master
     * @param conpanyCd
     * @return
     */
    List<ShelfNameVO> selectShelfName(@Param("conpanyCd") String conpanyCd);

    int deleteShelfNameInfo(Integer id);

    ShelfNameUpdVO selShelfNameInfoById(Integer id);

    Integer selectDistinctName(ShelfNameDto shelfNameDto);

    List<Integer> selectPatternCd(Integer id);

    List<ShelfPatternTreeVO> selectShelfPatternTree(String companyCd);
}
