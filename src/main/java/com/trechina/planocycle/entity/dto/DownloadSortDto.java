package com.trechina.planocycle.entity.dto;

import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;

import java.util.List;

public class DownloadSortDto {
    private String companyCd;
    private  String jan;
    private String taiCd;
    private  String tanaCd;
    private  String mode;
    private Integer priorityOrderCd;
    private List<PriorityOrderAttributeClassify> list;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getTaiCd() {
        return taiCd;
    }

    public void setTaiCd(String taiCd) {
        this.taiCd = taiCd;
    }

    public String getTanaCd() {
        return tanaCd;
    }

    public void setTanaCd(String tanaCd) {
        this.tanaCd = tanaCd;
    }

    public Integer getPriorityOrderCd() {
        return priorityOrderCd;
    }

    public void setPriorityOrderCd(Integer priorityOrderCd) {
        this.priorityOrderCd = priorityOrderCd;
    }


    public List<PriorityOrderAttributeClassify> getList() {
        return list;
    }

    public void setList(List<PriorityOrderAttributeClassify> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "DownloadSortDto{" +
                "companyCd='" + companyCd + '\'' +
                ", jan='" + jan + '\'' +
                ", taiCd='" + taiCd + '\'' +
                ", tanaCd='" + tanaCd + '\'' +
                ", mode='" + mode + '\'' +
                ", priorityOrderCd=" + priorityOrderCd +
                ", list=" + list +
                '}';
    }
}
