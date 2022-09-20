package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class PriorityOrderMstDto {
    private String companyCd;

    private Integer priorityOrderCd;

    private String priorityOrderName;

    private Integer productPowerCd;

    private String shelfPatternCd;

    private String priorityData;

    private String attributeCd;

    private String rankAttributeList;

    private String attrOption;

    private Integer modeCheck;

    private Integer setSpecialFlag;

}
