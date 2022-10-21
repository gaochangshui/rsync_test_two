package com.trechina.planocycle.service;

import java.util.Map;

public interface ParamConfigService {

    Map<String, Object> getParamConfigList(Integer showItemCheck);

    Map<String, Object> getCompanyConfig(Map<String, Object> map);

    Map<String, Object> setCompanyConfig(Map<String, Object> map);
}
