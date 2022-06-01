package com.trechina.planocycle.entity.vo;

public class ShelfNameVO {
    private Integer id;
    private String shelfName;

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

    @Override
    public String toString() {
        return "ShelfNameVO{" +
                "id=" + id +
                ", shelfName='" + shelfName + '\'' +
                '}';
    }
}
