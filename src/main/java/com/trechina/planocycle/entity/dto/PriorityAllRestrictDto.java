package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriorityAllRestrictDto {
    private String companyCd;
    private Integer priorityAllCd;
    private String authorCd;
    private Integer patternCd;
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
    private Long restrictCd;
    private BigDecimal tanaCnt;
    private BigDecimal basicTanaCnt;
    private Integer skuCnt;

}
