package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.CommonPartsDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import com.trechina.planocycle.entity.vo.PtsTaiVo;

import java.util.List;
import java.util.Map;

public interface CommonMstService {

    /**
     * Area Master情報の取得
     * @return
     */
    Map<String,Object> getAreaInfo(String companyCd);

    /**
     * 棚名cdよりareaを取る
     * @param ShelfNameCd
     * @return
     */
    Map<String,Object> getAreaForShelfName(Integer ShelfNameCd);

    /**
     * 商品を置く一般的なロジックは、基本的には全パタンと同じです
     * @return
     */
    Map<String, List<PriorityOrderResultDataDto>> commSetJan(Short partitionFlag, Short partitionVal,
                                                             List<PtsTaiVo> taiData, List<PriorityOrderResultDataDto> workPriorityOrderResultData,
                                                             List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations,
                                                             Integer minFace);

    List<WorkPriorityOrderResultDataDto> calculateTanaPosition(List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData);

    CommonPartsDto getCommonPartsData(Integer productPowerCd, String companyCd);

    CommonPartsDto getPriorityCommonPartsData(Integer priorityOrderCd, String companyCd);
}
