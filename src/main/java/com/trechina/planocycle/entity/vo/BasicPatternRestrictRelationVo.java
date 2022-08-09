package com.trechina.planocycle.entity.vo;

import lombok.Data;

/**
 * 
 * @TableName basic_pattern_restrict_relation
 */
@Data
public class BasicPatternRestrictRelationVo {
    /**
     * 
     */
    private Long priorityOrderCd;

    /**
     * 
     */
    private String companyCd;

    /**
     * 
     */
    private String authorCd;

    /**
     * 
     */
    private Integer taiCd;

    /**
     * 
     */
    private Integer tanaCd;

    /**
     * 
     */
    private Integer tanaPosition;
    private Integer areaPosition;

    /**
     * 
     */
    private Long restrictCd;

    /**
     * 
     */
    private Double area;

    private static final long serialVersionUID = 1L;

}