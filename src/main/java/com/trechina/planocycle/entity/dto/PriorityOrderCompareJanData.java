package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderCompareJanData {
    private Integer shelfPatternCd;
    private String shelfPatternName;
    private String shelfName;
    private Object branchNum;
    private Integer saleForecast;
    private Integer skuNew;
    private Integer skuOld;
    private Integer skuCompare;
    private Integer skuAdd;
    private Integer skuCut;
    private Integer faceOld;
    private Integer faceNew;
    private Integer faceCompare;
    private String ptsOld = "PTS旧";
    private String ptsNew = "PTS新";
    private Integer oldAmount;
    private Integer newAmount;

}
