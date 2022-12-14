package com.trechina.planocycle.entity.vo;

import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;
import lombok.Data;

import java.util.List;
@Data
public class PtsTanaVo {
    private Integer taiCd;
    private Integer tanaCd;
    private Integer tanaHeight;
    private Integer tanaWidth;
    private Integer tanaDepth;
    private Integer tanaThickness;
    private Integer tanaType;

    private String companyCd;

    private Integer priorityOrderCd;
    private List<BasicPatternRestrictRelation> group;
    private String remarks;

}
