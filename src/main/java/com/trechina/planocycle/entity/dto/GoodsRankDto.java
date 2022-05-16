package com.trechina.planocycle.entity.dto;


public class GoodsRankDto {
    private String jan;
    private Integer goodsRank;

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public Integer getGoodsRank() {
        return goodsRank;
    }

    public void setGoodsRank(Integer goodsRank) {
        this.goodsRank = goodsRank;
    }

    @Override
    public String toString() {
        return "GoodsRankDto{" +
                "jan='" + jan + '\'' +
                ", goodsRank=" + goodsRank +
                '}';
    }
}
