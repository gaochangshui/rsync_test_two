package com.trechina.planocycle.entity.dto;

import java.util.List;

public class PriorityAllSaveDto {
    private String companyCd;
    private Integer priorityAllCd;
    private Integer priorityOrderCd;
    private List<Integer> patterns;
    private String priorityAllName;

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getPriorityAllCd() {
        return priorityAllCd;
    }

    public void setPriorityAllCd(Integer priorityAllCd) {
        this.priorityAllCd = priorityAllCd;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public List<Integer> getPatterns() {
        return patterns;
    }

    public String getPriorityAllName() {
        return priorityAllName;
    }

    public void setPriorityAllName(String priorityAllName) {
        this.priorityAllName = priorityAllName;
    }

    public void setPatterns(List<Integer> patterns) {

        this.patterns = patterns;
    }
}
