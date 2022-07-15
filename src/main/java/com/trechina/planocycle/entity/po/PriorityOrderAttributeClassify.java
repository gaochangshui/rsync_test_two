package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class PriorityOrderAttributeClassify {
    private String companyCd;
    private Integer priorityOrderCd;
    private Integer taiCd;
    private String attr1;
    private Integer tanaCd;
    private String attr2;
    private boolean taiCdFlg = false;
    private boolean tanaCdFlg = false;

}
