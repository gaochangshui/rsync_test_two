package com.trechina.planocycle.entity.po;

import lombok.Data;

/**
 * 
 * @TableName planocycle_zokusei_mst
 */
@Data
public class ZokuseiMst   {
    /**
     * 
     */
    private String companyCd;

    /**
     * 
     */
    private String classCd;

    /**
     * 
     */
    private Integer zokuseiId;

    /**
     * 
     */
    private String zokuseiNm;

    /**
     * 
     */
    private Integer zokuseiSort;

    /**
     * 
     */
    private Integer type;

    /**
     * 
     */
    private Integer sortType;

    /**
     * 
     */
    private Integer zokuseiCol;

    private static final long serialVersionUID = 1L;

}