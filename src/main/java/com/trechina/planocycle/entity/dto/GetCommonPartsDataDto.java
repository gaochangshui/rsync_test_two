package com.trechina.planocycle.entity.dto;

import lombok.Data;

@Data
public class GetCommonPartsDataDto {

    /**
     * 商品属性テーブル
     */
    private String proAttrTable;
    /**
     * 商品情報シート
     */
    private String proInfoTable;
    /**
     * 商品分類表
     */
    private String proKaisouTable;
    /**
     * 店舗ヘッドテーブル
     */
    private String storeAttrTable;
    /**
     * 店舗属性テーブル
     */
    private String storeKaisouTable;
    /**
     * 店舗情報テーブル
     */
    private String storeInfoTable;

    private String proZokuseiMstTable;

    private String proZokuseiDataTable;
    private String prodMstClass;
    private String prodIsCore;


}
