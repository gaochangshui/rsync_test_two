package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.entity.po.JanInfoList;
import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.entity.vo.JanPresetAttribute;
import com.trechina.planocycle.service.JanKaisouService;
import com.trechina.planocycle.service.MstJanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/planoCycleApi/MstJan")
public class MstJanController {

    @Autowired
    private MstJanService mstJanService;

    @Autowired
    private JanKaisouService janKaisouService;

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
    /**
     * janデータの取得
     * @param map 検索条件
     * @return
     */
    @PostMapping("/setJanListInfo")
    public Map<String,Object> setJanListInfo(@RequestBody Map<String,Object> map){
        return mstJanService.setJanListInfo(map);
    }

    @PostMapping("/saveKaisouInfo")
    public Map<String,Object> saveKaisouInfo(@RequestBody Map<String,Object> map){
        return janKaisouService.saveProductItem(map);
    }

    /**
     * 表示項目設定の取得
     *
     * @param enterpriseAxisDto
     * @return
     */
    @PostMapping("/getPresetAttribute")
    public Map<String, Object> getAttrName(@RequestBody EnterpriseAxisDto enterpriseAxisDto) {
        return mstJanService.getAttrName(enterpriseAxisDto);
    }

    /**
     * 表示項目設定のプリセット
     *
     * @param janPresetAttribute
     * @return
     */
    @PostMapping("/setPresetAttribute")
    public Map<String, Object> setPresetParam(@RequestBody JanPresetAttribute janPresetAttribute){
        return mstJanService.setPresetAttribute(janPresetAttribute);
    }

}
