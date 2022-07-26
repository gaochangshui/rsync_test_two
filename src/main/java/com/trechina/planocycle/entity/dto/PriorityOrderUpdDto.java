package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderUpdDto {
    private String colNameList;
    private Integer priorityOrderCd;
    private String companyCd;
    private String shelfPatternCd;
    private String rankAttributeList;
    private Integer delFlg;


}
