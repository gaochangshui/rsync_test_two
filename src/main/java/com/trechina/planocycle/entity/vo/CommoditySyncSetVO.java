package com.trechina.planocycle.entity.vo;

import com.trechina.planocycle.entity.po.CommoditySyncSet;
import lombok.Data;

import java.util.List;

@Data
public class CommoditySyncSetVO {
    private String companyCd;
    private List<CommoditySyncSet> commonPartsData;
}
