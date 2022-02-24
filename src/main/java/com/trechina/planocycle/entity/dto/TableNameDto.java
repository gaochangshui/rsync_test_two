package com.trechina.planocycle.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TableNameDto {
    private String id;

    private String fileName;

    @JsonFormat(pattern="yyyy/MM/dd")
    private String createTime;


    private String authorName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }



    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TableNameDto{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", authorName='" + authorName + '\'' +
                '}';
    }
}
