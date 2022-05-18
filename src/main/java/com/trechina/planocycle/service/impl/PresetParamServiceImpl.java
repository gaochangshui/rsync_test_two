package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.PresetParam;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PresetParamMapper;
import com.trechina.planocycle.service.PresetParamService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PresetParamServiceImpl implements PresetParamService {
    @Autowired
    private PresetParamMapper presetParamMapper;
    @Autowired
    private HttpSession session;
    @Override
    public Map<String, Object> setPresetParam(PresetParam presetParam) {
        String authorCd = session.getAttribute("aud").toString();
        presetParamMapper.deleteByAuthorCd(authorCd);
        String presetParams = presetParam.getPresetParam();
        presetParamMapper.insertPresetParam(authorCd, presetParams.split(","));
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> getPresetParam() {
        String authorCd = session.getAttribute("aud").toString();
        List<String> presetParam = presetParamMapper.getPresetParam(authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS, presetParam.stream().collect(Collectors.joining(",")));
    }
}
