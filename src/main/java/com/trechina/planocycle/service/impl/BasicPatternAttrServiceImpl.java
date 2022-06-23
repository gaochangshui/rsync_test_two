package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.BasicPatternAttrListDto;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.BasicPatternAttrMapper;
import com.trechina.planocycle.service.BasicPatternAttrService;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BasicPatternAttrServiceImpl implements BasicPatternAttrService {
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private BasicPatternAttrMapper basicPatternAttrMapper;
    @Override
    public Map<String, Object> getAttribute(PriorityOrderAttrDto priorityOrderAttrDto) {
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderAttrDto.getCommonPartsData(),priorityOrderAttrDto.getCompanyCd());
        List<BasicPatternAttrListDto> attributeList = basicPatternAttrMapper.getAttribute(commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        return ResultMaps.result(ResultEnum.SUCCESS, attributeList);
    }
}
