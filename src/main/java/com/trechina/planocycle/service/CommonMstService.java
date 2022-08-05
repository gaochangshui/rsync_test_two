package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.CommonPartsDto;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import org.springframework.transaction.annotation.Transactional;

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
                                           Integer minFace, List<ZokuseiMst> zokuseiMsts, List<Integer> allCdList,
                                           List<Map<String, Object>> restrictResult, List<Integer> attrList, String aud,
                                           GetCommonPartsDataDto commonTableName, Short partitionVal, Short topPartitionVal,
                                           Integer tanaWidthCheck, List<Map<String, Object>> tanaList, List<Map<String, Object>> relationMap,
                                           int isReOrder);

    List<WorkPriorityOrderResultDataDto> calculateTanaPosition(List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData, int isReOrder);

    CommonPartsDto getCommonPartsData(Integer productPowerCd, String companyCd);

    CommonPartsDto getPriorityCommonPartsData(Integer priorityOrderCd, String companyCd);
}
