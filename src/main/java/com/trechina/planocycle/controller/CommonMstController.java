package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.CommonMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/Common")
public class CommonMstController {
    @Autowired
    private CommonMstService commonMstService;

    /**
     * 取得エリア
     * @return
     */
    @GetMapping("/getAreaInfo")
    public Map<String,Object> getAreaInfo(String companyCd) {
        return commonMstService.getAreaInfo(companyCd);
    }

    /**
     * 取得棚name相关的エリア
     * @param shelfNameCd
     * @return
     */
    @GetMapping("/getAreaForShelfName")
    public Map<String,Object> getAreaForShelfName(Integer shelfNameCd) {
        return commonMstService.getAreaForShelfName(shelfNameCd);
    }


}
