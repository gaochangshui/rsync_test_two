package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.SalesReportService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Stream;

@Service
public class SalesReportServiceImpl implements SalesReportService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;

    /**
     * 获取棚别实际数据
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getShelfReportInfo(String taskID) {
        logger.info("获取棚别实际数据参数："+taskID);
        String tokenInfo = String.valueOf(session.getAttribute("MSPACEDGOURDLP"));
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("TaskQuery").toString();
        cgiUtils cgi = new cgiUtils();
        Map<String,Object> result = cgi.postCgiOfWeb(path,taskID,tokenInfo);
        if(!"9".equals(result.get("data"))){
            result.put("data",JSON.parseArray(String.valueOf(result.get("data").toString().replaceAll(" ",""))));
        }
        logger.info("获取棚别实际数据参数cgi返回数据："+result.toString());


        return result;
    }

    /**
     * 获取商品别实际数据
     *
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getProductReportInfo(String taskID) {
        logger.info("获取棚别实际数据参数："+taskID);
        String tokenInfo = String.valueOf(session.getAttribute("MSPACEDGOURDLP"));
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("TaskQuery").toString();
        cgiUtils cgi = new cgiUtils();
        Map<String,Object> result = cgi.postCgiOfWeb(path,taskID,tokenInfo);
        logger.info("获取棚别实际数据参数cgi返回数据："+result.toString());
        if(!"9".equals(result.get("data"))){
            result.put("data",JSON.parseArray(String.valueOf(result.get("data").toString().replaceAll(" ",""))));
        }
        return result;
    }

    /**
     * 获取单jan别实际数据
     *
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getJanReportDetailInfo(String taskID,String startDay,String endDay) {
        logger.info("获取单jan别实际数据参数："+taskID+"开始日,"+startDay+"结束日,"+endDay);
        String tokenInfo = String.valueOf(session.getAttribute("MSPACEDGOURDLP"));
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("TaskQuery").toString();
        cgiUtils cgi = new cgiUtils();
        Map<String,Object> result =  cgi.postCgiOfWeb(path,taskID,tokenInfo);
        logger.info("获取单jan别实际数据参数cgi返回数据："+result.toString());
        if(!"9".equals(result.get("data"))){
            JSONArray jsonArray = JSON.parseArray(String.valueOf(result.get("data").toString().replaceAll(" ","")));
            if (jsonArray.size()>0) {
                Stream title = ((Map) jsonArray.get(0)).keySet().stream().sorted();
                jsonArray.add(0, title);
            }
            result.put("data", jsonArray);
        }
        return result;
    }
}
