package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.vo.ShelfPatternBranchVO;

import java.util.List;
import java.util.Map;

public interface ShelfPatternService {

    /**
     * 获取棚pattern信息
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfPatternInfo(String companyCd);

    /**
     * 保存棚pattren信息
     * @param shelfPatternDto
     * @return
     */
    Map<String,Object> setShelfPatternInfo(ShelfPatternDto shelfPatternDto);

    /**
     * 通过棚名称获取area和棚pattern
     * @param companyCd
     * @param shelfNameCd
     * @return
     */
    Map<String,Object> getShelfPatternArea(String companyCd,Integer shelfNameCd);

    /**
     * 获取棚pattren关联的店cd
     * @param shelfPatternCd
     * @return
     */
    Map<String,Object> getShelfPatternBranch(Integer shelfPatternCd);

    /**
     * 保存棚pattern的店cd
     * @param shelfPatternBranchVO
     * @return
     */
    Map<String,Object> setShelfPatternBranch(ShelfPatternBranchVO shelfPatternBranchVO);

    /**
     * 获取所有棚pattern的name
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfPatternName(String companyCd);

    /**
     * 获取关联了店铺的棚pattern的name（优先顺位表用）
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfPatternNameBranch(String companyCd);
    /**
     * 删除棚pattern
     * @param jsonObject
     * @return
     */
    Map<String, Object> delShelfPatternInfo(JSONObject jsonObject);

    /**
     * 获取shelfPattern 1@棚パータン名称１,2@棚パータン名称2 格式的字符串
     * @param shelfPatternNo
     * @return
     */
    String getShePatternNoNm(String shelfPatternNo);

    /**
     * 根据ptsKey获取patternid
     * @param ptsKey
     * @return
     */
    List<Integer> getpatternIdOfPtsKey(String ptsKey);


}
