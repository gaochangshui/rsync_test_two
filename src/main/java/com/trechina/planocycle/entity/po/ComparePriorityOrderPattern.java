package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class ComparePriorityOrderPattern {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer shelfNameCd;
    private Integer compareFlag;
    private Integer repeatFlag;
}
