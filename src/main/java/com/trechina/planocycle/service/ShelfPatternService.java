package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.vo.ShelfPatternBranchVO;

import java.util.List;
import java.util.Map;

public interface ShelfPatternService {

    /**
     * 棚pattern情報の取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfPatternInfo(String companyCd);

    /**
     * 保存棚pattern情報
     * @param shelfPatternDto
     * @return
     */
    Map<String,Object> setShelfPatternInfo(ShelfPatternDto shelfPatternDto);
    /**
     * 修正棚pattern情報
     * @param shelfPatternDto
     * @return
     */
    Map<String,Object> updateShelfPatternInfo(ShelfPatternDto shelfPatternDto);
    /**
     * 通を過ぎて棚名称棚pattern
     * @param companyCd
     * @param shelfNameCd
     * @return
     */
    List<Integer> getShelfPattern(String companyCd,Integer shelfNameCd);


    /**
     * 棚pattern関連店cdを取得
     * @param shelfPatternCd
     * @return
     */
    Map<String,Object> getShelfPatternBranch(Integer shelfPatternCd);

    /**
     * 保存棚patternのお店cd
     * @param shelfPatternBranchVO
     * @return
     */
    Map<String,Object> setShelfPatternBranch(ShelfPatternBranchVO shelfPatternBranchVO);

    /**
     * すべての棚patternのnameを取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfPatternName(String companyCd);

    /**
     * 店舗に関連付けられた棚patternのnameを取得（優先順位表用）
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfPatternNameBranch(String companyCd);
    /**
     * 棚の削除
     * @param jsonObject
     * @return
     */
    Map<String, Object> delShelfPatternInfo(JSONObject jsonObject);

    /**
     * つかむ取shelfPattern 1@棚パータン名称１,2@棚パータン名称2 格式的字符串
     * @param shelfPatternNo
     * @return
     */
    String getShePatternNoNm(String shelfPatternNo);

    /**
     * ptsKeyによるpatternidの取得
     * @param ptsKey
     * @return
     */
    List<Integer> getpatternIdOfPtsKey(String ptsKey);

    /**
     * areaに基づいて棚名と棚patternを取得
     * @param companyCd
     * @param areaCds
     * @return
     */
     Map<String,Object> getShelfPatternForArea(String companyCd,int[] areaCds);
}
