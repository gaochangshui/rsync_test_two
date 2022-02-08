package com.trechina.planocycle.entity.dto;

public class GetPtsCsvCgiDto {
    private String company;
    private String guid;
    private String mode;
    private String ptsIdCsvNm;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public String getPtsIdCsvNm() {
        return ptsIdCsvNm;
    }

    public void setPtsIdCsvNm(String ptsIdCsvNm) {
        this.ptsIdCsvNm = ptsIdCsvNm;
    }

    @Override
    public String toString() {
        return "getPtsCsvCgiDto{" +
                "company='" + company + '\'' +
                ", guid='" + guid + '\'' +
                ", mode='" + mode + '\'' +
                ", ptsIdCsvNm='" + ptsIdCsvNm + '\'' +
                '}';
    }
}
