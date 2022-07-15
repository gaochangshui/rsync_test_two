package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class ProductPowerDataForCgiDto {
    private String mode;
    private String company;
    private String guid;
    private Integer productPowerNo;
    private String recentlyEndTime;
    private String recentlyFlag;
    private String recentlyStTime;
    private String seasonEndTime;
    private String seasonFlag;
    private String seasonStTime;
    private String prdCd;
    private String storeCd;
    private String yearFlag;
    private String channelNm;
    private String placeNm;

}
