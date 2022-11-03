package com.trechina.planocycle.entity.vo;

import lombok.Data;

@Data
public class ProductItemVO {
    private String name;
    private String value;
    private String type;
    private CommonPartsDataVO commonPartsData;
    private String companyCd;
    private String itemType;

}
