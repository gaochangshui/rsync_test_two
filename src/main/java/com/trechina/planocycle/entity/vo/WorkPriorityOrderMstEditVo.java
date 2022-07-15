package com.trechina.planocycle.entity.vo;

import lombok.Data;

@Data
public class WorkPriorityOrderMstEditVo {


    private Integer productPowerCd;

    private Integer shelfCd;

    private Long shelfPatternCd;

    private Short partitionFlag;

    private Short partitionVal;

    private String commonPartsData;

    private Integer topPartitionVal;
    private Integer topPartitionFlag;
    private Integer tanaWidCheck;

}