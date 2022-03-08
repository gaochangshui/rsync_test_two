package com.trechina.planocycle.entity.vo;

public class PriorityOrderAttrVO {
    private String attrACd;
    private String attrAName;
    private String attrBCd;
    private String attrBName;
    private String jansAColnm;
    private String jansBColnm;
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

    public String getJansAColnm() {
        return jansAColnm;
    }

    public void setJansAColnm(String jansAColnm) {
        this.jansAColnm = jansAColnm;
    }

    public String getJansBColnm() {
        return jansBColnm;
    }

    public void setJansBColnm(String jansBColnm) {
        this.jansBColnm = jansBColnm;
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

    @Override
    public String toString() {
        return "PriorityOrderAttrVO{" +
                "attrACd='" + attrACd + '\'' +
                ", attrAName='" + attrAName + '\'' +
                ", attrBCd='" + attrBCd + '\'' +
                ", attrBName='" + attrBName + '\'' +
                ", jansAColnm='" + jansAColnm + '\'' +
                ", jansBColnm='" + jansBColnm + '\'' +
                ", existingZoning=" + existingZoning +
                ", newZoning=" + newZoning +
                ", tanaPattan='" + tanaPattan + '\'' +
                ", rank=" + rank +
                '}';
    }
}
