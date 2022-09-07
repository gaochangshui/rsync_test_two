package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductPowerDataMapper {

    //テンポラリ・テーブルワークproduct_power_syokika
    int deleteWKSyokika(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("productPowerCd") Integer productPowerCd);


    List<String> getjan();
    //テンポラリ・テーブルのすべて

    int deleteWKKokyaku(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("productPowerCd") Integer productPowerCd);


    /**
     * 最終テーブルをテンポラリ・テーブルに保存
     */
    int insertWkKokyakuForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);



    //表示データ項目
    List<Map<String, String>> selectShowData(@Param("productPowerCd") Integer productPowerCd,
                                             @Param("showList")List<ParamConfigVO> showList,
                                             @Param("showPreparedList")List<Map<String, Object>> showPreparedList,
                                             String[] customerCd, List<String> prepareCd, String[] intageCd);

    //テンポラリ・テーブルyobilitemとdata
    int deleteWKYobiiiternCd(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("valueCd") Integer valueCd, @Param("productPowerCd") Integer productPowerCd);

    int deleteWKYobiiitern(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    int deleteWKYobiiiternDataCd(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("valueCd") Integer valueCd,@Param("productPowerCd") Integer productPowerCd);

    int deleteWKYobiiiternData(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    int deleteWKIntage(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("productPowerCd") Integer productPowerCd);


    List<WKYobiiiternData> selectWKYobiiiternData(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd);

    //smartデータをテンポラリ・テーブルに保存
    Integer getWKYobiiiternSort(@Param("companyCd") String companyCd, @Param("aud") String aud,@Param("productPowerCd")Integer productPowerCd);

    int insertYobilitem(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("dataCd") Integer valueCd, @Param("itemName") String itemName, @Param("dataSort") Integer dataSort,@Param("productPowerCd")Integer productPowerCd);

    int insertYobilitemData(@Param("dataList") List<WorkProductPowerReserveData> dataList);

    //最終テーブルをテンポラリ・テーブルに保存
    int setWkSyokikaForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
        ,@Param("newProductPowerCd")Integer newProductPowerCd);

    int setWkGroupForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
         ,@Param("newProductPowerCd")Integer newProductPowerCd);

    int setWkYobilitemForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
        ,@Param("newProductPowerCd")Integer newProductPowerCd);

    int setWkYobilitemDataForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
        ,@Param("newProductPowerCd")Integer newProductPowerCd);

    int setWkDataForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
    ,@Param("newProductPowerCd")Integer newProductPowerCd,@Param("dataCol")List<String> dataCol);

    int setWKIntageForFinally(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
            ,@Param("newProductPowerCd")Integer newProductPowerCd);

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
    int endSyokikaForWK(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
    ,@Param("newProductPowerCd")Integer newProductPowerCd);

    int endGroupForWK(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
    ,@Param("newProductPowerCd")Integer newProductPowerCd);

    int endYobiiiternForWk(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
    ,@Param("newProductPowerCd")Integer newProductPowerCd);

    int endYobiiiternDataForWk(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
    ,@Param("newProductPowerCd")Integer newProductPowerCd);

    int endIntageForWK(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd
    ,@Param("newProductPowerCd")Integer newProductPowerCd);

    //クエリの最終テーブル数
    Integer syokikaPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer groupPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer yobiiiternPowerCdNum(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);


    //rank計算
    //三表合流一表


    int deleteWKData(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("productPowerCd") Integer productPowerCd);


    int setWKData(@Param("authorCd") String authorCd, @Param("companyCd") String companyCd,@Param("productPowerCd")Integer productPowerCd
            ,@Param("ptsCd")List<Integer> ptsCd,@Param("storeCd") List<String> storeCd);

    int deleteData(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("authorCd") String authorCd);

    int setData(@Param("productPowerCd") Integer productPowerCd, @Param("companyCd") String companyCd
    ,@Param("newProductPowerCd")Integer newProductPowerCd);

    List<LinkedHashMap<String,Object>> getAllDataAttr(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("list")List<String> list
            ,@Param("janCdColName") String janCdColName,@Param("classify") List<Map<String, Object>> classify, @Param("tableName") String tableName);


    List<LinkedHashMap<String,Object>> getAllDataItem(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("list")List<String> list
            ,@Param("janCdColName") String janCdColName,@Param("classify") List<Map<String, Object>> classify, @Param("tableName") String tableName);

    List<LinkedHashMap<String,Object>> getAllDataRank(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd, @Param("list")List<String> list
            ,@Param("janCdColName") String janCdColName,@Param("classify") List<Map<String, Object>> classify, @Param("tableName") String tableName);

    ProductPowerParamVo getParam(@Param("companyCd") String companyCd, @Param("productPowerCd") Integer productPowerCd);

    Integer getParamCount(Map<String,Object> map);

    List<ReserveMstVo> getReserve(@Param("productPowerCd") Integer productPowerCd, @Param("companyCd") String companyCd);

    List<ReserveMstVo> getCheckedReserve(@Param("productPowerCd") Integer productPowerCd, @Param("companyCd") String companyCd,
             @Param("checkedCodeList") String[] checkedCodeList);

    List<Map<String, Object>> getSyokikaAllData(@Param("companyCd") String companyCd, @Param("tableName") String tableName,
                                                @Param("janCdColName") String janCdColName,@Param("classify") List<Map<String, Object>> classify
            ,@Param("authorCd") String authorCd,@Param("productPowerCd") Integer productPowerCd,@Param("ptsCd")List<Integer> ptsCd,@Param("storeCd") List<String> storeCd);
    List<Map<String, Object>> getDynamicAllData(String companyCd, Integer productPowerCd, String tableName,
                                                String janCdColName, List<Map<String, Object>> classify, String[] projects,
                                                Integer janNameColIndex);

    int setSyokikaAllData(@Param("lists") List<Map<String,Object>>lists);

    List<Map<String,Object>> rankCalculation(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    List<Map<String, Object>> getProductRankCalculate(@Param("map") Map<String, Object> map, @Param("companyCd") String companyCd,@Param("productPowerCd") Integer productPowerCd,@Param("authorCd")String authorCd);

    void setWkDataRank(List<Map<String, Object>> rankCalculate, String authorCd, String companyCd, Integer productPowerCd,String colName);

    List<String> getShowItemCd(@Param("list") List<String> cdList);


    List<String> getYobi(String companyCd, Integer productPowerCd, String authorCd);
    List<Map<String,Object>> getYobiHeader(String companyCd, Integer productPowerCd, String authorCd);

    List<PriorityOrderJanProposal> selectSameNameJan(Integer productPowerNo, String ptsCd, String tableName, String janCol, String janNameCol);

    String getBranch(Integer productPowerCd);

    String getPatternList(List<String> branchList);


    void insertWkRank(List<Map<String, Object>> rankCalculate, String authorCd, String companyCd, Integer productPowerCd);


    List<String> getDataCol();

    List<String> getWorkTableName();

    void delWork(String tableName);

    List<String> getStoreName(List<String> storeList, String tableName);

    List<Map<String,Object>> getJanClassify(List<Map<String, Object>> janClassCd, String tableName,List<LinkedHashMap<String, Object>> colMap);

    String getAttrName(String col, String tableName);

    List<LinkedHashMap<String,Object>> getClassifyHeader(String tableName);

    String getItemCol(String company, String classCd);
}
