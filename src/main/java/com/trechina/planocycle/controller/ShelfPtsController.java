package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
import com.trechina.planocycle.service.ShelfPtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/shelfPts")
public class ShelfPtsController {
    @Autowired
    private ShelfPtsService shelfPtsService;

    /**
     * 获取棚割pts信息
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPtsInfo")
    public Map<String, Object> getShelfPtsInfo(String companyCd, Integer rangFlag, String areaList) {
        return shelfPtsService.getShelfPtsInfo(companyCd, rangFlag, areaList);
    }

    /**
     * 保存棚割pts信息
     *
     * @param shelfPtsDto
     * @return
     */
    @PostMapping("/setShelfPtsInfo")
    public Map<String, Object> setShelfPtsInfo(@RequestBody ShelfPtsDto shelfPtsDto) {
        return shelfPtsService.setShelfPtsInfo(shelfPtsDto, 1);
    }

    /**
     * pts关联pattern
     *
     * @param shelfPtsJoinPatternDto
     * @return
     */
    @PutMapping("/saveShelfPts")
    public Map<String, Object> saveShelfPts(@RequestBody List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto) {
        return shelfPtsService.saveShelfPts(shelfPtsJoinPatternDto);
    }

    /**
     * 棚pattern别pts关联pattern
     *
     * @param shelfPtsJoinPatternDto
     * @return
     */
    @PutMapping("/saveShelfPtsOfPattern")
    public Map<String, Object> saveShelfPtsOfPattern(@RequestBody List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto) {
        return shelfPtsService.saveShelfPtsOfPattern(shelfPtsJoinPatternDto);
    }

    /**
     * 获取棚pattern关联过的csv履历数据
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getHistoryData")
    public Map<String, Object> getHistoryData(String companyCd) {
        return shelfPtsService.getHistoryData(companyCd);
    }

    /**
     * 棚pattern关联pts的下拉框数据
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getPtsName")
    public Map<String, Object> getPtsName(String companyCd) {
        return shelfPtsService.getPtsName(companyCd);
    }

    /**
     * 获取棚pattern别的pts信息
     *
     * @param companyCd
     * @param rangFlag
     * @param areaList
     * @return
     */
    @GetMapping("/getPtsInfoOfPattern")
    public Map<String, Object> getPtsInfoOfPattern(String companyCd, Integer rangFlag, String areaList) {
        return shelfPtsService.getPtsInfoOfPattern(companyCd, rangFlag, areaList);
    }

    /**
     * 删除棚割pts信息
     *
     * @param
     * @return
     */
    @DeleteMapping("/delShelfPtsInfo")
    public Map<String, Object> delShelfPtsInfo(@RequestBody JSONObject jsonObject) {
        return shelfPtsService.delShelfPtsInfo(jsonObject);
    }

    /**
     * 获取棚pattern关联的pts的棚/段数
     */

    @GetMapping("getTaiNumTanaNum")
    public  Map<String,Object> getTaiNumTanaNum(Integer patternCd){
        return shelfPtsService.getTaiNumTanaNum(patternCd);

    }

    /**
     * 获取棚pattern 关联pts的详细信息
     * @param patternCd
     * @return
     */
    @GetMapping("getPtsDetailData")
    public  Map<String,Object> getPtsDetailData(Integer patternCd){
        return shelfPtsService.getPtsDetailData(patternCd);

    }
}
