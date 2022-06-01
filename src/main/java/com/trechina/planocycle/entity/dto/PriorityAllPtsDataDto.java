package com.trechina.planocycle.entity.dto;


public class PriorityAllPtsDataDto {
    private Integer id;
    private Integer oldPtsCd;
    private Integer priorityAllCd;
    private String authorCd;
    private String companyCd;
    private String fileName;
    private Integer shelfPatternCd;

    public Integer getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Integer shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public Integer getPriorityAllCd() {
        return priorityAllCd;
    }

    public void setPriorityAllCd(Integer priorityAllCd) {
        this.priorityAllCd = priorityAllCd;
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
