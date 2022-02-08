package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerMst;
import com.trechina.planocycle.entity.vo.CommodityListInfoVO;
import com.trechina.planocycle.entity.vo.EnterPriseInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerMstMapper {
    int insert(ProductPowerMst record);


    List<CommodityListInfoVO> selectCommodityList(@Param("conpanyCd") String conpanyCd);

    int delete(String conpanyCd, Integer productPowerCd);


    Integer selectExistsName(String productPowerName,String companyCd,Integer productPowerCd);
}
