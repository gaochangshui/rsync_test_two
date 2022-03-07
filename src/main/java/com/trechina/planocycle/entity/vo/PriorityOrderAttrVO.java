package com.trechina.planocycle.entity.vo;

public class PriorityOrderAttrVO {
    private String attrACd;
    private String attrAName;
    private String attrBCd;
    private String attrBName;
    private Integer existingZoning;
    private Integer newZoning;
    private String tanaPattan;
    private Integer rank;

    public String getAttrACd() {
        return attrACd;
    }

    public void setAttrACd(String attrACd) {
        this.attrACd = attrACd;
    }

    public String getAttrAName() {
        return attrAName;
    }

    public void setAttrAName(String attrAName) {
        this.attrAName = attrAName;
    }

    public String getAttrBCd() {
        return attrBCd;
    }

    public void setAttrBCd(String attrBCd) {
        this.attrBCd = attrBCd;
    }

    public String getAttrBName() {
        return attrBName;
    }

    public void setAttrBName(String attrBName) {
        this.attrBName = attrBName;
    }

    public Integer getExistingZoning() {
        return existingZoning;
    }

    public void setExistingZoning(Integer existingZoning) {
        this.existingZoning = existingZoning;
    }

    public Integer getNewZoning() {
        return newZoning;
    }

    public void setNewZoning(Integer newZoning) {
        this.newZoning = newZoning;
    }

    public String getTanaPattan() {
        return tanaPattan;
    }

    public void setTanaPattan(String tanaPattan) {
        this.tanaPattan = tanaPattan;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
