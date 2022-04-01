package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.GetTaskIdService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

@Service
public class GetTaskIdServiceImpl implements GetTaskIdService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private cgiUtils cgiUtil;
    /**
     * 访问cgi获取taskid
     * @param para
     * @return
     */
    @Override
    public Map<String, Object> getTaskId(Map<String, Object> para) {
        logger.info("获取taskid共同方法接收到的参数：{}",para.toString());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        // mode就是path地址的key
        String path = resourceBundle.getString(para.get("mode").toString());
        String uuid = UUID.randomUUID().toString();
        String tokenInfo = String.valueOf(session.getAttribute("MSPACEDGOURDLP"));
        para.put("guid",uuid);
        String result = cgiUtil.postCgi(path,para,tokenInfo);
        logger.info("获取taskid共同方法返回：{}",result);
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }
}
