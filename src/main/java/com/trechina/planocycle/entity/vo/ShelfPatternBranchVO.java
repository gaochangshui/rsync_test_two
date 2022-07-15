package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class ShelfPatternBranchVO {
    private Integer shelfPatternCd;
    private List<String> branchCd;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date startTime;

}
