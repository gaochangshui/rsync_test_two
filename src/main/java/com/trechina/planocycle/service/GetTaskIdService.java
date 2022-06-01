package com.trechina.planocycle.service;

import java.util.Map;

public interface GetTaskIdService {
    /**
     * cgiつかむ取taskid
     * @param para
     * @return
     */
    Map<String, Object> getTaskId(Map<String, Object> para);
}
