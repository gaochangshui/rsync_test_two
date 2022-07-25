package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.entity.po.JanInfoList;

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
}
