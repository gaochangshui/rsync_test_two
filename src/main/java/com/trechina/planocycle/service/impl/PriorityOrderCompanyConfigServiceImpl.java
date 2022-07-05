package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.CompanyConfig;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderCompanyConfigMapper;
import com.trechina.planocycle.service.PriorityOrderCompanyConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class PriorityOrderCompanyConfigServiceImpl implements PriorityOrderCompanyConfigService {
    @Autowired
    private PriorityOrderCompanyConfigMapper priorityOrderCompanyConfigMapper;
    @Autowired
    private HttpSession session;
    @Override
    public Map<String, Object> saveUserCompany(CompanyConfig companyConfig) {
        try {
            String companyCd = companyConfig.getCompanyCd();
            String authorCd = session.getAttribute("aud").toString();
            priorityOrderCompanyConfigMapper.deleteUserCompany(authorCd);
            priorityOrderCompanyConfigMapper.saveUserCompany(companyCd,authorCd);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> getUserCompany() {
        String authorCd = session.getAttribute("aud").toString();
        String userCompany = priorityOrderCompanyConfigMapper.getUserCompany(authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS,userCompany);
    }
}
