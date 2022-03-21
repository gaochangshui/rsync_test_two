package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.PriorityOrderMst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderMstMapper {

    List<TableNameDto> getTableNameByCompanyCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteByAuthorCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(PriorityOrderMst record);

    List<PriorityOrderMst> selectByPrimaryKey(@Param("companyCd") String companyCd);

    int selectPriorityOrderCount(@Param("companyCd") String companyCd);

    Map<String,Object> selectProductPowerCd(Integer priorityOrderCd);

    int deleteforid(Integer priorityOrderCd);

    String selectPriorityOrderCdForProdCd(String companyCd, Integer productPowerCd);

    /**
     * 逻辑删除，更新delete_flag=1
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     * @return
     */
    int logicDeleteByPriorityOrderCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd")Integer priorityOrderCd);

    /**
     * 从work临时表中查询数据插入到实际表中
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     * @return
     */
    int insertBySelect(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,
                       @Param("priorityOrderCd")Integer priorityOrderCd, @Param("priorityOrderName") String priorityOrderName);

    int selectByOrderName(@Param("priorityOrderName")String priorityOrderName, @Param("priorityOrderCd")Integer priorityOrderCd);
}
