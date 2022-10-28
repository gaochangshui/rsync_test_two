package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.AllParamConfigVO;

import java.util.Map;

public interface ParamConfigService {

    Map<String, Object> getParamConfigList(Integer showItemCheck);

    Map<String, Object> getCompanyConfig(Map<String, Object> map);

    Map<String, Object> setCompanyConfig(Map<String, Object> map);

    Map<String, Object> getCompanyList();

    AllParamConfigVO getAllParamConfig();

    Map<String, Object> updateParamConfig(AllParamConfigVO allParamConfigVO);
    Map<String, Object> getCompanyParam(String companyCd);

    Map<String, Object> getCommonMaster(String companyCd);
}
