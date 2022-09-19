package com.trechina.planocycle.entity.vo;

import lombok.Data;

@Data
public class ShelfPatternNameVO {
    private Integer shelfPatternCd;
    private Integer shelfCd;
    private String shelfPatternName;
    private String shelfName;
    private String storeCd;
    private String storeIsCore;

}
