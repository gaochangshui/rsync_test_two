package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.PriorityOrderMst;
import com.trechina.planocycle.entity.vo.StarReadingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderMstMapper {

    List<TableNameDto> getTableNameByCompanyCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);


    int deleteByAuthorCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderMst> selectByPrimaryKey(@Param("companyCd") String companyCd);

    int selectPriorityOrderCount(@Param("companyCd") String companyCd);


    String selectPriorityOrderCdForProdCd(String companyCd, Integer productPowerCd);

    /**
     * 論理削除、delete_の更新flag=1
     *
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     * @return
     */
    int logicDeleteByPriorityOrderCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    /**
     * ワークテンポラリ・テーブルからクエリー・データを実際のテーブルに挿入
     *
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     * @return
     */
    int insertBySelect(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,
                       @Param("priorityOrderCd") Integer priorityOrderCd, @Param("priorityOrderName") String priorityOrderName);

    int selectByOrderName(@Param("priorityOrderName") String priorityOrderName, @Param("companyCd") String companyCd,
                          @Param("authorCd") String authorCd);

    int selectByPriorityOrderCd(@Param("priorityOrderCd") Integer priorityOrderCd);

    PriorityOrderMst selectOrderMstByPriorityOrderCd(@Param("priorityOrderCd") Integer priorityOrderCd);

    int updateOrderName(@Param("priorityOrderCd") Integer priorityOrderCd, @Param("priorityOrderName") String priorityOrderName);

    //テンポラリ・テーブル・ストア
    int setPartition(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd") String authorCd,
                     @Param("partition") Integer partition, Integer heightSpace, @Param("tanaWidthCheck") Integer tanaWidthCheck);

    PriorityOrderMstDto getPatternOrProduct(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    PriorityOrderAttrDto selectCommonPartsData(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    PriorityOrderAttrDto getCommonPartsData(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    void updateModeCheck(@Param("item") StarReadingVo starReadingVo);

    Integer getModeCheck(Integer priorityOrderCd);

    List<String> getBasicAllList(String companyCd, Integer priorityOrderCd);
}
