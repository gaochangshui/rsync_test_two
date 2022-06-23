package com.trechina.planocycle.entity.dto;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trechina.planocycle.entity.po.BasicPatternRestrictRelation;

import java.util.List;

public class BasicPatternRestrictRelationDto extends BasicPatternRestrictRelation {
    private String name;

    @JsonIgnore
    private JSONObject json;

    private String attrList;

    public String getAttrList() {
        return attrList;
    }

    public void setAttrList(String attrList) {
        this.attrList = attrList;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
