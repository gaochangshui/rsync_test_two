package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PresetAttribute {
    @JsonIgnore
    private String authorCd;
    private String attrData;
    private String commonPartsData;

}
