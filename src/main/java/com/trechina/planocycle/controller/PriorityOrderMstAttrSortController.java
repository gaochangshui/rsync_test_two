package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderMstAttrSort")
public class PriorityOrderMstAttrSortController {
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    /**
     * 获取既存数据的排序
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderMstAttrSort")
   public Map<String,Object> getPriorityAttrSort(String companyCd, Integer priorityOrderCd){
       return priorityOrderMstAttrSortService.getPriorityAttrSort(companyCd,priorityOrderCd);
   }
}
