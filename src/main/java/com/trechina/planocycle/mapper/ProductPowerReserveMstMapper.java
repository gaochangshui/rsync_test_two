package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerReserveMst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerReserveMstMapper {
    int deleteByPrimaryKey(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int delete(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);

    int insert(@Param("lists") List<ProductPowerReserveMst> record, @Param("authorCd") String authorCd);

    List<ProductPowerReserveMst> selectByPrimaryKey(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);

    int updateByPrimaryKey(ProductPowerReserveMst record);
}
