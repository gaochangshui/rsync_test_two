package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.JanAttrName;
import com.trechina.planocycle.entity.po.JanHeaderAttr;
import com.trechina.planocycle.entity.vo.JanParamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface MstJanMapper {

    /**
     * janHeaderの取得
     * @return
     */
    List<JanHeaderAttr> getJanHeader(String tableName, String tableNameKaisou, String tableNamePlanoCycle, String janColumn);

    /**
     * 名前でjanHeaderを取得
     * @return
     */
    List<JanHeaderAttr> getJanHeaderByName(String tableName, String tableNameKaisou, String headerName);

    /**
     * janデータの計数
     * @param janParamVO 検索条件
     * @return
     */
    long getJanCount(@Param("janParamVO") JanParamVO janParamVO,
                     @Param("tableName") String tableName,
                     @Param("janInfoTablePlanoCycle") String janInfoTablePlanoCycle,
                     @Param("column") String column);
    /**
     * janデータの取得
     * @param janParamVO 検索条件
     * @return
     */
    List<LinkedHashMap<String, Object>> getJanList(@Param("janParamVO") JanParamVO janParamVO,
                                                   @Param("tableName") String tableName,
                                                   @Param("janInfoTablePlanoCycle") String janInfoTablePlanoCycle,
                                                   @Param("column") String column);

    LinkedHashMap<String,Object> getJanInfoList(@Param("tableName")String janInfoTableName, @Param("jan") String jan);

    List<LinkedHashMap<String,Object>> getJanAttrList(@Param("tableName")String tableNameAttr);

    List<LinkedHashMap<String,Object>> getJanKaisouList(@Param("tableName")String tableNameAttr);

    /**
     * 表示項目設定の取得
     * @return
     */
    List<JanAttrName> getAttrName(String authorCd,
                                  String tableName,
                                  String tableNamePreset,
                                  String tableNameKaisou,
                                  String tableNamePlanoCycle);

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

    /**
     * jan詳細保存
     *
     * @param setInfoMap
     * @param jan
     * @param janInfoTableName
     */
    void setJanInfo(@Param("list") LinkedHashMap<String, Object> setInfoMap, @Param("jan") String jan, @Param("tableName") String janInfoTableName);

    Map<String, Object> getKaiSouName(@Param("map") Map<String, Object> map, @Param("tableName") String janInfoTableName,@Param("list")List<String> list);

    void clearCol(String colName, String janInfoTableName);

    String getNameExist(String name,String tableName);

    /**
     * batch更新JAN
     * @param tableName
     * @param janData
     * @param
     * @return
     */
    int insertJanList(String tableName, List<LinkedHashMap<String, Object>> janData,String date,String authorCd);

    String checkKaisou(String tableNameInfo, Map<String,Object>map);

    Integer getKaiSouLength(String tableNameInfo);

    List<JanHeaderAttr> getPlanoType(String tableName);

    /**
     * Jan Header同期
     * @return
     */
    int syncJanHeader(String tableName,String tableNameWK);

    /**
     * Janデータ同期
     * @return
     */
    int syncJanData(String tableName,String tableNameWK,String column);

    /**
     * 階層Masterを削除
     * @return
     */
    int deleteKaisou(String tableName);

    /**
     * 階層Masterを保存
     * @return
     */
    int insertKaisou(String tableName,String tableNameWK);


    List<String> getJanInfoCol();

    void setJanSpecial(@Param("list") LinkedHashMap<String, Object> janSpecialData, @Param("jan") String jan);

    int insertJanSpecialList(List<LinkedHashMap<String, Object>> janData);

    /**
     * 同期Janコラムを取得
     * @return
     */
    List<String> getJanAttrColWK(String tableName);

    /**
     * 重複Janを削除
     * @return
     */
    int deleteMultipleJan(String tableName);


    List<String> getAttrValueList(String colName,String company,String classCd);

    String getAttrNameForId(String colName, String company, String classCd);
}