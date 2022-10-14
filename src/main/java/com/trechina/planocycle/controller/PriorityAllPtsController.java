package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.BasicAllPts;
import com.trechina.planocycle.entity.po.PriorityAllFaceVo;
import com.trechina.planocycle.entity.vo.PriorityAllVO;
import com.trechina.planocycle.service.PriorityAllPtsService;
import com.trechina.planocycle.service.PriorityOrderShelfDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/priorityAllPts")
public class PriorityAllPtsController {
    @Autowired
    private PriorityAllPtsService priorityAllPtsService;
    @Autowired
    private PriorityOrderShelfDataService priorityOrderShelfDataService;
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

    /**
     * skuNum,修改/削除
     */
    @PostMapping("setFaceNumAndPositionForData")
    public Map<String,Object> setFaceNumAndPositionForData(@RequestBody Map<String,Object> map) {
        map.put("ptsFlag",1);
        return  priorityOrderShelfDataService.setFaceNumAndPositionForData(map);
    }


    /**
     * 基本パタン台棚別詳細
     */
    @PostMapping("getBasicAllPlatformShedJans")
    public Map<String,Object> getBasicAllPlatformShedJans(@RequestBody BasicAllPts basicAllPts) {
        return  priorityAllPtsService.getBasicAllPlatformShedJans(basicAllPts);
    }


    /**
     * faceNum修改
     */
    @PostMapping("setFaceNumForPriorityAll")
    public Map<String,Object> setFaceNumForPriorityAll(@RequestBody List<PriorityAllFaceVo> priorityAllFaceVo) {
        return  priorityAllPtsService.setFaceNumForPriorityAll(priorityAllFaceVo);
    }
}
