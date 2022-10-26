package com.trechina.planocycle.entity.dto;

import com.trechina.planocycle.entity.vo.CommonPartsDataVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CompanyConfigureDto {
    private  Integer basketPriceFlag;
    private  Integer intageFlag;
    private CommonPartsDataVO commonPartsData;
    private String janName2colNum;
    private Integer showJanSkuFlag;
    private Integer kokyakuFlag;
    private List janUnit;
    private String janItem2colNum;
    private Map<String,Object> companyColMap;
}
