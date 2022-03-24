package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;

import java.util.List;
import java.util.Map;

public interface PriorityAllMstService {

    /**
     * 新規作成＆編集の処理
     *
     * @param jsonObject@return
     */
    Map<String, Object> addPriorityAllData(JSONObject jsonObject);

    /**
     * 基本パターン一覧のList
     * @param companyCd
     * @return
     */
    Map<String, Object> getPriorityOrderList(String companyCd);

    /**
     * 選択された基本パターンにより基本パターンの連携棚パターン、そして同じ棚名称したの棚パターンListを取得する
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String ,Object> getAllPatternData(String companyCd, Integer priorityAllCd, Integer priorityOrderCd);

    /**
     * 自動計算する前にデータを一時テーブルに保存
     *
     * @param priorityAllSaveDto@return
     */
    Integer saveWKAllPatternData(PriorityAllSaveDto priorityAllSaveDto);

    /**
     * 計算
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String, Object> autoCalculation(PriorityAllSaveDto priorityAllSaveDto);

    boolean setJan(String companyCd, String authorCd, Integer priorityAllCd, Integer priorityOrderCd, Integer shelfPatternCd, Integer minFace);

    /**
     * 自动计算
     * @return
     */
    Map<String, Object> savePriorityAll(String companyCd,Integer priorityOrderCd);

    /**
     * pts詳細
     * @param companyCd
     * @param priorityAllCd
     * @param patternCd
     * @return
     */
    Map<String, Object> getPriorityPtsInfo(String companyCd, Integer priorityAllCd, Integer patternCd);

}
