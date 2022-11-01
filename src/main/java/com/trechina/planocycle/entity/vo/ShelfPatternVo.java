package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class ShelfPatternVo {
    private Integer shelfPatternCd;
    private String companyCd;
    private Integer shelfNameCD;
    private List<Integer> area;
    private String shelfPatternName;
    private String ptsRelationID;
    private String commonPartsData;
    private String[] storeCd;


}
