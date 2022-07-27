package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.entity.po.JanInfoList;
import com.trechina.planocycle.entity.vo.JanPresetAttribute;

import java.util.Map;

public interface MstJanService {

    /**
     * janデータの取得
     * @param janParamVO 検索条件
     * @return
     */
    JanInfoVO getJanList(JanParamVO janParamVO);

    Map<String, Object> getJanListInfo(JanInfoList janInfoList);

    /**
     * 表示項目設定の取得
     *
     * @param enterpriseAxisDto
     * @return
     */
    Map<String, Object> getAttrName(EnterpriseAxisDto enterpriseAxisDto);

    /**
     * 表示項目設定のプリセット
     *
     * @param janPresetAttribute
     * @return
     */
    Map<String, Object> setPresetAttribute(JanPresetAttribute janPresetAttribute);

    Map<String, Object> setJanListInfo(Map<String, Object> map);
}
