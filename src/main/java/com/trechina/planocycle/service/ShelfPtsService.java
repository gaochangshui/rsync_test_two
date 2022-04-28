package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderSort;
import com.trechina.planocycle.entity.vo.PtsCsvVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ShelfPtsService {
    /**
     * 棚割pts情報の取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfPtsInfo(String companyCd);

    /**
     * 棚割pts情報の保存
     * @param shelfPtsDto
     * @return
     */
    Map<String,Object> setShelfPtsInfo(ShelfPtsDto shelfPtsDto,Integer flg);

    /**
     * pts関連pattern
     * @param shelfPtsJoinPatternDto
     * @return
     */
    Map<String,Object> saveShelfPts(List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    /**
     * 保存pts数据到一時表里
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     */
    void saveWorkPtsData(String companyCd, String authorCd, Integer priorityOrderCd);
    /**
     * 保存pts数据到最終表里
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     */
    void saveFinalPtsData(String companyCd, String authorCd, Integer priorityOrderCd);

    /**
     * 棚pattern関連csv履歴データの取得
     * @param companyCd
     * @return
     */
    Map<String, Object> getHistoryData(String companyCd);

    /**
     * 棚pattern関連ptsのドロップダウンボックスデータ
     * @param companyCd
     * @return
     */
    Map<String, Object> getPtsName(String companyCd);

    /**
     * 棚pattern別pts情報の取得
     * @param companyCd
     * @param rangFlag
     * @param areaList
     * @return
     */
    Map<String, Object> getPtsInfoOfPattern(String companyCd, Integer rangFlag, String areaList);

    /**
     * 棚pattern別pts関連pattern
     * @param shelfPtsJoinPatternDto
     * @return
     */
    Map<String, Object> saveShelfPtsOfPattern(List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);
    /**
     * 棚割pts情報の削除
     * @param
     * @return
     */
     Map<String,Object> delShelfPtsInfo(JSONObject jsonObject);

    /**
     *棚pattern関連ptsの棚/セグメント数を取得
     * @param patternCd
     * @return
     */
      Map<String,Object> getTaiNumTanaNum(Integer patternCd,Integer priorityOrderCd);
    /**
     * 棚pattern関連ptsの詳細の取得
     * @param patternCd
     * @return
     */
      Map<String,Object> getPtsDetailData(Integer patternCd,String companyCd,Integer priorityOrderCd);

    /**
     * 陳列順設定追加
     * @param workPriorityOrderSort
     * @return
     */
    Map<String,Object> setDisplay( List<WorkPriorityOrderSort> workPriorityOrderSort);
    /**
     * 陳列順設定展示
     * @param companyCd
     * @return
     */

    Map<String,Object> getDisplay( String companyCd,Integer priorityOrderCd);

    /**
     * csvファイルのダウンロード
     * @param ptsCsvVO
     * @param response
     * @throws IOException
     */
    void downloadPtsCsv(PtsCsvVO ptsCsvVO, HttpServletResponse response) throws IOException;
}
