package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.DownloadSortDto;
import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderAttributeClassifyService {
    /**
     * 获取jan所有属性的分类排序
     * @param downloadSortDto
     * @return
     */
    Map<String,Object> getClassifyList(@RequestBody DownloadSortDto downloadSortDto);

    /**
     * 修改jan所有属性的分类排序
     * @param classifyList
     * @return
     */

    Map<String,Object> setClassifyList(@RequestBody List<PriorityOrderAttributeClassify> classifyList);

}
