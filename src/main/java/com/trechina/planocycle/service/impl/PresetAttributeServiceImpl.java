package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.PresetAttribute;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PresetAttributeMapper;
import com.trechina.planocycle.service.PresetAttributeService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class PresetAttributeServiceImpl implements PresetAttributeService {
    @Autowired
    private PresetAttributeMapper presetAttributeMapper;
    @Autowired
    private HttpSession session;
    public Map<String, Object> getPresetAttribute(){
        String aud = session.getAttribute("aud").toString();
        PresetAttribute presetAttribute = presetAttributeMapper.getPresetAttribute(aud);
        return ResultMaps.result(ResultEnum.SUCCESS, presetAttribute);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setPresetAttribute(PresetAttribute presetAttribute) {
        String aud = session.getAttribute("aud").toString();
        presetAttributeMapper.deleteByAuthorCd(aud);
        presetAttributeMapper.insertPresetAttribute(aud, presetAttribute);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
}
