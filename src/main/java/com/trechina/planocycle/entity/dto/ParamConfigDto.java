package com.trechina.planocycle.entity.dto;


public class ParamConfigDto {
    private String label;
    private Integer value;
    private  String colName;

    private String itemType;
    private Integer rankFlag;

    private boolean disabled = false;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getRankFlag() {
        return rankFlag;
    }

    public void setRankFlag(Integer rankFlag) {
        this.rankFlag = rankFlag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    @Override
    public String toString() {
        return "ParamConfigDto{" +
                "label='" + label + '\'' +
                ", value=" + value +
                ", colName='" + colName + '\'' +
                '}';
    }
}
