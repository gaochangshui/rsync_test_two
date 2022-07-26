package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderPlatformShedDto {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer taiCd;
    private Integer tanaCd;
    private Integer faceNum;
    private Integer skuNum;

}
