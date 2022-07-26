package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkPriorityAllRestrictResult {
    private String companyCd;

    private String authorCd;

    private Integer priorityAllCd;

    private Integer patternCd;

    private Long restrictCd;

    private String zokusei1;

    private String zokusei2;

    private String zokusei3;

    private String zokusei4;

    private String zokusei5;

    private String zokusei6;

    private String zokusei7;

    private String zokusei8;

    private String zokusei9;

    private String zokusei10;

    private BigDecimal tanaCnt;

    private Long skuCnt;

}
