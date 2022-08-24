package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
@Data
public class ShelfPatternMst {
    private String conpanyCd;
    private Integer shelfNameCd;
    private String shelfName;
    private Integer shelfPatternCd;
    private String ptsRelationID;

    private String areaName;
    private String shelfPatternName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;

    private String authorCd;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date registered;

    private String maintainerCd;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date modified;
    private Integer branch;

    private String[] storeCd;
    private String commonPartsData;
    @JsonIgnore
    private String storeCdStr;
    private Integer branchNum;
    private Integer specialFlag;

}
