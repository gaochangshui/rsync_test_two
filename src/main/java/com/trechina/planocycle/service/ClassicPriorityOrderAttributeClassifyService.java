package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.DownloadSortDto;
import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderAttributeClassifyService {
    /**
     * janのすべての属性の分類ソートを取得する
     * @param downloadSortDto
     * @return
     */
    Map<String,Object> getClassifyList(@RequestBody DownloadSortDto downloadSortDto);

    /**
     * janのすべての属性の分類順序を変更する
     * @param classifyList
     * @return
     */

    Map<String,Object> setClassifyList(@RequestBody List<PriorityOrderAttributeClassify> classifyList);

}
