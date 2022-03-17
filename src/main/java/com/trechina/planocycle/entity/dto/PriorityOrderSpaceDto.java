package com.trechina.planocycle.entity.dto;

import com.trechina.planocycle.entity.vo.PriorityOrderAttrVO;

import java.util.List;

public class PriorityOrderSpaceDto {
    private String companyCd;
    private Short attr1;
    private Short attr2;
    private Long patternCd;
    private List<PriorityOrderAttrVO> dataList;
    private Integer priorityOrderCd;
    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Short getAttr1() {
        return attr1;
    }

    public void setAttr1(Short attr1) {
        this.attr1 = attr1;
    }

    public Short getAttr2() {
        return attr2;
    }

    public void setAttr2(Short attr2) {
        this.attr2 = attr2;
    }

    public Long getPatternCd() {
        return patternCd;
    }

    public void setPatternCd(Long patternCd) {
        this.patternCd = patternCd;
    }

    public List<PriorityOrderAttrVO> getDataList() {
        return dataList;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public void setDataList(List<PriorityOrderAttrVO> dataList) {

        this.dataList = dataList;
    }
}
