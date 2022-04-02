package com.trechina.planocycle.enums;

public enum ProductPowerHeaderEnum {
    BASIC(""),
    CLASSIFY("商品分類"),
    POS("POS項目"),
    POS_RANK("POS項目Rank"),
    CUSTOMER("顧客グループ"),
    CUSTOMER_RANK("顧客グループRank"),
    PREPARE("予備項目"),
    PREPARE_RANK("予備項目Rank"),
    RANK("rank");

    private String name;

    ProductPowerHeaderEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
