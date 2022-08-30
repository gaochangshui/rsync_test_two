package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.ProductPowerParamMst;
import com.trechina.planocycle.entity.vo.ProductOrderAttrAndItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ProductPowerParamMstMapper {

    int insert(@Param("record") ProductPowerParamMst record, @Param("authorCd") String authorCd);

    ProductPowerParamMst selectCommodityParam(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);


    int deleteCommofityParam(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int delete(@Param("conpanyCd") String conpanyCd, @Param("productPowerCd") Integer productPowerCd);

    ProductOrderAttrAndItemVO selectAttrAndItem(String companyCd, Integer productOrderCd);

    ProductOrderAttrAndItemVO selectAttrAndValue(String companyCd, Integer productOrderCd);

    int deleteParam(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    int insertParam(@Param("item") ProductPowerParam productPowerParam, @Param("customerCondition") String customerCondition, @Param("authorCd") String authorCd
    ,@Param("newProductPowerCd")Integer newProductPowerCd,@Param("prodAttrData")String prodAttrData);

    ProductPowerParam getParam(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    void setWork(@Param("item") Map<String, Object> map,@Param("authorCd") String authorCd,@Param("customerCondition") String customerCondition,@Param("prodAttrData")String prodAttrData);

    void deleteWork(String companyCd, Integer productPowerCd);

    ProductPowerParam getWorkParam(String companyCd, Integer productPowerCd);

    void setWorkForFinal(@Param("companyCd")String companyCd, @Param("productPowerCd") Integer productPowerCd,@Param("newProductPowerCd")Integer newProductPowerCd);
}
