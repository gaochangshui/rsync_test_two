package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class WorkPriorityAllRestrictRelation {
    private Integer priorityAllCd;

    private String companyCd;

    private String authorCd;

    private Integer patternCd;

    private Integer taiCd;

    private Integer tanaCd;

    private Short tanaType;

    private Long restrictCd;

}
