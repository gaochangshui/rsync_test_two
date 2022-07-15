package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class ProductPowerSyokika {
    private String companyCd;
    private String productPowerCd;
    private String jan;
    private String skuName;
    private String classifyBig;
    private String classifyMiddle;
    private String classifySmall;
    private String classifyFine;
    // POS
    private String posAmount;
    private Integer posNum;
    private String branchAmount;
    private Integer branchNum;
    private String compareAmount;
    private Integer compareNum;
    private String branchCompareAmount;
    private Integer branchCompareNum;

}
