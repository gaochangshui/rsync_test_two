package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper {
    int saveErrorLog(String controller, String params, String msg);

    int deleteLog();
}
