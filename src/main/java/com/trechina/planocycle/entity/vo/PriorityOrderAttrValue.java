package com.trechina.planocycle.entity.vo;

import java.util.List;

public class PriorityOrderAttrValue {
    private String attrCd;
    private String val;
    private String type;
    private String attrName;
    private String classifyName;
    private List<PriorityOrderAttrValue> children;
    private Boolean show = true;

    public String getAttrCd() {
        return attrCd;
    }

    public void setAttrCd(String attrCd) {
        this.attrCd = attrCd;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public List<PriorityOrderAttrValue> getChildren() {
        return children;
    }

    public void setChildren(List<PriorityOrderAttrValue> children) {
        this.children = children;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "PriorityOrderAttrValue{" +
                "attrCd='" + attrCd + '\'' +
                ", val='" + val + '\'' +
                ", type='" + type + '\'' +
                ", attrName='" + attrName + '\'' +
                ", classifyName='" + classifyName + '\'' +
                ", children=" + children +
                ", show=" + show +
                '}';
    }
}
