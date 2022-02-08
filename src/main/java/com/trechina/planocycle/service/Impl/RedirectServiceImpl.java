package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.RedirectService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedirectServiceImpl implements RedirectService {
    @Override
    public Map<String, Object> getRedirectUrl(String pathName) {
        String[] path = pathName.split("\\?");
        Map<String,Object> result = new HashMap<>();
        result.put("path",path[0]);
        
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }
}
