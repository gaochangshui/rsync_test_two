package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderSort;
import com.trechina.planocycle.entity.vo.PtsCsvVO;
import com.trechina.planocycle.entity.vo.PtsTanaVo;
import com.trechina.planocycle.service.ShelfPtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/shelfPts")
public class ShelfPtsController {
    @Autowired
    private ShelfPtsService shelfPtsService;

    /**
     * 棚割pts情報の取得
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPtsInfo")
    public Map<String, Object> getShelfPtsInfo(String companyCd) {
        return shelfPtsService.getShelfPtsInfo(companyCd);
    }

    /**
     *
     * 棚割pts情報の保存
     *
     * @param shelfPtsDto
     * @return
     */
    @PostMapping("/setShelfPtsInfo")
    public Map<String, Object> setShelfPtsInfo(@RequestBody ShelfPtsDto shelfPtsDto) {
        return shelfPtsService.setShelfPtsInfo(shelfPtsDto, 1);
    }

    /**
     *
     * pts関連pattern
     *
     * @param shelfPtsJoinPatternDto
     * @return
     */
    @PutMapping("/saveShelfPts")
    public Map<String, Object> saveShelfPts(@RequestBody List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto) {
        return shelfPtsService.saveShelfPts(shelfPtsJoinPatternDto);
    }

    /**
     * 棚pattern別pts関連pattern
     *
     * @param shelfPtsJoinPatternDto
     * @return
     */
    @PutMapping("/saveShelfPtsOfPattern")
    public Map<String, Object> saveShelfPtsOfPattern(@RequestBody List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto) {
        return shelfPtsService.saveShelfPtsOfPattern(shelfPtsJoinPatternDto);
    }

    /**
     * 棚pattern関連csv履歴データの取得
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getHistoryData")
    public Map<String, Object> getHistoryData(String companyCd) {
        return shelfPtsService.getHistoryData(companyCd);
    }

    /**
     * 棚pattern関連ptsのドロップダウンボックスデータ
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getPtsName")
    public Map<String, Object> getPtsName(String companyCd) {
        return shelfPtsService.getPtsName(companyCd);
    }

    /**
     * 棚pattern別pts情報の取得
     *
     * @param companyCd
     * @param
     * @return
     */
    @GetMapping("/getPtsInfoOfPattern")
    public Map<String, Object> getPtsInfoOfPattern(String companyCd) {
        return shelfPtsService.getPtsInfoOfPattern(companyCd);
    }

    /**
     * 棚割pts情報の削除
     *
     * @param
     * @return
     */
    @DeleteMapping("/delShelfPtsInfo")
    public Map<String, Object> delShelfPtsInfo(@RequestBody JSONObject jsonObject) {
        return shelfPtsService.delShelfPtsInfo(jsonObject);
    }

    /**
     * 棚pattern関連ptsの棚/セグメント数を取得
     */

    @GetMapping("getTaiNumTanaNum")
    public  Map<String,Object> getTaiNumTanaNum(Integer patternCd,Integer priorityOrderCd){
        return shelfPtsService.getTaiNumTanaNum(patternCd,priorityOrderCd);

    }
    /**
     * 棚pattern関連ptsの詳細の取得
     * @param patternCd
     * @return
     */
    @GetMapping("getPtsDetailData")
    public  Map<String,Object> getPtsDetailData(Integer patternCd,String companyCd,@RequestParam(required = false) Integer priorityOrderCd,
    @RequestParam(required = false) Integer flag){
        return shelfPtsService.getPtsDetailData(patternCd,companyCd,priorityOrderCd,flag);

    }
    /**
     * 棚pattern関連ptsの詳細(新)の取得
     * @param patternCd
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("getNewPtsDetailData")
    public  Map<String,Object> getNewPtsDetailData(Integer patternCd,String companyCd,Integer priorityOrderCd){
        return shelfPtsService.getNewPtsDetailData(patternCd,companyCd,priorityOrderCd);

    }
    

    /**
     * 陳列順設定追加
     * @param workPriorityOrderSort
     * @return
     */
    @PostMapping("setDisplay")
    public  Map<String,Object> setDisplay(@RequestBody List<WorkPriorityOrderSort> workPriorityOrderSort){
        return shelfPtsService.setDisplay(workPriorityOrderSort);

    }
    /**
     * 陳列順設定展示
     * @param companyCd
     * @return
     */
    @GetMapping("getDisplay")
    public  Map<String,Object> getDisplay(String companyCd,Integer priorityOrderCd){
        return shelfPtsService.getDisplay(companyCd,priorityOrderCd);

    }

    /**
     * csvファイルのダウンロード
     * @param ptsCsvVO
     * @return
     */
    @PostMapping("downloadPtsCsv")
    public  void downloadPtsCsv(@RequestBody PtsCsvVO ptsCsvVO, HttpServletResponse response) throws IOException {
        shelfPtsService.downloadPtsCsv(ptsCsvVO, response);
    }

    /**
     * 新しいpts棚段数の修正
     */
    @PostMapping("setPtsTanaSize")
    public  Map<String,Object> setPtsTanaSize(@RequestBody List<PtsTanaVo> ptsTanaVoList){
        return shelfPtsService.setPtsTanaSize(ptsTanaVoList);
    }

    /**
     * check ptsKey
     * @param
     * @return
     */
    @PostMapping("/ptsKeyCheck")
    public Map<String,Object> ptsKeyCheck(@RequestBody Map<String,Object> map) {
        List<String> tableNameList = (List<String>) map.get("tableNameList");
        String companyCd = map.get("companyCd").toString();
        return shelfPtsService.ptsKeyCheck(tableNameList,companyCd);
    }
}
