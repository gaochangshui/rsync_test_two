package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class PriorityOrderMst {
    private String companyCd;

    private Integer priorityOrderCd;

    private String priorityOrderName;

    private Integer productPowerCd;

    private Short partitionFlag;

    private Short partitionVal;

    private Short topPartitionFlag;

    private Short topPartitionVal;

    private String attrOption;

    private String commonPartsData;

    private Integer tanaWidCheck;


}
