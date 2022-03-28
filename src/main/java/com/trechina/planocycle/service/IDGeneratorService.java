package com.trechina.planocycle.service;

import java.util.Map;

public interface IDGeneratorService {
    /**
     * 优先顺位表自动采号，并返回
     * @return
     */
    Map<String,Object> priorityOrderNumGenerator();

    /**
     * 商品力点数表自动采号，并返回
     */
    Map<String,Object> productPowerNumGenerator();

    Map<String,Object> priorityAllID();
}
