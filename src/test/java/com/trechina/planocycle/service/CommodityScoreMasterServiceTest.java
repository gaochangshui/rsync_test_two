package com.trechina.planocycle.service;

import com.trechina.planocycle.service.impl.CommodityScoreMasterServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommodityScoreMasterServiceTest {
    @Autowired

    CommodityScoreMasterServiceImpl commodityScoreMasterService = new CommodityScoreMasterServiceImpl();
    String conpanyCd;
    @Before
    public void setUp() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("inCharge","test");
        session.setAttribute("MSPACEDGOURDLP","test");
        conpanyCd = "87c6f4";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getEnterpriseInfo() {
//
//        System.out.println(conpanyCd);
//        Map<String,Object> result = commodityScoreMasterService.getEnterpriseInfo();
//        assertEquals(result.get("code"),101);
    }

    @Test
    public void getCommodityListInfo() {
    }

    @Test
    public void getCommodityParam() {
    }

    @Test
    public void setCommodityList() {
    }

    @Test
    public void setCommodityParam() {
    }

    @Test
    public void getChanelInfo() {
    }

    @Test
    public void getPrefectureInfo() {
    }

    @Test
    public void delCommodityParam() {
    }

    @Test
    public void productPowerParamAttr() {
    }

    @Test
    public void productPowerParamAttrName() {
    }

    @Test
    public void getAttrAndItmemInfo() {
    }
}
