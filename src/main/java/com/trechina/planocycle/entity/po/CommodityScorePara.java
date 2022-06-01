package com.trechina.planocycle.entity.po;

import java.util.List;

public class CommodityScorePara {
    private ProductPowerParamMst productPowerParamMst;
    private List<ProductPowerShowMst> productPowerShowMst;
    private List<ProductPowerWeight> productPowerWeight;
    private List<ProductPowerReserveMst> productPowerReserveMst;
    private String deleteDataCd;
    private String jan;
    private String rank;
    private String attributeCd;
    private String attr;

    public ProductPowerParamMst getProductPowerParamMst() {
        return productPowerParamMst;
    }

    public void setProductPowerParamMst(ProductPowerParamMst productPowerParamMst) {
        this.productPowerParamMst = productPowerParamMst;
    }

    public List<ProductPowerShowMst> getProductPowerShowMst() {
        return productPowerShowMst;
    }

    public void setProductPowerShowMst(List<ProductPowerShowMst> productPowerShowMst) {
        this.productPowerShowMst = productPowerShowMst;
    }

    public List<ProductPowerWeight> getProductPowerWeight() {
        return productPowerWeight;
    }

    public void setProductPowerWeight(List<ProductPowerWeight> productPowerWeight) {
        this.productPowerWeight = productPowerWeight;
    }

    public List<ProductPowerReserveMst> getProductPowerReserveMst() {
        return productPowerReserveMst;
    }

    public void setProductPowerReserveMst(List<ProductPowerReserveMst> productPowerReserveMst) {
        this.productPowerReserveMst = productPowerReserveMst;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getDeleteDataCd() {
        return deleteDataCd;
    }

    public void setDeleteDataCd(String deleteDataCd) {
        this.deleteDataCd = deleteDataCd;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getAttributeCd() {
        return attributeCd;
    }

    public void setAttributeCd(String attributeCd) {
        this.attributeCd = attributeCd;
    }


    @Override
    public String toString() {
        return "CommodityScorePara{" +
                "productPowerParamMst=" + productPowerParamMst +
                ", productPowerShowMst=" + productPowerShowMst +
                ", productPowerWeight=" + productPowerWeight +
                ", productPowerReserveMst=" + productPowerReserveMst +
                ", deleteDataCd='" + deleteDataCd + '\'' +
                ", jan='" + jan + '\'' +
                ", rank='" + rank + '\'' +
                ", attributeCd='" + attributeCd + '\'' +
                ", attr='" + attr + '\'' +
                '}';
    }
}
