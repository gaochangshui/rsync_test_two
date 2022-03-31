package com.trechina.planocycle.enums;

public enum CustomerLabelEnum {
    GD_POS_AMOUNT(21001,"直近POS金額", "gdPosAmount", "gdPosAmountRank"),
    GD_POS_NUM(21002,"直近POS数量", "gdPosNum", "gdPosNumRank"),
    GD_BRANCH_AMOUNT(21003,"直近＠店金額", "gdBranchAmount", "gdBranchAmountRank"),
    GD_BRANCH_NUM(21004,"直近＠店数量", "gdBranchNum", "gdBranchNumRank"),
    GD_COMPARE_AMOUNT(21005,"比較POS金額", "gdCompareAmount", "gdCompareAmountRank"),
    GD_COMPARE_NUM(21006,"比較POS数量", "gdCompareNum", "gdCompareNumRank"),
    GD_BRANCH_COMPARE_AMOUNT(21007,"比較＠店金額", "gdBranchCompareAmount", "gdBranchCompareAmountRank"),
    GD_BRANCH_COMPARE_NUM(21008,"比較＠店数量", "gdBranchCompareNum", "gdBranchCompareNumRank");

    private Integer code;
    private String lable;
    private String columnName;
    private String columnRankName;

    CustomerLabelEnum(Integer code, String lable, String columnName, String columnRankName) {
        this.code = code;
        this.lable = lable;
        this.columnName = columnName;
        this.columnRankName = columnRankName;
    }

    public Integer getCode() {
        return code;
    }

    public String getLable() {
        return lable;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnRankName() {
        return columnRankName;
    }

    public static CustomerLabelEnum getCustomerLabelByCode(Integer code){
        for (CustomerLabelEnum value : CustomerLabelEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return CustomerLabelEnum.GD_POS_AMOUNT;
    }
}
