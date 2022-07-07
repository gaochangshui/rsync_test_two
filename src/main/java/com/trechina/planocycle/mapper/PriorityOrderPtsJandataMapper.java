package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.DownloadDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderPtsJandataMapper {

    int insert(@Param("list") List<DownloadDto> datas);

    int insertNewJan(@Param("list") List<DownloadDto> datas);

    int delete(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);


    Integer updatePtsJanRank(String companyCd, Integer priorityOrderCd, List<DownloadDto> ptsJanList);

    List<DownloadDto> selectJanRank(String companyCd, Integer priorityOrderCd, List<String> sortList);

    int updateRankUpd(List<DownloadDto> ptsJanList, String attr1, String attr2, Integer priorityOrderCd);

    int updateAttr(List<DownloadDto> ptsJanList, Integer priorityOrderCd, String attr1, String attr2);

    List<DownloadDto> selectCutJan(String companyCd, Integer priorityOrderCd, List<DownloadDto> ptsJanList);

    List<DownloadDto> selectNewJan(String companyCd, Integer priorityOrderCd, List<DownloadDto> ptsJanList);

    void updateCutByJan(String companyCd, Integer priorityOrderCd, List<DownloadDto> cutJanList);
}
