package com.trechina.planocycle.entity.dto;

import java.math.BigDecimal;

public class FaceNumDataDto {
    private Integer faceMaxNum;
    private Integer faceMinNum;
    private Double faceAvgNum;

    public Integer getFaceMaxNum() {
        return faceMaxNum;
    }

    public void setFaceMaxNum(Integer faceMaxNum) {
        this.faceMaxNum = faceMaxNum;
    }

    public Integer getFaceMinNum() {
        return faceMinNum;
    }

    public void setFaceMinNum(Integer faceMinNum) {
        this.faceMinNum = faceMinNum;
    }

    public Double getFaceAvgNum() {
       faceAvgNum =  new BigDecimal(this.faceAvgNum).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return faceAvgNum;
    }

    public void setFaceAvgNum(Double faceAvgNum) {
        faceAvgNum =  new BigDecimal(faceAvgNum).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        this.faceAvgNum = faceAvgNum;
    }

    @Override
    public String toString() {
        return "FaceNumDataDto{" +
                "faceMaxNum=" + faceMaxNum +
                ", faceMinNum=" + faceMinNum +
                ", faceAvgNum=" + faceAvgNum +
                '}';
    }
}
