package com.trechina.planocycle.entity.dto;

import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import lombok.Data;

@Data
public class WorkPriorityAllRestrictRelationDto extends WorkPriorityOrderRestrictRelation {
    private Integer shelfPatternCd;

}
