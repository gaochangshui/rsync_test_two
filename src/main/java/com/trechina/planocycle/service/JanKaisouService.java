package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.ProductItemVO;

import java.util.Map;

public interface JanKaisouService {
    Map<String, Object> saveProductItem(ProductItemVO productItemVO);
}
