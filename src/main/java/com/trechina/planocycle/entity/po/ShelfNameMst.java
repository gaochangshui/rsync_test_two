package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ShelfNameMst {
    private Integer id;

    private String conpanyCd;

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
        this.conpanyCd = conpanyCd == null ? null : conpanyCd.trim();
    }


    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName == null ? null : shelfName.trim();
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
        return "ShelfNameMst{" +
                "id=" + id +
                ", conpanyCd='" + conpanyCd + '\'' +
                ", shelfName='" + shelfName + '\'' +
                ", createTime=" + createTime +
                ", authorCd=" + authorCd +
                '}';
    }

}
