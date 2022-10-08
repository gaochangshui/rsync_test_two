package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.CommonPartsDto;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface CommonMstService {

    /**
     * 商品を置く一般的なロジックは、基本的には全パタンと同じです
     * @return
     */
    Map<String, List<PriorityOrderResultDataDto>> commSetJan(Short partitionFlag, Short partitionVal,
                                                             List<PtsTaiVo> taiData, List<PriorityOrderResultDataDto> workPriorityOrderResultData,
                                                             List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations,
                                                             Integer minFace);


    @Transactional(rollbackFor = Exception.class)
    Map<String, Object> commSetJanForShelf(Integer patternCd, String companyCd, Integer priorityOrderCd,
                                           List<ZokuseiMst> zokuseiMsts, List<Integer> allCdList,
                                           List<Map<String, Object>> restrictResult, List<Integer> attrList, String aud,
                                           GetCommonPartsDataDto commonTableName, Short partitionVal, Short topPartitionVal,
                                           Integer tanaWidthCheck, List<Map<String, Object>> tanaList, List<Map<String, Object>> relationMap,
                                           List<PriorityOrderResultDataDto> janResult, List<Map<String, Object>> sizeAndIrisu,
                                           int isReOrder, Integer productPowerCd, List<String> colNmforMst) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List<WorkPriorityOrderResultDataDto> calculateTanaPosition(List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData, int isReOrder);

    CommonPartsDto getCommonPartsData(Integer productPowerCd, String companyCd);

    CommonPartsDto getPriorityCommonPartsData(Integer priorityOrderCd, String companyCd);

    boolean taiTanaEquals(Integer taiCd1, Integer taiCd2, Integer tanaCd1, Integer tanaCd2);

    String getClassifyKey(List<Integer> zokuseiList, Map<String, Object> janMap);

    boolean zokuseiEquals(List<Integer> attrList, Map<String, Object> restrict, Map<String, Object> zokusei);

    List<Map<String, Object>> recalculationArea(List<PriorityOrderResultDataDto> workData, List<Map<String, Object>> tanaList);
}
