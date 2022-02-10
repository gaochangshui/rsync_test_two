package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfNameDto;

import java.util.Map;

public interface ShelfNameService {

    /**
     * 棚名称信息获取接口
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfNameInfo(String companyCd);

    /**
     * 棚名称做成接口
     * @param shelfNameDto
     * @return
     */
    Map<String,Object> setShelfName(ShelfNameDto shelfNameDto);

    /**
     * 棚名称修改
     * @param shelfNameDto
     * @return
     */
    Map<String,Object> updateShelfName(ShelfNameDto shelfNameDto);
    /**
     * 只获取棚名称Name
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfName(String companyCd);

    /**
     * 删除棚名称
     * @param jsonObject
     * @return
     */
    Map<String, Object> delShelfNameInfo(JSONObject jsonObject);
    /**
     * 获取单表棚名称信息，用于修改
     * @param id
     * @return
     */
    Map<String, Object> getShelfNameInfoById(Integer id);

    /**
     * 获取棚名称和棚pattern的属性结构
     * @param companyCd
     * @return
     */
    Map<String, Object> getShelfPatternMaster(String companyCd);
}
