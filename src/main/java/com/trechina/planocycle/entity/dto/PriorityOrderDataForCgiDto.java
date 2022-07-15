package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class PriorityOrderDataForCgiDto {
    private String company;
    private String shelfPatternNo;
    private String guid;
    private String mode;
    private Integer productPowerNo;
    private String attributeCd;
    private Integer productNmFlag;
    private Integer priorityNO;
    private String writeReadFlag;
    private String[] dataArray;
    private List<String> orderCol;
    private Integer itemFlg;
    private String attrList;

}
