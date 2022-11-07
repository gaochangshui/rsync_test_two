package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PtsPatternRelationDto {
    private Integer fileCd;
    private String fileName;
    private Integer shelfPatternCd;
    private String shelfPatternName;
    private String shelfName;
    private Integer shelfNameCd;
}
