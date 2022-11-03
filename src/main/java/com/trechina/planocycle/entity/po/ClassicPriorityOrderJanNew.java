package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ClassicPriorityOrderJanNew {
    private String companyCd;

    private Integer priorityOrderCd;

    private String janNew;

    private Integer rank;

    private String branchNum;

    private BigDecimal branchAccount;

    private String nameNew;

    private String actualityCompareBranch;

    private String exceptBranch;


}
