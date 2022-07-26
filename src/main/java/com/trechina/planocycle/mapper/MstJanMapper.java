package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.JanAttrName;
import com.trechina.planocycle.entity.po.JanHeaderAttr;
import com.trechina.planocycle.entity.vo.JanParamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

@Mapper
public interface MstJanMapper {

    /**
     * janHeaderの取得
     * @return
     */
    List<JanHeaderAttr> getJanHeader(String tableName);

    /**
     * janデータの計数
     * @param janParamVO 検索条件
     * @return
     */
    long getJanCount(@Param("janParamVO") JanParamVO janParamVO,
                     @Param("tableName") String tableName,
                     @Param("column") String column);
    /**
     * janデータの取得
     * @param janParamVO 検索条件
     * @return
     */
    List<LinkedHashMap<String, Object>> getJanList(@Param("janParamVO") JanParamVO janParamVO,
                                                   @Param("tableName") String tableName,
                                                   @Param("column") String column,
                                                   @Param("pageSize") Integer pageSize,
                                                   @Param("pageCount") Integer pageCount);

    LinkedHashMap<String,Object> getJanInfoList(@Param("tableName")String janInfoTableName, @Param("jan") String jan);

    List<LinkedHashMap<String,Object>> getJanAttrList(@Param("tableName")String tableNameAttr);

    List<LinkedHashMap<String,Object>> getJanKaisouList(@Param("tableName")String tableNameAttr);

    /**
     * 表示項目設定の取得
     * @return
     */
    List<JanAttrName> getAttrName(String authorCd, String tableName, String tableNamePreset);

    /**
     * ユーザー表示項目設定を削除
     * @return
     */
    int insertPresetAttribute(String authorCd, String[] presetAttr, String tableName);

    /**
     * ユーザー表示項目設定を保存
     * @return
     */
    int deleteByAuthorCd(String authorCd, String tableName);
}