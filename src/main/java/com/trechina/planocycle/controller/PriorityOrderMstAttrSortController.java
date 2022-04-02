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
     *つかむ取属性1と属性2
     */
    @GetMapping("/getAttribute")
    public Map<String,Object> getAttribute(){
        return priorityOrderMstAttrSortService.getAttribute();
    }
    /**
     * 陳列設定つぐむ取属性1と属性2
     */
@GetMapping("/getAttributeSort")
    public Map<String,Object> getAttributeSort(){
        return priorityOrderMstAttrSortService.getAttributeSort();
    }
    /**
     *新規計算属性1属性2の組合せに対応する面積
     */
    @GetMapping("/getAttributeArea")
    public Map<String,Object> getAttributeArea(Integer patternCd,Integer attr1,Integer attr2){
        return priorityOrderMstAttrSortService.getAttributeArea(patternCd,attr1,attr2);
    }

    /**
     *編集時につくむ取属性1属性2の組み合わせに対応する面積
     */
    @GetMapping("getEditAttributeArea")
    public Map<String,Object> getEditAttributeArea(String companyCd){
        return priorityOrderMstAttrSortService.getEditAttributeArea(companyCd);
    }


    /**
     * つかむ取属性的分類及商品分類列表
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
