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
    private Integer taiNum =0;
    private Integer tanaNum =0;
    private Integer faceNum =0;
    private Integer skuNum=0;
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

    public Integer getTaiNum() {
        return taiNum;
    }

    public void setTaiNum(Integer taiNum) {
        this.taiNum = taiNum == null ? 0 : taiNum;
    }

    public Integer getTanaNum() {
        return tanaNum;
    }

    public void setTanaNum(Integer tanaNum) {
        this.tanaNum = tanaNum == null ? 0 : tanaNum;
    }

    public Integer getFaceNum() {
        return faceNum;
    }

    public void setFaceNum(Integer faceNum) {
        this.faceNum = faceNum == null ? 0 : faceNum;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum == null ? 0 : skuNum;
    }

    @Override
    public String toString() {
        return "PtsDetailDataVo{" +
                "id=" + id +
                ", taiHeader='" + taiHeader + '\'' +
                ", tanaHeader='" + tanaHeader + '\'' +
                ", janHeader='" + janHeader + '\'' +
                ", modename='" + modename + '\'' +
                ", commoninfo='" + commoninfo + '\'' +
                ", versioninfo='" + versioninfo + '\'' +
                ", outflg='" + outflg + '\'' +
                ", taiNum=" + taiNum +
                ", tanaNum=" + tanaNum +
                ", faceNum=" + faceNum +
                ", skuNum=" + skuNum +
                ", ptsTaiList=" + ptsTaiList +
                ", ptsTanaVoList=" + ptsTanaVoList +
                ", ptsJanDataList=" + ptsJanDataList +
                '}';
    }
}
