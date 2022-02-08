package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerParamAttribute;
import com.trechina.planocycle.entity.vo.ProductOrderParamAttrVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerParamAttributeMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    int insert(@Param("lists") List<ProductPowerParamAttribute> record);

    ProductOrderParamAttrVO selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    ProductOrderParamAttrVO selectAttrName(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);
}
