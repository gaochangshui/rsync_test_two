package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductPowerMstData {
    private String companyCd;
    private Long productPowerCd;
    private String jan;
    private String skuName;
    private String classifyBig;

    private String classifyMiddle;
    private String classifySmall;
    private String classifyFine;
    // POS
    private BigDecimal pdPosAmount = new BigDecimal("0.00");
    private BigDecimal pdPosNum= new BigDecimal("0.00");
    private BigDecimal pdBranchAmount= new BigDecimal("0.00");
    private BigDecimal pdBranchNum= new BigDecimal("0.00");
    private BigDecimal pdCompareAmount= new BigDecimal("0.00");
    private BigDecimal pdCompareNum= new BigDecimal("0.00");
    private BigDecimal pdBranchCompareAmount= new BigDecimal("0.00");
    private BigDecimal pdBranchCompareNum= new BigDecimal("0.00");
    // 顧客グループ
    private BigDecimal gdPosAmount= new BigDecimal("0.00");
    private BigDecimal gdPosNum= new BigDecimal("0.00");
    private BigDecimal gdBranchAmount= new BigDecimal("0.00");
    private BigDecimal gdBranchNum= new BigDecimal("0.00");
    private BigDecimal gdCompareAmount= new BigDecimal("0.00");
    private BigDecimal gdCompareNum= new BigDecimal("0.00");
    private BigDecimal gdBranchCompareAmount= new BigDecimal("0.00");
    private BigDecimal gdBranchCompareNum= new BigDecimal("0.00");
    // プロジェクト
    private BigDecimal item1= new BigDecimal("0.00");
    private BigDecimal item2= new BigDecimal("0.00");
    private BigDecimal item3= new BigDecimal("0.00");
    private BigDecimal item4= new BigDecimal("0.00");
    private BigDecimal item5= new BigDecimal("0.00");
    private BigDecimal item6= new BigDecimal("0.00");
    private BigDecimal item7= new BigDecimal("0.00");
    private BigDecimal item8= new BigDecimal("0.00");
    private BigDecimal item9= new BigDecimal("0.00");
    private BigDecimal item10= new BigDecimal("0.00");
    private Integer rankNum;
    //rank

    private Integer pdPosAmountRank=0;
    private Integer pdPosNumRank=0;
    private Integer pdBranchAmountRank=0;
    private Integer pdBranchNumRank=0;
    private Integer pdCompareAmountRank=0;
    private Integer pdCompareNumRank=0;
    private Integer pdBranchCompareAmountRank=0;
    private Integer pdBranchCompareNumRank=0;
    private Integer gdPosAmountRank=0;
    private Integer gdPosNumRank=0;
    private Integer gdBranchAmountRank=0;
    private Integer gdBranchNumRank=0;
    private Integer gdCompareAmountRank=0;
    private Integer gdCompareNumRank=0;
    private Integer gdBranchCompareAmountRank=0;
    private Integer gdBranchCompareNumRank=0;
    private Integer item1Rank=0;
    private Integer item2Rank=0;
    private Integer item3Rank=0;
    private Integer item4Rank=0;
    private Integer item5Rank=0;
    private Integer item6Rank=0;
    private Integer item7Rank=0;
    private Integer item8Rank=0;
    private Integer item9Rank=0;
    private Integer item10Rank=0;

    private Integer rankResult;

}
