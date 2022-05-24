package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PresetAttribute {
    @JsonIgnore
    private String authorCd;
    private String attrData;
    private String commonPartsData;

    public String getAttrData() {
        return attrData;
    }

    public void setAttrData(String attrData) {
        this.attrData = attrData;
    }

    public String getCommonPartsData() {
        return commonPartsData;
    }

    public void setCommonPartsData(String commonPartsData) {
        this.commonPartsData = commonPartsData;
    }

    public String getAuthorCd() {
        return authorCd;
    }

    public void setAuthorCd(String authorCd) {
        this.authorCd = authorCd;
    }
}