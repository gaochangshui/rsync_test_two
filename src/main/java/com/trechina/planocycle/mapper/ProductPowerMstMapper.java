package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.ProductPowerMst;
import com.trechina.planocycle.entity.vo.CommodityListInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerMstMapper {
    int insert(ProductPowerMst record);


    List<CommodityListInfoVO> selectCommodityList(@Param("conpanyCd") String conpanyCd);

    int delete(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);


    Integer selectExistsName(String productPowerName,String companyCd,Integer productPowerCd);

    List<TableNameDto> getTableNameByCompanyCd(@Param("companyCd")String companyCd);
}
