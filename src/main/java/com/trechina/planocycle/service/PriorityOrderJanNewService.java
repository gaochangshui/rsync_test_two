package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface PriorityOrderJanNewService {
    /**
     * つかむ取新規janlist
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanNew(String companyCd,Integer priorityOrderCd, Integer productPowerNo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    /**
     * つかむ取新規jan的名字分クラス
     * @param janNew
     * @return
     *
     */

    Map<String,Object> getPriorityOrderJanNewInfo(String[] janNew,String companyCd, Integer priorityOrderCd,Integer model);

    /**
     *保存新規商品list
     * @param
     * @return
     */
    Map<String,Object> setPriorityOrderJanNew(List<PriorityOrderJanNew> priorityOrderJanNew);


    /**
     * 根据分クラス去商品力点数表抽同クラス商品
     * @param
     * @return
     */
    Map<String, Object> getSimilarity(Map<String,Object> map ) ;
    /**
     * 新規不存在商品詳細情報
     * @param janMstPlanocycleVo
     * @return
     */
    Map<String,Object>setJanNewInfo(@RequestBody List<JanMstPlanocycleVo> janMstPlanocycleVo);
    /**
     * 検索不存在商品詳細情報
     * @param
     * @return
     */
    Map<String,Object>getJanNewInfo(String companyCd);

    List<Map<String, Object>> janSort(List<Map<String, Object>> ptsJanList, List<Map<String, Object>> JanNewList, String rankName);
}
