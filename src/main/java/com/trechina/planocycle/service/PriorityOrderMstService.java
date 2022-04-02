package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.PriorityOrderMstVO;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface PriorityOrderMstService {

    /**
     * 获取优先顺位表list
     * @param companyCd
     * @return
     */
    Map<String,Object> getPriorityOrderList(String companyCd);


    /**
     * 获取登录这所在企业是否有优先顺位表
     * @return
     */
    Map<String,Object> getPriorityOrderExistsFlg(String companyCd);



    /**
     * 根据优先顺位表cd获取商品力点数表cd
     * @param priorityOrderCd
     * @return
     */
    Map<String, Object> getProductPowerCdForPriority(Integer priorityOrderCd);


    /**
     * 根据productpowercd查询关联的优先顺位表cd
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    String selPriorityOrderCdForProdCd(String companyCd,Integer productPowerCd);

    /**
     * S自动计算-Step1
     * @param companyCd
     * @param patternCd
     * @param priorityOrderCd
     * @return
     */
    Map<String, Object> preCalculation(String companyCd, Long patternCd,Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    /**
     * 自动计算
     * @return
     */
    Map<String, Object> autoCalculation(String companyCd,Integer priorityOrderCd,Integer partition);

    /**
     * 重新计算rank排序
     * @param companyCd
     * @return
     */
    Map<String, Object>getReorder(String companyCd,Integer priorityOrderCd,Integer productPowerCd);

    /**
     * 新规时清空对应临时表所有信息
     * @param companyCd
     * @return
     */
    void deleteWorkTable(String companyCd,Integer priorityOrderCd);

    Map<String,Object> getFaceKeisanForCgi(String[]array,String companyCd,Integer shelfPatternNo,String authorCd);


    @Transactional(rollbackFor = Exception.class)
    Map<String, Object> saveAllWorkPriorityOrder(PriorityOrderMstVO primaryKeyVO);

    /**
     * 编辑时展示所有信息
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderAll(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    Map<String, Object> checkOrderName(PriorityOrderMstVO priorityOrderMstVO);

    Map<String,Object> deletePriorityOrderAll( PriorityOrderMstVO priorityOrderMstVO);

    Map<String,Object> getVariousMst(String companyCd,Integer priorityOrderCd,Integer flag) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
