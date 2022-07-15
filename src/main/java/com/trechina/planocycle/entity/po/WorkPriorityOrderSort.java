package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class WorkPriorityOrderSort {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer zokuseiId;
    private String zokuseiName;
    private Integer sortNum;
    private Boolean disabled = false;

}
