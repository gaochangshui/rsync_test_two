package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkPriorityOrderJanNewAttrMapper {

   void insert(List<Integer>attrList,String companyCd,Integer priorityOrderCd,String authorCd);
   void deleteWork(String companyCd,Integer priorityOrderCd);
}
