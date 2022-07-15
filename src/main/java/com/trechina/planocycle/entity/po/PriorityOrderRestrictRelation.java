package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class PriorityOrderRestrictRelation {
    private String companyCd;

    private Integer priorityOrderCd;

    private Integer taiCd;

    private Integer tanaCd;

    private Long restrictCd;

}