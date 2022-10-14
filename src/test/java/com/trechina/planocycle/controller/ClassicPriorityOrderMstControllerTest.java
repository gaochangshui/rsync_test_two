package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.trechina.planocycle.entity.dto.PriorityOrderDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDownDto;
import com.trechina.planocycle.entity.dto.PriorityOrderUpdDto;
import com.trechina.planocycle.entity.po.PriorityOrderNumGenerator;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderNumGeneratorMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import javax.servlet.http.Cookie;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitWebConfig
public class ClassicPriorityOrderMstControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PriorityOrderNumGeneratorMapper priorityOrderNumGeneratorMapper;

    public static final String MSPACEDGOURDLP = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNjUyNzQ1NTM0OTUxIiwiaXNzIjoiU3BhY2Vnb3VyZC5KV1QiLCJzdWIiOiJvYXV0aCIsImF1ZCI6IjEwMjE4NTA0IiwiaWF0IjoxOTMyMjcwOTUzLCJleHAiOjE5MzIyNzA5NTMsInN5c3RlbXMiOiJTYWxlUmVwb3J0cyxNYXN0ZXIsVGFuYVdhcmksSURQT1MiLCJuYmYiOjE5MzIyNzA5NTMsImluY2hhcmdlIjoiNjQzMzJlLGFiYzAwMSw4N2M2ZjQsYWFmZWMzLDJlYWEzMSw5OTk5OTksOWZmZmZmLGFiYzAwNCxhYmMwMDIsYWJjMDAzLGFiYzAwMCxkSzBkQjEseEwwY0oyLHJEMGRCMCxoRjVqUjYsY0EyZEYwLDk5OTk5OCw5OTk5OTcsNmIyZjUzIn0.aJMDT7TDD42PpTWsdYVTgHsihKr7jpxuKhd-c3AJd1I";
    private Cookie cookie = new Cookie("MSPACEDGOURDLP", MSPACEDGOURDLP);
    @Test
    public void getPtsFileDownLoadTest() throws Exception {
        /**
         * 1.new priority order
         * 2.rank
         * 3.save priority order
         */
        PriorityOrderDataDto params = createPriority();

        /**
         * 4.pts出力
         */
        downloadPts(params);
    }

    private void downloadPts(PriorityOrderDataDto params) throws Exception {
        PriorityOrderPtsDownDto body = new PriorityOrderPtsDownDto();
        body.setPriorityNo(params.getPriorityOrderCd());
        body.setPtsVerison(1);
        body.setNewCutFlg(0);
        body.setCompany(params.getCompanyCd());
        body.setTaskId("");

        mockMvc.perform(post("/planoCycleApi/priority/PriorityOrderMst/getPtsFileDownLoad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(JSON.toJSONString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String jsonResult = mvcResult.getResponse().getContentAsString();
                    JSONObject jsonObject = JSONObject.parseObject(jsonResult);
                    Assert.assertEquals(jsonObject.getInteger("code"),ResultEnum.SUCCESS.getCode());
                        JSONObject data = jsonObject.getJSONObject("data");
                        String taskId = data.getString("taskId");

                        mockMvc.perform(get("/planoCycleApi/priority/PriorityOrderMst/getPtsFileDownLoadByTask?taskId=" + taskId)
                                        .cookie(cookie)
                                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                                .andDo(result -> {
                                    String header = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
                                    String fileName = header.replaceAll("^attachment;filename=|;$", "");

                                    File file = new File(this.getClass().getResource("/").getPath() + fileName);
                                    file.delete();
                                    FileOutputStream fout = new FileOutputStream(file);
                                    ByteArrayInputStream bin = new ByteArrayInputStream(result.getResponse().getContentAsByteArray());
                                    StreamUtils.copy(bin, fout);
                                    fout.close();
                                    System.out.println("is exist:" + file.exists());
                                    //assert
                                    System.out.println("file length:" + file.length());
                                    assertThat(file.exists(), equalTo(true));
                                    assertThat(file.length(), greaterThan(0L));
                                    file.delete();
                                });
                });
    }

    private PriorityOrderDataDto createPriority() throws Exception {
        PriorityOrderDataDto body = new PriorityOrderDataDto();
        body.setCompanyCd("0001");
        body.setShelfPatternCd("540,541,542,544,545");
        LinkedHashMap<String,Object> attrList = new LinkedHashMap<>();
        attrList.put("0001_0000_10", "カテゴリー(企業-基準マスタ)");
        attrList.put("0001_0000_12", "サブカテゴリー(企業-基準マスタ)");
        attrList.put("0001_0000_16", "サブセグメント(企業-基準マスタ)");
        attrList.put("0001_0000_17", "規格(企業-基準マスタ)");
        attrList.put("0001_0000_8", "部門(企業-基準マスタ)");
        attrList.put("1000_0000_10", "細分類(自社-酒分類マスタ)");
        attrList.put("1000_0000_11", "規格(自社-酒分類マスタ)");
        attrList.put("1000_0000_12", "メーカー(自社-酒分類マスタ)");
        attrList.put("1000_0000_4", "大分類(自社-酒分類マスタ)");
        body.setAttrList(attrList);
        body.setAttrOption("[{\"attr_type\":1,\"label\":\"大分類(自社-酒分類マスタ)\",\"value\":\"1000_0000_4\",\"show\":false,\"backgroundColor\":false},{\"attr_type\":2,\"label\":\"メーカー(自社-酒分類マスタ)\",\"value\":\"1000_0000_12\",\"show\":false,\"backgroundColor\":false},{\"attr_type\":2,\"label\":\"規格(自社-酒分類マスタ)\",\"value\":\"1000_0000_11\",\"show\":false,\"backgroundColor\":false},{\"attr_type\":1,\"label\":\"細分類(自社-酒分類マスタ)\",\"value\":\"1000_0000_10\",\"show\":false,\"backgroundColor\":false},{\"attr_type\":1,\"label\":\"部門(企業-基準マスタ)\",\"value\":\"0001_0000_8\",\"show\":false,\"backgroundColor\":false},{\"attr_type\":1,\"label\":\"サブカテゴリー(企業-基準マスタ)\",\"value\":\"0001_0000_12\",\"show\":false,\"backgroundColor\":false},{\"attr_type\":1,\"label\":\"カテゴリー(企業-基準マスタ)\",\"value\":\"0001_0000_10\",\"show\":false,\"backgroundColor\":false},{\"attr_type\":1,\"label\":\"サブセグメント(企業-基準マスタ)\",\"value\":\"0001_0000_16\",\"show\":false,\"backgroundColor\":false},{\"attr_type\":2,\"label\":\"規格(企業-基準マスタ)\",\"value\":\"0001_0000_17\",\"show\":false,\"backgroundColor\":false}]");

        PriorityOrderNumGenerator priorityOrderNumGenerator = new PriorityOrderNumGenerator();
        priorityOrderNumGeneratorMapper.insertPriority(priorityOrderNumGenerator);
        Integer priorityOrderCd = priorityOrderNumGenerator.getId();
        String shelfCd = "12,13";

        Integer productPowerCd = 2230;
        body.setPriorityOrderCd(priorityOrderCd);
        body.setProductPowerCd(productPowerCd);
        body.setShelfCd(shelfCd);

        mockMvc.perform(post("/planoCycleApi/priority/PriorityOrderData/getPriorityOrderData")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(JSON.toJSONString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo((handler)->{
                    MockHttpServletResponse response = handler.getResponse();
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    String result = response.getContentAsString();
                    JSONObject resultJson = JSONObject.parseObject(result);
                    Assert.assertEquals(resultJson.getInteger("code"), ResultEnum.SUCCESS.getCode());

                    attrRank(body);
                });
        return body;
    }

    private void attrRank(PriorityOrderDataDto priorityOrderDataDto) throws Exception{
        PriorityOrderUpdDto body = new PriorityOrderUpdDto();
        body.setPriorityOrderCd(priorityOrderDataDto.getPriorityOrderCd());
        body.setDelFlg(0);
        body.setCompanyCd(priorityOrderDataDto.getCompanyCd());
        body.setColNameList("attr1,attr2,attr3");

        mockMvc.perform(post("/planoCycleApi/priority/PriorityOrderData/getPriorityOrderDataUpd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie).content(JSONObject.toJSONString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    MockHttpServletResponse response = handler.getResponse();
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    String result = response.getContentAsString();
                    JSONObject resultJson = JSONObject.parseObject(result);
                    Assert.assertEquals(resultJson.getInteger("code"), ResultEnum.SUCCESS.getCode());
                    JSONArray data = resultJson.getJSONArray("data");
                    savePriority(data, priorityOrderDataDto, "");
                });
    }

    private void savePriority(JSONArray data, PriorityOrderDataDto priorityOrderDataDto, String taskId) throws Exception{
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        PriorityOrderMstDto body = new PriorityOrderMstDto();
        body.setPriorityOrderCd(priorityOrderDataDto.getPriorityOrderCd());
        body.setCompanyCd(priorityOrderDataDto.getCompanyCd());
        body.setShelfCd(priorityOrderDataDto.getShelfCd());
        body.setPriorityData(data.toJSONString());
        body.setProductPowerCd(priorityOrderDataDto.getProductPowerCd());
        body.setPriorityOrderName(MessageFormat.format("junit-test-{0}",formatter.format(now)));
        body.setShelfPatternCd(priorityOrderDataDto.getShelfPatternCd());
        body.setTaskID(taskId);

        mockMvc.perform(post("/planoCycleApi/priority/PriorityOrderMst/setPriorityOrderMst")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie).content(JSONObject.toJSONString(body)))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    MockHttpServletResponse response = handler.getResponse();
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    String result = response.getContentAsString();

                    JSONObject resultJson = JSONObject.parseObject(result);
                    Assert.assertEquals(resultJson.getInteger("code"), ResultEnum.SUCCESS.getCode());
                    String reciveTaskId = resultJson.getString("data");
                    if(!Strings.isNullOrEmpty(reciveTaskId)){
                        savePriority(data, priorityOrderDataDto, reciveTaskId);
                    }
                });
    }
}
