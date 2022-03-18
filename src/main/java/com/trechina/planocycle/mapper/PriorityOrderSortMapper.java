package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriorityOrderSortMapper {
    int deleteByAuthorCd(String companyCd, String authorCd, Integer priorityOrderCd);

    void insertBySelect(String companyCd, String authorCd, Integer priorityOrderCd);
}
