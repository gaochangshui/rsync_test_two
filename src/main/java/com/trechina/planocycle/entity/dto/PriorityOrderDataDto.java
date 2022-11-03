package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class PriorityOrderDataDto {
    private String companyCd;
    private Integer productPowerCd;
    private String shelfPatternCd;
    private String shelfCd;
    private LinkedHashMap<String,Object> attrList;
    private Integer priorityOrderCd;
    private String attrOption;
    private  Integer flag;
    private Integer isCover;
    private List<Map<String,Object>> referenceData;

}
