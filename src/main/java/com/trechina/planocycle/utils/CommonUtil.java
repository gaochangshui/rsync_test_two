package com.trechina.planocycle.utils;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {
    private CommonUtil(){}

    public static List<String> getZokuseiList(Integer zokuseiCount, String prefix){
        List<String> zokuseiList = new ArrayList<>();

        for (Integer integer = 1; integer <= zokuseiCount; integer++) {
            zokuseiList.add(prefix+integer);
        }

        return zokuseiList;
    }
}
