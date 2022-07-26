package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.util.List;
@Data
public class PriorityAllSaveDto {
    private String companyCd;
    private Integer priorityAllCd;
    private Integer priorityOrderCd;
    private List<Integer> patterns;
    private String priorityAllName;

}
