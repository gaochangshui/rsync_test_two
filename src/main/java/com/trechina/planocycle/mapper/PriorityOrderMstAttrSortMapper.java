package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriorityOrderMstAttrSortMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderMstAttrSort> record);


    List<PriorityOrderMstAttrSort> selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);
    List<PriorityOrderMstAttrSort> selectByPrimaryKeyForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<Map<String,Object>> selectZokuseiCol(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);



    //属性リストの取得
    List<PriorityOrderAttrListVo> getAttribute(String companyCd, String classCd);


    //つかむ取列名
    List<String> getColNmforMst(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("priorityOrderCd") Integer priorityOrderCd,
                                @Param("commonTableName") GetCommonPartsDataDto commonTableName);

    int deleteAttrFinal(String companyCd, Integer priorityOrderCd);

    void insertAttrFinal(String companyCd, Integer priorityOrderCd);

    void insertAttrSortFinal(String companyCd, Integer priorityOrderCd);

    void deleteAttrSortFinal(String companyCd, Integer priorityOrderCd);


    List<String> getAttrList(String companyCd, Integer priorityOrderCd);
    List<String> getAttrSortList(String companyCd, Integer priorityOrderCd);
    List<String> getAttrListFinal(String companyCd, Integer priorityOrderCd);


    void setAttrList(String companyCd, Integer priorityOrderCd, List<Integer> attrList);

    void deleteAttrList(String companyCd, Integer priorityOrderCd);

    List<Map<String, Object>> getAttrName(String classCd, String companyCd, List<Integer> attrs);

    List<Map<String, Object>> getAttrDistinct(String classCd, String companyCd, Integer priorityOrderCd
            , String attr, Integer id, Integer width,String zokuseiSortCd,String colName,String colCd);

    void deletFinal(String companyCd, Integer priorityOrderCd);

    void setFinalForWork(String companyCd, Integer priorityOrderCd);

    void delete(String companyCd, Integer priorityOrderCd);

    void setWorkForFinal(String companyCd, Integer priorityOrderCd,Integer newPriorityOrderCd);

    List<Map<String,Object>> getAttrCol(String companyCd, Integer priorityOrderCd,String company,String classCd);
    List<Map<String,Object>> getAttrColForFinal(String companyCd, Integer priorityOrderCd,String company,String classCd);
    List<Map<String,Object>> getAttrColForName(String companyCd, Integer priorityOrderCd,String company,String classCd);

}
