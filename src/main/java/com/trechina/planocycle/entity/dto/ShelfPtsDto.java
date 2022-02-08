package com.trechina.planocycle.entity.dto;


public class ShelfPtsDto {
    private Integer id;
    private String companyCd;
    private String fileName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "ShelfPtsDto{" +
                "companyCd='" + companyCd + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
