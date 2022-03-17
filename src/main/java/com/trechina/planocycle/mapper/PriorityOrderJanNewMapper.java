package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderJanNewDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
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

    List<PriorityOrderJanNewVO> selectJanNew(String companyCd, Integer priorityOrderCd);

    List<Map<String, Object>> selectJanNewNotExistsMst(String companyCd, Integer priorityOrderCd, String tablename);


    PriorityOrderJanNewVO selectColName(String companyCd, Integer productPowerNo);

    List<Map<String, Object>> selectJanNewOrAttr(String companyCd, Integer priorityOrderCd);

    void updateBranchNum(Integer priorityOrderCd, String janNew, Integer branchNum);

    Integer getJanMstNum( @Param("priorityOrderJanNew") PriorityOrderJanNew priorityOrderJanNew);

    Integer getJanMstPlanocycleNum( @Param("priorityOrderJanNew") PriorityOrderJanNew priorityOrderJanNew);


    List<PriorityOrderJanNewVO> getJanNameClassify(@Param("janNew") String [] janNew);

    List<PriorityOrderJanNewDto> getProductPowerData(@Param("productPowerCd")Integer productPowerCd, @Param("item") PriorityOrderJanNewDto priorityOrderJanNewVO);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);

}
