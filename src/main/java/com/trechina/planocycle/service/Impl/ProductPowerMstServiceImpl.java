package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ProductPowerMstMapper;
import com.trechina.planocycle.service.ProductPowerMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductPowerMstServiceImpl implements ProductPowerMstService {
    @Autowired
    ProductPowerMstMapper productPowerMstMapper;
    @Override
    public Map<String, Object> getTableName(String companyCd) {
        List<TableNameDto> commodityData = productPowerMstMapper.getTableNameByCompanyCd(companyCd);
        List<TableNameDto> basicPtsData = productPowerMstMapper.getTableNameByCompanyCd(companyCd);
        List<TableNameDto> wholePtsData = productPowerMstMapper.getTableNameByCompanyCd(companyCd);
        Map<String,Object> tableNameMap = new HashMap<>();
        tableNameMap.put("commodityData",commodityData);
        tableNameMap.put("basicPtsData",basicPtsData);
        tableNameMap.put("wholePtsData",wholePtsData);
        return ResultMaps.result(ResultEnum.SUCCESS,tableNameMap);
    }
}
