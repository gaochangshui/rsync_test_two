package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.SkuNameConfigMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.SysConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysConfigServiceImpl implements SysConfigService {
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private SkuNameConfigMapper skuNameConfigMapper;
    @Override
    public Map<String, Object> getShowJanSku(EnterpriseAxisDto enterpriseAxisDto) {
        Map<String, Object> resultMap = new HashMap<>();
        //
        String companyCd = enterpriseAxisDto.getCompanyCd();
        String commonPartsData = enterpriseAxisDto.getCommonPartsData();
        JSONObject jsonObject = JSON.parseObject(commonPartsData);
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
            List<String> janUnit = sysConfigMapper.selectByPrefix("jan_unit_");
            resultMap.put("showJanSkuFlag", 1);
            resultMap.put("janUnitList", janUnit);
        }else {
            resultMap.put("showJanSkuFlag", 0);
        }
        return ResultMaps.result(ResultEnum.SUCCESS, resultMap);
    }
}
