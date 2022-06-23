package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ZokuseiMst;

import java.util.List;

public interface ZokuseiMapper {
    List<ZokuseiMst> selectZokusei(String companyCd, String classCd);
}
