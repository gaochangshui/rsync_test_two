package com.trechina.planocycle.entity.dto;

public class ShelfPtsHeaderDto {
    private String fileName;

    private String commonInfo;
    private String versionInfo;
    private String modeName;
    private String outFlg;
    private String taiHeader;
    private String tanaHeader;
    private String janHeader;

    private Integer ptsCd;

    public Integer getPtsCd() {
        return ptsCd;
    }

    public void setPtsCd(Integer ptsCd) {
        this.ptsCd = ptsCd;
    }

    public String getCommonInfo() {
        return commonInfo;
    }

    public void setCommonInfo(String commonInfo) {
        this.commonInfo = commonInfo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public String getOutFlg() {
        return outFlg;
    }

    public void setOutFlg(String outFlg) {
        this.outFlg = outFlg;
    }

    public String getTaiHeader() {
        return taiHeader;
    }

    public void setTaiHeader(String taiHeader) {
        this.taiHeader = taiHeader;
    }

    public String getTanaHeader() {
        return tanaHeader;
    }

    public void setTanaHeader(String tanaHeader) {
        this.tanaHeader = tanaHeader;
    }

    public String getJanHeader() {
        return janHeader;
    }

    public void setJanHeader(String janHeader) {
        this.janHeader = janHeader;
    }
}
