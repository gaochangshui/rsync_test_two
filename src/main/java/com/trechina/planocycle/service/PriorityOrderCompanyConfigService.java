package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.CompanyConfig;
import java.util.Map;

public interface PriorityOrderCompanyConfigService {
    Map<String,Object> saveUserCompany(CompanyConfig companyConfig);

    Map<String, Object> getUserCompany();

}
