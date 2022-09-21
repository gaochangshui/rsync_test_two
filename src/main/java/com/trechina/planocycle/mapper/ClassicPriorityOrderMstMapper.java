package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ShelfPtsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.PriorityOrderMst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderMstMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("list") PriorityOrderMstDto list, @Param("authorCd") String authorCd);

    List<PriorityOrderMst> selectByPrimaryKey(@Param("companyCd") String companyCd);

    Integer selectModeCheck(@Param("priorityOrderCd") Integer priorityOrderCd);

    int selectPriorityOrderCount(@Param("lists") List<String> companyCd);

    Map<String,Object> selectProductPowerCd(Integer priorityOrderCd);

    int deleteforid(Integer priorityOrderCd);

    String selectPriorityOrderCdForProdCd(String companyCd, Integer productPowerCd);

    List<TableNameDto> getTableNameByCompanyCd(String companyCd, String authorCd);

    String getCommonPartsData(String companyCd, Integer priorityOrderCd);

    /**
     * テンポラリ・テーブルの削除
     * @param priorityOrderCd
     */
    void deleteWork(Integer priorityOrderCd);

    void setWork(@Param("list") PriorityOrderDataDto priorityOrderDataDto, @Param("authorCd") String authorCd,@Param("date")String date);

    void setWorkForFinal(String companyCd, Integer priorityOrderCd,String date,Integer newPriorityOrderCd);

    PriorityOrderMstDto getPriorityOrderMst(String companyCd, Integer priorityOrderCd);

    List<ShelfPtsDataDto> selectPattern(String companyCd, Integer priorityOrderCd);

    String getProductName(String companyCd, Integer priorityOrderCd);

    List<String> getShelfPatternName(String companyCd, Integer priorityOrderCd);

    String getMakerCol(String company,String classCd);
}
