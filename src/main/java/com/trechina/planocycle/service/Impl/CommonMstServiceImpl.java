package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.Areas;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.AreasMapper;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class CommonMstServiceImpl implements CommonMstService {
    @Autowired
    private AreasMapper areasMapper;
    @Override
    public Map<String, Object> getAreaInfo(String companyCd) {
        List<Areas> areasList = areasMapper.select(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    }

    @Override
    public Map<String, Object> getAreaForShelfName(Integer ShelfNameCd) {
        List<Areas> areasList = areasMapper.selectForShelfName(ShelfNameCd);
        return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    }


}
