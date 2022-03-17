package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityOrderJanNewDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
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
     * @param janNew
     * @return
     *
     */
    @GetMapping("getPriorityOrderJanNewInfo")
    public Map<String, Object> getPriorityOrderJanNewInfo(String[] janNew) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderJanNewService.getPriorityOrderJanNewInfo(janNew);
    }

    /**
     * 根据分类去商品力点数表抽同类商品
     * @param priorityOrderJanNewVO
     * @return
     */
    @PostMapping("getSimilarity")
    public Map<String, Object> getSimilarity(@RequestBody PriorityOrderJanNewDto priorityOrderJanNewDto)  {
        return priorityOrderJanNewService.getSimilarity(priorityOrderJanNewDto);
    }

}
