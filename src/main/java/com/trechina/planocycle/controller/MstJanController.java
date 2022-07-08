package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.mapper.JanInfoList;
import com.trechina.planocycle.service.MstJanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/planoCycle/MstJan")
public class MstJanController {

    @Autowired
    private MstJanService mstJanService;

    /**
     * janデータの取得
     * @param janParamVO 検索条件
     * @return
     */
    @PostMapping("/getJanList")
    public JanInfoVO getJanList(@RequestBody JanParamVO janParamVO){
        return mstJanService.getJanList(janParamVO);
    }

    /**
     * janデータの取得
     * @param janInfoList 検索条件
     * @return
     */
    @PostMapping("/getJanListInfo")
    public Map<String,Object> getJanListInfo(@RequestBody JanInfoList janInfoList){
        return mstJanService.getJanListInfo(janInfoList);
    }

}
