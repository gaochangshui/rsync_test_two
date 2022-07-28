package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

@Data
public class JanInfoVO {
    private String janHeader;
    private String janColumn;
    private List<LinkedHashMap<String, Object>> janDataList;

}
