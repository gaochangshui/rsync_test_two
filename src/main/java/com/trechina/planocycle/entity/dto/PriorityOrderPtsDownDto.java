package com.trechina.planocycle.entity.dto;


public class PriorityOrderPtsDownDto {
    private String company;
    private String shelfPatternNo;
    private String guid;
    private String mode;
    private Integer priorityNo;
    private String rankAttributeList;
    private String attributeCd;
    private String rankAttributeCd;
    private Integer ptsVerison;
    private String shelfPatternNoNm;
    private Integer newCutFlg;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getShelfPatternNo() {
        return shelfPatternNo;
    }

    public void setShelfPatternNo(String shelfPatternNo) {
        this.shelfPatternNo = shelfPatternNo;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getPriorityNo() {
        return priorityNo;
    }

    public void setPriorityNo(Integer priorityNo) {
        this.priorityNo = priorityNo;
    }

    public String getRankAttributeList() {
        return rankAttributeList;
    }

    public void setRankAttributeList(String rankAttributeList) {
        this.rankAttributeList = rankAttributeList;
    }

    public String getAttributeCd() {
        return attributeCd;
    }

    public void setAttributeCd(String attributeCd) {
        this.attributeCd = attributeCd;
    }

    public Integer getPtsVerison() {
        return ptsVerison;
    }

    public void setPtsVerison(Integer ptsVerison) {
        this.ptsVerison = ptsVerison;
    }

    public String getShelfPatternNoNm() {
        return shelfPatternNoNm;
    }

    public void setShelfPatternNoNm(String shelfPatternNoNm) {
        this.shelfPatternNoNm = shelfPatternNoNm;
    }

    public String getRankAttributeCd() {
        return rankAttributeCd;
    }

    public void setRankAttributeCd(String rankAttributeCd) {
        this.rankAttributeCd = rankAttributeCd;
    }

    public Integer getNewCutFlg() {
        return newCutFlg;
    }

    public void setNewCutFlg(Integer newCutFlg) {
        this.newCutFlg = newCutFlg;
    }

    @Override
    public String toString() {
        return "PriorityOrderPtsDownDto{" +
                "company='" + company + '\'' +
                ", shelfPatternNo='" + shelfPatternNo + '\'' +
                ", guid='" + guid + '\'' +
                ", mode='" + mode + '\'' +
                ", priorityNo=" + priorityNo +
                ", rankAttributeList='" + rankAttributeList + '\'' +
                ", attributeCd='" + attributeCd + '\'' +
                ", rankAttributeCd='" + rankAttributeCd + '\'' +
                ", ptsVerison=" + ptsVerison +
                ", shelfPatternNoNm='" + shelfPatternNoNm + '\'' +
                ", newCutFlg=" + newCutFlg +
                '}';
    }
}
