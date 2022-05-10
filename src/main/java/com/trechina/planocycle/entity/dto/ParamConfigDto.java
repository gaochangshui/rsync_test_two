package com.trechina.planocycle.entity.dto;


public class ParamConfigDto {
    private String label;
    private Integer value;
    private  String colName;

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
