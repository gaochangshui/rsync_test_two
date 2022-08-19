package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.WorkPriorityOrderResultData;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface WorkPriorityOrderResultDataMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("janCd") String janCd);

    int insert(WorkPriorityOrderResultData record);

    int insertSelective(WorkPriorityOrderResultData record);

    WorkPriorityOrderResultData selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("janCd") String janCd);

    List<WorkPriorityOrderResultDataDto> selectByAuthorCd(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd")Integer priorityOrderCd);

    int updateByPrimaryKeySelective(WorkPriorityOrderResultData record);

    int updateByPrimaryKey(WorkPriorityOrderResultData record);

    int setResultDataList(@Param("list") List<ProductPowerDataDto> productPowerDataDtoList, @Param("companyCd") String companyCd,
                          @Param("authorCd") String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int delResultData(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("priorityOrderCd") Integer priorityOrderCd);

    String getResultDataList(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int update(@Param("list") List<WorkPriorityOrderResultData> list,@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int updateFace(@Param("list") List<WorkPriorityOrderResultData> list,@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    List<WorkPriorityOrderResultData>  getResultDatas(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    //再計算rank
    List<WorkPriorityOrderResultData> getReorder(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("productPowerCd")Integer productPowerCd,
                                                 @Param("priorityOrderCd")Integer priorityOrderCd, @Param("commonTableName")GetCommonPartsDataDto getCommonPartsDataDto
                                    , @Param("sortName1")String sortName1, @Param("sortName2")String sortName2);

    List<WorkPriorityOrderResultData> getReorderByJan(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("productPowerCd")Integer productPowerCd,
                                                 @Param("priorityOrderCd")Integer priorityOrderCd, @Param("commonTableName")GetCommonPartsDataDto getCommonPartsDataDto
            , @Param("sortName1")String sortName1, @Param("sortName2")String sortName2, List<PriorityOrderResultDataDto> backupJan);

    //属性ランキングの取得
    List<PriorityOrderJanNewDto> getAttrRank(@Param("companyCd")String companyCd, @Param("authorCd")String authorCd, @Param("priorityOrderCd")Integer priorityOrderCd, @Param("sortName1")String sortName1,
                                             @Param("sortName2")String sortName2);

    List<PriorityOrderResultDataDto>  getResultJans(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd")Integer priorityOrderCd);
    //最終rankの一括更新
     int  setSortRank(@Param("list")List<Map<String,Object>> list,@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd")Integer priorityOrderCd);

    /**
     * janの配置位置情報を一括更新
     * @param list
     * @return
     */
    int updateTaiTanaBatch(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd") String authorCd, @Param("list") List<PriorityOrderResultDataDto> list);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd")String authorCd
    ,@Param("newPriorityOrderCd")Integer newPriorityOrderCd);

    //ユーザ編集faceNum
    int updateFaceNum(@Param("list") List<PriorityOrderRestrictJanDto> list,@Param("ptsCd")Integer ptsCd);

    List<Map<String,Object>> getProductReorder(String companyCd, String authorCd, Integer productPowerCd
            , Integer priorityOrderCd,String tableName, List<ZokuseiMst> attrList
            , List<Integer> allCdList);

    Integer getPtsCd(Integer priorityOrderCd);

    List<Map<String, Object>> getNewJanProductReorder(String companyCd, String authorCd
            , Integer productPowerCd, Integer priorityOrderCd, String tableName, List<ZokuseiMst> attrList, List<Integer> allCdList);

    void setRank(Set<Map<String,Object>> list, String companyCd, String authorCd, Integer priorityOrderCd);
}