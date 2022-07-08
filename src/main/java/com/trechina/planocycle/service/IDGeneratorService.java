package com.trechina.planocycle.service;

import java.util.Map;

public interface IDGeneratorService {
    /**
     * 優先順位表自動采号，并返回
     * @return
     */
    Map<String,Object> priorityOrderNumGenerator();

    /**
     * 商品力点数表自動采号，并返回
     */
    Map<String,Object> productPowerNumGenerator();
    /**
     * 全pattern自動取號
     * @return
     */
    Map<String,Object> priorityAllID();

    /**
     * ゆうせんじゅんいひょう自動番号付け
     * @return
     */
    Map<String, Object> classPriorityOrderNumGenerator();



}
