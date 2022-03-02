package com.trechina.planocycle.entity.vo;


import java.util.List;

public class ShelfNamePatternVo {
    private Integer value;
    private String label;
    private List<ShelfPatternChidrenVo> children;

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

    public List<ShelfPatternChidrenVo> getChildren() {
        return children;
    }

    public void setChildren(List<ShelfPatternChidrenVo> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "ShelfNamePatternVo{" +
                "value=" + value +
                ", label='" + label + '\'' +
                ", children=" + children +
                '}';
    }


}
