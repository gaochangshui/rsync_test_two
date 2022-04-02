package com.trechina.planocycle.service;

import com.trechina.planocycle.mapper.WorkPriorityAllResultDataMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CommodityScoreDataServiceTest {

    @Autowired
    private CommodityScoreDataService commodityScoreDataService;
    @Autowired
    private WorkPriorityAllResultDataMapper workPriorityAllResultDataMapper;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @org.junit.jupiter.api.Test
    void getCommodityScoreData() {
//        ProductPowerDataForCgiDto productPowerDataForCgiDto = new ProductPowerDataForCgiDto();
//        productPowerDataForCgiDto.setCompany("87c6f4");
//        productPowerDataForCgiDto.setProductPowerNo(1);
//        productPowerDataForCgiDto.setMode("shoki_data");
//        Map<String,Object> result = commodityScoreDataService.getCommodityScoreData(productPowerDataForCgiDto);
//        assertEquals(result.get("code"),101);

    }

    @Test
    void getAttrFileSaveForCgi() {
//        String companyCd="87c6f4";
//        String filename="test";
//        String datacd="1";
//        Integer productPowerNo=1;
//        String dataNm="1@test";
//        Map<String,Object> result = commodityScoreDataService.getAttrFileSaveForCgi(companyCd,filename,datacd,productPowerNo,dataNm);
//        assertEquals(result.get("code"),101);
    }
}
