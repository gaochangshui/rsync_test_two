package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class PriorityOrderJanProposal {
    private String companyCd;

    private Integer priorityOrderCd;

    private String janOld;

    private String janNew;

    private Integer flag;

}
