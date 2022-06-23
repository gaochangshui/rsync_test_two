package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.mapper.ZokuseiMapper;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BasicPatternMstServiceImpl implements BasicPatternMstService {
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ZokuseiMapper zokuseiMapper;

    @Override
    public Map<String, Object> autoDetect(BasicPatternAutoDetectVO basicPatternAutoDetectVO) {
        Integer shelfPatternCd = basicPatternAutoDetectVO.getShelfPatternCd();
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei("0000", "0000");
        String commonPartsData = basicPatternAutoDetectVO.getCommonPartsData();


        return null;
    }
}
