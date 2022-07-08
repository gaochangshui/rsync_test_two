package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.service.MstJanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
}
