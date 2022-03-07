package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ProductPowerGroupDataForCgiDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductPowerDataMapper {

    //临时表work_product_power_syokika
    int deleteWKSyokika(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    //smart数据存到临时表
    int insert(@Param("keyNameList") List<String[]> keyNameList);

    //最终表存到临时表
    int insertWkSyokikaForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    //临时表数据返回页面
    List<ProductPowerMstData> selectWKSyokika(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    //临时表所有

    int deleteWKKokyaku(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    //smart数据存到临时表
    int insertGroup(@Param("keyNameList") List<List<String>> keyNameList);

    //最终表存到临时表
    int insertWkKokyakuForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    //临时表数据返回页面
    List<ProductPowerMstData> selectWKKokyaku(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd);


    //临时表yobilitem和data
    int deleteWKYobiiiternCd(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("valueCd") Integer valueCd);

    int deleteWKYobiiitern(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd);

    int deleteWKYobiiiternDataCd(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("valueCd") Integer valueCd);

    int deleteWKYobiiiternData(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd);

    List<WKYobiiiternData> selectWKYobiiiternData(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd);

    //smart数据存到临时表
    Integer getWKYobiiiternSort(@Param("companyCd") String companyCd, @Param("aud") String aud);

    int insertYobilitem(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("dataCd") Integer valueCd, @Param("itemName") String itemName, @Param("dataSort") Integer dataSort);

    int insertYobilitemData(@Param("dataList") List<WorkProductPowerReserveData> dataList);

    //最终表存到临时表
    int setWkSyokikaForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWkGroupForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWkYobilitemForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWkYobilitemDataForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWkDataForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    //最终表删除
    int deleteSyokika(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int deleteGroup(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int deleteYobiiitern(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int deleteYobiiiternData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int deleteRankData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    //最终表物理删除
    int phyDeleteSyokika(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int phyDeleteGroup(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int phyDeleteYobiiitern(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int phyDeleteYobiiiternData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);


    //最终表查询
    List<ProductPowerSyokika> selectSyokika(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    List<ProductPowerMstData> getProductPowerMstData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    //临时表转到最终表
    int endSyokikaForWK(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int endGroupForWK(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int endYobiiiternForWk(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int endYobiiiternDataForWk(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    //查询最终表的数量
    Integer syokikaPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer groupPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer yobiiiternPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);


    //rank计算
    //三表汇合一表


    int deleteWKData(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);


    List<ProductPowerMstData> rankCalculates();

    int setWKData(@Param("list") List<ProductPowerMstData> productPowerMstData, @Param("authorCd") String authorCd, @Param("companyCd") String companyCd);

    int deleteData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setData(@Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd, @Param("companyCd") String companyCd);

    List<ProductPowerMstData> getAllData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    ProductPowerParamVo getParam(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer getParamCount(ProductPowerGroupDataForCgiDto productPowerParamVo);

    List<ReserveMstVo> getReserve(@Param("productPowerCd") Integer productPowerCd, @Param("companyCd") String companyCd);
}
