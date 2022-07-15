package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShelfNameMst {
    private Integer id;

    private String conpanyCd;

    private String shelfName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;

    private String authorCd;

}
