package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class PriorityOrderAttrTree {
    private Integer prdCd;
    private String prdName;
    private boolean show = true;
    private List<PriorityOrderAttrTree> children;

}
