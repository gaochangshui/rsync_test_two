package com.trechina.planocycle.entity.vo;

import java.util.List;

public class PriorityOrderAttrValueVo {
   private Integer attrCd;
   private String attrName;
   private String tableName;
   private String jansColNm;
   private Integer sort;
   private List<PriorityOrderAttrValue> values;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getJansColNm() {
        return jansColNm;
    }

    public void setJansColNm(String jansColNm) {
        this.jansColNm = jansColNm;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<PriorityOrderAttrValue> getValues() {
        return values;
    }

    public void setValues(List<PriorityOrderAttrValue> values) {
        this.values = values;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "PriorityOrderAttrValueVo{" +
                "attrCd=" + attrCd +
                ", attrName='" + attrName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", jansColNm='" + jansColNm + '\'' +
                ", sort=" + sort +
                ", values=" + values +
                ", show=" + show +
                '}';
    }
}
