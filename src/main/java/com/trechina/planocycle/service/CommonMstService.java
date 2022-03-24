package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import com.trechina.planocycle.entity.vo.PtsTaiVo;

import java.util.List;
import java.util.Map;

public interface CommonMstService {

    /**
     * 获取Area Master信息
     * @return
     */
    Map<String,Object> getAreaInfo(String companyCd);

    /**
     * 根据棚名称cd取area
     * @param ShelfNameCd
     * @return
     */
    Map<String,Object> getAreaForShelfName(Integer ShelfNameCd);

    /**
     * 放置商品的通用逻辑，基本和全パターン一样
     * @return
     */
    Map<String, List<PriorityOrderResultDataDto>> commSetJan(Short partitionFlag, Short partitionVal,
                                                             List<PtsTaiVo> taiData, List<PriorityOrderResultDataDto> workPriorityOrderResultData,
                                                             List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations,
                                                             Integer minFace);
}
