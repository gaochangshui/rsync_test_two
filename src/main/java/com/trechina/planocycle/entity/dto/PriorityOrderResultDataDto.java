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

    private Integer tanaType;

    private Integer restrictType;

    private Long faceKeisan;
    private Double salesCnt;

    private Long width;
    private Long height;

}
