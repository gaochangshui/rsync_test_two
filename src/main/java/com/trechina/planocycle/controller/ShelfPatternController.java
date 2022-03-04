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
     * 获取棚pattern信息
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPatternInfo")
    public Map<String, Object> getshelfPatternInfo(String companyCd) {
        return shelfPatternService.getShelfPatternInfo(companyCd);
    }

    /**
     * 保存棚pattern信息
     *
     * @param shelfPatternDto
     * @return
     */
    @PostMapping("/setShelfPatternInfo")
    public Map<String, Object> setShelfPatternInfo(@RequestBody ShelfPatternDto shelfPatternDto) {
        return shelfPatternService.setShelfPatternInfo(shelfPatternDto);
    }

    /**
     * 修改棚pattern信息
     *
     * @param shelfPatternDto
     * @return
     */
    @PostMapping("/updateShelfPatternInfo")
    public Map<String, Object> updateShelfPatternInfo(@RequestBody ShelfPatternDto shelfPatternDto) {
        return shelfPatternService.updateShelfPatternInfo(shelfPatternDto);
    }

    /**
     * 获取棚pattern关联的店cd
     *
     * @param shelfPattrenCd
     * @return
     */
    @GetMapping("/getShelfPatternBranch")
    public Map<String, Object> getShelfPatternBranch(Integer shelfPattrenCd) {
        return shelfPatternService.getShelfPatternBranch(shelfPattrenCd);
    }

    /**
     * 保存棚pattern的店cd
     *
     * @param shelfPatternBranchVO
     * @return
     */
    @PostMapping("/setShelfPatternBranch")
    public Map<String, Object> setShelfPatternBranch(@RequestBody ShelfPatternBranchVO shelfPatternBranchVO) {
        return shelfPatternService.setShelfPatternBranch(shelfPatternBranchVO);
    }

    /**
     * 获取所有棚pattern的name
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPatternName")
    public Map<String, Object> getShelfPatternName(String companyCd) {
        return shelfPatternService.getShelfPatternName(companyCd);
    }

    /**
     * 获取关联了店铺的棚pattern的name（优先顺位表用）
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfPatternNameBranch")
    public Map<String, Object> getShelfPatternNameBranch(String companyCd) {
        return shelfPatternService.getShelfPatternNameBranch(companyCd);
    }

    /**
     * 删除棚pattern
     *
     * @param jsonObject
     * @return
     */
    @DeleteMapping("/delshelfPatternInfo")
    public Map<String, Object> delShelfPatternInfo(@RequestBody JSONObject jsonObject) {
        return shelfPatternService.delShelfPatternInfo(jsonObject);
    }

    /**
     * 根据area获取棚名称和棚pattern
     */
    @GetMapping("getShelfPatternForArea")
    public Map<String, Object> getShelfPatternForArea(String companyCd, int[] areaCds) {

        return shelfPatternService.getShelfPatternForArea(companyCd, areaCds);
    }
}
