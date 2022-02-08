package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.Jans;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JansMapper {
    int deleteByPrimaryKey();


    int updateTransfer();
}
