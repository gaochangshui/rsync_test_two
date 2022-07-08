package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.MstCommodityVO;
import com.trechina.planocycle.service.MstCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/planoCycle/MstCommodity")
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
}
