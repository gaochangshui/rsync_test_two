package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.ShelfPatternArea;

import java.util.List;
import java.util.Map;

public interface ShelfPatternAreaService {
    /**
     * 保存shelfPattern関連付け的area
     * @param shelfPatternArea
     * @return
     */
    Map<String,Object> setShelfPatternArea(List<ShelfPatternArea> shelfPatternArea,String authorCd);

    /**
     * 削除shelfPattern関連付け的area
     * @param id
     * @return
     */
    Integer delShelfPatternArea(Integer id,String authorCd);

    /**
     * 検索shelfPattern関連付け的area
     * @param id
     *  @return
     */
    List<Integer> getShelfPatternArea( Integer id,String companyCd);

    int deleteAreaCd (List<Integer> areaCd, Integer shelfPatternCd,String authorCd);

    //恢復削除数据
    Integer setDelFlg(Integer areaCd, Integer shelfPatternCd,String authorCd);


}
