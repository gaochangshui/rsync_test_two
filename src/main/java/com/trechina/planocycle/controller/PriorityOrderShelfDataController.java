package com.trechina.planocycle.controller;


import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictJanDto;
import com.trechina.planocycle.service.PriorityOrderShelfDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/PriorityOrderShelfData")
public class PriorityOrderShelfDataController {

    @Autowired
    private PriorityOrderShelfDataService priorityOrderShelfDataService;


    /**
     * 新規では基本的なパター制約に関する情報を入手
     * @param companyCd
     * @return
     */
    @GetMapping("getRestrictData")
    public Map<String,Object> getRestrictData(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return  priorityOrderShelfDataService.getRestrictData(companyCd,priorityOrderCd);
    }

    /**
     * 新規では基本パター制約别janの详細情報を取得
     * @param
     * @return
     */
    @PostMapping("getRestrictJans")
    public Map<String,Object> getRestrictJans(@RequestBody PriorityOrderRestDto priorityOrderRestDto) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderShelfDataService.getRestrictJans(priorityOrderRestDto);

    }

    /**
     * 新規では基本的なパター棚别の情报を取得
     * @param companyCd
     * @return
     */
    @GetMapping("getPlatformShedData")
    public Map<String,Object> getPlatformShedData(String companyCd,Integer priorityOrderCd){
        return  priorityOrderShelfDataService.getPlatformShedData(companyCd,priorityOrderCd);
    }
    /**
     * 新規では基本的なパタ台棚别jansの详細情报を入手
     * @param priorityOrderPlatformShedDto
     * @return
     */
    @PostMapping("getPlatformShedJans")
    public Map<String,Object> getPlatformShedJans(@RequestBody PriorityOrderPlatformShedDto priorityOrderPlatformShedDto) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return  priorityOrderShelfDataService.getPlatformShedJans(priorityOrderPlatformShedDto);
    }

    /**
     * faceNumの保存
     * @param priorityOrderRestrictJanDto
     * @return
     */
    @PostMapping("setFaceNumForData")
    public Map<String,Object> setFaceNumForData(@RequestBody List<PriorityOrderRestrictJanDto> priorityOrderRestrictJanDto) {
        return  priorityOrderShelfDataService.setFaceNumForData(priorityOrderRestrictJanDto);
    }

}
