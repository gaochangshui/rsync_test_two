package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfNameDto;

import java.util.Map;

public interface ShelfNameService {

    /**
     * 棚名情報の取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfNameInfo(String companyCd);

    /**
     * ハウス名情報の保存
     * @param shelfNameDto
     * @return
     */
    Map<String,Object> setShelfName(ShelfNameDto shelfNameDto);

    /**
     * 小屋名情報の変更
     * @param shelfNameDto
     * @return
     */
    Map<String,Object> updateShelfName(ShelfNameDto shelfNameDto);
    /**
     * 棚名を取得Name
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfName(String companyCd);

    /**
     * 小屋名の削除
     * @param jsonObject
     * @return
     */
    Map<String, Object> delShelfNameInfo(JSONObject jsonObject);
    /**
     * 変更用のシングル・テーブル・ハウス名情報の取得
     * @param id
     * @return
     */
    Map<String, Object> getShelfNameInfoById(Integer id);

    /**
     * 単一テーブルハウス名情報を取得し、取得ハウス名とハウスpatternのプロパティ構造を変更します。
     * @param companyCd
     * @return
     */
    Map<String, Object> getShelfPatternMaster(String companyCd);
}
