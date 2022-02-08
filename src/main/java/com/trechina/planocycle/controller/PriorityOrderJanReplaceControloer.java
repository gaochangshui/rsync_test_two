package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priorityOrderJanReplace")
public class PriorityOrderJanReplaceControloer {
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;

    /**
     * 获取jan变list的信息
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanInfo")
    public Map<String,Object> getPriorityOrderJanInfo(String companyCd,Integer priorityOrderCd){
        return priorityOrderJanReplaceService.getPriorityOrderJanInfo(companyCd,priorityOrderCd);
    }

    /**
     * 保存jan变list的信息（全删全插）
     * @param priorityOrderJanReplace
     * @return
     */
    @PostMapping("/setPriorityOrderJanInfo")
    public Map<String,Object> setPriorityOrderJanInfo(@RequestBody List<PriorityOrderJanReplace> priorityOrderJanReplace){
        return priorityOrderJanReplaceService.setPriorityOrderJanInfo(priorityOrderJanReplace);
    }


}
