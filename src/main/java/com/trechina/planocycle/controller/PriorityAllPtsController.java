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
     * pts詳細インタフェースの取得
     * @param patternCd
     * @return
     */
    @GetMapping("getPtsDetailData")
    public Map<String,Object> getPtsDetailData(Integer patternCd, String companyCd, Integer priorityAllCd){

        return priorityAllPtsService.getPtsDetailData(patternCd,companyCd,priorityAllCd);

    }

    /**
     * 全patternのptsファイルをパッケージしてダウンロードします
     * @return
     */
    @PostMapping("batchDownloadPtsData")
    public void batchDownloadPtsData(@RequestBody PriorityAllVO priorityAllVO, HttpServletResponse response) throws IOException {
        priorityAllPtsService.batchDownloadPtsData(priorityAllVO, response);
    }
}
