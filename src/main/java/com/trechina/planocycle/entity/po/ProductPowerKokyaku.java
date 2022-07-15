package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 顧客グループ
 *
 */
@Data
public class ProductPowerKokyaku {
    private String companyCd;
    private Long productPowerCd;
    private String jan;
    private BigDecimal posAmount;
    private BigDecimal posNum;
    private BigDecimal branchAmount;
    private BigDecimal branchNum;
    private BigDecimal compareAmount;
    private BigDecimal compareNum;
    private BigDecimal branchCompareAmount;
    private BigDecimal branchCompareNum;

}
