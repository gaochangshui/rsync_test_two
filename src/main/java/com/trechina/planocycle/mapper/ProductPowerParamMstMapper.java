package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.ProductPowerParamMst;
import com.trechina.planocycle.entity.vo.ProductOrderAttrAndItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface ProductPowerParamMstMapper {

    int insert(@Param("record") ProductPowerParamMst record,@Param("authorCd")String authorCd);

    ProductPowerParamMst selectCommodityParam(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);

    int deleteCommofityParamForChange(ProductPowerParamMst record);

    int deleteCommofityParam(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd,@Param("authorCd")String authorCd);

    int delete(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);

    ProductOrderAttrAndItemVO selectAttrAndItem(String companyCd, Integer productOrderCd);
    ProductOrderAttrAndItemVO selectAttrAndValue(String companyCd, Integer productOrderCd);

    int deleteParam(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    int insertParam(@Param("item")ProductPowerParam productPowerParam,@Param("authorCd")String authorCd);

    ProductPowerParam getParam(@Param("companyCd")String companyCd,@Param("productPowerCd")Integer productPowerCd);

}
