package com.trechina.planocycle.entity.dto;

public class WorkPriorityOrderResultDataDto {
    private String companyCd;
    private String authorCd;
    private Integer priorityOrderCd;
    private String janCd;
    private Long face;
    private Integer taiCd;
    private Integer tanaCd;
    private Integer tanaPositionCd;

    public Integer getTanaPositionCd() {
        return tanaPositionCd;
    }

    public void setTanaPositionCd(Integer tanaPositionCd) {
        this.tanaPositionCd = tanaPositionCd;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

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

    public String getJanCd() {
        return janCd;
    }

    public void setJanCd(String janCd) {
        this.janCd = janCd;
    }

    public Long getFace() {
        return face;
    }

    public void setFace(Long face) {
        this.face = face;
    }

    public Integer getTaiCd() {
        return taiCd;
    }

    public void setTaiCd(Integer taiCd) {
        this.taiCd = taiCd;
    }

    public Integer getTanaCd() {
        return tanaCd;
    }

    public void setTanaCd(Integer tanaCd) {
        this.tanaCd = tanaCd;
    }
}
