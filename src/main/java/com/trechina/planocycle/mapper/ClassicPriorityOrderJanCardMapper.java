package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.DownloadDto;
import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanCard;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanCardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderJanCardMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<ClassicPriorityOrderJanCardVO> selectJanCard(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<ClassicPriorityOrderJanCard> record);

    int setFinalForWork(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int deleteFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<String> selectExistJanCut(String companyCd, Integer priorityOrderCd, List<DownloadDto> cutJanList);

    void deleteByJan(String companyCd, Integer priorityOrderCd, List<DownloadDto> cutJanList);

    List<String> existJan(List<String> jan, String companyCd, Integer priorityOrderCd);

    void setDelJanList(@Param("lists") List<Map> delJanList, @Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<String> checkJanNew(@Param("list") List<ClassicPriorityOrderJanCard> priorityOrderJanCard);

    List<String> checkJanReplace(@Param("list") List<ClassicPriorityOrderJanCard> priorityOrderJanCard);

    List<String> checkJanMust(@Param("list") List<ClassicPriorityOrderJanCard> priorityOrderJanCard);

    List<String> checkJanProposal(List<ClassicPriorityOrderJanCard> priorityOrderJanCard);

    List<String> getExistOtherMst(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<String> checkJanReplaceOld(List<ClassicPriorityOrderJanCard> priorityOrderJanCard);

    List<String> checkJanProposalOld(List<ClassicPriorityOrderJanCard> priorityOrderJanCard);

}
