package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.SkuNameConfigMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.SysConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            List<Map<String, String>> janUnit = sysConfigMapper.selectByPrefix("jan_unit_");
            List<Map<String, Object>> janUnitList = new ArrayList<>();
            Map<String, Object> itemJanUnit = null;
            for (Map<String, String> s : janUnit) {
                itemJanUnit = new HashMap<>();
                String itemName = MapUtils.getString(s, "item_name");
                String[] sArray = itemName.split("_");
                itemJanUnit.put("label", MapUtils.getString(s, "item_value"));
                itemJanUnit.put("value", Integer.parseInt(sArray[sArray.length-1]));
                janUnitList.add(itemJanUnit);
            }
            resultMap.put("showJanSkuFlag", 1);
            resultMap.put("janUnit", janUnitList);
        }else {
            resultMap.put("showJanSkuFlag", 0);
        }
        Map<String, Object> kokyakuShow = skuNameConfigMapper.getKokyakuShow(isCompanyCd, prodMstClass);
        if (kokyakuShow.isEmpty()){
            resultMap.put("kokyakuFlag",0);
            resultMap.put("intageFlag",0);
        }else {
            resultMap.put("kokyakuFlag",Integer.parseInt(kokyakuShow.get("kokyaku").toString())==1?1:0);
            resultMap.put("intageFlag",Integer.parseInt(kokyakuShow.get("intage").toString())==1?1:0);
        }

        return ResultMaps.result(ResultEnum.SUCCESS, resultMap);
    }
}
