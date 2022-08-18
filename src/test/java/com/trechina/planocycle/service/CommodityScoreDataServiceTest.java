package com.trechina.planocycle.service;

import com.trechina.planocycle.mapper.PriorityOrderMstAttrSortMapper;
import com.trechina.planocycle.mapper.WorkPriorityAllResultDataMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CommodityScoreDataServiceTest {

    @Autowired
    private CommodityScoreDataService commodityScoreDataService;
    @Autowired
    private WorkPriorityAllResultDataMapper workPriorityAllResultDataMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    void getCommodityScoreData() {

        List<Integer> list = new ArrayList<>();
        list.add(1);
        //priorityOrderMstAttrSortMapper.setAttrList("0001",123,list);
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
