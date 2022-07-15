package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShelfPtsData {
    private Integer id;

    private String conpanyCd;

    private String fileName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;

    private String authorcd;

    private Integer shelfPatternCd;

    private Integer validFlg;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date startDay;

    private Date editTime;

    private String editerCd;

    private Integer deleteflg;

}
