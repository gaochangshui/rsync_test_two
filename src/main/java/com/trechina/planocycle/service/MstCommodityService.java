package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.MstCommodityVO;

import java.util.List;

public interface MstCommodityService {

    /**
     * 商品マスタ
     * @return
     */
    List<MstCommodityVO> getCommodityList();

}
