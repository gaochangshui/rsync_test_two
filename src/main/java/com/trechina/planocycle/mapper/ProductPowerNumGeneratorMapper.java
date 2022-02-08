package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerNumGenerator;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductPowerNumGeneratorMapper {

    int insert(ProductPowerNumGenerator record);


}
