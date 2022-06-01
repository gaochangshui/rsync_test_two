package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.service.SalesReportService;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;

@Service
public class SalesReportServiceImpl implements SalesReportService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private cgiUtils cgiUtil;

    private static final String FINAL_TASK_QUERY="TaskQuery";
    private static final String FINAL_PATH_CONFIG = "pathConfig";
    private static final String FINAL_MSPACEDGOURDLP = "MSPACEDGOURDLP";

    /**
     * 棚別実績データの取得
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getShelfReportInfo(String taskID) {
        logger.info("つかむ取棚別実際数据参数：{}",taskID);
        String tokenInfo = String.valueOf(session.getAttribute(FINAL_MSPACEDGOURDLP));
        ResourceBundle resourceBundle = ResourceBundle.getBundle(FINAL_PATH_CONFIG);
        String path = resourceBundle.getString(FINAL_TASK_QUERY);
        Map<String,Object> result = cgiUtil.postCgiOfWeb(path,taskID,tokenInfo);
        if(!"9".equals(result.get("data"))){
            result.put("data",JSON.parseArray(result.get("data").toString().replaceAll(" ", "")));
        }
        logger.info("つかむ取棚別実際数据参数cgi返回数据：{}", result);


        return result;
    }

    /**
     * 商品別実績データの取得
     *
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getProductReportInfo(String taskID) {
        logger.info("つかむ取棚別実際数据参数：{}",taskID);
        String tokenInfo = String.valueOf(session.getAttribute(FINAL_MSPACEDGOURDLP));
        ResourceBundle resourceBundle = ResourceBundle.getBundle(FINAL_PATH_CONFIG);
        String path = resourceBundle.getString(FINAL_TASK_QUERY);
        Map<String,Object> result = cgiUtil.postCgiOfWeb(path,taskID,tokenInfo);
        logger.info("つかむ取棚別実際数据参数cgi返回数据：{}",result.toString());
        if(!"9".equals(result.get("data"))){
            result.put("data",JSON.parseArray(result.get("data").toString().replaceAll(" ", "")));
        }
        return result;
    }

    /**
     * 単一janの他のデータの取得
     *
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getJanReportDetailInfo(String taskID,String startDay,String endDay) {
        logger.info("つかむ取単jan別実際数据参数：{}開始日,{}終了日,{}",taskID,startDay,endDay);
        String tokenInfo = String.valueOf(session.getAttribute(FINAL_MSPACEDGOURDLP));
        ResourceBundle resourceBundle = ResourceBundle.getBundle(FINAL_PATH_CONFIG);
        String path = resourceBundle.getString(FINAL_TASK_QUERY);
        Map<String,Object> result =  cgiUtil.postCgiOfWeb(path,taskID,tokenInfo);
        logger.info("つかむ取単jan別実際数据参数cgi返回数据：{}",result.toString());
        if(!"9".equals(result.get("data"))){
            JSONArray jsonArray = JSON.parseArray(result.get("data").toString().replaceAll(" ", ""));
            if (!jsonArray.isEmpty()) {
                Stream title = ((Map) jsonArray.get(0)).keySet().stream().sorted();
                jsonArray.add(0, title);
            }
            result.put("data", jsonArray);
        }
        return result;
    }
}
