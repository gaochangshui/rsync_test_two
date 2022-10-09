package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.ShelfPtsDataDto;
import com.trechina.planocycle.entity.dto.ShelfPtsHeaderDto;
import com.trechina.planocycle.entity.vo.PriorityOrderCatePakVO;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface ClassicPriorityOrderMstService {

    /**
     * 優先順位テーブルlistの取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getPriorityOrderList(String companyCd);

    /**
     * 優先順位テーブルパラメータの保存
     * @param priorityOrderMstDto
     * @return
     */
    Map<String,Object> setPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto) throws ExecutionException, InterruptedException;

    @Transactional(rollbackFor = Exception.class)
    void setPriorityOrderMstAndCalc(PriorityOrderMstDto priorityOrderMstDto, String authorCd);

    /**
     * この企業に優先順位テーブルがあるかどうかのログインを取得します。
     * @return
     */
    Map<String,Object> getPriorityOrderExistsFlg();

    /**
     * 優先順位テーブルrank属性の動的列の取得
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    Map<String,Object> getRankAttr(String companyCd,Integer productPowerCd);

    /**
     * 優先順位テーブルcdに基づく商品力点数テーブルcdの取得
     * @param priorityOrderCd
     * @return
     */
    Map<String, Object> getProductPowerCdForPriority(Integer priorityOrderCd);

    /**
     * すべての優先順位テーブル情報を削除
     * @param primaryKeyVO
     * @return
     */
    Map<String, Object> delPriorityOrderAllInfo(PriorityOrderPrimaryKeyVO primaryKeyVO);


    @Transactional(rollbackFor = Exception.class)
    Map<String, Object> setWorkPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto, String authorCd);

    void generateNewPtsData(String taskID, String companyCd, Integer priorityOrderCd, Integer ptsVersion);

    Map<String, Object> downloadPts(String taskId, String companyCd, Integer priorityOrderCd, Integer newCutFlg, Integer ptsVersion, HttpServletResponse response) throws IOException;

    Map<String, Object> downloadPtsTask(String taskId, String companyCd, Integer priorityOrderCd, Integer newCutFlg,
                                               Integer ptsVersion, HttpServletResponse response);

    void packagePtsZip(String taskId, HttpServletResponse response) throws IOException;

    Map<String, Object> generatePtsTask(String taskId, String companyCd, Integer priorityOrderCd, Integer newCutFlg,
                                        Integer ptsVersion, HttpServletResponse response);

    Map<String, Object> getAttrInfo(String companyCd, Integer priorityOrderCd);

    void priorityOrderDataForExcel(PriorityOrderMstDto priorityOrderMstDto,HttpServletResponse response);

    Map<String, List<Map<String, Object>>> doNewOldPtsCompare(String taskID, Map<String, List<Map<String, Object>>> ptsJanDtoListByGroup,
                                                              List<Map<String, Object>> resultDataList, List<Map<String, Object>> ptsSkuNum,
                                                              ShelfPtsDataDto pattern, ShelfPtsHeaderDto shelfPtsHeaderDto,
                                                              Integer ptsVersion, List<PriorityOrderCatePakVO> catePakList, String companyCd, Map<String, Object> branch, List<Map<String, Object>> commodityMustJans, Integer priorityOrderCd,
                                                              List<Map<String, Object>> commodityNotJans, Map<String, String> janReplaceMap, List<Map<String, Object>> ptsJanDtoList, List<String> patternBranchCd);

    void pluralJan(Map<String, List<Map<String, Object>>> newPtsJanMap, Integer priorityOrderCd, ShelfPtsDataDto shelfPtsDataDto,
                   String branchCd, Integer ptsVersion, String companyCd, List<Map<String, Object>> deleteList,
                   List<Map<String, Object>> newList, String fileName, List<String> branchList);

    void saveJanShelfNameCd(List<Map<String, Object>> newJanList, Integer shelfNameCd, Integer priorityOrderCd,
                            Integer patternCd, List<String> branchList);
}
