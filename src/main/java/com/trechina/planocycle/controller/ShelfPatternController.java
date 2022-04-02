package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPatternDto;
import com.trechina.planocycle.entity.vo.ShelfPatternBranchVO;
import com.trechina.planocycle.service.ShelfPatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/ShelfPattern")
public class ShelfPatternController {
    @Autowired
    private ShelfPatternService shelfPatternService;

    /**
     * 棚pattern情報の取得
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPatternInfo")
    public Map<String, Object> getshelfPatternInfo(String companyCd) {
        return shelfPatternService.getShelfPatternInfo(companyCd);
    }

    /**
     * 保存棚pattern情報
     *
     * @param shelfPatternDto
     * @return
     */
    @PostMapping("/setShelfPatternInfo")
    public Map<String, Object> setShelfPatternInfo(@RequestBody ShelfPatternDto shelfPatternDto) {
        return shelfPatternService.setShelfPatternInfo(shelfPatternDto);
    }

    /**
     * 修正棚pattern情報
     *
     * @param shelfPatternDto
     * @return
     */
    @PostMapping("/updateShelfPatternInfo")
    public Map<String, Object> updateShelfPatternInfo(@RequestBody ShelfPatternDto shelfPatternDto) {
        return shelfPatternService.updateShelfPatternInfo(shelfPatternDto);
    }

    /**
     * 棚pattern関連店cdを取得
     *
     * @param shelfPattrenCd
     * @return
     */
    @GetMapping("/getShelfPatternBranch")
    public Map<String, Object> getShelfPatternBranch(Integer shelfPattrenCd) {
        return shelfPatternService.getShelfPatternBranch(shelfPattrenCd);
    }

    /**
     * 保存棚patternのお店cd
     *
     * @param shelfPatternBranchVO
     * @return
     */
    @PostMapping("/setShelfPatternBranch")
    public Map<String, Object> setShelfPatternBranch(@RequestBody ShelfPatternBranchVO shelfPatternBranchVO) {
        return shelfPatternService.setShelfPatternBranch(shelfPatternBranchVO);
    }

    /**
     * すべての棚patternのnameを取得
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPatternName")
    public Map<String, Object> getShelfPatternName(String companyCd) {
        return shelfPatternService.getShelfPatternName(companyCd);
    }

    /**
     * 店舗に関連付けられた棚patternのnameを取得（优先顺位表用）
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPatternNameBranch")
    public Map<String, Object> getShelfPatternNameBranch(String companyCd) {
        return shelfPatternService.getShelfPatternNameBranch(companyCd);
    }

    /**
     * 棚の削除
     *
     * @param jsonObject
     * @return
     */
    @DeleteMapping("/delshelfPatternInfo")
    public Map<String, Object> delShelfPatternInfo(@RequestBody JSONObject jsonObject) {
        return shelfPatternService.delShelfPatternInfo(jsonObject);
    }

    /**
     * areaに基づいて棚名と棚patternを取得
     */
    @GetMapping("getShelfPatternForArea")
    public Map<String, Object> getShelfPatternForArea(@RequestParam("companyCd") String companyCd, @RequestParam("areaCds") int[] areaCds) {

        return shelfPatternService.getShelfPatternForArea(companyCd, areaCds);
    }
}
