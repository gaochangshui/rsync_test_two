package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.vo.PriorityAllVO;
import com.trechina.planocycle.service.PriorityAllPtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priorityAllPts")
public class PriorityAllPtsController {
    @Autowired
    private PriorityAllPtsService priorityAllPtsService;
    /**
     * 获取pts详情接口
     * @param patternCd
     * @return
     */
    @GetMapping("getPtsDetailData")
    public Map<String,Object> getPtsDetailData(Integer patternCd, String companyCd, Integer priorityAllCd){
        return priorityAllPtsService.getPtsDetailData(patternCd,companyCd,priorityAllCd);

    }

    /**
     * 打包下载全pattern的pts文件
     * @return
     */
    @PostMapping("batchDownloadPtsData")
    public void batchDownloadPtsData(@RequestBody PriorityAllVO priorityAllVO, HttpServletResponse response) throws IOException {
        priorityAllPtsService.batchDownloadPtsData(priorityAllVO, response);
    }
}
