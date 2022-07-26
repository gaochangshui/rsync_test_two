package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class PriorityOrderCommodityNot {
    private String companyCd;

    private Integer priorityOrderCd;

    private String branch;

    private String jan;

    private Integer shelfPatternCd;

    private String branchOrigin;

    private Integer beforeFlag;

    private Integer flag;
}
