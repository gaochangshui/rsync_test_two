package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderJanCgiDto {
    private String company;
    private Integer shelfPatternNo;
    private String guid;
    private String mode;
    private String[] dataArray;
    private String usercd;
}
