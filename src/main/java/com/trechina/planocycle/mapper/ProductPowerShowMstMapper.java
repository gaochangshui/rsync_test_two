package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerShowMst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ProductPowerShowMstMapper {
    int deleteByPrimaryKey(@Param("productPowerCd") Integer productPowerCd, @Param("conpanyCd") String conpanyCd);

    int insert(@Param("lists") List<ProductPowerShowMst> record);

    List<ProductPowerShowMst> selectByPrimaryKey(@Param("productPowerCd") Integer productPowerCd, @Param("conpanyCd") String conpanyCd);

}
