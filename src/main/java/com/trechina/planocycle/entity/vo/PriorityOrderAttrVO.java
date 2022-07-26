package com.trechina.planocycle.entity.vo;

import lombok.Data;

@Data
public class PriorityOrderAttrVO {
    private String attrACd;
    private String attrAName;
    private String attrBCd;
    private String attrBName;
    private String jansAColnm;
    private String jansBColnm;
    private Integer existingZoning;
    private Integer newZoning;
    private Integer tanaPattan;
    private Integer rank;


}
