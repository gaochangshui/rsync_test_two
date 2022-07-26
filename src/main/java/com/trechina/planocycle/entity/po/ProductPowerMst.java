package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ProductPowerMst {
    private String conpanyCd;

    private Integer productPowerCd;

    private String productPowerName;

    private String authorCd;

    private String authorName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date registered;

    private String maintainerCd;

    private String maintainerName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date modified;

}
