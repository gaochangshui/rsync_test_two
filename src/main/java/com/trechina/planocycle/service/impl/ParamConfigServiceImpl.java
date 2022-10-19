package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.CompanyConfigMapper;
import com.trechina.planocycle.mapper.ParamConfigMapper;
import com.trechina.planocycle.service.ParamConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ParamConfigServiceImpl implements ParamConfigService {
    @Autowired
    private ParamConfigMapper paramConfigMapper;
    @Autowired
    private CompanyConfigMapper companyConfigMapper;
    @Override
    public Map<String, Object> getParamConfigList(Integer showItemCheck) {
        List<ParamConfigDto> paramConfig = paramConfigMapper.getParamConfig(showItemCheck);
        return ResultMaps.result(ResultEnum.SUCCESS,paramConfig);
    }

    @Override
    public Map<String, Object> getCompanyConfig(Map<String, Object> map) {

        if (!MapUtils.getString(map,"F1").equals("")){
            companyConfigMapper.getMstkigyocore(MapUtils.getString(map,MagicString.F1));
        }else {

        }
        return null;
    }
}
