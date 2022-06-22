package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BasicPatternMstServiceImpl implements BasicPatternMstService {
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public Map<String, Object> autoDetect(BasicPatternAutoDetectVO basicPatternAutoDetectVO) {
        Integer shelfPatternCd = basicPatternAutoDetectVO.getShelfPatternCd();
        String zokuseiCount = sysConfigMapper.selectSycConfig(MagicString.ZOKUSEI_COUNT);

        return null;
    }
}
