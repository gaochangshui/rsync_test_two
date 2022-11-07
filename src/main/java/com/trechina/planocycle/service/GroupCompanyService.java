package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.GroupCompanyVO;

import java.util.Map;

public interface GroupCompanyService {
    Map<String, Object> saveGroupCompany(GroupCompanyVO groupCompanyVO);
}
