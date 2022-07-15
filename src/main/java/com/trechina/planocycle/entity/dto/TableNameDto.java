package com.trechina.planocycle.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TableNameDto {
    private Integer id;

    private String fileName;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy/MM/dd")
    private Date createTime;


    private String authorName;

}
