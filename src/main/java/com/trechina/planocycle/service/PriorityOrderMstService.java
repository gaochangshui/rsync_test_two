package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDownDto;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface PriorityOrderMstService {

    /**
     * 获取优先顺位表list
     * @param companyCd
     * @return
     */
    Map<String,Object> getPriorityOrderList(String companyCd);

    /**
     * 保存优先顺位表参数
     * @param priorityOrderMstDto
     * @return
     */
    Map<String,Object> setPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto);
    /**
     * 读写priorityorderData
     * @param priorityOrderMstDto
     * @param res
     * @param wirteReadFlag
     * @return
     */
    Map<String,Object> priorityDataWRFlag(PriorityOrderMstDto priorityOrderMstDto, String[] res, String wirteReadFlag);

    /**
     * 获取登录这所在企业是否有优先顺位表
     * @return
     */
    Map<String,Object> getPriorityOrderExistsFlg();

    /**
     * 优先顺位表获取rank属性的动态列
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    Map<String,Object> getRankAttr(String companyCd,Integer productPowerCd);

    /**
     * 获取pts文件下载
     * @param priorityOrderPtsDownDto
     * @param response
     * @return
     */
    Map<String, Object> getPtsFileDownLoad(PriorityOrderPtsDownDto priorityOrderPtsDownDto, HttpServletResponse response,String ptsDownPath);

    /**
     * 根据优先顺位表cd获取商品力点数表cd
     * @param priorityOrderCd
     * @return
     */
    Map<String, Object> getProductPowerCdForPriority(Integer priorityOrderCd);

    /**
     * 删除所有优先顺位表信息
     * @param primaryKeyVO
     * @return
     */
    Map<String, Object> delPriorityOrderAllInfo(PriorityOrderPrimaryKeyVO primaryKeyVO);

    /**
     * 根据productpowercd查询关联的优先顺位表cd
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    String selPriorityOrderCdForProdCd(String companyCd,Integer productPowerCd);
}
