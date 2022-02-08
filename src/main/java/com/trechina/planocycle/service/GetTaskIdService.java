package com.trechina.planocycle.service;

import java.util.Map;

public interface GetTaskIdService {
    /**
     * 访问cgi获取taskid
     * @param para
     * @return
     */
    Map<String, Object> getTaskId(Map<String, Object> para);
}
