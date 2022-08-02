package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.vo.MstCommodityVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MstCommodityMapper {

    /**
     * 商品マスタ
     * @return
     */
    List<MstCommodityVO> getCommodityList(String tableName);

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

}