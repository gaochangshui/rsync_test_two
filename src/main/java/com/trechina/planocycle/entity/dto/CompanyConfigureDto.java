package com.trechina.planocycle.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CompanyConfigureDto {
    /**
     * 商品マスタ
     */
    private String prodIsCore;
    /**
     * 店舗マスタ
     */
    private String storeIsCore;
    /**
     * カレンダー
     */
    private String dateIsCore;
    private  Integer basketPriceFlag;
    private  Integer intageFlag;
    private Integer kokyakuFlag;
    private Integer isIdPos;

    private List<Map<String,Object>> storeClassList;

    private List<Map<String,Object>> prodClassList;

    private List<Map<String,Object>> prodClassAttrList;

    private List<Map<String,Object>> companyColMap;
}
