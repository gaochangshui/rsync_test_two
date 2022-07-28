package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.ProductItemVO;

import java.util.Map;

public interface JanAttrService {
    Map<String, Object> saveProductItem(ProductItemVO productItemVO);


    Map<String, Object> delProductItem(ProductItemVO productItemVO);


    Map<String, Object> updateAttrInfo(ProductItemVO productItemVO);

}
