package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanCard;
import com.trechina.planocycle.entity.vo.PriorityOrderJanCardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderJanCardMapper {

    int workDelete(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);

    List<PriorityOrderJanCardVO> selectJanCard(String companyCd, Integer priorityOrderCd, GetCommonPartsDataDto commonTableName);

    int insert(@Param("lists") List<PriorityOrderJanCard> record,@Param("authorCd")String authorCd);
    //最終テーブルデータをテンポラリ・テーブルに移動
    int setWorkForFinal(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd
    ,@Param("newPriorityOrderCd")Integer newPriorityOrderCd);

    int deleteByAuthorCd(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);

    int insertBySelect(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd,@Param("authorCd")String authorCd);

}
