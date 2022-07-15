package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.util.List;
@Data
public class PriorityOrderAttrValue {
    private String attrCd;
    private String classifyCd;
    private String attrName;
    private String classifyName;
    private List<PriorityOrderAttrValue> children;
    private Boolean show = true;

}
