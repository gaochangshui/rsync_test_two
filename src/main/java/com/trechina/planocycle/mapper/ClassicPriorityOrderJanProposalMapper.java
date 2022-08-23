package com.trechina.planocycle.mapper;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;
import com.trechina.planocycle.entity.vo.PriorityOrderJanProposalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassicPriorityOrderJanProposalMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteFinalByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") JSONArray jsonArray,@Param("companyCd") String companyCd,@Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderJanProposalVO> selectByPrimaryKey(@Param("companyCd") String companyCd,
                                                        @Param("priorityOrderCd") Integer priorityOrderCd);

    List<PriorityOrderJanProposalVO> selectJanInfoByPrimaryKey(String companyCd, Integer priorityOrderCd, String tableName,
                                                               String janCol, String janNameCol);
    int updateByPrimaryKey(@Param("lists") List<PriorityOrderJanProposal> record);

    /**
     * テンポラリ・テーブルのデータを最終テーブルに保存
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    int insertFinalData(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    void setWorkForFinal(String companyCd, Integer priorityOrderCd,Integer newPriorityOrderCd);

    List<String> existJanNew(List<String> janList, String companyCd, Integer priorityOrderCd);

    List<String> existJanOld(List<String> janList, String companyCd, Integer priorityOrderCd);
}
