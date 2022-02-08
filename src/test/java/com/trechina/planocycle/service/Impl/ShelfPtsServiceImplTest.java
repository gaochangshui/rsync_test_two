package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShelfPtsServiceImplTest {
    @Autowired
    private ShelfPtsServiceImpl shelfPtsService;
    @Autowired
    private HttpSession session;

    @Test
    public void setShelfPtsInfo() {
        session.setAttribute("aud","test");
        ShelfPtsDto shelfPtsDto = new ShelfPtsDto();
        shelfPtsDto.setCompanyCd("87c6f4");
        shelfPtsDto.setFileName("11_11_11_11_★★13126_65900_001_0743164_6590可部　冷却42尺_2019春夏.csv");
        shelfPtsService.setShelfPtsInfo(shelfPtsDto,0);
    }
}
