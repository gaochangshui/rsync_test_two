package com.trechina.planocycle.entity.dto;

import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;
import lombok.Data;

import java.util.List;

@Data
public class DownloadSortDto {
    private String companyCd;
    private  String jan;
    private String taiCd;
    private  String tanaCd;
    private  String mode;
    private Integer priorityOrderCd;
    private  Integer flag = 1;
    private List<PriorityOrderAttributeClassify> list;

}
