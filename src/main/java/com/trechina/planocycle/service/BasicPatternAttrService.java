package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;

import java.util.Map;

public interface BasicPatternAttrService {
    Map<String, Object> getAttribute(PriorityOrderAttrDto priorityOrderAttrDto);
}
