package com.trechina.planocycle.entity.po;

import com.trechina.planocycle.entity.vo.CommonPartsDataVO;
import lombok.Data;

@Data
public class BranchList {
    private String companyCd;
    private CommonPartsDataVO commonPartsData;
    private String branchCd;
    private String branchName;
    private String areaName;
}
