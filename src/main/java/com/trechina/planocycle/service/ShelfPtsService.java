package com.trechina.planocycle.service;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import com.trechina.planocycle.entity.dto.ShelfPtsJoinPatternDto;
import com.trechina.planocycle.entity.po.WorkPriorityOrderSort;

import java.util.List;
import java.util.Map;

public interface ShelfPtsService {
    /**
     * 获取棚割pts信息
     * @param companyCd
     * @return
     */
    Map<String,Object> getShelfPtsInfo(String companyCd,Integer rangFlag,String areaList);

    /**
     * 保存棚割pts信息
     * @param shelfPtsDto
     * @return
     */
    Map<String,Object> setShelfPtsInfo(ShelfPtsDto shelfPtsDto,Integer flg);

    /**
     * pts关联pattern
     * @param shelfPtsJoinPatternDto
     * @return
     */
    Map<String,Object> saveShelfPts(List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);

    /**
     * 获取棚pattern关联过的csv履历数据
     * @param companyCd
     * @return
     */
    Map<String, Object> getHistoryData(String companyCd);

    /**
     * 棚pattern关联pts的下拉框数据
     * @param companyCd
     * @return
     */
    Map<String, Object> getPtsName(String companyCd);

    /**
     * 获取棚pattern别的pts信息
     * @param companyCd
     * @param rangFlag
     * @param areaList
     * @return
     */
    Map<String, Object> getPtsInfoOfPattern(String companyCd, Integer rangFlag, String areaList);

    /**
     * 棚pattern别pts关联pattern
     * @param shelfPtsJoinPatternDto
     * @return
     */
    Map<String, Object> saveShelfPtsOfPattern(List<ShelfPtsJoinPatternDto> shelfPtsJoinPatternDto);
    /**
     * 删除棚割pts信息
     * @param
     * @return
     */
     Map<String,Object> delShelfPtsInfo(JSONObject jsonObject);

    /**
     * 获取棚pattern关联的pts的棚/段数
     * @param patternCd
     * @return
     */
      Map<String,Object> getTaiNumTanaNum(Integer patternCd);
    /**
     * 获取棚pattern 关联pts的详细信息
     * @param patternCd
     * @return
     */
      Map<String,Object> getPtsDetailData(Integer patternCd,String companyCd,Integer priorityOrderCd);

    /**
     * 陈列顺设定添加
     * @param workPriorityOrderSort
     * @return
     */
    Map<String,Object> setDisplay( List<WorkPriorityOrderSort> workPriorityOrderSort);
    /**
     * 陈列顺设定展示
     * @param companyCd
     * @return
     */

    Map<String,Object> getDisplay( String companyCd,Integer priorityOrderCd);



}
