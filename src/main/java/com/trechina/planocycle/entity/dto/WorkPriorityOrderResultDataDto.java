package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class WorkPriorityOrderResultDataDto {
    private String companyCd;
    private String authorCd;
    private Integer priorityOrderCd;
    private String janCd;
    private Long face;
    private Long faceFact;
    private Integer taiCd;
    private Integer tanaCd;
    private Integer tanapositionCd;
    private Integer faceMen;
    private Integer faceKaiten;
    private Integer tumiagesu;
    private Integer zaikosu;
    private Integer faceDisplayflg;
    private Integer facePosition;
    private Integer depthDisplayNum;

    private Long skuRank;

    private Long newRank;

    private Long sortRank;

    private Long rank;

}
