package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ShelfPtsDataHistoryVO {
    private Integer patternCd;
    private String patternName;
    private Integer ptsCd;
    private String ptsName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date startDay;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;
    private String authorcd;

    public Integer getPatternCd() {
        return patternCd;
    }

    public void setPatternCd(Integer patternCd) {
        this.patternCd = patternCd;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public Integer getPtsCd() {
        return ptsCd;
    }

    public void setPtsCd(Integer ptsCd) {
        this.ptsCd = ptsCd;
    }

    public String getPtsName() {
        return ptsName;
    }

    public void setPtsName(String ptsName) {
        this.ptsName = ptsName;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
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

    @Override
    public String toString() {
        return "ShelfPtsDataHistoryVO{" +
                "patternCd=" + patternCd +
                ", patternName='" + patternName + '\'' +
                ", ptsCd=" + ptsCd +
                ", ptsName='" + ptsName + '\'' +
                ", startDay=" + startDay +
                ", createTime=" + createTime +
                ", authorcd='" + authorcd + '\'' +
                '}';
    }
}
