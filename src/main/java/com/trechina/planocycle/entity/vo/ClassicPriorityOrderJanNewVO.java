package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClassicPriorityOrderJanNewVO {
    private String janNew;

    private String janName;

    private String attr;

    private Integer rank;

    private Integer branchNum;

    private BigDecimal branchAccount;

    private String errMsg;

}
