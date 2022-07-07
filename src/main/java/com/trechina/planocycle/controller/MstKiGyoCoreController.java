package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.MstKiGyoCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/planoCycle/MstKiGyo")
public class MstKiGyoCoreController {

    @Autowired
    private MstKiGyoCoreService mstKiGyoCoreService;
    ///**
    // * 企業共通部品パラメータ取得
    // * @param companyCd
    // * @return
    // */
    //@GetMapping("/getMstKiGyo")
    //public Map<String,Object> getMstKiGyo(String companyCd){
    //    return mstKiGyoCoreService.getMstKiGyo(companyCd);
    //}
}
