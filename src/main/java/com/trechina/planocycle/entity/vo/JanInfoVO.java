package com.trechina.planocycle.entity.vo;

import java.util.LinkedHashMap;
import java.util.List;

public class JanInfoVO {
    private String janHeader;
    private List<LinkedHashMap<String, Object>> janDataList;
    private Long total;

    public String getJanHeader() {
        return janHeader;
    }

    public void setJanHeader(String janHeader) {
        this.janHeader = janHeader;
    }

    public List<LinkedHashMap<String, Object>> getJanDataList() {
        return janDataList;
    }

    public void setJanDataList(List<LinkedHashMap<String, Object>> janDataList) {
        this.janDataList = janDataList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
