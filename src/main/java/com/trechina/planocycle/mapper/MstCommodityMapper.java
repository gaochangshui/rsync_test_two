package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.CommoditySyncSet;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MstCommodityMapper {

    /**
     * 同期設定を検索
     * @return
     */
    List<CommoditySyncSet> getSyncSet(String tableName, String tableNameCompany);

    /**
     * 同期設定を保存
     * @param tableName
     * @param commoditySyncSet
     * @return
     */
    int setSyncSet(String tableName, CommoditySyncSet commoditySyncSet);


    /**
     * マスタ同期
     * @return
     */
    int syncCommodityMaster(String tableName,String tableNameWK);

    /**
     * マスタデータを検索
     * @return
     */
    List<CommoditySyncSet> getCommodityList(String tableName);

    List<String> getClassList(String companyCd);
}