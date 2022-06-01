package com.trechina.planocycle.entity.vo;

public class PtsCsvVO {
    private Integer type;
    private String companyCd;
    private Integer typeCd;
    private Long shelfPatternCd;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Integer getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(Integer typeCd) {
        this.typeCd = typeCd;
    }

    public Long getShelfPatternCd() {
        return shelfPatternCd;
    }

    public void setShelfPatternCd(Long shelfPatternCd) {
        this.shelfPatternCd = shelfPatternCd;
    }
}
