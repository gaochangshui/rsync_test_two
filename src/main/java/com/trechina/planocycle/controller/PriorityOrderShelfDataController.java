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
     * 新规时获取基本パタ制约别信息
     * @param companyCd
     * @return
     */
    @GetMapping("getRestrictData")
    public Map<String,Object> getRestrictData(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return  priorityOrderShelfDataService.getRestrictData(companyCd,priorityOrderCd);
    }

    /**
     * 新规时获取基本パタ制约别jan详细信息
     * @param
     * @return
     */
    @PostMapping("getRestrictJans")
    public Map<String,Object> getRestrictJans(@RequestBody PriorityOrderRestDto priorityOrderRestDto) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderShelfDataService.getRestrictJans(priorityOrderRestDto);

    }

    /**
     * 新规时获取基本パタ台棚别信息
     * @param companyCd
     * @return
     */
    @GetMapping("getPlatformShedData")
    public Map<String,Object> getPlatformShedData(String companyCd,Integer priorityOrderCd){
        return  priorityOrderShelfDataService.getPlatformShedData(companyCd,priorityOrderCd);
    }
    /**
     * 新规时获取基本パタ台棚别jans详细信息
     * @param priorityOrderPlatformShedDto
     * @return
     */
    @PostMapping("getPlatformShedJans")
    public Map<String,Object> getPlatformShedJans(@RequestBody PriorityOrderPlatformShedDto priorityOrderPlatformShedDto) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return  priorityOrderShelfDataService.getPlatformShedJans(priorityOrderPlatformShedDto);
    }

    /**
     * 保存faceNum
     * @param priorityOrderRestrictJanDto
     * @return
     */
    @PostMapping("setFaceNumForData")
    public Map<String,Object> setFaceNumForData(@RequestBody List<PriorityOrderRestrictJanDto> priorityOrderRestrictJanDto) {
        return  priorityOrderShelfDataService.setFaceNumForData(priorityOrderRestrictJanDto);
    }

}
