package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class ShelfPatternMst {
    private String conpanyCd;
    private Integer shelfNameCd;
    private String shelfName;
    private Integer shelfPatternCd;
    private String ptsRelationID;

    private String areaName;
    private String shelfPatternName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;

    private String authorCd;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date registered;

    private String maintainerCd;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date modified;
    private Integer branch;

    private String[] storeCd;

    @JsonIgnore
    private String storeCdStr;

    public String getStoreCdStr() {
        return storeCdStr;
    }

    public void setStoreCdStr(String storeCdStr) {
        this.storeCdStr = storeCdStr;
    }

    public String[] getStoreCd() {
        return storeCd;
    }

    public void setStoreCd(String[] storeCd) {
        this.storeCd = storeCd;
    }

    public String getPtsRelationID() {
        return ptsRelationID;
    }

    public void setPtsRelationID(String ptsRelationID) {
        this.ptsRelationID = ptsRelationID;
    }

    public String getConpanyCd() {
        return conpanyCd;
    }

    public void setConpanyCd(String conpanyCd) {
        this.conpanyCd = conpanyCd == null ? null : conpanyCd.trim();
    }

    public Integer getShelfNameCd() {
        return shelfNameCd;
    }

    public void setShelfNameCd(Integer shelfNameCd) {
        this.shelfNameCd = shelfNameCd;
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }


    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getShelfPatternName() {
        return shelfPatternName;
    }

    public void setShelfPatternName(String shelfPatternName) {
        this.shelfPatternName = shelfPatternName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public String getMaintainerCd() {
        return maintainerCd;
    }

    public void setMaintainerCd(String maintainerCd) {
        this.maintainerCd = maintainerCd;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "ShelfPatternMst{" +
                "conpanyCd='" + conpanyCd + '\'' +
                ", shelfNameCd=" + shelfNameCd +
                ", shelfName='" + shelfName + '\'' +
                ", shelfPatternCd=" + shelfPatternCd +
                ", ptsRelationID='" + ptsRelationID + '\'' +
                ", areaName='" + areaName + '\'' +
                ", shelfPatternName='" + shelfPatternName + '\'' +
                ", createTime=" + createTime +
                ", authorCd='" + authorCd + '\'' +
                ", registered=" + registered +
                ", maintainerCd='" + maintainerCd + '\'' +
                ", modified=" + modified +
                ", branch=" + branch +
                '}';
    }
}
