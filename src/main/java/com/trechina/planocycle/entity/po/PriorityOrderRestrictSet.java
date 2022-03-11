package com.trechina.planocycle.entity.po;

import java.util.Date;


public class PriorityOrderRestrictSet {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer taiCd;
    private Integer tanaCd;
    private Integer tanaType;
    private String zokusei1;
    private String zokusei2;
    private String zokusei3;
    private String zokusei4;
    private String zokusei5;
    private String zokusei6;
    private String zokusei7;
    private String zokusei8;
    private String zokusei9;
    private String zokusei10;
    private String authorCd;
    private Date createTime;
    private String editerCd;
    private Date editTime;
    private Short deleteflg;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

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

    public Integer gettanaType() {
        return tanaType;
    }

    public void settanaType(Integer tanaType) {
        this.tanaType = tanaType;
    }

    public String getZokusei1() {
        return zokusei1;
    }

    public void setZokusei1(String zokusei1) {
        this.zokusei1 = zokusei1 == null ? null : zokusei1.trim();
    }

    public String getZokusei2() {
        return zokusei2;
    }

    public void setZokusei2(String zokusei2) {
        this.zokusei2 = zokusei2 == null ? null : zokusei2.trim();
    }

    public String getZokusei3() {
        return zokusei3;
    }

    public void setZokusei3(String zokusei3) {
        this.zokusei3 = zokusei3 == null ? null : zokusei3.trim();
    }

    public String getZokusei4() {
        return zokusei4;
    }

    public void setZokusei4(String zokusei4) {
        this.zokusei4 = zokusei4 == null ? null : zokusei4.trim();
    }

    public String getZokusei5() {
        return zokusei5;
    }

    public void setZokusei5(String zokusei5) {
        this.zokusei5 = zokusei5 == null ? null : zokusei5.trim();
    }

    public String getZokusei6() {
        return zokusei6;
    }

    public void setZokusei6(String zokusei6) {
        this.zokusei6 = zokusei6 == null ? null : zokusei6.trim();
    }

    public String getZokusei7() {
        return zokusei7;
    }

    public void setZokusei7(String zokusei7) {
        this.zokusei7 = zokusei7 == null ? null : zokusei7.trim();
    }

    public String getZokusei8() {
        return zokusei8;
    }

    public void setZokusei8(String zokusei8) {
        this.zokusei8 = zokusei8 == null ? null : zokusei8.trim();
    }

    public String getZokusei9() {
        return zokusei9;
    }

    public void setZokusei9(String zokusei9) {
        this.zokusei9 = zokusei9 == null ? null : zokusei9.trim();
    }

    public String getZokusei10() {
        return zokusei10;
    }

    public void setZokusei10(String zokusei10) {
        this.zokusei10 = zokusei10 == null ? null : zokusei10.trim();
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd == null ? null : authorCd.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEditerCd() {
        return editerCd;
    }

    public void setEditerCd(String editerCd) {
        this.editerCd = editerCd == null ? null : editerCd.trim();
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Short getDeleteflg() {
        return deleteflg;
    }

    public void setDeleteflg(Short deleteflg) {
        this.deleteflg = deleteflg;
    }

    @Override
    public String toString() {
        return "PriorityOrderRestrictSet{" +
                "companyCd='" + companyCd + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", taiCd=" + taiCd +
                ", tanaCd=" + tanaCd +
                ", tanaType=" + tanaType +
                '}';
    }
}
