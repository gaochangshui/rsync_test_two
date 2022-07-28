package com.trechina.planocycle.entity.vo;

import lombok.Data;

@Data
public class JanParamVO {
    private CommonPartsDataVO commonPartsData;
    private String janContain;
    private String janKato;
    private String fuzzyQuery;
    private String companyCd;
    private String classCd;
    private String prodCd;

}
