package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.ShelfNameArea;

import java.util.List;
import java.util.Map;

public interface ShelfNameAreaService {
    /**
     * 保存shelfName关联的area
     * @param shelfNameArea
     * @return
     */
    Map<String,Object> setShelfNameArea(List<ShelfNameArea> shelfNameArea);

    /**
     * 删除shelfName关联的area
     * @param id
     * @return
     */
    Integer delShelfNameArea(Integer id);
}
