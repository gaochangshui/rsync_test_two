package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.vo.MstCommodityVO;
import com.trechina.planocycle.service.MstCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/planoCycleApi/MstCommodity")
public class MstCommodityController {

    @Autowired
    private MstCommodityService mstCommodityService;

    /**
     * 商品マスタ
     * @return
     */
    @GetMapping("/getCommodityList")
    public List<MstCommodityVO> getCommodityList(){
        return mstCommodityService.getCommodityList();
    }

    /**
     * 同期設定を検索
     * @param companyCd
     * @return
     */
    @GetMapping("/getSyncSet")
    public List<CommoditySyncSet> getSyncSet(String companyCd){
        return mstCommodityService.getSyncSet(companyCd);
    }
}
