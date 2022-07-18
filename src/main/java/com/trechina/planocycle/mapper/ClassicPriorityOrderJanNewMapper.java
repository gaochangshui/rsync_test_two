package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.DownloadDto;
import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanNew;
import com.trechina.planocycle.entity.po.Jans;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanNewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassicPriorityOrderJanNewMapper {
    int insert(@Param("lists") List<ClassicPriorityOrderJanNew> record);

    int delete(String companyCd, Integer priorityOrderCd);

    List<ClassicPriorityOrderJanNewVO> selectJanNew(String companyCd, Integer priorityOrderCd);

    List<Map<String,Object>> selectJanNewNotExistsMst(String companyCd, Integer priorityOrderCd,String tablename);


    ClassicPriorityOrderJanNewVO selectColName(String companyCd, Integer productPowerNo);

    List<Map<String, Object>> selectJanNewOrAttr(String companyCd, Integer priorityOrderCd);

    void updateBranchNum(Integer priorityOrderCd, String janNew, Integer branchNum);

    void updateBranchNumByList(Integer priorityOrderCd, List<Map<String,Object>> list);

    List<ClassicPriorityOrderJanNew> selectJanNameFromJanNewByCompanyAndCd(String company, Integer priorityOrderCd);

    //最終テーブルに保存
    int setFinalForWork(String companyCd, Integer priorityOrderCd);
    //最終テーブルデータの削除
    int deleteFinal(String companyCd, Integer priorityOrderCd);
    //最終表をテンポラリ・テーブルに戻す
    int setWorkForFinal(String companyCd, Integer priorityOrderCd);

    List<String> selectExistJanNew(String companyCd, Integer priorityOrderCd, List<DownloadDto> newJanList);

    void deleteByJan(String companyCd, Integer priorityOrderCd, List<DownloadDto> newJanList);

    List<ClassicPriorityOrderJanNewVO> getExistOtherMst(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    void deleteJanNew(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    List<Jans> getJanNewMst(@Param("list")List<String> janNews);

    List<Map<String,Object>> getJanNews(Integer priorityOrderCd, List<String> colName,String companyCd);

    List<Map<String,Object>> getErrorMsgJan(String companyCd, Integer priorityOrderCd);

    List<Map<String, Object>> getSimilarity(Map<String, Object> map, String companyCd, Integer priorityOrderCd);
}
