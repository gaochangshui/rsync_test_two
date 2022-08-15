package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderJanNewMapper {
    int insert(@Param("lists") List<PriorityOrderJanNew> record, @Param("authorCd") String authorCd);

    int workDelete(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String, Object>> selectJanNew(String companyCd, Integer priorityOrderCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable, List<Map<String, Object>> attrName);


    List<Map<String, Object>> getProductPowerData(Integer priorityOrderCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable, List<Map<String, Object>> attrName, Long shelfPatternCd
            , Integer productPowerCd, Map<String, Object> mapAttr);

    List<Map<String, Object>> getProductPowerDataList(Integer priorityOrderCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable, List<Map<String, Object>> attrName, Long shelfPatternCd
            , Integer productPowerCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd") String authorCd
    ,@Param("newPriorityOrderCd")Integer newPriorityOrderCd);

    int deleteByAuthorCd(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd") String authorCd);

    int insertBySelect(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd") String authorCd);


    //商品の基本情報は存在しません
    int setJanNewInfo(@Param("list") List<JanMstPlanocycleVo> janMstPlanocycleVo, @Param("companyCd") String companyCd);

    //ワークシート情報の削除
    int deleteJanNewInfo(@Param("companyCd") String companyCd);


    //ワークシート情報の表示
    List<JanMstPlanocycleVo> getJanNewInfo(@Param("companyCd") String companyCd);

    List<Map<String, Object>> getDynamicJanNameClassify(Integer priorityOrderCd, List<ZokuseiMst> attrList
            , List<Integer> allCdList, String proInfoTable, List<Map<String, Object>> attrName, String[] janCd, Integer model);

    List<String> getErrorMsgJan(String companyCd, Integer priorityOrderCd);
}
