package com.trechina.planocycle.enums;

public enum ProductPowerHeaderEnum {
    BASIC("", "basic"),
    CLASSIFY("商品分類", "classify"),
    ATTR("商品属性", "attr"),
    POS("全体項目", "pos"),
    POS_RANK("全体項目Rank", "posRank"),
    CUSTOMER("顧客条件項目", "customer"),
    CUSTOMER_RANK("顧客条件項目Rank", "customerRank"),
    INTAGE("市場", "intage"),
    INTAGE_RANK("市場Rank", "intageRank"),
    PREPARE("予備項目", "prepare"),
    PREPARE_RANK("予備項目Rank", "prepareRank"),
    RANK("rank", "rank");

    private String name;
    private String code;

    ProductPowerHeaderEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
