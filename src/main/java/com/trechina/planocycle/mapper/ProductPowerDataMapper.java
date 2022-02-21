package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.ProductPowerKokyaku;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.entity.po.ProductPowerSyokika;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerDataMapper {
    //临时表work_product_power_syokika
   int deleteWKSyokika();
    //smart数据存到临时表
    int insert(@Param("keyNameList") List<List<String>> keyNameList);
    //最终表存到临时表
    int insertWkSyokikaForFinally(@Param("companyCd")String companyCd, @Param("productPowerCd") Integer productPowerCd);
    //临时表数据返回页面
    List<ProductPowerSyokika> selectWKSyokika();

    //临时表group

    int deleteWKKokyaku();
    //smart数据存到临时表
    int insertGroup(@Param("keyNameList") List<List<String>> keyNameList);
    //最终表存到临时表
    int insertWkKokyakuForFinally(@Param("companyCd")String companyCd, @Param("productPowerCd") Integer productPowerCd);
    //临时表数据返回页面
    List<ProductPowerKokyaku> selectWKKokyaku();

    //临时表yobilitem和data
    int deleteWKYobiiitern();
    int deleteWKYobiiiternData();
    //smart数据存到临时表
    int insertYobilitem(@Param("companyCd") String companyCd,@Param("authorCd")String authorCd,@Param("itemCd")Integer itemCd,@Param("itemName")String itemName);

    //最终表存到临时表
    int insertWkYobiiiternForFinally();

    //最终表
    int deleteSyokika(@Param("companyCd")String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd")String authorCd);
    int deleteGroup(@Param("companyCd")String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd")String authorCd);
    int deleteYobiiitern(@Param("companyCd")String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd")String authorCd);
    int deleteYobiiiternData(@Param("companyCd")String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd")String authorCd);

     List<ProductPowerMstData> getProductPowerMstData(@Param("companyCd")String companyCd, @Param("productPowerCd") Integer productPowerCd);

}
