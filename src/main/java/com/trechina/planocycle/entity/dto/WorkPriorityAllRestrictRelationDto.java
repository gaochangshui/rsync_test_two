package com.trechina.planocycle.entity.dto;

import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;

public class WorkPriorityAllRestrictRelationDto extends WorkPriorityOrderRestrictRelation {
    private Integer shelfPatternCd;

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }
}
