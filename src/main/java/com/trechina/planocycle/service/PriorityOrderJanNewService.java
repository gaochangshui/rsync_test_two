package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderJanNewDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface PriorityOrderJanNewService {
    /**
     * つかむ取新规janlist
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderJanNew(String companyCd,Integer priorityOrderCd, Integer productPowerNo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    /**
     * つかむ取新规jan的名字分クラス
     * @param janNew
     * @return
     *
     */

    Map<String,Object> getPriorityOrderJanNewInfo(String[] janNew,String companyCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     *保存新规商品list
     * @param
     * @return
     */
    Map<String,Object> setPriorityOrderJanNew(List<PriorityOrderJanNew> priorityOrderJanNew);


    /**
     * 根据分クラス去商品力点数表抽同クラス商品
     * @param
     * @return
     */
    Map<String, Object> getSimilarity(PriorityOrderJanNewDto priorityOrderJanNewDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    /**
     * 新规不存在商品详细信息
     * @param janMstPlanocycleVo
     * @return
     */
    Map<String,Object>setJanNewInfo(@RequestBody List<JanMstPlanocycleVo> janMstPlanocycleVo);
    /**
     * 查询不存在商品详细信息
     * @param
     * @return
     */
    Map<String,Object>getJanNewInfo(String companyCd);
}
