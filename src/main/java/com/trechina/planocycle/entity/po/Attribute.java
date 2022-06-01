package com.trechina.planocycle.entity.po;

public class Attribute {
    private Integer attrcd;

    private String attrname;

    public Integer getAttrcd() {
        return attrcd;
    }

    public void setAttrcd(Integer attrcd) {
        this.attrcd = attrcd;
    }

    public String getAttrname() {
        return attrname;
    }

    public void setAttrname(String attrname) {
        this.attrname = attrname == null ? null : attrname.trim();
    }
}