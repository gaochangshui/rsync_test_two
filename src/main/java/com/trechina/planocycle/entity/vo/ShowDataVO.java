package com.trechina.planocycle.entity.vo;

import lombok.Data;

@Data
public class ShowDataVO {
    Integer productPowerCd;
    String companyCd;
    String[] posCd;
    String[] prepareCd;
    String[] intageCd;
    String[] customerCd;
}
