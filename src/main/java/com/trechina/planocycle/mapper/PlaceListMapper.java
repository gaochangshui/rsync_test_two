package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlaceListMapper {

    List<String> getPlaceList();
}
