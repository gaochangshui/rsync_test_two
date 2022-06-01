package com.trechina.planocycle.entity.vo;

import java.util.List;

public class PriorityOrderAttrTree {
    private Integer prdCd;
    private String prdName;
    private boolean show = true;
    private List<PriorityOrderAttrTree> children;

    public Integer getPrdCd() {
        return prdCd;
    }

    public void setPrdCd(Integer prdCd) {
        this.prdCd = prdCd;
    }

    public String getPrdName() {
        return prdName;
    }

    public void setPrdName(String prdName) {
        this.prdName = prdName;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public List<PriorityOrderAttrTree> getChildren() {
        return children;
    }

    public void setChildren(List<PriorityOrderAttrTree> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "PriorityOrderAttrTree{" +
                "prdCd=" + prdCd +
                ", prdName='" + prdName + '\'' +
                ", show=" + show +
                ", children=" + children +
                '}';
    }
}
