package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPowderYobiitemData {
    private String companyCd;
    private Long productPowerCd;
    private Integer itemCd;
    private String jan;
    private BigDecimal value;

}
