package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;

import java.util.List;
import java.util.Map;

public interface PriorityOrderJanReplaceService {
    /**
     * つかむ取jan変的信息
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 保存jan変的信息
     * @param priorityOrderJanReplace
     * @return
     */
    Map<String,Object> setPriorityOrderJanInfo(List<PriorityOrderJanReplace> priorityOrderJanReplace);




}
