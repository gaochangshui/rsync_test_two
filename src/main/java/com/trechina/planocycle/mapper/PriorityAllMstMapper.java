package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.PriorityAllMst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityAllMstMapper {

    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityAllCd") Integer priorityAllCd);

    List<TableNameDto> getTableNameByCompanyCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);


}
