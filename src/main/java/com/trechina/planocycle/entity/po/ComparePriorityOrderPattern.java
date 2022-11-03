package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class ComparePriorityOrderPattern {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer shelfNameCd;
    private String compareFlag;
    private String repeatFlag;
}
