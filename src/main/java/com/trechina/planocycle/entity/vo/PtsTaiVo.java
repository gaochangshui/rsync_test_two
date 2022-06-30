package com.trechina.planocycle.entity.vo;

public class PtsTaiVo {
    private Integer taiCd;
    private Integer taiHeight;
    private Integer taiWidth;
    private Integer taiDepth;
    private String taiName;
    private Boolean add;

    public Integer getTaiCd() {
        return taiCd;
    }

    public void setTaiCd(Integer taiCd) {
        this.taiCd = taiCd;
    }

    public Integer getTaiHeight() {
        return taiHeight;
    }

    public void setTaiHeight(Integer taiHeight) {
        this.taiHeight = taiHeight;
    }

    public Integer getTaiWidth() {
        return taiWidth;
    }

    public void setTaiWidth(Integer taiWidth) {
        this.taiWidth = taiWidth;
    }

    public Integer getTaiDepth() {
        return taiDepth;
    }

    public void setTaiDepth(Integer taiDepth) {
        this.taiDepth = taiDepth;
    }

    public String getTaiName() {
        return taiName;
    }

    public void setTaiName(String taiName) {
        this.taiName = taiName;
    }

    public Boolean getAdd() {
        return add;
    }

    public void setAdd(Boolean add) {
        this.add = add;
    }

    @Override
    public String toString() {
        return "PtsTaiVo{" +
                "taiCd=" + taiCd +
                ", taiHeight=" + taiHeight +
                ", taiWidth=" + taiWidth +
                ", taiDepth=" + taiDepth +
                ", taiName='" + taiName + '\'' +
                ", add=" + add +
                '}';
    }
}
