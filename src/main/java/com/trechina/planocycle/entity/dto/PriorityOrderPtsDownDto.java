package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderPtsDownDto {
    private String company;
    private String shelfPatternNo;
    private String guid;
    private String mode;
    private Integer priorityNo;
    private String taskId;
    private String rankAttributeList;
    private String attributeCd;
    private String rankAttributeCd;
    private Integer ptsVerison;
    private String shelfPatternNoNm;
    private Integer newCutFlg;

}
