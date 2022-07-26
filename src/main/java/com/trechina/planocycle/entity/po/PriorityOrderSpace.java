package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.util.Date;

@Data
public class PriorityOrderSpace {
    private String companyCd;

    private Integer priorityOrderCd;

    private String attribute1Value;

    private String attribute2Value;

    private Integer oldZoning;

    private Integer newZoning;

    private Integer tanaCount;

    private Integer zoningNum;

    private String authorCd;

    private Date createTime;

    private String editerCd;

    private Date editTime;

    private Short deleteflg;

    private String attribute1Name;

    private String attribute2Name;

}