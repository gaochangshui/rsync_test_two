package com.trechina.planocycle.entity.vo;

public class PtsTanaVo {
    private Integer taiCd;
    private Integer tanaCd;
    private Integer tanaHeight;
    private Integer tanaWidth;
    private Integer tanaDepth;
    private Integer tanaThickness;
    private Integer tanaType;

    public Integer getTaiCd() {
        return taiCd;
    }

    public void setTaiCd(Integer taiCd) {
        this.taiCd = taiCd;
    }

    public Integer getTanaCd() {
        return tanaCd;
    }

    public void setTanaCd(Integer tanaCd) {
        this.tanaCd = tanaCd;
    }

    public Integer getTanaHeight() {
        return tanaHeight;
    }

    public void setTanaHeight(Integer tanaHeight) {
        this.tanaHeight = tanaHeight;
    }

    public Integer getTanaWidth() {
        return tanaWidth;
    }

    public void setTanaWidth(Integer tanaWidth) {
        this.tanaWidth = tanaWidth;
    }

    public Integer getTanaDepth() {
        return tanaDepth;
    }

    public void setTanaDepth(Integer tanaDepth) {
        this.tanaDepth = tanaDepth;
    }

    public Integer getTanaThickness() {
        return tanaThickness;
    }

    public void setTanaThickness(Integer tanaThickness) {
        this.tanaThickness = tanaThickness;
    }

    public Integer getTanaType() {
        return tanaType;
    }

    public void setTanaType(Integer tanaType) {
        this.tanaType = tanaType;
    }

    @Override
    public String toString() {
        return "PtsTanaVo{" +
                "taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", tanaHeight=" + tanaHeight +
                ", tanaWidth=" + tanaWidth +
                ", tanaDepth=" + tanaDepth +
                ", tanaThickness=" + tanaThickness +
                ", tanaType=" + tanaType +
                '}';
    }
}