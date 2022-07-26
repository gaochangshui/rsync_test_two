package com.trechina.planocycle.entity.dto;

import com.trechina.planocycle.entity.vo.PriorityOrderAttrVO;
import lombok.Data;

import java.util.List;
@Data
public class PriorityOrderSpaceDto {
    private String companyCd;
    private Short attr1;
    private Short attr2;
    private Long patternCd;
    private String areaNameCd;
    private List<PriorityOrderAttrVO> dataList;
    private Integer priorityOrderCd;
}
