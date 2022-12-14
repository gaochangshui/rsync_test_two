package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderDataService {
    /**
     * 初期取得優先順位テーブルデータ
     *
     * @param priorityOrderDataDto
     * @return
     */
    Map<String, Object> getPriorityOrderData(PriorityOrderDataDto priorityOrderDataDto);

    Map<String, String> checkIsJanNew(List<String> janList, String company, Integer priorityOrderCd, String tableName);

    /**
     * ptsアップロード後のソート+優先順位表反応ボタン抽出データ
     *
     * @param company
     * @param priorityOrderCd
     * @return
     */
    Map<String, Object> getUploadPriorityOrderData(String company, Integer priorityOrderCd);

    @Transactional(rollbackFor = Exception.class)
    Map<String, Object> uploadPriorityOrderData(String taiCd, String tanaCd, MultipartFile file, String company, Integer priorityOrderCd,
                                                String attrStr);

    void doJanCut(List<DownloadDto> cutJanList, String company, Integer priorityOrderCd);

    Map<String, Object> doJanNew(List<DownloadDto> newJanList, String company, Integer priorityOrderCd, String taiCd, String tanaCd,
                                 String attrList, List<PriorityOrderAttributeClassify> classifyList, List<PriorityOrderMstAttrSortDto> attrSorts);

    /**
     * rank属性ソート+優先順位表反応ボタン抽出データ
     *
     * @param colNameList
     * @return
     */
    Map<String, Object> getPriorityOrderDataUpd(List<String> colNameList, Integer priorityOrderCd, String companyCd, Integer delFlg);

    Map<String, Object> getPriorityOrderListInfo(String companyCd, Integer priorityOrderCd, List<String> colNameList);

    List<Map<String, Object>> calRank(List<Map<String, Object>> result, List<String> colNameList);

    /**
     * 属性列名の名前を取得
     *
     * @param enterpriseAxisDto
     * @return
     */
    Map<String, Object> getAttrName(EnterpriseAxisDto enterpriseAxisDto);

    void downloadForCsv(DownloadSortDto downloadDto, HttpServletResponse response) throws IOException;

    Map<String, Object> getPriorityOrderDataForDb(String[] jans, Map<String, String> attrSortMap);

    Map<String, Object> getPatternAndName(Integer productPowerCd);

    /**
     * 取得優先順位テーブルデータの編集
     *
     * @param priorityOrderDataDto
     * @return
     */
    Map<String, Object> editPriorityOrderData(PriorityOrderDataDto priorityOrderDataDto);

    void setPtsClassify(List<String> colNameList, String shelfPatternCd, String companyCd, Integer priorityOrderCd);

    Map<String, Object> getBranchNum(List<Map<String, Object>> map);

     void deleteWorkData(String companyCd, Integer newPriorityOrderCd);

    Map<String, Object> getPatternCompare(String companyCd, Integer priorityOrderCd);

    Map<String, Object> getAttrCompare(String companyCd, Integer priorityOrderCd,String attrList);

    Map<String, Object> getPriorityOrderNewPts(String companyCd, Integer priorityOrderCd, Integer shelfPatternCd,Integer flag);

}
