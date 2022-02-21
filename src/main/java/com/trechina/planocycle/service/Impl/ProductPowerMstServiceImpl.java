package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ProductPowerMstMapper;
import com.trechina.planocycle.service.ProductPowerMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductPowerMstServiceImpl implements ProductPowerMstService {
    @Autowired
    ProductPowerMstMapper productPowerMstMapper;
    @Override
    public Map<String, Object> getTableName(String companyCd) {
        List<TableNameDto> tableNameByCompanyCd = productPowerMstMapper.getTableNameByCompanyCd(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,tableNameByCompanyCd);
    }
}
