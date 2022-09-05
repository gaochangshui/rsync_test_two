package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class StarReadingTableDto {
    private String companyCd;
    private Integer priorityOrderCd;
    private List<String> janList;
    private List<String> expressItemList;
    private Integer modeCheck;
}
