package com.trechina.planocycle.entity.vo;


import java.util.List;

public class PtsDetailDataVo {

    private Integer id;
    private String taiHeader;
    private String tanaHeader;
    private String janHeader;
    private String modename;
    private String commoninfo;
    private String versioninfo;
    private String outflg;
    private List<PtsTaiVo> ptsTaiList;
    private List<PtsTanaVo> ptsTanaVoList;
    private List<PtsJanDataVo> ptsJanDataList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<PtsTaiVo> getPtsTaiList() {
        return ptsTaiList;
    }

    public void setPtsTaiList(List<PtsTaiVo> ptsTaiList) {
        this.ptsTaiList = ptsTaiList;
    }

    public List<PtsTanaVo> getPtsTanaVoList() {
        return ptsTanaVoList;
    }

    public void setPtsTanaVoList(List<PtsTanaVo> ptsTanaVoList) {
        this.ptsTanaVoList = ptsTanaVoList;
    }

    public List<PtsJanDataVo> getPtsJanDataList() {
        return ptsJanDataList;
    }

    public void setPtsJanDataList(List<PtsJanDataVo> ptsJanDataList) {
        this.ptsJanDataList = ptsJanDataList;
    }

    public String getModename() {
        return modename;
    }

    public void setModename(String modename) {
        this.modename = modename;
    }

    public String getCommoninfo() {
        return commoninfo;
    }

    public void setCommoninfo(String commoninfo) {
        this.commoninfo = commoninfo;
    }

    public String getVersioninfo() {
        return versioninfo;
    }

    public void setVersioninfo(String versioninfo) {
        this.versioninfo = versioninfo;
    }

    public String getOutflg() {
        return outflg;
    }

    public void setOutflg(String outflg) {
        this.outflg = outflg;
    }

    @Override
    public String toString() {
        return "PtsDetailDataVo{" +
                "taiHeader='" + taiHeader + '\'' +
                ", tanaHeader='" + tanaHeader + '\'' +
                ", janHeader='" + janHeader + '\'' +
                ", modename='" + modename + '\'' +
                ", commoninfo='" + commoninfo + '\'' +
                ", versioninfo='" + versioninfo + '\'' +
                ", outflg='" + outflg + '\'' +
                ", ptsTaiList=" + ptsTaiList +
                ", ptsTanaVoList=" + ptsTanaVoList +
                ", ptsJanDataList=" + ptsJanDataList +
                '}';
    }
}
