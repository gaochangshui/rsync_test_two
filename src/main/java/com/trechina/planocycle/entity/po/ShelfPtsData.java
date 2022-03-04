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

    private Date editTime;

    private String editerCd;

    private Integer deleteflg;

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

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getEditerCd() {
        return editerCd;
    }

    public void setEditerCd(String editerCd) {
        this.editerCd = editerCd == null ? null : editerCd.trim();
    }

    public Integer getDeleteflg() {
        return deleteflg;
    }

    public void setDeleteflg(Integer deleteflg) {
        this.deleteflg = deleteflg;
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
