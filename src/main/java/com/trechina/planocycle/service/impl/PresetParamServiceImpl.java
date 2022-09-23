package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.po.PresetAttribute;
import com.trechina.planocycle.entity.po.PresetParam;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PresetParamMapper;
import com.trechina.planocycle.service.PresetParamService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setPresetParamForProduct(PresetAttribute presetAttribute) {
        String authorCd = session.getAttribute("aud").toString();

        String aud = session.getAttribute("aud").toString();
        presetParamMapper.deleteProductPresetParam(authorCd,presetAttribute);
        presetParamMapper.insertProductPresetParam(aud, presetAttribute);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> getPresetParamForProduct(PresetAttribute presetAttribute) {
        String authorCd = session.getAttribute("aud").toString();
        JSONObject jsonObject = JSON.parseObject(presetAttribute.getCommonPartsData());
        List<PresetAttribute> presetParam = presetParamMapper.getProductPresetParam(authorCd);

        for (PresetAttribute attribute : presetParam) {
            JSONObject jsonObject1 = JSON.parseObject(attribute.getCommonPartsData());

            if (jsonObject.get("storeIsCore").equals(jsonObject1.get("storeIsCore"))
                    && jsonObject.get("storeMstClass").equals(jsonObject1.get("storeMstClass"))){
                return ResultMaps.result(ResultEnum.SUCCESS,attribute);
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS,null);
    }
}
