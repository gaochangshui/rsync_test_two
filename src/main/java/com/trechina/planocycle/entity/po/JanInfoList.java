package com.trechina.planocycle.entity.po;


import com.trechina.planocycle.entity.vo.CommonPartsDataVO;
import lombok.Data;

@Data
public class JanInfoList {
    private String jan;
    private String companyCd;
    private CommonPartsDataVO commonPartsData;

}
