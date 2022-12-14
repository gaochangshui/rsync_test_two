package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.FaceNumDataDto;
import com.trechina.planocycle.entity.dto.GetPatternForProductPowerCd;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.ProductPowerMst;
import com.trechina.planocycle.entity.vo.CommodityListInfoVO;
import com.trechina.planocycle.entity.vo.ProductPowerMstVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerMstMapper {
    int insert(ProductPowerMst record);


    List<CommodityListInfoVO> selectCommodityList(@Param("conpanyCd") String conpanyCd);

    int delete(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd,@Param("authorCd")String authorCd,@Param("authorName")String authorName);


    Integer selectExistsName(@Param("productPowerName") String productPowerName,@Param("companyCd") String companyCd,@Param("productPowerCd") Integer productPowerCd);

    List<TableNameDto> getTableNameByCompanyCd(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    Integer selectUpdExistsName(@Param("companyCd") String companyCd,@Param("productPowerCd") Integer productPowerCd);

    Integer update(ProductPowerMst record);

    ProductPowerMstVo getProductPowerInfo(@Param("companyCd")String companyCd,@Param("productPowerCd")Integer productPowerCd);

    Integer getSkuNum(@Param("companyCd")String companyCd,@Param("productPowerCd")Integer productPowerCd);

    int setProductPowerCdForMst(@Param("productPowerCd")Integer productPowerCd,@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    Integer getProductPowerCd(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    Integer getpatternCd(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    FaceNumDataDto getFaceNum(@Param("patternCd")Integer patternCd);

    Double getSalesCntAvg(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    GetPatternForProductPowerCd getPatternAndName(@Param("productPowerCd") Integer productPowerCd);

    void setWork(Integer productPowerCd, String companyCd,String authorCd, String date);

    void deleteWork(String companyCd, Integer productPowerCd);

    String getCompanyName(String companyCd);

    List<Integer> getBasicList(String companyCd, Integer productPowerCd);
    List<Integer> getPriorityOrderList(String companyCd, Integer productPowerCd);
}
