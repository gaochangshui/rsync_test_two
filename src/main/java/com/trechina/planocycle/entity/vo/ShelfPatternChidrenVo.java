package com.trechina.planocycle.entity.vo;



public class ShelfPatternChidrenVo {
    private Integer value;
    private String label;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ShelfPatternChidrenVo{" +
                "value=" + value +
                ", label='" + label + '\'' +
                '}';
    }
}
