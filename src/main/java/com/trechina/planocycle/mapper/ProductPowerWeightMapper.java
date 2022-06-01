package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerWeight;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerWeightMapper {
    int deleteByPrimaryKey(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int insert(@Param("lists") List<ProductPowerWeight> record, @Param("authorCd") String authorCd);

    List<ProductPowerWeight> selectByPrimaryKey(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);

    int updateByPrimaryKey(ProductPowerWeight record);

    int delete(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);
}
