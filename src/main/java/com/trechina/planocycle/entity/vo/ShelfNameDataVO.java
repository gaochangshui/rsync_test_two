package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ShelfNameDataVO {
    private Integer id;

    private String conpanyCd;

    private String areaName;
    private String shelfName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;

    private String authorCd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConpanyCd() {
        return conpanyCd;
    }

    public void setConpanyCd(String conpanyCd) {
        this.conpanyCd = conpanyCd;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
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

    @Override
    public String toString() {
        return "ShelfNameDataVO{" +
                "id=" + id +
                ", conpanyCd='" + conpanyCd + '\'' +
                ", areaName=" + areaName +
                ", shelfName='" + shelfName + '\'' +
                ", createTime=" + createTime +
                ", authorCd=" + authorCd +
                '}';
    }
}
