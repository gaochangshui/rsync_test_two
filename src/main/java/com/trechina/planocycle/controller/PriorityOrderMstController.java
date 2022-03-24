package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDownDto;
import com.trechina.planocycle.entity.vo.PriorityOrderMstVO;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;
import com.trechina.planocycle.service.PriorityOrderMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
@RestController
@RequestMapping("/planoCycle/PriorityOrderMst")
public class PriorityOrderMstController {
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Value("${ptsDownPath}")
    String ptsDownPath;

    /**
     * 获取优先顺位表list
     * @param companyCd
     * @return
     */
    @GetMapping("/getPriorityOrderList")
    public Map<String,Object> getPriorityOrderList(String companyCd){
        return priorityOrderMstService.getPriorityOrderList(companyCd);
    }
    /**
     * 保存优先顺位表参数
     * @param priorityOrderMstDto
     * @return
     */
    @PostMapping("/setPriorityOrderMst")
    public Map<String,Object> setPriorityOrderMst(@RequestBody PriorityOrderMstDto priorityOrderMstDto){
        return priorityOrderMstService.setPriorityOrderMst(priorityOrderMstDto);
    }


    /**
     * 查询登陆者所在企业有没有优先顺位表
     * @return
     */
    @GetMapping("/getPriorityOrderExistsFlg")
    public Map<String,Object> getPriorityOrderExistsFlg(String companyCd){
        return priorityOrderMstService.getPriorityOrderExistsFlg(companyCd);
    }

    /**
     * 优先顺位表获取rank属性的动态列
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @GetMapping("/getRankAttr")
    public Map<String,Object> getRankAttr(String companyCd,Integer productPowerCd){
        return priorityOrderMstService.getRankAttr(companyCd,productPowerCd);
    }

    /**
     * 根据优先顺位表cd获取商品力点数表cd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getProductPowerCdForPriority")
    public Map<String,Object> getProductPowerCdForPriority(Integer priorityOrderCd){
        return priorityOrderMstService.getProductPowerCdForPriority(priorityOrderCd);
    }

    /**
     * pts下载
     * @param priorityOrderPtsDownDto
     * @param response
     * @return
     */
    @PostMapping("/getPtsFileDownLoad")
    public Map<String,Object> getPtsFileDownLoad(@RequestBody PriorityOrderPtsDownDto priorityOrderPtsDownDto, HttpServletResponse response) {
        return priorityOrderMstService.getPtsFileDownLoad(priorityOrderPtsDownDto,response,ptsDownPath);
    }

    /**
     * 删除所有优先顺位表信息
     * @param primaryKeyVO
     * @return
     */
    @DeleteMapping("/delPriorityOrderAllInfo")
    public Map<String,Object> delPriorityOrderAllInfo(@RequestBody PriorityOrderPrimaryKeyVO primaryKeyVO){
        return priorityOrderMstService.delPriorityOrderAllInfo(primaryKeyVO);
    }

    @GetMapping("/preCalculation")
    public Map<String, Object> preCalculation(String companyCd, Long patternCd,Integer priorityOrderCd ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return priorityOrderMstService.preCalculation(companyCd, patternCd,priorityOrderCd);
    }

    /**
     * 自动计算
     * @return
     */
    // TODO: 2200866
    @GetMapping("/autoCalculation")
    public Map<String,Object> autoCalculation(String companyCd,Integer priorityOrderCd){
        return priorityOrderMstService.autoCalculation(companyCd,priorityOrderCd);
    }

    /**
     * 编辑时展示所有信息
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("getPriorityOrderAll")
    public Map<String,Object> getPriorityOrderAll(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
       return priorityOrderMstService.getPriorityOrderAll(companyCd,priorityOrderCd);
    }

    /**
     * 最终保存
     * @param primaryKeyVO
     * @return
     */
    @PostMapping("/saveAllWorkPriorityOrder")
    public Map<String,Object> saveAllWorkPriorityOrder(@RequestBody PriorityOrderMstVO primaryKeyVO){
        return priorityOrderMstService.saveAllWorkPriorityOrder(primaryKeyVO);
    }

    /**
     * 编辑时校验pts的名字是否存在
     * @return
     */
    @PostMapping("/checkOrderName")
    public Map<String,Object> checkOrderName(@RequestBody PriorityOrderMstVO priorityOrderMstVO){
        return priorityOrderMstService.checkOrderName(priorityOrderMstVO);
    }

    /**
     * 基本パターン删除
     * @param priorityOrderMstVO
     * @return
     */
    @DeleteMapping("/deletePriorityOrderAll")
    public Map<String,Object> deletePriorityOrderAll(@RequestBody PriorityOrderMstVO priorityOrderMstVO){
        return priorityOrderMstService.deletePriorityOrderAll(priorityOrderMstVO);
    }
}
