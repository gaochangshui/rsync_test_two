package com.trechina.planocycle.mapper;


import com.trechina.planocycle.entity.po.ShelfPtsData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkPriorityOrderPtsClassifyMapper {


    void setWorkPtsClassify(String companyCd,Integer priorityOrderCd, List<ShelfPtsData> shelfPtsData,List<String> colNameList);

    void deleteWork(String companyCd, Integer priorityOrderCd);

    List<Integer> getJanPtsCd(String companyCd, Integer priorityOrderCd, Map<String, Object> map );
    List<Integer> getAllJanPtsCd(String companyCd, Integer priorityOrderCd, Map<String, Object> map, List<String> colNameList);

    Integer getJanBranchNum(List<Integer> ptsCd,Map<String,Object> map);


    List<Map<String, Object>> getPtsSkuNum(String companyCd, Integer priorityOrderCd, Integer ptsCd, List<String> rankAttr);


    void setSkuForInit(String companyCd, Integer priorityOrderCd);

    int setSkuForCatePakSmall(Map<String,Object> maps, String companyCd, Integer priorityOrderCd);

    void setSkuForCatePakBig(Map<String, Object> maps, String companyCd, Integer priorityOrderCd);

    void deleteFinal(String companyCd, Integer priorityOrderCd);

    void setFinalForWork(String companyCd, Integer priorityOrderCd);

    List<Map<String,Object>> getAttrInfo(String companyCd, Integer priorityOrderCd);

    void setWorkForFinal(String companyCd, Integer priorityOrderCd,Integer newPriorityOrderCd);

}
