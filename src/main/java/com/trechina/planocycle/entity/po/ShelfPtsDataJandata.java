package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.util.Date;

@Data
public class ShelfPtsDataJandata {
    private String companyCd;

    private Integer ptsCd;

    private Integer taiCd;

    private Integer tanaCd;

    private Integer tanapositionCd;

    private String jan;

    private Integer faceCount;

    private Integer faceMen;

    private Integer faceKaiten;

    private Integer tumiagesu;

    private Integer zaikosu;

    private Integer faceDisplayflg;

    private Integer facePosition;

    private Integer depthDisplayNum;

    private Date createTime;

    private String authorCd;

    private Date editTime;

    private String editerCd;

    private Integer deleteflg;

}