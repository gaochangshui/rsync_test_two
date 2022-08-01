package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.vo.MstCommodityVO;

import java.util.List;

public interface MstCommodityService {

    /**
     * 商品マスタ
     * @return
     */
    List<MstCommodityVO> getCommodityList();

    /**
     * 同期設定を検索
     * @param companyCd
     * @return
     */
    List<CommoditySyncSet> getSyncSet(String companyCd);
}
