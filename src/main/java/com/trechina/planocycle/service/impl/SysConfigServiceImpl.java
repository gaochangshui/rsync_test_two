package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.SkuNameConfigMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.SysConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SysConfigServiceImpl implements SysConfigService {
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private SkuNameConfigMapper skuNameConfigMapper;
    @Override
    public Map<String, Object> getShowJanSku(Map<String ,Object>map) {
        Map<String, Object> resultMap = new HashMap<>();
        //
        String companyCd = map.get("companyCd").toString();
        String commonPartsData = map.get("commonPartsData").toString();
        JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        String isCompanyCd = null;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        Integer colNum = skuNameConfigMapper.getJanName2colNum(isCompanyCd,prodMstClass);
        if (colNum != null){
            resultMap.put("showJanSkuFlag", 1);
        }else {
            resultMap.put("showJanSkuFlag", 0);
        }
        return ResultMaps.result(ResultEnum.SUCCESS, resultMap);
    }
}
