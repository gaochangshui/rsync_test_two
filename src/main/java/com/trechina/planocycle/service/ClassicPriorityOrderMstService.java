package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDownDto;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

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
    Map<String,Object> setPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto);
    /**
     * リードライトpriorityorderData
     * @param priorityOrderMstDto
     * @param res
     * @param wirteReadFlag
     * @return
     */
    Map<String,Object> priorityDataWRFlag(PriorityOrderMstDto priorityOrderMstDto, String[] res, String wirteReadFlag);

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
     * ptsファイルのダウンロードを取得する
     * @param priorityOrderPtsDownDto
     * @param response
     * @return
     */
    Map<String, Object> getPtsFileDownLoad(PriorityOrderPtsDownDto priorityOrderPtsDownDto, HttpServletResponse response,String ptsDownPath);

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

    /**
     * productpowercdによる関連優先順位テーブルcdのクエリ
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    String selPriorityOrderCdForProdCd(String companyCd,Integer productPowerCd);

    Map<String, Object> setWorkPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto);


    Map<String, Object> downloadPts(String taskId, String companyCd, Integer priorityOrderCd, Integer newCutFlg, Integer ptsVersion, HttpServletResponse response) throws IOException;
}
