package com.trechina.planocycle.entity.dto;

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

    public String getProAttrTable() {
        return proAttrTable;
    }

    public void setProAttrTable(String proAttrTable) {
        this.proAttrTable = proAttrTable;
    }

    public String getProInfoTable() {
        return proInfoTable;
    }

    public void setProInfoTable(String proInfoTable) {
        this.proInfoTable = proInfoTable;
    }

    public String getProKaisouTable() {
        return proKaisouTable;
    }

    public void setProKaisouTable(String proKaisouTable) {
        this.proKaisouTable = proKaisouTable;
    }

    public String getStoreAttrTable() {
        return storeAttrTable;
    }

    public void setStoreAttrTable(String storeAttrTable) {
        this.storeAttrTable = storeAttrTable;
    }

    public String getStoreInfoTable() {
        return storeInfoTable;
    }

    public void setStoreInfoTable(String storeInfoTable) {
        this.storeInfoTable = storeInfoTable;
    }

    public String getStoreKaisouTable() {
        return storeKaisouTable;
    }

    public void setStoreKaisouTable(String storeKaisouTable) {
        this.storeKaisouTable = storeKaisouTable;
    }

    @Override
    public String toString() {
        return "GetCommonPartsDataDto{" +
                "proAttrTable='" + proAttrTable + '\'' +
                ", proInfoTable='" + proInfoTable + '\'' +
                ", proKaisouTable='" + proKaisouTable + '\'' +
                ", storeAttrTable='" + storeAttrTable + '\'' +
                ", storeKaisouTable='" + storeKaisouTable + '\'' +
                ", storeInfoTable='" + storeInfoTable + '\'' +
                '}';
    }
}
