package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderDataForCgiDto;

import java.util.List;
import java.util.Map;

public interface PriorityOrderDataService {
    /**
     * 优先顺位表初期设定数据
     * @param priorityOrderDataForCgiDto
     * @return
     */
    Map<String,Object> getPriorityOrderData(PriorityOrderDataForCgiDto priorityOrderDataForCgiDto);

    /**
     * 优先顺位表反应按钮抽出数据
     * @param colNameList
     * @return
     */
    Map<String,Object> getPriorityOrderDataUpd(List<String> colNameList,Integer priorityOrderCd);

    /**
     * 获取属性名
     * @param productPowerCd
     * @return
     */
    List<Map<String,Object>> getAttrName(Integer productPowerCd);

}
