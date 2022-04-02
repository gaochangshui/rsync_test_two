package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityOrderSpaceDto;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderMstAttrSort")
public class PriorityOrderMstAttrSortController {
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;

    /**
     *つかむ取属性1和属性2
     */
    @GetMapping("/getAttribute")
    public Map<String,Object> getAttribute(){
        return priorityOrderMstAttrSortService.getAttribute();
    }
    /**
     * 陈列设定つかむ取属性1和属性2
     */
@GetMapping("/getAttributeSort")
    public Map<String,Object> getAttributeSort(){
        return priorityOrderMstAttrSortService.getAttributeSort();
    }
    /**
     *新规计算属性1属性2组合对应的面积
     */
    @GetMapping("/getAttributeArea")
    public Map<String,Object> getAttributeArea(Integer patternCd,Integer attr1,Integer attr2){
        return priorityOrderMstAttrSortService.getAttributeArea(patternCd,attr1,attr2);
    }

    /**
     *编辑时つかむ取属性1属性2组合对应的面积
     */
    @GetMapping("getEditAttributeArea")
    public Map<String,Object> getEditAttributeArea(String companyCd){
        return priorityOrderMstAttrSortService.getEditAttributeArea(companyCd);
    }


    /**
     * つかむ取属性的分类及商品分类列表
     */
    @GetMapping("/getAttributeList")
    public Map<String,Object> getAttributeList(){
        return priorityOrderMstAttrSortService.getAttributeList();
    }

    /**
     * ゾーニング設定
     * @param dto
     * @return
     */
    @PostMapping("/setAttribute")
    public Map<String,Object> setAttribute(@RequestBody PriorityOrderSpaceDto dto){
        return priorityOrderMstAttrSortService.setAttribute(dto);
    }
}
