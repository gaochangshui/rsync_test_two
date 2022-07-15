package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.JanParamVO;

import java.util.Map;

public interface MstJanParamService {

    Map<String, Object> getAttributeTree(JanParamVO janParamVO);

    Map<String, Object> getMaintenanceItem();

}
