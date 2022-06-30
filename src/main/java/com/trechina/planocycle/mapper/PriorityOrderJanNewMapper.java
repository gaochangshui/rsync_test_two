package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderJanNewDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import com.trechina.planocycle.entity.vo.PriorityOrderJanNewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderJanNewMapper {
    int insert(@Param("lists") List<PriorityOrderJanNew> record,@Param("authorCd")String authorCd);

    int delete(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);
    int workDelete(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    List<Map<String,Object>> selectJanNew(@Param("companyCd") String companyCd,@Param("priorityOrderCd") Integer priorityOrderCd
            , @Param("commonTableName") GetCommonPartsDataDto commonTableName,@Param("list")List<Map<String,Object>> attrList);

    List<Map<String, Object>> selectJanNewNotExistsMst(String companyCd, Integer priorityOrderCd, String tablename);


    PriorityOrderJanNewVO selectColName(String companyCd, Integer productPowerNo);

    List<Map<String, Object>> selectJanNewOrAttr(String companyCd, Integer priorityOrderCd);

    void updateBranchNum(Integer priorityOrderCd, String janNew, Integer branchNum);

    Integer getJanMstNum( @Param("priorityOrderJanNew") PriorityOrderJanNew priorityOrderJanNew);

    Integer getJanMstPlanocycleNum( @Param("priorityOrderJanNew") PriorityOrderJanNew priorityOrderJanNew);


    List<PriorityOrderJanNewVO> getJanNameClassify(@Param("janNew") String [] janNew);
    List<PriorityOrderJanNewVO> getJanNameClass(@Param("janNew") String [] janNew,@Param("companyCd")String companyCd);

    List<Map<String,Object>> getProductPowerData(@Param("productPowerCd")Integer productPowerCd
            , @Param("attrMap") List<Map<String,Object>> attrMap,@Param("authorCd")String authorCd
            ,@Param("tableName") String tableName,@Param("priorityOrderCd")Integer priorityOrderCd,long shelfPatternCd);

    PriorityOrderJanNewDto getProductForWork( @Param("item") PriorityOrderJanNewDto priorityOrderJanNewVO,@Param("companyCd")String companyCd);

    List<PriorityOrderJanNewDto> getJanNew(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);

    int deleteByAuthorCd(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);
    int insertBySelect(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);



    //商品の基本情報は存在しません
    int setJanNewInfo(@Param("list") List<JanMstPlanocycleVo> janMstPlanocycleVo,@Param("companyCd")String companyCd);
    //ワークシート情報の削除
    int deleteJanNewInfo(@Param("companyCd")String companyCd);


    //ワークシート情報の表示
    List<JanMstPlanocycleVo> getJanNewInfo(@Param("companyCd")String companyCd);

    List<Map<String,Object>> getDynamicJanNameClassify(@Param("tableName") String tableName, @Param("col") List<Map<String,Object>> col, @Param("janNew") String [] janNew);
}
