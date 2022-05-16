package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.ProductPowerGroupDataForCgiDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductPowerDataMapper {

    //テンポラリ・テーブルワークproduct_power_syokika
    int deleteWKSyokika(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("productPowerCd") Integer productPowerCd);

    //smartデータをテンポラリ・テーブルに保存
    int insert(@Param("keyNameList") List<String[]> keyNameList);

    //最終テーブルをテンポラリ・テーブルに保存
    int insertWkSyokikaForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    //テンポラリ・テーブル・データの戻りページ
    int  selectWKSyokika(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("productPowerCd")Integer productPowerCd,@Param("list") List<String> jan);
    List<String> getjan();
    //テンポラリ・テーブルのすべて

    int deleteWKKokyaku(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("productPowerCd") Integer productPowerCd);

    //smartデータをテンポラリ・テーブルに保存
    int insertGroup(@Param("keyNameList") List<List<String>> keyNameList);

    //最終テーブルをテンポラリ・テーブルに保存
    int insertWkKokyakuForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    //テンポラリ・テーブル・データの戻りページ
    int selectWKKokyaku(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd,@Param("productPowerCd")Integer productPowerCd,@Param("list") List<String> jan);
    int selectWKKIntager(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd,@Param("productPowerCd")Integer productPowerCd,@Param("list") List<String> jan);

    //表示データ項目
    List<Map<String, String>> selectShowData(@Param("productPowerCd") Integer productPowerCd,
                                             @Param("showList")List<ParamConfigVO> showList,
                                             @Param("showPreparedList")List<Map<String, Object>> showPreparedList,
                                             String[] customerCd, List<String> prepareCd, String[] intageCd);

    //テンポラリ・テーブルyobilitemとdata
    int deleteWKYobiiiternCd(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("valueCd") Integer valueCd);

    int deleteWKYobiiitern(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    int deleteWKYobiiiternDataCd(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("valueCd") Integer valueCd);

    int deleteWKYobiiiternData(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    int deleteWKIntage(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("productPowerCd") Integer productPowerCd);


    List<WKYobiiiternData> selectWKYobiiiternData(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd);

    //smartデータをテンポラリ・テーブルに保存
    Integer getWKYobiiiternSort(@Param("companyCd") String companyCd, @Param("aud") String aud);

    int insertYobilitem(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("dataCd") Integer valueCd, @Param("itemName") String itemName, @Param("dataSort") Integer dataSort);

    int insertYobilitemData(@Param("dataList") List<WorkProductPowerReserveData> dataList);

    //最終テーブルをテンポラリ・テーブルに保存
    int setWkSyokikaForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWkGroupForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWkYobilitemForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWkYobilitemDataForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWkDataForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setWKIntageForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    //最終テーブルの削除
    int deleteSyokika(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int deleteGroup(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int deleteYobiiitern(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int deleteYobiiiternData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int deleteRankData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    //最終テーブル物理削除{{さいしゅうてーぶる:ぶつりてきさくじょ}}
    int phyDeleteSyokika(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int phyDeleteGroup(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int phyDeleteYobiiitern(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int phyDeleteYobiiiternData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int phyDeleteIntage(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);


    //最終テーブルクエリー
    List<ProductPowerSyokika> selectSyokika(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    List<ProductPowerMstData> getProductPowerMstData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    //テンポラリ・テーブルを最終テーブルに移動
    int endSyokikaForWK(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int endGroupForWK(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int endYobiiiternForWk(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int endYobiiiternDataForWk(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int endIntageForWK(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    //クエリの最終テーブル数
    Integer syokikaPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer groupPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer yobiiiternPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);


    //rank計算
    //三表合流一表


    int deleteWKData(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("productPowerCd") Integer productPowerCd);


    List<ProductPowerMstData> rankCalculates();

    int setWKData(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd,@Param("productPowerCd")Integer productPowerCd);

    int deleteData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setData(@Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd, @Param("companyCd") String companyCd);

    List<Map<String,Object>> getAllData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd,@Param("list")List<String> list);

    ProductPowerParamVo getParam(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer getParamCount(ProductPowerGroupDataForCgiDto productPowerParamVo);

    List<ReserveMstVo> getReserve(@Param("productPowerCd") Integer productPowerCd, @Param("companyCd") String companyCd);

    List<ReserveMstVo> getCheckedReserve(@Param("productPowerCd") Integer productPowerCd, @Param("companyCd") String companyCd,
             @Param("checkedCodeList") String[] checkedCodeList);

    List<Map<String, Object>> getSyokikaAllData(@Param("companyCd") String companyCd, @Param("tableName") String tableName,
                                                @Param("janCdColName") String janCdColName,@Param("classify") List<Map<String, Object>> classify,@Param("authorCd") String authorCd,@Param("productPowerCd") Integer productPowerCd);
    List<Map<String, Object>> getDynamicAllData(String companyCd, Integer productPowerCd, String tableName,
                                                String janCdColName, List<Map<String, Object>> classify);

    int setSyokikaAllData(@Param("lists") List<Map<String,Object>>lists);

    List<Map<String,Object>> rankCalculation(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    List<Map<String, Object>> getProductRankCalculate(@Param("map") Map<String, Object> map, @Param("companyCd") String companyCd,@Param("productPowerCd") Integer productPowerCd);

    void setWkDataRank(List<Map<String, Object>> rankCalculate, String authorCd, String companyCd, Integer productPowerCd);

    List<String> getShowItemCd(@Param("list") List<String> cdList);


}
