package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.ProductPowerDataDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderResultData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkPriorityOrderResultDataMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("janCd") String janCd);

    int insert(WorkPriorityOrderResultData record);

    int insertSelective(WorkPriorityOrderResultData record);

    WorkPriorityOrderResultData selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("janCd") String janCd);

    int updateByPrimaryKeySelective(WorkPriorityOrderResultData record);

    int updateByPrimaryKey(WorkPriorityOrderResultData record);

    int setResultDataList(@Param("list") List<ProductPowerDataDto> productPowerDataDtoList, @Param("restrictCd") Long restrictCd, @Param("companyCd") String companyCd,
                          @Param("authorCd") String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int delResultData(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("priorityOrderCd") Integer priorityOrderCd);

    String getResultDataList(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int update(@Param("list") List<WorkPriorityOrderResultData> list,@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int updateFace(@Param("list") List<WorkPriorityOrderResultData> list,@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);

    List<WorkPriorityOrderResultData>  getResultDatas(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    //重新计算rank
    List<WorkPriorityOrderResultData> getReorder(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("sortName1")String sortName1,
                                                 @Param("sortName2")String sortName2);

    //获取属性排名
    List<WorkPriorityOrderResultData> getAttrRank(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd,@Param("sortName1")String sortName1,
                                                 @Param("sortName2")String sortName2);

    List<PriorityOrderResultDataDto>  getResultJans(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd);

    /**
     * 批量更新jan的放置位置信息
     * @param list
     * @return
     */
    int updateTaiTanaBatch(@Param("companyCd") String companyCd, @Param("authorCd") String authorCd, @Param("list") List<PriorityOrderResultDataDto> list);
}