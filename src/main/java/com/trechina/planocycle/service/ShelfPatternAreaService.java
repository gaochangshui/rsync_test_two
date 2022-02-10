package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.ShelfPatternArea;

import java.util.List;
import java.util.Map;

public interface ShelfPatternAreaService {
    /**
     * 保存shelfPattern关联的area
     * @param shelfPatternArea
     * @return
     */
    Map<String,Object> setShelfPatternArea(List<ShelfPatternArea> shelfPatternArea,String authorCd);

    /**
     * 删除shelfPattern关联的area
     * @param id
     * @return
     */
    Integer delShelfPatternArea(Integer id,String authorCd);
}
