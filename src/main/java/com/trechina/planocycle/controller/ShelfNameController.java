package com.trechina.planocycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfNameDto;
import com.trechina.planocycle.service.ShelfNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/planoCycle/ShelfName")
public class ShelfNameController {
    @Autowired
    private ShelfNameService shelfNameService;

    /**
     * 棚名情報の取得
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfNameInfo")
    public Map<String, Object> getShelfNameInfo(String companyCd) {
        return shelfNameService.getShelfNameInfo(companyCd);
    }

    /**
     * 棚名を取得Name
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfName")
    public Map<String, Object> getShelfName(String companyCd) {
        return shelfNameService.getShelfName(companyCd);
    }

    /**
     * ハウス名情報の保存
     *
     * @param shelfNameDto
     * @return
     */
    @PostMapping("/setShelfNameInfo")
    public Map<String, Object> setShelfNameInfo(@RequestBody ShelfNameDto shelfNameDto) {
        return shelfNameService.setShelfName(shelfNameDto);
    }

    /**
     * 小屋名情報の変更
     */
    @PostMapping("/updateShelfNameInfo")
    public Map<String, Object> updateShelfNameInfo(@RequestBody ShelfNameDto shelfNameDto) {
        return shelfNameService.updateShelfName(shelfNameDto);
    }

    /**
     * 小屋名の削除
     *
     * @param jsonObject
     * @return
     */
    @DeleteMapping("/delShelfNameInfo")
    public Map<String, Object> delShelfNameInfo(@RequestBody JSONObject jsonObject) {
        return shelfNameService.delShelfNameInfo(jsonObject);
    }

    /**
     * 変更用のシングル・テーブル・ハウス名情報の取得
     *
     * @param id
     * @return
     */
    @GetMapping("/getShelfNameInfoById")
    public Map<String, Object> getShelfNameInfoById(Integer id) {
        return shelfNameService.getShelfNameInfoById(id);
    }
}
