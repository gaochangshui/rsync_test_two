package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.service.MstJanParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("planoCycleApi/MstJanParam")
public class MstJanParamController {

    @Autowired
    private MstJanParamService mstJanParamService;
    /**
     * つかむ取商品分類列表
     */
    @PostMapping("/getAttributeTree")
    public Map<String,Object> getAttributeTree(@RequestBody JanParamVO janParamVO){
        return mstJanParamService.getAttributeTree(janParamVO);
    }

    @PostMapping("/getMaintenanceItem")
    public Map<String,Object> getMaintenanceItem(){
        return mstJanParamService.getMaintenanceItem();
    }
}
