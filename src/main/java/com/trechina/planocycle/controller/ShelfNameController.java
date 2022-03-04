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
     * 获取棚名称信息
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfNameInfo")
    public Map<String, Object> getShelfNameInfo(String companyCd) {
        return shelfNameService.getShelfNameInfo(companyCd);
    }

    /**
     * 获取棚名称Name
     *
     * @param companyCd
     * @return
     */
    @GetMapping("/getShelfName")
    public Map<String, Object> getShelfName(String companyCd) {
        return shelfNameService.getShelfName(companyCd);
    }

    /**
     * 保存棚名称信息
     *
     * @param shelfNameDto
     * @return
     */
    @PostMapping("/setShelfNameInfo")
    public Map<String, Object> setShelfNameInfo(@RequestBody ShelfNameDto shelfNameDto) {
        return shelfNameService.setShelfName(shelfNameDto);
    }

    /**
     * 修改棚名称信息
     */
    @PostMapping("/updateShelfNameInfo")
    public Map<String, Object> updateShelfNameInfo(@RequestBody ShelfNameDto shelfNameDto) {
        return shelfNameService.updateShelfName(shelfNameDto);
    }

    /**
     * 删除棚名称
     *
     * @param jsonObject
     * @return
     */
    @DeleteMapping("/delShelfNameInfo")
    public Map<String, Object> delShelfNameInfo(@RequestBody JSONObject jsonObject) {
        return shelfNameService.delShelfNameInfo(jsonObject);
    }

    /**
     * 获取单表棚名称信息，用于修改
     *
     * @param id
     * @return
     */
    @GetMapping("/getShelfNameInfoById")
    public Map<String, Object> getShelfNameInfoById(Integer id) {
        return shelfNameService.getShelfNameInfoById(id);
    }
}
