package com.trechina.planocycle.mapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BranchsMapper {
    int deleteByPrimaryKey();

    int updateTransfer();
}
