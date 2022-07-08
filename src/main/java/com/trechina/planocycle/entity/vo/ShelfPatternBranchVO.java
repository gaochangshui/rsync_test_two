package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class ShelfPatternBranchVO {
    private Integer shelfPatternCd;
    private List<String> branchCd;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date startTime;

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public List<String> getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(List<String> branchCd) {
        this.branchCd = branchCd;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "ShelfPatternBranchVO{" +
                "shelfPatternCd=" + shelfPatternCd +
                ", branchCd=" + branchCd +
                ", startTime=" + startTime +
                '}';
    }
}
