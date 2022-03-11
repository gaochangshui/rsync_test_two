package com.trechina.planocycle.entity.po;

public class WorkPriorityOrderZokuseiOrder {
    private String companyCd;

    private String authorCd;

    private Short zokuseiId;

    private Short zokuseiRank;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd == null ? null : companyCd.trim();
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd == null ? null : authorCd.trim();
    }

    public Short getZokuseiId() {
        return zokuseiId;
    }

    public void setZokuseiId(Short zokuseiId) {
        this.zokuseiId = zokuseiId;
    }

    public Short getZokuseiRank() {
        return zokuseiRank;
    }

    public void setZokuseiRank(Short zokuseiRank) {
        this.zokuseiRank = zokuseiRank;
    }
}