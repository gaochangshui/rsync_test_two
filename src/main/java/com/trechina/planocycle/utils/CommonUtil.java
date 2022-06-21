package com.trechina.planocycle.utils;

import com.trechina.planocycle.constant.MagicString;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {
    private CommonUtil(){}

    public static List<String> getZokuseiList(Integer zokuseiCount){
        List<String> zokuseiList = new ArrayList();
        for (Integer integer = 1; integer <= zokuseiCount; integer++) {
            zokuseiList.add(MagicString.ZOKUSEI_PREFIX+integer);
        }
        return zokuseiList;
    }
}
