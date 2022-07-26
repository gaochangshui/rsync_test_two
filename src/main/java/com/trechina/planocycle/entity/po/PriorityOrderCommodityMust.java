package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class PriorityOrderCommodityMust {
    private String companyCd;

    private Integer priorityOrderCd;

    private String branchOrigin;

    private String branch;

    private String jan;

    private Integer shelfPatternCd;

    private Integer beforeFlag;

    private Integer flag;

}
