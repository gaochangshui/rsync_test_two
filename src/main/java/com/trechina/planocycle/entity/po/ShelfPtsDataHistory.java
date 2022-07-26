package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.util.Date;
@Data
public class ShelfPtsDataHistory {
    private Integer ptsCd;

    private Integer patternCd;

    private String companyCd;

    private Integer validFlg;

    private Date startDay;

    private Date createTime;

    private String authorCd;

    private Date editTime;

    private String editerCd;

    private Integer deleteflg;

}