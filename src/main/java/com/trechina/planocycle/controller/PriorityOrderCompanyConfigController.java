package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.CompanyConfig;
import com.trechina.planocycle.service.PriorityOrderCompanyConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/planoCycleApi/PriorityOrderCompanyConfig")
public class PriorityOrderCompanyConfigController {
    @Autowired
    private PriorityOrderCompanyConfigService priorityOrderCompanyConfigService;
    /**
     ** ユーザー選択を保存するエンタープライズ
     * @return
     */
    @PostMapping("saveUserCompany")
    public Map<String,Object> saveUserCompany(@RequestBody CompanyConfig companyConfig) {
        return  priorityOrderCompanyConfigService.saveUserCompany(companyConfig);
    }


    /**
     ** ユーザーが選択したエンタープライズの取得
     * @return
     */
    @GetMapping("getUserCompany")
    public Map<String,Object> getUserCompany() {
        return  priorityOrderCompanyConfigService.getUserCompany();
    }
}
