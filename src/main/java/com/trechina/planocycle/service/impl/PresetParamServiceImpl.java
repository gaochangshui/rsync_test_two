package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PresetParamMapper;
import com.trechina.planocycle.service.PresetParamService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class PresetParamServiceImpl implements PresetParamService {
    @Autowired
    private PresetParamMapper presetParamMapper;
    @Autowired
    private HttpSession session;
    @Override
    public Map<String, Object> setPresetParam(String presetParam) {
        String authorCd = session.getAttribute(presetParam).toString();
        presetParamMapper.deleteByAuthorCd(authorCd);
        presetParamMapper.insertPresetParam(authorCd, presetParam.split(","));
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
}
