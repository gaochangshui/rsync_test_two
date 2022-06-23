package com.trechina.planocycle.entity.vo;

public class PriorityOrderAttrListVo {
    private String attrCd;
    private String attrName;


    public String getAttrCd() {
        return attrCd;
    }

    public void setAttrCd(String attrCd) {
        this.attrCd = attrCd;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }



    @Override
    public String toString() {
        return "PriorityOrderAttrListVo{" +
                "attrCd='" + attrCd + '\'' +
                ", attrName='" + attrName + '\'' +
                '}';
    }
}
