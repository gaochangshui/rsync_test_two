package com.trechina.planocycle.entity.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
       faceAvgNum = BigDecimal.valueOf(this.faceAvgNum).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return faceAvgNum;
    }

    public void setFaceAvgNum(Double faceAvgNum) {
        faceAvgNum = BigDecimal.valueOf(faceAvgNum).setScale(2, RoundingMode.HALF_UP).doubleValue();
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
