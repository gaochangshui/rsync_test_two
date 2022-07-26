package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPowerWeight {
    private String conpanyCd;

    private Integer productPowerCd;

    private Integer marketPosFlag;

    private Integer dataCd;

    private BigDecimal dataWeight;


}