package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CommodityBranchVO {
    @JsonIgnore
    private String shelfPatternName;

    private Integer shelfPatternCd;
    private String branchName;

    @JsonIgnore
    private String branch;
    @JsonIgnore
    private Integer beforeFlag;
    private Integer flag;


}
