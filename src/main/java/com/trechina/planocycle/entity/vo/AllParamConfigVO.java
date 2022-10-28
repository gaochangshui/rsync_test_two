package com.trechina.planocycle.entity.vo;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ParamConfigDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AllParamConfigVO {
    private List<ParamConfigDto> paramConfigList;
    private Integer showIntage;
    private List<Map<String, Object>> janUnit;
    private JSONObject level;
}
