package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.Attribute;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttributeMapper {
    int deleteByPrimaryKey();

    int updateTransfer();
}
