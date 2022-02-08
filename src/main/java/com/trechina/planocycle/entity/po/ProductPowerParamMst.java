package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ProductPowerParamMst {
    private String conpanyCd;

    private Integer productPowerCd;

    private Integer peridFlag;

    private String startPerid;

    private String endPerid;

    private Integer seasonPeridFlag;

    private String seasonStPerid;

    private String seasonEndPerid;

    private String storeCd;

    private String prdCd;

    private String channelName;

    private String countyName;

    private Integer itemFlg;

    public String getConpanyCd() {
        return conpanyCd;
    }

    public void setConpanyCd(String conpanyCd) {
        this.conpanyCd = conpanyCd;
    }

    public Integer getProductPowerCd() {
        return productPowerCd;
    }

    public void setProductPowerCd(Integer productPowerCd) {
        this.productPowerCd = productPowerCd;
    }

    public Integer getPeridFlag() {
        return peridFlag;
    }

    public void setPeridFlag(Integer peridFlag) {
        this.peridFlag = peridFlag;
    }
    @JsonFormat(pattern = "yyyy-MM-dd")
    public String getStartPerid() {
        return startPerid;
    }

    public void setStartPerid(String startPerid) {
        this.startPerid = startPerid;
    }
    @JsonFormat(pattern = "yyyy-MM-dd")
    public String getEndPerid() {
        return endPerid;
    }

    public void setEndPerid(String endPerid) {
        this.endPerid = endPerid;
    }

    public Integer getSeasonPeridFlag() {
        return seasonPeridFlag;
    }

    public void setSeasonPeridFlag(Integer seasonPeridFlag) {
        this.seasonPeridFlag = seasonPeridFlag;
    }
    @JsonFormat(pattern = "yyyy-MM-dd")
    public String getSeasonStPerid() {
        return seasonStPerid;
    }

    public void setSeasonStPerid(String seasonStPerid) {
        this.seasonStPerid = seasonStPerid;
    }
    @JsonFormat(pattern = "yyyy-MM-dd")
    public String getSeasonEndPerid() {
        return seasonEndPerid;
    }

    public void setSeasonEndPerid(String seasonEndPerid) {
        this.seasonEndPerid = seasonEndPerid;
    }

    public String getStoreCd() {
        return storeCd;
    }

    public void setStoreCd(String storeCd) {
        this.storeCd = storeCd;
    }

    public String getPrdCd() {
        return prdCd;
    }

    public void setPrdCd(String prdCd) {
        this.prdCd = prdCd;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName == null ? null : channelName.trim();
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName == null ? null : countyName.trim();
    }

    public Integer getItemFlg() {
        return itemFlg;
    }

    public void setItemFlg(Integer itemFlg) {
        this.itemFlg = itemFlg;
    }

    @Override
    public String toString() {
        return "ProductPowerParamMst{" +
                "conpanyCd='" + conpanyCd + '\'' +
                ", productPowerCd=" + productPowerCd +
                ", peridFlag=" + peridFlag +
                ", startPerid='" + startPerid + '\'' +
                ", endPerid='" + endPerid + '\'' +
                ", seasonPeridFlag=" + seasonPeridFlag +
                ", seasonStPerid='" + seasonStPerid + '\'' +
                ", seasonEndPerid='" + seasonEndPerid + '\'' +
                ", storeCd='" + storeCd + '\'' +
                ", prodCd='" + prdCd + '\'' +
                ", channelName='" + channelName + '\'' +
                ", countyName='" + countyName + '\'' +
                ", itemFlg=" + itemFlg +
                '}';
    }
}
