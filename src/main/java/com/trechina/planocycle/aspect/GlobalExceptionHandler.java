package com.trechina.planocycle.aspect;

import cn.hutool.extra.mail.MailAccount;
import com.alibaba.fastjson.JSON;
import com.trechina.planocycle.config.MailConfig;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.utils.MailUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Component
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Value("projectIds")
    private String projectIds;
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String,Object> handlerException(Exception e){
        logger.error("",e);

        MailAccount account = MailConfig.getMailAccount(!projectIds.equals("nothing"));
        MailUtils.sendEmail(account, "10218504chen_ke@cn.tre-inc.com", "test","testtest");
        return ResultMaps.error(ResultEnum.FAILURE, JSON.toJSONString(e));
    }
}
