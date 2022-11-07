package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class CompanyList {
    private String companyCd;
    private String companyName;
    private String isUse;
    private String updater;
    private Integer dateIsCore;
    private Integer storeIsCore;
    private Integer prodIsCore;
    private Integer kokyaku;
    private Integer intage;
    private Integer basketPrice;
    private Integer isIdPos;
}
