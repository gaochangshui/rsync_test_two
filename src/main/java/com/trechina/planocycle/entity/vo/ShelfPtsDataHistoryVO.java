package com.trechina.planocycle.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShelfPtsDataHistoryVO {
    private Integer patternCd;
    private String patternName;
    private Integer ptsCd;
    private String ptsName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date startDay;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date createTime;
    private String authorcd;

}
