package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.MstKiGyoCore;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MstKiGyoCoreMapper {
    MstKiGyoCore selectByPrimaryKey(String companyCd);
}
