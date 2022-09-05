package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Data
public class StarReadingVo {
    private List<LinkedHashMap<String,Object>> data;
    private String column;
    private String header;
    private Integer modeCheck;
    private Map<String,Object> group;
    private String companyCd;
    private Integer priorityOrderCd;

}
