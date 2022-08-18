package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class PriorityOrderDataDto {
    private String companyCd;
    private Integer productPowerCd;
    private String shelfPatternCd;
    private LinkedHashMap<String,Object> attrList;
    private Integer priorityOrderCd;
    private String attrOption;
    private  Integer flag;
    private Integer isCover;

}
