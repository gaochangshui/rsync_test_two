package com.trechina.planocycle.entity.vo;

public class ShelfPtsNameVO {
    private Integer id;
    private String fileName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "ShelfPtsNameVO{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
