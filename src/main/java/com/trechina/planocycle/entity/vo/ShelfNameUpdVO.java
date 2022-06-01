package com.trechina.planocycle.entity.vo;

import java.util.List;

public class ShelfNameUpdVO {
    private Integer id;

    private String shelfName;

    private List<Integer> area;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }

    public List<Integer> getArea() {
        return area;
    }

    public void setArea(List<Integer> area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "ShelfNameUpdVO{" +
                "id=" + id +
                ", shelfName='" + shelfName + '\'' +
                ", area=" + area +
                '}';
    }
}
