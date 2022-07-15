package com.trechina.planocycle.entity.po;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPowerWKSyokika {
    private String jan;
    private String skuName;
    private String classifyBig;
    private String classifyMiddle;
    private String classifySmall;
    private String classifyFine;
    // POS
    private BigDecimal posAmount;
    private BigDecimal posNum;
    private BigDecimal branchAmount;
    private BigDecimal branchNum;
    private BigDecimal compareAmount;
    private BigDecimal compareNum;
    private BigDecimal branchCompareAmount;
    private BigDecimal branchCompareNum;


}
