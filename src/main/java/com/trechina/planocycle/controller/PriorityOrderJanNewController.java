package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityOrderJanNewDto;
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
     * 获取新规商品list
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanNew")
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd,Integer productPowerNo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderJanNewService.getPriorityOrderJanNew(companyCd,priorityOrderCd,productPowerNo);
    }

    /**
     * work表保存新规商品list
     * @param
     * @return
     */
    //TODO:保存新规商品list
    @PostMapping("/setPriorityOrderJanNew")
    public Map<String,Object> setPriorityOrderJanNew(@RequestBody List<PriorityOrderJanNew> priorityOrderJanNew){
        return priorityOrderJanNewService.setPriorityOrderJanNew(priorityOrderJanNew);
    }

    /**
     * 获取新规jan的名字分类
     * @param
     * @return
     *
     */
    @GetMapping("getPriorityOrderJanNewInfo")
    public Map<String, Object> getPriorityOrderJanNewInfo(String[] janCd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderJanNewService.getPriorityOrderJanNewInfo(janCd);
    }

    /**
     * 根据分类去商品力点数表抽同类商品
     * @param
     * @return
     */
    @PostMapping("getSimilarity")
    public Map<String, Object> getSimilarity(@RequestBody PriorityOrderJanNewDto priorityOrderJanNewDto)  {
        return priorityOrderJanNewService.getSimilarity(priorityOrderJanNewDto);
    }

    /**
     * 新规不存在商品详细信息
     * @param
     * @return
     */
    @PostMapping("setJanNewInfo")
    public Map<String,Object>setJanNewInfo(@RequestBody List<JanMstPlanocycleVo> janMstPlanocycleVos)  {
        return priorityOrderJanNewService.setJanNewInfo(janMstPlanocycleVos);
    }

    /**
     * 查询不存在商品详细信息
     * @param
     * @return
     */
    @GetMapping("getJanNewInfo")
    public Map<String,Object>getJanNewInfo(String companyCd)  {
        return priorityOrderJanNewService.getJanNewInfo(companyCd);
    }
}
