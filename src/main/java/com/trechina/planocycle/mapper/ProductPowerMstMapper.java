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

    int delete(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd,@Param("authorCd")String authorCd,@Param("authorName")String authorName);


    Integer selectExistsName(@Param("productPowerName") String productPowerName,@Param("companyCd") String companyCd,@Param("productPowerCd") Integer productPowerCd);

    List<TableNameDto> getTableNameByCompanyCd(@Param("companyCd")String companyCd);

    Integer selectUpdExistsName(@Param("companyCd") String companyCd,@Param("productPowerCd") Integer productPowerCd);

    Integer update(ProductPowerMst record);


}
