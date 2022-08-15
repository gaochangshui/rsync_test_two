package com.trechina.planocycle.aspect;

import com.alibaba.fastjson.JSON;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Component
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String,Object> handlerException(Exception e){
        return ResultMaps.error(ResultEnum.FAILURE, JSON.toJSONString(e));
    }
}
