package com.trechina.planocycle.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShelfPatternBranch {

    private Integer shelfPatternCd;

    private String branch;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date startTime;

    private String authorCd;

}
