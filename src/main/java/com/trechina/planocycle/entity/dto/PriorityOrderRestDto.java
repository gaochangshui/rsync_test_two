package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderRestDto {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer restrictCd;
    private String restrictName;

    private Integer skuNum;
    private Integer faceNum;

}
