package com.trechina.planocycle.utils;

import com.google.common.base.Strings;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonUtil {
    private CommonUtil(){}

    /**
     * sort
     * @param ptsJanList
     * @param janNewList
     * @param rankName
     * @return
     */
    public static List<Map<String, Object>> janSort(List<Map<String, Object>> ptsJanList, List<Map<String, Object>> janNewList, String rankName) {
        ptsJanList = ptsJanList.stream().sorted(Comparator.comparing(map -> MapUtils.getInteger(map, rankName))).collect(Collectors.toList());
        janNewList = janNewList.stream().sorted(Comparator.comparing(map -> MapUtils.getInteger(map, rankName))).collect(Collectors.toList());
        janNewList = janNewList.stream().filter(map->map.get("errMsg")==null || !map.get("errMsg").toString().equals("pts棚に並んでいる可能性がありますので削除してください。")).collect(Collectors.toList());
        int i = 1;
        for (Map<String, Object> objectMap : ptsJanList) {
            objectMap.put(rankName,i++);
        }
        for (Map<String, Object> objectMap : janNewList) {
            if (Integer.parseInt(objectMap.get(rankName).toString())>ptsJanList.size()){
                ptsJanList.add(objectMap);
            }else {
                ptsJanList.add(Integer.parseInt(objectMap.get(rankName).toString())-1,objectMap);
            }
        }
        i = 1;
        for (Map<String, Object> objectMap : ptsJanList) {
            objectMap.put(rankName,i++);
        }
        return ptsJanList;
    }

    public static String defaultIfEmpty(String val, String defaultVal){
        return Strings.isNullOrEmpty(val)?defaultVal:val;
    }

}
