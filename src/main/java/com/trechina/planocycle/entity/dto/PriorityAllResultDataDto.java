package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityAllResultDataDto extends PriorityOrderResultDataDto {
    private Integer shelfPatternCd;
    private Double salesCnt;
    private Integer priorityAllCd;
    private Integer faceNum;
    private String companyCd;
    private String janCd;

}
