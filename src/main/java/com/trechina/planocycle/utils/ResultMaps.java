package com.trechina.planocycle.utils;

import com.trechina.planocycle.enums.ResultEnum;

import java.util.HashMap;
import java.util.Map;

public final class ResultMaps {
    /**
     * 返回数据
     * @param resultEnum
     * @param data
     * @return
     */
    public static Map<String,Object> result(ResultEnum resultEnum, Object data){
        Map<String, Object> map = new HashMap<>();
        map.put("code", resultEnum.getCode());
        map.put("msg", resultEnum.getMsg());
        map.put("data", data);
        return map;
    }

    /**
     * 返回いいえ数据
     * @param resultEnum
     * @return
     */
    public static Map<String,Object> result(ResultEnum resultEnum){
        Map<String, Object> map = new HashMap<>();
        map.put("code", resultEnum.getCode());
        map.put("msg", resultEnum.getMsg());
        return map;
    }

    public static Map<String,Object> error(ResultEnum resultEnum, String error){
        Map<String, Object> map = new HashMap<>();
        map.put("code", resultEnum.getCode());
        map.put("msg", resultEnum.getMsg());
        map.put("error", error);
        return map;
    }

    public static Map<String,Object> result(Integer code, String msg){
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", msg);
        return map;
    }
}
