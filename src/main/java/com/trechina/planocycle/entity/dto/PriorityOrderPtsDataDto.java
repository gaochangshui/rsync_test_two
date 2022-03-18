package com.trechina.planocycle.entity.dto;


public class PriorityOrderPtsDataDto {
    private Integer id;
    private Integer oldPtsCd;
    private Integer priorityOrderCd;
    private String authorCd;

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOldPtsCd() {
        return oldPtsCd;
    }

    public void setOldPtsCd(Integer oldPtsCd) {
        this.oldPtsCd = oldPtsCd;
    }
}
