package com.trechina.planocycle.entity.po;

import java.math.BigDecimal;

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
    private BigDecimal pPosAmount = new BigDecimal("0.00");
    private BigDecimal pPosNum= new BigDecimal("0.00");
    private BigDecimal pBranchAmount= new BigDecimal("0.00");
    private BigDecimal pBranchNum= new BigDecimal("0.00");
    private BigDecimal pCompareAmount= new BigDecimal("0.00");
    private BigDecimal pCompareNum= new BigDecimal("0.00");
    private BigDecimal pBranchCompareAmount= new BigDecimal("0.00");
    private BigDecimal pBranchCompareNum= new BigDecimal("0.00");
    // 顾客group
    private BigDecimal gPosAmount= new BigDecimal("0.00");
    private BigDecimal gPosNum= new BigDecimal("0.00");
    private BigDecimal gBranchAmount= new BigDecimal("0.00");
    private BigDecimal gBranchNum= new BigDecimal("0.00");
    private BigDecimal gCompareAmount= new BigDecimal("0.00");
    private BigDecimal gCompareNum= new BigDecimal("0.00");
    private BigDecimal gBranchCompareAmount= new BigDecimal("0.00");
    private BigDecimal gBranchCompareNum= new BigDecimal("0.00");
    // 项目
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
    private BigDecimal rankNum;
    //rank

    private Integer pPosAmountRank;
    private Integer pPosNumRank;
    private Integer pBranchAmountRank;
    private Integer pBranchNumRank;
    private Integer pCompareAmountRank;
    private Integer pCompareNumRank;
    private Integer pBranchCompareAmountRank;
    private Integer pBranchCompareNumRank;
    private Integer gPosAmountRank;
    private Integer gPosNumRank;
    private Integer gBranchAmountRank;
    private Integer gBranchNumRank;
    private Integer gCompareAmountRank;
    private Integer gCompareNumRank;
    private Integer gBranchCompareAmountRank;
    private Integer gBranchCompareNumRank;
    private Integer item1Rank;
    private Integer item2Rank;
    private Integer item3Rank;
    private Integer item4Rank;
    private Integer item5Rank;
    private Integer item6Rank;
    private Integer item7Rank;
    private Integer item8Rank;
    private Integer item9Rank;
    private Integer item10Rank;

    private Integer rankResult;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Long getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Long productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getClassifyBig() {
        return classifyBig;
    }

    public void setClassifyBig(String classifyBig) {
        this.classifyBig = classifyBig;
    }

    public String getClassifyMiddle() {
        return classifyMiddle;
    }

    public void setClassifyMiddle(String classifyMiddle) {
        this.classifyMiddle = classifyMiddle;
    }

    public String getClassifySmall() {
        return classifySmall;
    }

    public void setClassifySmall(String classifySmall) {
        this.classifySmall = classifySmall;
    }

    public String getClassifyFine() {
        return classifyFine;
    }

    public void setClassifyFine(String classifyFine) {
        this.classifyFine = classifyFine;
    }

    public BigDecimal getpPosAmount() {
        return pPosAmount;
    }

    public void setpPosAmount(BigDecimal pPosAmount) {
        this.pPosAmount = pPosAmount;
    }

    public BigDecimal getpPosNum() {
        return pPosNum;
    }

    public void setpPosNum(BigDecimal pPosNum) {
        this.pPosNum = pPosNum;
    }

    public BigDecimal getpBranchAmount() {
        return pBranchAmount;
    }

    public void setpBranchAmount(BigDecimal pBranchAmount) {
        this.pBranchAmount = pBranchAmount;
    }

    public BigDecimal getpBranchNum() {
        return pBranchNum;
    }

    public void setpBranchNum(BigDecimal pBranchNum) {
        this.pBranchNum = pBranchNum;
    }

    public BigDecimal getpCompareAmount() {
        return pCompareAmount;
    }

    public void setpCompareAmount(BigDecimal pCompareAmount) {
        this.pCompareAmount = pCompareAmount;
    }

    public BigDecimal getpCompareNum() {
        return pCompareNum;
    }

    public void setpCompareNum(BigDecimal pCompareNum) {
        this.pCompareNum = pCompareNum;
    }

    public BigDecimal getpBranchCompareAmount() {
        return pBranchCompareAmount;
    }

    public void setpBranchCompareAmount(BigDecimal pBranchCompareAmount) {
        this.pBranchCompareAmount = pBranchCompareAmount;
    }

    public BigDecimal getpBranchCompareNum() {
        return pBranchCompareNum;
    }

    public void setpBranchCompareNum(BigDecimal pBranchCompareNum) {
        this.pBranchCompareNum = pBranchCompareNum;
    }

    public BigDecimal getgPosAmount() {
        return gPosAmount;
    }

    public void setgPosAmount(BigDecimal gPosAmount) {
        this.gPosAmount = gPosAmount;
    }

    public BigDecimal getgPosNum() {
        return gPosNum;
    }

    public void setgPosNum(BigDecimal gPosNum) {
        this.gPosNum = gPosNum;
    }

    public BigDecimal getgBranchAmount() {
        return gBranchAmount;
    }

    public void setgBranchAmount(BigDecimal gBranchAmount) {
        this.gBranchAmount = gBranchAmount;
    }

    public BigDecimal getgBranchNum() {
        return gBranchNum;
    }

    public void setgBranchNum(BigDecimal gBranchNum) {
        this.gBranchNum = gBranchNum;
    }

    public BigDecimal getgCompareAmount() {
        return gCompareAmount;
    }

    public void setgCompareAmount(BigDecimal gCompareAmount) {
        this.gCompareAmount = gCompareAmount;
    }

    public BigDecimal getgCompareNum() {
        return gCompareNum;
    }

    public void setgCompareNum(BigDecimal gCompareNum) {
        this.gCompareNum = gCompareNum;
    }

    public BigDecimal getgBranchCompareAmount() {
        return gBranchCompareAmount;
    }

    public void setgBranchCompareAmount(BigDecimal gBranchCompareAmount) {
        this.gBranchCompareAmount = gBranchCompareAmount;
    }

    public BigDecimal getgBranchCompareNum() {
        return gBranchCompareNum;
    }

    public void setgBranchCompareNum(BigDecimal gBranchCompareNum) {
        this.gBranchCompareNum = gBranchCompareNum;
    }

    public BigDecimal getItem1() {
        return item1;
    }

    public void setItem1(BigDecimal item1) {
        this.item1 = item1;
    }

    public BigDecimal getItem2() {
        return item2;
    }

    public void setItem2(BigDecimal item2) {
        this.item2 = item2;
    }

    public BigDecimal getItem3() {
        return item3;
    }

    public void setItem3(BigDecimal item3) {
        this.item3 = item3;
    }

    public BigDecimal getItem4() {
        return item4;
    }

    public void setItem4(BigDecimal item4) {
        this.item4 = item4;
    }

    public BigDecimal getItem5() {
        return item5;
    }

    public void setItem5(BigDecimal item5) {
        this.item5 = item5;
    }

    public BigDecimal getItem6() {
        return item6;
    }

    public void setItem6(BigDecimal item6) {
        this.item6 = item6;
    }

    public BigDecimal getItem7() {
        return item7;
    }

    public void setItem7(BigDecimal item7) {
        this.item7 = item7;
    }

    public BigDecimal getItem8() {
        return item8;
    }

    public void setItem8(BigDecimal item8) {
        this.item8 = item8;
    }

    public BigDecimal getItem9() {
        return item9;
    }

    public void setItem9(BigDecimal item9) {
        this.item9 = item9;
    }

    public BigDecimal getItem10() {
        return item10;
    }

    public void setItem10(BigDecimal item10) {
        this.item10 = item10;
    }

    public Integer getRankResult() {
        return rankResult;
    }

    public void setRankResult(Integer rankResult) {
        this.rankResult = rankResult;
    }

    public Integer getpPosAmountRank() {
        return pPosAmountRank;
    }

    public void setpPosAmountRank(Integer pPosAmountRank) {
        this.pPosAmountRank = pPosAmountRank;
    }

    public Integer getpPosNumRank() {
        return pPosNumRank;
    }

    public void setpPosNumRank(Integer pPosNumRank) {
        this.pPosNumRank = pPosNumRank;
    }

    public Integer getpBranchAmountRank() {
        return pBranchAmountRank;
    }

    public void setpBranchAmountRank(Integer pBranchAmountRank) {
        this.pBranchAmountRank = pBranchAmountRank;
    }

    public Integer getpBranchNumRank() {
        return pBranchNumRank;
    }

    public void setpBranchNumRank(Integer pBranchNumRank) {
        this.pBranchNumRank = pBranchNumRank;
    }

    public Integer getpCompareAmountRank() {
        return pCompareAmountRank;
    }

    public void setpCompareAmountRank(Integer pCompareAmountRank) {
        this.pCompareAmountRank = pCompareAmountRank;
    }

    public Integer getpCompareNumRank() {
        return pCompareNumRank;
    }

    public void setpCompareNumRank(Integer pCompareNumRank) {
        this.pCompareNumRank = pCompareNumRank;
    }

    public Integer getpBranchCompareAmountRank() {
        return pBranchCompareAmountRank;
    }

    public void setpBranchCompareAmountRank(Integer pBranchCompareAmountRank) {
        this.pBranchCompareAmountRank = pBranchCompareAmountRank;
    }

    public Integer getpBranchCompareNumRank() {
        return pBranchCompareNumRank;
    }

    public void setpBranchCompareNumRank(Integer pBranchCompareNumRank) {
        this.pBranchCompareNumRank = pBranchCompareNumRank;
    }

    public Integer getgPosAmountRank() {
        return gPosAmountRank;
    }

    public void setgPosAmountRank(Integer gPosAmountRank) {
        this.gPosAmountRank = gPosAmountRank;
    }

    public Integer getgPosNumRank() {
        return gPosNumRank;
    }

    public void setgPosNumRank(Integer gPosNumRank) {
        this.gPosNumRank = gPosNumRank;
    }

    public Integer getgBranchAmountRank() {
        return gBranchAmountRank;
    }

    public void setgBranchAmountRank(Integer gBranchAmountRank) {
        this.gBranchAmountRank = gBranchAmountRank;
    }

    public Integer getgBranchNumRank() {
        return gBranchNumRank;
    }

    public void setgBranchNumRank(Integer gBranchNumRank) {
        this.gBranchNumRank = gBranchNumRank;
    }

    public Integer getgCompareAmountRank() {
        return gCompareAmountRank;
    }

    public void setgCompareAmountRank(Integer gCompareAmountRank) {
        this.gCompareAmountRank = gCompareAmountRank;
    }

    public Integer getgCompareNumRank() {
        return gCompareNumRank;
    }

    public void setgCompareNumRank(Integer gCompareNumRank) {
        this.gCompareNumRank = gCompareNumRank;
    }

    public Integer getgBranchCompareAmountRank() {
        return gBranchCompareAmountRank;
    }

    public void setgBranchCompareAmountRank(Integer gBranchCompareAmountRank) {
        this.gBranchCompareAmountRank = gBranchCompareAmountRank;
    }

    public Integer getgBranchCompareNumRank() {
        return gBranchCompareNumRank;
    }

    public void setgBranchCompareNumRank(Integer gBranchCompareNumRank) {
        this.gBranchCompareNumRank = gBranchCompareNumRank;
    }

    public Integer getItem1Rank() {
        return item1Rank;
    }

    public void setItem1Rank(Integer item1Rank) {
        this.item1Rank = item1Rank;
    }

    public Integer getItem2Rank() {
        return item2Rank;
    }

    public void setItem2Rank(Integer item2Rank) {
        this.item2Rank = item2Rank;
    }

    public Integer getItem3Rank() {
        return item3Rank;
    }

    public void setItem3Rank(Integer item3Rank) {
        this.item3Rank = item3Rank;
    }

    public Integer getItem4Rank() {
        return item4Rank;
    }

    public void setItem4Rank(Integer item4Rank) {
        this.item4Rank = item4Rank;
    }

    public Integer getItem5Rank() {
        return item5Rank;
    }

    public void setItem5Rank(Integer item5Rank) {
        this.item5Rank = item5Rank;
    }

    public Integer getItem6Rank() {
        return item6Rank;
    }

    public void setItem6Rank(Integer item6Rank) {
        this.item6Rank = item6Rank;
    }

    public Integer getItem7Rank() {
        return item7Rank;
    }

    public void setItem7Rank(Integer item7Rank) {
        this.item7Rank = item7Rank;
    }

    public Integer getItem8Rank() {
        return item8Rank;
    }

    public void setItem8Rank(Integer item8Rank) {
        this.item8Rank = item8Rank;
    }

    public Integer getItem9Rank() {
        return item9Rank;
    }

    public void setItem9Rank(Integer item9Rank) {
        this.item9Rank = item9Rank;
    }

    public Integer getItem10Rank() {
        return item10Rank;
    }

    public BigDecimal getRankNum() {
        return rankNum;
    }

    public void setRankNum(BigDecimal rankNum) {
        this.rankNum = rankNum;
    }

    public void setItem10Rank(Integer item10Rank) {

        this.item10Rank = item10Rank;
    }
}
