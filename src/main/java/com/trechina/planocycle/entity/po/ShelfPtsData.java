package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ShelfPtsData {
    private Integer id;

    private String conpanyCd;

    private String fileName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;

    private String authorcd;

    private Integer shelfPatternCd;

    private Integer validFlg;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date startDay;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAuthorcd() {
        return authorcd;
    }

    public void setAuthorcd(String authorcd) {
        this.authorcd = authorcd;
    }

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public Integer getValidFlg() {
        return validFlg;
    }

    public void setValidFlg(Integer validFlg) {
        this.validFlg = validFlg;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    @Override
    public String toString() {
        return "ShelfPtsData{" +
                "id=" + id +
                ", conpanyCd='" + conpanyCd + '\'' +
                ", fileName='" + fileName + '\'' +
                ", createTime=" + createTime +
                ", authorcd='" + authorcd + '\'' +
                ", shelfPatternCd=" + shelfPatternCd +
                ", validFlg=" + validFlg +
                ", startDay=" + startDay +
                '}';
    }
}
