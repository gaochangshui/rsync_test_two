package com.trechina.planocycle.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class GroupCompanyVO {
    private String groupCd;
    private String groupName;
    private List<String> companyCds;
}
