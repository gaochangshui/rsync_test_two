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
    private BigDecimal pdPosAmount = new BigDecimal("0.00");
    private BigDecimal pdPosNum= new BigDecimal("0.00");
    private BigDecimal pdBranchAmount= new BigDecimal("0.00");
    private BigDecimal pdBranchNum= new BigDecimal("0.00");
    private BigDecimal pdCompareAmount= new BigDecimal("0.00");
    private BigDecimal pdCompareNum= new BigDecimal("0.00");
    private BigDecimal pdBranchCompareAmount= new BigDecimal("0.00");
    private BigDecimal pdBranchCompareNum= new BigDecimal("0.00");
    // 顾客group
    private BigDecimal gdPosAmount= new BigDecimal("0.00");
    private BigDecimal gdPosNum= new BigDecimal("0.00");
    private BigDecimal gdBranchAmount= new BigDecimal("0.00");
    private BigDecimal gdBranchNum= new BigDecimal("0.00");
    private BigDecimal gdCompareAmount= new BigDecimal("0.00");
    private BigDecimal gdCompareNum= new BigDecimal("0.00");
    private BigDecimal gdBranchCompareAmount= new BigDecimal("0.00");
    private BigDecimal gdBranchCompareNum= new BigDecimal("0.00");
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

    private Integer pdPosAmountRank;
    private Integer pdPosNumRank;
    private Integer pdBranchAmountRank;
    private Integer pdBranchNumRank;
    private Integer pdCompareAmountRank;
    private Integer pdCompareNumRank;
    private Integer pdBranchCompareAmountRank;
    private Integer pdBranchCompareNumRank;
    private Integer gdPosAmountRank;
    private Integer gdPosNumRank;
    private Integer gdBranchAmountRank;
    private Integer gdBranchNumRank;
    private Integer gdCompareAmountRank;
    private Integer gdCompareNumRank;
    private Integer gdBranchCompareAmountRank;
    private Integer gdBranchCompareNumRank;
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

    public BigDecimal getPdPosAmount() {
        return pdPosAmount;
    }

    public void setPdPosAmount(BigDecimal pdPosAmount) {
        this.pdPosAmount = pdPosAmount;
    }

    public BigDecimal getPdPosNum() {
        return pdPosNum;
    }

    public void setPdPosNum(BigDecimal pdPosNum) {
        this.pdPosNum = pdPosNum;
    }

    public BigDecimal getPdBranchAmount() {
        return pdBranchAmount;
    }

    public void setPdBranchAmount(BigDecimal pdBranchAmount) {
        this.pdBranchAmount = pdBranchAmount;
    }

    public BigDecimal getPdBranchNum() {
        return pdBranchNum;
    }

    public void setPdBranchNum(BigDecimal pdBranchNum) {
        this.pdBranchNum = pdBranchNum;
    }

    public BigDecimal getPdCompareAmount() {
        return pdCompareAmount;
    }

    public void setPdCompareAmount(BigDecimal pdCompareAmount) {
        this.pdCompareAmount = pdCompareAmount;
    }

    public BigDecimal getPdCompareNum() {
        return pdCompareNum;
    }

    public void setPdCompareNum(BigDecimal pdCompareNum) {
        this.pdCompareNum = pdCompareNum;
    }

    public BigDecimal getPdBranchCompareAmount() {
        return pdBranchCompareAmount;
    }

    public void setPdBranchCompareAmount(BigDecimal pdBranchCompareAmount) {
        this.pdBranchCompareAmount = pdBranchCompareAmount;
    }

    public BigDecimal getPdBranchCompareNum() {
        return pdBranchCompareNum;
    }

    public void setPdBranchCompareNum(BigDecimal pdBranchCompareNum) {
        this.pdBranchCompareNum = pdBranchCompareNum;
    }

    public BigDecimal getGdPosAmount() {
        return gdPosAmount;
    }

    public void setGdPosAmount(BigDecimal gdPosAmount) {
        this.gdPosAmount = gdPosAmount;
    }

    public BigDecimal getGdPosNum() {
        return gdPosNum;
    }

    public void setGdPosNum(BigDecimal gdPosNum) {
        this.gdPosNum = gdPosNum;
    }

    public BigDecimal getGdBranchAmount() {
        return gdBranchAmount;
    }

    public void setGdBranchAmount(BigDecimal gdBranchAmount) {
        this.gdBranchAmount = gdBranchAmount;
    }

    public BigDecimal getGdBranchNum() {
        return gdBranchNum;
    }

    public void setGdBranchNum(BigDecimal gdBranchNum) {
        this.gdBranchNum = gdBranchNum;
    }

    public BigDecimal getGdCompareAmount() {
        return gdCompareAmount;
    }

    public void setGdCompareAmount(BigDecimal gdCompareAmount) {
        this.gdCompareAmount = gdCompareAmount;
    }

    public BigDecimal getGdCompareNum() {
        return gdCompareNum;
    }

    public void setGdCompareNum(BigDecimal gdCompareNum) {
        this.gdCompareNum = gdCompareNum;
    }

    public BigDecimal getGdBranchCompareAmount() {
        return gdBranchCompareAmount;
    }

    public void setGdBranchCompareAmount(BigDecimal gdBranchCompareAmount) {
        this.gdBranchCompareAmount = gdBranchCompareAmount;
    }

    public BigDecimal getGdBranchCompareNum() {
        return gdBranchCompareNum;
    }

    public void setGdBranchCompareNum(BigDecimal gdBranchCompareNum) {
        this.gdBranchCompareNum = gdBranchCompareNum;
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

    public BigDecimal getRankNum() {
        return rankNum;
    }

    public void setRankNum(BigDecimal rankNum) {
        this.rankNum = rankNum;
    }

    public Integer getPdPosAmountRank() {
        return pdPosAmountRank;
    }

    public void setPdPosAmountRank(Integer pdPosAmountRank) {
        this.pdPosAmountRank = pdPosAmountRank;
    }

    public Integer getPdPosNumRank() {
        return pdPosNumRank;
    }

    public void setPdPosNumRank(Integer pdPosNumRank) {
        this.pdPosNumRank = pdPosNumRank;
    }

    public Integer getPdBranchAmountRank() {
        return pdBranchAmountRank;
    }

    public void setPdBranchAmountRank(Integer pdBranchAmountRank) {
        this.pdBranchAmountRank = pdBranchAmountRank;
    }

    public Integer getPdBranchNumRank() {
        return pdBranchNumRank;
    }

    public void setPdBranchNumRank(Integer pdBranchNumRank) {
        this.pdBranchNumRank = pdBranchNumRank;
    }

    public Integer getPdCompareAmountRank() {
        return pdCompareAmountRank;
    }

    public void setPdCompareAmountRank(Integer pdCompareAmountRank) {
        this.pdCompareAmountRank = pdCompareAmountRank;
    }

    public Integer getPdCompareNumRank() {
        return pdCompareNumRank;
    }

    public void setPdCompareNumRank(Integer pdCompareNumRank) {
        this.pdCompareNumRank = pdCompareNumRank;
    }

    public Integer getPdBranchCompareAmountRank() {
        return pdBranchCompareAmountRank;
    }

    public void setPdBranchCompareAmountRank(Integer pdBranchCompareAmountRank) {
        this.pdBranchCompareAmountRank = pdBranchCompareAmountRank;
    }

    public Integer getPdBranchCompareNumRank() {
        return pdBranchCompareNumRank;
    }

    public void setPdBranchCompareNumRank(Integer pdBranchCompareNumRank) {
        this.pdBranchCompareNumRank = pdBranchCompareNumRank;
    }

    public Integer getGdPosAmountRank() {
        return gdPosAmountRank;
    }

    public void setGdPosAmountRank(Integer gdPosAmountRank) {
        this.gdPosAmountRank = gdPosAmountRank;
    }

    public Integer getGdPosNumRank() {
        return gdPosNumRank;
    }

    public void setGdPosNumRank(Integer gdPosNumRank) {
        this.gdPosNumRank = gdPosNumRank;
    }

    public Integer getGdBranchAmountRank() {
        return gdBranchAmountRank;
    }

    public void setGdBranchAmountRank(Integer gdBranchAmountRank) {
        this.gdBranchAmountRank = gdBranchAmountRank;
    }

    public Integer getGdBranchNumRank() {
        return gdBranchNumRank;
    }

    public void setGdBranchNumRank(Integer gdBranchNumRank) {
        this.gdBranchNumRank = gdBranchNumRank;
    }

    public Integer getGdCompareAmountRank() {
        return gdCompareAmountRank;
    }

    public void setGdCompareAmountRank(Integer gdCompareAmountRank) {
        this.gdCompareAmountRank = gdCompareAmountRank;
    }

    public Integer getGdCompareNumRank() {
        return gdCompareNumRank;
    }

    public void setGdCompareNumRank(Integer gdCompareNumRank) {
        this.gdCompareNumRank = gdCompareNumRank;
    }

    public Integer getGdBranchCompareAmountRank() {
        return gdBranchCompareAmountRank;
    }

    public void setGdBranchCompareAmountRank(Integer gdBranchCompareAmountRank) {
        this.gdBranchCompareAmountRank = gdBranchCompareAmountRank;
    }

    public Integer getGdBranchCompareNumRank() {
        return gdBranchCompareNumRank;
    }

    public void setGdBranchCompareNumRank(Integer gdBranchCompareNumRank) {
        this.gdBranchCompareNumRank = gdBranchCompareNumRank;
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

    public void setItem10Rank(Integer item10Rank) {
        this.item10Rank = item10Rank;
    }

    public Integer getRankResult() {
        return rankResult;
    }

    public void setRankResult(Integer rankResult) {
        this.rankResult = rankResult;
    }

    @Override
    public String toString() {
        return "ProductPowerMstData{" +
                "companyCd='" + companyCd + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", jan='" + jan + '\'' +
                ", skuName='" + skuName + '\'' +
                ", classifyBig='" + classifyBig + '\'' +
                ", classifyMiddle='" + classifyMiddle + '\'' +
                ", classifySmall='" + classifySmall + '\'' +
                ", classifyFine='" + classifyFine + '\'' +
                ", pdPosAmount=" + pdPosAmount +
                ", pdPosNum=" + pdPosNum +
                ", pdBranchAmount=" + pdBranchAmount +
                ", pdBranchNum=" + pdBranchNum +
                ", pdCompareAmount=" + pdCompareAmount +
                ", pdCompareNum=" + pdCompareNum +
                ", pdBranchCompareAmount=" + pdBranchCompareAmount +
                ", pdBranchCompareNum=" + pdBranchCompareNum +
                ", gdPosAmount=" + gdPosAmount +
                ", gdPosNum=" + gdPosNum +
                ", gdBranchAmount=" + gdBranchAmount +
                ", gdBranchNum=" + gdBranchNum +
                ", gdCompareAmount=" + gdCompareAmount +
                ", gdCompareNum=" + gdCompareNum +
                ", gdBranchCompareAmount=" + gdBranchCompareAmount +
                ", gdBranchCompareNum=" + gdBranchCompareNum +
                ", item1=" + item1 +
                ", item2=" + item2 +
                ", item3=" + item3 +
                ", item4=" + item4 +
                ", item5=" + item5 +
                ", item6=" + item6 +
                ", item7=" + item7 +
                ", item8=" + item8 +
                ", item9=" + item9 +
                ", item10=" + item10 +
                ", rankNum=" + rankNum +
                ", pdPosAmountRank=" + pdPosAmountRank +
                ", pdPosNumRank=" + pdPosNumRank +
                ", pdBranchAmountRank=" + pdBranchAmountRank +
                ", pdBranchNumRank=" + pdBranchNumRank +
                ", pdCompareAmountRank=" + pdCompareAmountRank +
                ", pdCompareNumRank=" + pdCompareNumRank +
                ", pdBranchCompareAmountRank=" + pdBranchCompareAmountRank +
                ", pdBranchCompareNumRank=" + pdBranchCompareNumRank +
                ", gdPosAmountRank=" + gdPosAmountRank +
                ", gdPosNumRank=" + gdPosNumRank +
                ", gdBranchAmountRank=" + gdBranchAmountRank +
                ", gdBranchNumRank=" + gdBranchNumRank +
                ", gdCompareAmountRank=" + gdCompareAmountRank +
                ", gdCompareNumRank=" + gdCompareNumRank +
                ", gdBranchCompareAmountRank=" + gdBranchCompareAmountRank +
                ", gdBranchCompareNumRank=" + gdBranchCompareNumRank +
                ", item1Rank=" + item1Rank +
                ", item2Rank=" + item2Rank +
                ", item3Rank=" + item3Rank +
                ", item4Rank=" + item4Rank +
                ", item5Rank=" + item5Rank +
                ", item6Rank=" + item6Rank +
                ", item7Rank=" + item7Rank +
                ", item8Rank=" + item8Rank +
                ", item9Rank=" + item9Rank +
                ", item10Rank=" + item10Rank +
                ", rankResult=" + rankResult +
                '}';
    }
}
