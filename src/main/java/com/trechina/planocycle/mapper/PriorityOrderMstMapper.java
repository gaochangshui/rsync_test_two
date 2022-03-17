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

    int insert(PriorityOrderMst record);

    List<PriorityOrderMst> selectByPrimaryKey(@Param("companyCd") String companyCd);

    int selectPriorityOrderCount(@Param("lists") List<String> companyCd);

    Map<String,Object> selectProductPowerCd(Integer priorityOrderCd);

    int deleteforid(Integer priorityOrderCd);

    String selectPriorityOrderCdForProdCd(String companyCd, Integer productPowerCd);
}
