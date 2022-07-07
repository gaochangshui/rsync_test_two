package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
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
     * 获取group数据
     */
    @PostMapping("/getAttrGroup")
    public Map<String,Object> getAttrGroup(@RequestBody PriorityOrderAttrDto priorityOrderAttrDto){
        return priorityOrderMstAttrSortService.getAttrGroup(priorityOrderAttrDto);
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
