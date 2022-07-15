package com.trechina.planocycle.entity.vo;

import lombok.Data;

@Data
public class PriorityAllPatternListVO {
    private String shelfName;
    private Integer shelfPatternCd;
    private String shelfPatternName;
    private Integer taiCnt;
    private Integer tanaCnt;
    private Integer checkFlag;
    private Boolean disabled = true;
}
