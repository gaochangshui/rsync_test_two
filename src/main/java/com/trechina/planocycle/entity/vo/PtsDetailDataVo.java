package com.trechina.planocycle.entity.vo;


import lombok.Data;

import java.util.List;
@Data
public class PtsDetailDataVo {

    private Integer id;
    private String taiHeader;
    private String tanaHeader;
    private String janHeader;
    private String modename;
    private String commoninfo;
    private String versioninfo;
    private String outflg;
    private Integer taiNum =0;
    private Integer tanaNum =0;
    private Integer faceNum =0;
    private Integer skuNum=0;
    private List<PtsTaiVo> ptsTaiList;
    private List<PtsTanaVo> ptsTanaVoList;
    private List ptsJanDataList;
    private String janColumns;

}
