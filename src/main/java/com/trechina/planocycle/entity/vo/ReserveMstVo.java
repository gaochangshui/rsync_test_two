package com.trechina.planocycle.entity.vo;

public class ReserveMstVo {
    private Integer valueCd;
    private String dataName;



    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getValueCd() {
        return valueCd;
    }

    public void setValueCd(Integer valueCd) {
        this.valueCd = valueCd;
    }

    @Override
    public String toString() {
        return "ReserveMstVo{" +
                "valueCd=" + valueCd +
                ", dataName='" + dataName + '\'' +
                '}';
    }
}
