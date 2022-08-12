package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.BasicPatternJanPlacementMapper;
import com.trechina.planocycle.service.BasicPatternJanPlacementService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BasicPatternJanPlacementServiceImpl implements BasicPatternJanPlacementService {
    @Autowired
    private BasicPatternJanPlacementMapper basicPatternJanPlacementMapper;
    @Override
    public Map<String, Object> getJanPlacement() {
        List<Map<String,Object>> janPlacement = basicPatternJanPlacementMapper.getJanPlacement();
        return ResultMaps.result(ResultEnum.SUCCESS,janPlacement);
    }
}
