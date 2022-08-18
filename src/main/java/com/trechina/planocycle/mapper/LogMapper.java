package com.trechina.planocycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface LogMapper {
    int saveErrorLog(String controller, String params, String msg);

    int deleteLog();

    void addTimeLog(String methodName, LocalDateTime startTime, LocalDateTime endTime, String params, Long totalTime);


}
