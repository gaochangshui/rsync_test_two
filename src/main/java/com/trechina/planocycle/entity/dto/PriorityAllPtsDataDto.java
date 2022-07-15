package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityAllPtsDataDto {
    private Integer id;
    private Integer oldPtsCd;
    private Integer priorityAllCd;
    private String authorCd;
    private String companyCd;
    private String fileName;
    private Integer shelfPatternCd;

}
