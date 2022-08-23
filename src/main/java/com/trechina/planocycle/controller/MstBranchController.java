package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.po.BranchList;
import com.trechina.planocycle.service.MstBranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/planoCycleApi/MstBranch")
public class MstBranchController {

    @Autowired
    private MstBranchService mstBranchService;

    /**
     * 店舗詳細抽出
     * @param branchList
     * @return
     */
    @PostMapping("getBranchInfo")
    public Map<String,Object> getBranchInfo(@RequestBody BranchList branchList){
       return mstBranchService.getBranchInfo(branchList);
    }

    /**
     *店舗詳細保存
     * @param branchList
     * @return
     */
    @PostMapping("setBranchInfo")
    public Map<String,Object> setBranchInfo(@RequestBody List<BranchList> branchList){
        return mstBranchService.setBranchInfo(branchList);
    }

    @GetMapping("syncTenInfo")
    public Map<String,Object> syncTenInfo(){
        return mstBranchService.syncTenData();
    }
}
