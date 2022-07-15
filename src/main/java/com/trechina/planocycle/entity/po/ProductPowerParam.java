package com.trechina.planocycle.entity.po;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class ProductPowerParam {
    private String company;
    private JSONObject customerCondition;
    private String prdCd;
    private Integer productPowerNo;
    private String rankWeight;
    private String recentlyEndTime;
    private String recentlyFlag;
    private String recentlyStTime;
    private String seasonEndTime;
    private String seasonFlag;
    private String seasonStTime;
    private String storeCd;
    private String yearFlag;
    private String commonPartsData;
    private String channelNm;
    private String placeNm;
    private String project;
    private String shelfPatternCd;
    private Integer janName2colNum;

}
