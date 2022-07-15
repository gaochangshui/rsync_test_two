package com.trechina.planocycle.controller;


import com.alibaba.fastjson.JSON;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import org.junit.Test;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.servlet.http.Cookie;
import javax.swing.plaf.basic.BasicTextUI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BasicPatternMstControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void autoDetectTest() throws Exception {
        BasicPatternAutoDetectVO basicPatternAutoDetectVO = new BasicPatternAutoDetectVO();
        basicPatternAutoDetectVO.setShelfPatternCd(520);
        basicPatternAutoDetectVO.setAttrList("1,2,3");
        basicPatternAutoDetectVO.setPriorityOrderCd(596);
        basicPatternAutoDetectVO.setCompanyCd("0001");
        basicPatternAutoDetectVO.setCommonPartsData("{\"prodIsCore\":\"1\",\"prodMstClass\":\"0000\"}");

        Cookie cookie = new Cookie("MSPACEDGOURDLP", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNjUyNzQ1NTM0OTUxIiwiaXNzIjoiU3BhY2Vnb3VyZC5KV1QiLCJzdWIiOiJvYXV0aCIsImF1ZCI6IjEwMjE4NTA0IiwiaWF0IjoxOTMyMjcwOTUzLCJleHAiOjE5MzIyNzA5NTMsInN5c3RlbXMiOiJTYWxlUmVwb3J0cyxNYXN0ZXIsVGFuYVdhcmksSURQT1MiLCJuYmYiOjE5MzIyNzA5NTMsImluY2hhcmdlIjoiNjQzMzJlLGFiYzAwMSw4N2M2ZjQsYWFmZWMzLDJlYWEzMSw5OTk5OTksOWZmZmZmLGFiYzAwNCxhYmMwMDIsYWJjMDAzLGFiYzAwMCxkSzBkQjEseEwwY0oyLHJEMGRCMCxoRjVqUjYsY0EyZEYwLDk5OTk5OCw5OTk5OTcsNmIyZjUzIn0.aJMDT7TDD42PpTWsdYVTgHsihKr7jpxuKhd-c3AJd1I");

        mockMvc.perform(post("/planoCycle/BasicPatternMst/autoDetect")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(JSON.toJSONString(basicPatternAutoDetectVO))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }
}
