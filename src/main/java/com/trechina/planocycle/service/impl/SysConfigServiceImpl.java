package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.enums.ResultEnum;
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

    @Override
    public Map<String, Object> getShowJanSku() {
        Map<String, Object> resultMap = new HashMap<>();

        String showJanSkuFlag = sysConfigMapper.selectSycConfig("show_jan_sku_flag");
        resultMap.put("showJanSkuFlag", showJanSkuFlag);
        return ResultMaps.result(ResultEnum.SUCCESS, showJanSkuFlag);
    }
}
