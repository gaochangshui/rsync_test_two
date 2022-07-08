package com.trechina.planocycle.mapper;

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

}