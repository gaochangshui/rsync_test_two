package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JansMapper {
    int deleteByPrimaryKey();


    int updateTransfer();

    String selectDummyJan(String companyCd, String jan);
}
