package com.trechina.planocycle.enums;

public enum PosLabelEnum {
    PD_POS_AMOUNT(11001,"直近POS金額", "pdPosAmount", "pdPosAmountRank"),
    PD_POS_NUM(11002,"直近POS数量", "pdPosNum", "pdPosNumRank"),
    PD_BRANCH_AMOUNT(11003,"直近＠店金額", "pdBranchAmount", "pdBranchAmountRank"),
    PD_BRANCH_NUM(11004,"直近＠店数量", "pdBranchNum", "pdBranchNumRank"),
    PD_COMPARE_AMOUNT(11005,"比較POS金額", "pdCompareAmount", "pdCompareAmountRank"),
    PD_COMPARE_NUM(11006,"比較POS数量", "pdCompareNum", "pdCompareNumRank"),
    PD_BRANCH_COMPARE_AMOUNT(11007,"比較＠店金額", "pdBranchCompareAmount", "pdBranchCompareAmountRank"),
    PD_BRANCH_COMPARE_NUM(11008,"比較＠店数量", "pdBranchCompareNum", "pdBranchCompareNumRank");

    private Integer code;
    private String lable;
    private String columnName;
    private String columnRankName;

    PosLabelEnum(Integer code, String lable, String columnName, String columnRankName) {
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

    public static PosLabelEnum getPosLabelByCode(Integer code){
        for (PosLabelEnum value : PosLabelEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return PosLabelEnum.PD_BRANCH_AMOUNT;
    }
}
