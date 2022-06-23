package com.trechina.planocycle.entity.vo;

import java.util.List;

public class PriorityOrderAttrValueVo {
   private Integer attrCd;
   private String attrName;
   private Integer sort;
   private List<PriorityOrderAttrValue> children;
   private Boolean show =true;

    public Integer getAttrCd() {
        return attrCd;
    }

    public void setAttrCd(Integer attrCd) {
        this.attrCd = attrCd;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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
}
