package com.trechina.planocycle.service;

import java.util.Map;

public interface MstKiGyoCoreService {
    /**
     * つかむ取企业的共通部品参数
     * @param companyCd
     * @return
     */
    Map<String, Object> getMstKiGyo(String companyCd);
}
