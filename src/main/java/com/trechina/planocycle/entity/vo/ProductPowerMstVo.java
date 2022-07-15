package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ProductPowerMstVo {
    private String productPowerName;
    private String authorName;
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy/MM/dd hh:mm" )
    private Date registered;
    private Integer sku;
    private Integer noRestrictionNum = 0;

}
