package com.trechina.planocycle.entity.po;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class WorkProductPowerReserveData {
    private String companyCd;
    private String authorCd;
    private Integer dataCd;
    private String jan;
    private BigDecimal dataValue;
    private Integer productPowerCd;


}
