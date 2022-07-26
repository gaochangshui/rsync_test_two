package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;

import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderJanReplaceService {
    /**
     * jan変更リストの情報を取得する
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanInfo(String companyCd,Integer priorityOrderCd);

    /**
     * jan変listの情報を保存する（全削除全挿入）
     * @param priorityOrderJanReplace
     * @return
     */
    Map<String,Object> setPriorityOrderJanInfo(List<PriorityOrderJanReplace> priorityOrderJanReplace);

    String getJanInfo(String proInfoTable);

    boolean isExistJanInfo(String proInfoTable, String jan);

    /**
     * janを削除してリストを変更する
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Integer delJanReplaceInfo(String companyCd,Integer priorityOrderCd);
}
