package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.StarReadingTableDto;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.entity.vo.CommodityBranchPrimaryKeyVO;
import com.trechina.planocycle.service.ClassicPriorityOrderBranchNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/priority/PriorityOrderBranchNum")
public class ClassicPriorityOrderBranchNumController {
    @Autowired
    private ClassicPriorityOrderBranchNumService priorityOrderBranchNumService;

    /**
     * 必須商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderCommodityMust")
    public Map<String,Object> getPriorityOrderCommodityMust(String companyCd,Integer priorityOrderCd){
        return priorityOrderBranchNumService.getPriorityOrderCommodityMust(companyCd,priorityOrderCd);
    }

    /**
     * 不可商品リストの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("/getPriorityOrderCommodityNot")
    public Map<String,Object> getPriorityOrderCommodityNot(String companyCd,Integer priorityOrderCd){
        return priorityOrderBranchNumService.getPriorityOrderCommodityNot(companyCd,priorityOrderCd);
    }

    /**
     * 保存必須商品リスト
     * @param priorityOrderCommodityMust
     * @return
     */
    @PostMapping("/setPriorityOrderCommodityMust")
    public Map<String,Object> setPriorityOrderCommodityMust(@RequestBody List<PriorityOrderCommodityMust> priorityOrderCommodityMust) {
        return priorityOrderBranchNumService.setPriorityOrderCommodityMust(priorityOrderCommodityMust);
    }

    /**
     * 不可商品リストの保存
     * @param priorityOrderCommodityNot
     * @return
     */
    @PostMapping("/setPriorityOrderCommodityNot")
    public Map<String,Object> setPriorityOrderCommodityNot(@RequestBody List<PriorityOrderCommodityNot> priorityOrderCommodityNot) {
        return priorityOrderBranchNumService.setPriorityOrderCommodityNot(priorityOrderCommodityNot);
    }

    /**
     * 必須リスト
     * @param companyCd
     * @return
     */
    @GetMapping("getPriorityOrderMustList")
    public Map<String,Object> getPriorityOrderMustList(String companyCd, Integer priorityOrderCd) {
        return priorityOrderBranchNumService.getPriorityOrderMustList(companyCd,priorityOrderCd);
    }
    /**
     * リスト不可
     * @param companyCd
     * @return
     */
    @GetMapping("getPriorityOrderNotList")
    public Map<String,Object> getPriorityOrderNotList(String companyCd, Integer priorityOrderCd) {
        return priorityOrderBranchNumService.getPriorityOrderNotList(companyCd,priorityOrderCd);
    }

    /**
     * 詳細が必要です
     * @param companyCd
     * @param priorityOrderCd
     * @param jan
     * @return
     */
    @GetMapping("/getCommodityMustBranchList")
    public Map<String,Object> getCommodityMustBranchList(String companyCd, Integer priorityOrderCd, String jan) {
        return priorityOrderBranchNumService.getCommodityMustBranchList(companyCd, priorityOrderCd, jan);
    }

    /**
     * 詳細不可
     * @param companyCd
     * @param priorityOrderCd
     * @param jan
     * @return
     */
    @GetMapping("/getCommodityNotBranchList")
    public Map<String,Object> getCommodityNotBranchList(String companyCd, Integer priorityOrderCd, String jan) {
        return priorityOrderBranchNumService.getCommodityNotBranchList(companyCd, priorityOrderCd, jan);
    }

    /**
     * 詳細保存不可
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    @PostMapping("/saveCommodityNotBranch")
    public Map<String,Object> saveCommodityNotBranchList(@RequestBody CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO) {
        return priorityOrderBranchNumService.saveCommodityNotBranchList(commodityBranchPrimaryKeyVO.getCompanyCd(),
                commodityBranchPrimaryKeyVO.getPriorityOrderCd(), commodityBranchPrimaryKeyVO.getJan(), commodityBranchPrimaryKeyVO.getCommodityList());
    }

    /**
     * 詳細を保存する必要があります
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    @PostMapping("/saveCommodityMustBranch")
    public Map<String,Object> saveCommodityMustBranchList(@RequestBody CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO) {
        return priorityOrderBranchNumService.saveCommodityMustBranchList(commodityBranchPrimaryKeyVO.getCompanyCd(),
                commodityBranchPrimaryKeyVO.getPriorityOrderCd(), commodityBranchPrimaryKeyVO.getJan(), commodityBranchPrimaryKeyVO.getCommodityList());
    }

    /**
     * 詳細を削除する必要があります
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    @DeleteMapping("/delCommodityMustBranch")
    public Map<String,Object> delCommodityMustBranch(@RequestBody CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO){
        return priorityOrderBranchNumService.delCommodityMustBranch(commodityBranchPrimaryKeyVO);
    }

    /**
     * 詳細削除不可
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    @DeleteMapping("/delCommodityNotBranch")
    public Map<String,Object> delCommodityNotBranch(@RequestBody CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO){
        return priorityOrderBranchNumService.delCommodityNotBranch(commodityBranchPrimaryKeyVO);
    }


    /**
     * 获取星取表
     */
    @PostMapping("getStarReadingTable")
    public Map<String,Object> getStarReadingTable(@RequestBody StarReadingTableDto starReadingTableDto){
        return priorityOrderBranchNumService.getStarReadingTable(starReadingTableDto);
    }


    /**
     * 获取星取表参数信息
     */
    @GetMapping("getStarReadingParam")
    public Map<String,Object> getStarReadingParam(String companyCd,Integer priorityOrderCd){
        return priorityOrderBranchNumService.getStarReadingParam(companyCd,priorityOrderCd);
    }


    /**
     * 星取表信息保存
     */
    @GetMapping("setStarReadingData")
    public Map<String,Object> setStarReadingData(String companyCd,Integer priorityOrderCd){
        return priorityOrderBranchNumService.setStarReadingData(companyCd,priorityOrderCd);
    }
}
