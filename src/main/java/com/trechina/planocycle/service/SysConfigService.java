package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;

import java.util.Map;

public interface  SysConfigService {
    Map<String, Object> getShowJanSku(EnterpriseAxisDto enterpriseAxisDto);
}
