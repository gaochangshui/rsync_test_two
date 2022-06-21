package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.entity.po.Zokusei;
import com.trechina.planocycle.entity.vo.WorkPriorityOrderMstEditVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkPriorityOrderMstMapper {
    int insert(WorkPriorityOrderMst record);

    int insertSelective(WorkPriorityOrderMst record);

    int deleteByAuthorCd(String companyCd, String authorCd,Integer priorityOrderCd);
    Integer getProductPowerCd(@Param("companyCd")String companyCd,@Param("priorityOrderCd")Integer priorityOrderCd);
    WorkPriorityOrderMst selectByAuthorCd(@Param("companyCd") String companyCd,@Param("authorCd") String authorCd,  @Param("priorityOrderCd") Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);

    WorkPriorityOrderMstEditVo getWorkPriorityOrderMst(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd, @Param("authorCd")String authorCd);
    //跟据shelfPatternCd找name
    Integer getShelfName(@Param("shelfPattern")Integer shelfPattern);

    List<Map<String, Object>> selectByAttr(Integer shelfPatternCd, String janInfoTB, List<Zokusei> zokuseiList, List<Integer> cdList);
}