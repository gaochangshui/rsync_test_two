package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.vo.CommoditySyncSetVO;

import java.util.List;
import java.util.Map;

public interface MstCommodityService {

    /**
     * 同期設定を検索
     * @param companyCd
     * @return
     */
    List<CommoditySyncSet> getSyncSet(String companyCd);

    /**
     * 同期設定を保存
     * @param commoditySyncSetVO
     * @return
     */
    Map<String,Object> setSyncSet(CommoditySyncSetVO commoditySyncSetVO);

    /**
     * 商品マスタ同期
     * @return
     */
    int syncCommodityMaster(String companyCd);

    /**
     * 商品マスタを検索
     * @return
     */
    List<CommoditySyncSet> getCommodityList(String companyCd);
}
