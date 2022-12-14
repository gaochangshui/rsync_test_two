package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.ShelfNameArea;

import java.util.List;
import java.util.Map;

public interface ShelfNameAreaService {
    /**
     * 保存shelfName関連付け的area
     * @param shelfNameArea
     * @return
     */
    Map<String,Object> setShelfNameArea(List<ShelfNameArea> shelfNameArea,String authorCd);

    /**
     * 削除shelfName関連付け的area
     * @param id
     * @return
     */
    Integer delShelfNameArea(Integer id,String authorCd);

    /**
     * 検索shelfName関連付け的area
     * @param id
     *  @return
     */
    List<Integer> getShelfNameArea(Integer id,String companyCd);

    /**
     * 根据areaCd
     * @param areaCd
     * @return
     */
    Integer delAreaCd(List<Integer> areaCd,Integer id,String authorCd);

    /**
     * 恢復削除数据
     */
    Integer setDelFlg(Integer areaCd,Integer id ,String authorCd);
}
