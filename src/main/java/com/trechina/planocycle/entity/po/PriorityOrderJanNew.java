package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriorityOrderJanNew {
    private String companyCd;

    private Integer priorityOrderCd;

    private String janNew;

    private Integer rank;

    private Integer branchNum;

    private BigDecimal branchAccount;

}
