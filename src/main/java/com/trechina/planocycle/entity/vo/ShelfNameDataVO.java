package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class ShelfNameDataVO {
    private Integer id;

    private String conpanyCd;

    private String shelfName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;

    private String authorCd;


}
