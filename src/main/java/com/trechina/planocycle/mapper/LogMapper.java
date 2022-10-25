package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ErrorMsg;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface LogMapper {
    int saveErrorLog(String controller, String params, String msg, String browser, String authorCd, String requestUri);

    int deleteLog();

    void addTimeLog(String methodName, LocalDateTime startTime, LocalDateTime endTime, String params, Long totalTime);
    List<ErrorMsg> selectErrorLog();

    void updateErrorLogFlag(Integer id);
}
