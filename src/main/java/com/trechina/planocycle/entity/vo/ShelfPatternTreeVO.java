package com.trechina.planocycle.entity.vo;

public class ShelfPatternTreeVO {
    private Integer id;
    private Integer pId;
    private String Value;
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ShelfPatternTreeInfo{" +
                "id=" + id +
                ", pId=" + pId +
                ", Value=" + Value +
                ", title='" + title + '\'' +
                '}';
    }
}
