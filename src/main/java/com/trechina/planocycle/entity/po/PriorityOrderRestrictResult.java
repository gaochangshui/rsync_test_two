package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class PriorityOrderRestrictResult {
    private String companyCd;

    private Integer priorityOrderCd;

    private Long restrictCd;

    private Integer category;

    private Integer pkg;

    private Integer capacity;


}