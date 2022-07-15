package com.trechina.planocycle.entity.po;

import lombok.Data;

@Data
public class ProductPowerParamMst {
    private String conpanyCd;

    private Integer productPowerCd;

    private Integer peridFlag;

    private String startPerid;

    private String endPerid;

    private Integer seasonPeridFlag;

    private String seasonStPerid;

    private String seasonEndPerid;

    private String storeCd;

    private String prdCd;

    private String channelName;

    private String countyName;

    private Integer itemFlg;

}
