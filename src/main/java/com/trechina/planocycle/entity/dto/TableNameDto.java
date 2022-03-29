package com.trechina.planocycle.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TableNameDto {
    private Integer id;

    private String fileName;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy/MM/dd")
    private Date createTime;


    private String authorName;

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



    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
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
