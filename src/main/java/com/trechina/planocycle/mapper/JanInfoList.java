package com.trechina.planocycle.mapper;


import com.trechina.planocycle.entity.vo.CommonPartsDataVO;

public class JanInfoList {
    private String jan;
    private String companyCd;
    private CommonPartsDataVO commonPartsData;

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public CommonPartsDataVO getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(CommonPartsDataVO commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    @Override
    public String toString() {
        return "JanInfoList{" +
                "jan='" + jan + '\'' +
                ", companyCd='" + companyCd + '\'' +
                ", commonPartsData='" + commonPartsData + '\'' +
                '}';
    }
}
