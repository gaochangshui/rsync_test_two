package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderResultDataDto {
    private String companyCd;

    private String authorCd;

    private String janCd;

    private Long restrictCd;

    private Long skuRank;

    private Long newRank;

    private Long sortRank;

    private Long rank;

    private Integer adoptFlag;

    private Integer cutFlag;

    private Long face;

    private Long faceFact;

    private Long faceSku;

    private String irisu;

    private Integer taiCd;

    private Integer tanaCd;

    private Integer tanapositionCd;

    private Integer oldTaiCd;

    private Integer oldTanaCd;

    private Integer oldTanapositionCd;

    private Integer tanaType;

    private Integer restrictType;

    private Integer faceKaiten=0;
    private Long faceMen=1L;
    private Double salesCnt;
    private Integer tumiagesu=1;
    private Integer zaikosu=0;
    private Integer faceDisplayflg;
    private Integer facePosition;
    private Long width;
    private Long planoWidth;
    private Long height;
    private Long planoHeight;
    private Long planoDepth;
    private String planoIrisu;
    private Integer newFlag = 0;
    private Integer depthDisplayNum;

}
