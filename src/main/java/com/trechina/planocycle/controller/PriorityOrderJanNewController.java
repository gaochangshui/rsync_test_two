package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderJanNew")
public class PriorityOrderJanNewController {
    @Autowired
    private PriorityOrderJanNewService priorityOrderJanNewService;

    /**
     * 新規商品リスト取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanNew")
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd,Integer productPowerNo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderJanNewService.getPriorityOrderJanNew(companyCd,priorityOrderCd,productPowerNo);
    }

    /**
     * ワークシート保存新規商品リスト
     * @param
     * @return
     */
    @PostMapping("/setPriorityOrderJanNew")
    public Map<String,Object> setPriorityOrderJanNew(@RequestBody List<PriorityOrderJanNew> priorityOrderJanNew){
        return priorityOrderJanNewService.setPriorityOrderJanNew(priorityOrderJanNew);
    }

    /**
     * 新しいjanの名前分類を取得
     * @param
     * @return
     *
     */
    @GetMapping("getPriorityOrderJanNewInfo")
    public Map<String, Object> getPriorityOrderJanNewInfo(String[] janCd,String companyCd, Integer priorityOrderCd,Integer model) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderJanNewService.getPriorityOrderJanNewInfo(janCd,companyCd,priorityOrderCd,model);
    }

    /**
     * 分類によって商品の力点数表を除いて同類の商品を抽出する
     * @param
     * @return
     */
    @PostMapping("getSimilarity")
    public Map<String, Object> getSimilarity(@RequestBody Map<String,Object> map) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderJanNewService.getSimilarity(map);
    }

    /**
     * 商品詳細は新規では存在しません
     * @param
     * @return
     */
    @PostMapping("setJanNewInfo")
    public Map<String,Object>setJanNewInfo(@RequestBody List<JanMstPlanocycleVo> janMstPlanocycleVos)  {
        return priorityOrderJanNewService.setJanNewInfo(janMstPlanocycleVos);
    }

    /**
     * 存在しない商品の詳細を調べる
     * @param
     * @return
     */
    @GetMapping("getJanNewInfo")
    public Map<String,Object>getJanNewInfo(String companyCd)  {
        return priorityOrderJanNewService.getJanNewInfo(companyCd);
    }
}
