package com.trechina.planocycle.entity.dto;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;
import lombok.Data;

@Data
public class BasicPatternRestrictRelationDto extends BasicPatternRestrictRelation {
    private String name;

    @JsonIgnore
    private JSONObject json;

    private String attrList;

}
