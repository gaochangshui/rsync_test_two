package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class ParamConfigDto {
    private String label;
    private Integer value;
    private  String colName;
    private String itemType;
    private Integer rankFlag;
    private Integer flag;
    private boolean disabled = false;

}
