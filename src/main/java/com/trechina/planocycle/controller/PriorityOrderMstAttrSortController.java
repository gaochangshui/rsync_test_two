package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/PriorityOrderMstAttrSort")
public class PriorityOrderMstAttrSortController {
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;

    /**
     *つかむ取属性1と属性2
     */
    @PostMapping("/getAttribute")
    public Map<String,Object> getAttribute(@RequestBody PriorityOrderAttrDto priorityOrderAttrDto){
        return priorityOrderMstAttrSortService.getAttribute(priorityOrderAttrDto);
    }



    /**
     * つかむ取属性的分類及商品分類列表
     */
    @PostMapping("/getAttributeList")
    public Map<String,Object> getAttributeList(@RequestBody PriorityOrderAttrDto priorityOrderAttrDto){
        return priorityOrderMstAttrSortService.getAttributeList(priorityOrderAttrDto);
    }


    /**
     * groupデータの取得
     */
    @PostMapping("/getAttrGroup")
    public Map<String,Object> getAttrGroup(@RequestBody PriorityOrderAttrDto priorityOrderAttrDto){
        return priorityOrderMstAttrSortService.getAttrGroup(priorityOrderAttrDto);
    }
}
