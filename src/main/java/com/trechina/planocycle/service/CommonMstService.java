package com.trechina.planocycle.service;

import java.util.Map;

public interface CommonMstService {

    /**
     * 获取Area Master信息
     * @return
     */
    Map<String,Object> getAreaInfo(String companyCd);

    /**
     * 根据棚名称cd取area
     * @param ShelfNameCd
     * @return
     */
    Map<String,Object> getAreaForShelfName(Integer ShelfNameCd);
}
