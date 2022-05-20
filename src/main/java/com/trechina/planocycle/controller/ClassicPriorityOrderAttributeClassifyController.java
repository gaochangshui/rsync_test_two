package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.DownloadSortDto;
import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;
import com.trechina.planocycle.service.ClassicPriorityOrderAttributeClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priority/PriorityOrderAttributeClassify")
public class ClassicPriorityOrderAttributeClassifyController {
    @Autowired
    private ClassicPriorityOrderAttributeClassifyService priorityOrderAttributeClassifyService;
    @PostMapping("/getClassifyList")
    public Map<String,Object> getClassifyList(@RequestBody DownloadSortDto downloadSortDto){
        return priorityOrderAttributeClassifyService.getClassifyList(downloadSortDto);
    }
    @PostMapping("setClassifyList")
    public Map<String,Object> setClassifyList(@RequestBody List<PriorityOrderAttributeClassify> classifyList){
        return priorityOrderAttributeClassifyService.setClassifyList(classifyList);
    }
}
