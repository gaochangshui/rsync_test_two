package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.MstKiGyoCore;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.MstKiGyoCoreMapper;
import com.trechina.planocycle.service.MstKiGyoCoreService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MstKiGyoCoreServiceImpl implements MstKiGyoCoreService {
    @Autowired
    private MstKiGyoCoreMapper mstKiGyoCoreMapper;
    /**
     * 企業共通部品パラメータ取得
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getMstKiGyo(String companyCd) {
        MstKiGyoCore mstKiGyoCore = mstKiGyoCoreMapper.selectByPrimaryKey(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,mstKiGyoCore);
    }
}
