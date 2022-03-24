package com.trechina.planocycle.entity.dto;

public class ProductPowerDataDto {
    private String jan;
    private String janNew;
    private Long restrictCd;
    private Integer rankResult;
    private Integer rankNewResult;
    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getJanNew() {
        return janNew;
    }

    public void setJanNew(String janNew) {
        this.janNew = janNew;
    }


    public Long getRestrictCd() {
        return restrictCd;
    }

    public void setRestrictCd(Long restrictCd) {
        this.restrictCd = restrictCd;
    }

    public Integer getRankResult() {
        return rankResult;
    }

    public void setRankResult(Integer rankResult) {
        this.rankResult = rankResult;
    }

    public Integer getRankNewResult() {
        return rankNewResult;
    }

    public void setRankNewResult(Integer rankNewResult) {
        this.rankNewResult = rankNewResult;
    }

    @Override
    public String toString() {
        return "ProductPowerDataDto{" +
                "jan='" + jan + '\'' +
                ", janNew='" + janNew + '\'' +
                ", restrictCd=" + restrictCd +
                ", rankResult=" + rankResult +
                '}';
    }
}
