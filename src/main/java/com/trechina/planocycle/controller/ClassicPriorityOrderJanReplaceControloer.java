package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.service.ClassicPriorityOrderJanReplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priority/priorityOrderJanReplace")
public class ClassicPriorityOrderJanReplaceControloer {
    @Autowired
    private ClassicPriorityOrderJanReplaceService priorityOrderJanReplaceService;

    /**
     * jan変更リストの情報を取得する
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderJanInfo")
    public Map<String,Object> getPriorityOrderJanInfo(String companyCd,Integer priorityOrderCd){
        return priorityOrderJanReplaceService.getPriorityOrderJanInfo(companyCd,priorityOrderCd);
    }

    /**
     * jan変listの情報を保存する（全削除全挿入）
     * @param priorityOrderJanReplace
     * @return
     */
    @PostMapping("/setPriorityOrderJanInfo")
    public Map<String,Object> setPriorityOrderJanInfo(@RequestBody List<PriorityOrderJanReplace> priorityOrderJanReplace){
        return priorityOrderJanReplaceService.setPriorityOrderJanInfo(priorityOrderJanReplace);
    }


}
