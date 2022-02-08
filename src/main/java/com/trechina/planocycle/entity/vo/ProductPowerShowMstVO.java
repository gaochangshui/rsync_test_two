package com.trechina.planocycle.entity.vo;

import java.util.List;

public class ProductPowerShowMstVO {
    private String ConpanyCD;
    private Integer ProductPowerCD;
    private List<String> MarketData;
    private List<String> PosData;
    private Integer attributeName1;

    private Integer attributeName2;

    private Integer attributeName3;

    private Integer attributeName4;

    public String getConpanyCD() {
        return ConpanyCD;
    }

    public void setConpanyCD(String conpanyCD) {
        ConpanyCD = conpanyCD;
    }

    public Integer getProductPowerCD() {
        return ProductPowerCD;
    }

    public void setProductPowerCD(Integer productPowerCD) {
        ProductPowerCD = productPowerCD;
    }

    public List<String> getMarketData() {
        return MarketData;
    }

    public void setMarketData(List<String> marketData) {
        MarketData = marketData;
    }

    public List<String> getPosData() {
        return PosData;
    }

    public void setPosData(List<String> posData) {
        PosData = posData;
    }

    public Integer getAttributeName1() {
        return attributeName1;
    }

    public void setAttributeName1(Integer attributeName1) {
        this.attributeName1 = attributeName1;
    }

    public Integer getAttributeName2() {
        return attributeName2;
    }

    public void setAttributeName2(Integer attributeName2) {
        this.attributeName2 = attributeName2;
    }

    public Integer getAttributeName3() {
        return attributeName3;
    }

    public void setAttributeName3(Integer attributeName3) {
        this.attributeName3 = attributeName3;
    }

    public Integer getAttributeName4() {
        return attributeName4;
    }

    public void setAttributeName4(Integer attributeName4) {
        this.attributeName4 = attributeName4;
    }
}
