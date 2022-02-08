package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ShelfPatternBranch {

    private Integer shelfPatternCd;

    private String branch;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date startTime;

    public Integer getShelfPattrenCd() {
        return shelfPatternCd;
    }

    public void setShelfPattrenCd(Integer shelfPattrenCd) {
        this.shelfPatternCd = shelfPattrenCd;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "ShelfPatternBranch{" +
                "shelfPattrenCd=" + shelfPatternCd +
                ", branch=" + branch +
                ", startTime=" + startTime +
                '}';
    }
}
