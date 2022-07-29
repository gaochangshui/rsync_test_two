package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class JanPresetAttribute {
    @JsonIgnore
    private String companyCd;
    private String classCd;
    private CommonPartsDataVO commonPartsData;

}
