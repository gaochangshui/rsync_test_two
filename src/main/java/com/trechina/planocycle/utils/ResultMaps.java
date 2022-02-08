package com.trechina.planocycle.utils;

import com.trechina.planocycle.enums.ResultEnum;

import java.util.HashMap;
import java.util.Map;

public final class ResultMaps {
    /**
     * 返回带数据
     * @param resultEnum
     * @param data
     * @return
     */
    public final static Map<String,Object> result(ResultEnum resultEnum, Object data){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", resultEnum.getCode());
        map.put("msg", resultEnum.getMsg());
        map.put("data", data);
        return map;
    }

    /**
     * 返回不带数据
     * @param resultEnum
     * @return
     */
    public final static Map<String,Object> result(ResultEnum resultEnum){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", resultEnum.getCode());
        map.put("msg", resultEnum.getMsg());
        return map;
    }
}
