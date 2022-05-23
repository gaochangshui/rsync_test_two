package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.DownloadSortDto;
import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.entity.dto.PriorityOrderDataDto;
import com.trechina.planocycle.service.ClassicPriorityOrderDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycle/priority/PriorityOrderData")
public class ClassicPriorityOrderDataController {

    @Autowired
    private ClassicPriorityOrderDataService priorityOrderDataService;

    /**
     * 获取优先顺位表数据
     * @param priorityOrderDataForCgiDto
     * @return
     */
    @PostMapping("/getPriorityOrderData")
    public Map<String,Object> getPriorityOrderData(@RequestBody PriorityOrderDataDto priorityOrderDataDto){
        return priorityOrderDataService.getPriorityOrderData(priorityOrderDataDto);
    }

    /**
     * rank属性排序+优先顺位表反应按钮抽出数据
     * @param colNameList
     * @return
     */
    @GetMapping("/getPriorityOrderDataUpd")
    public Map<String,Object> getPriorityOrderDataUpd(@RequestParam List<String> colNameList,Integer priorityOrderCd,Integer delFlg,String companyCd){
        return priorityOrderDataService.getPriorityOrderDataUpd(colNameList,priorityOrderCd,delFlg,companyCd);
    }

    /**
     * pts上传后的排序+优先顺位表反应按钮抽出数据
     * @return
     */
    @GetMapping("/getUploadPriorityOrderData")
    public Map<String,Object> getUploadPriorityOrderData(){
        return priorityOrderDataService.getUploadPriorityOrderData();
    }

    /**
     * 获取属性列名的名称
     * @param enterpriseAxisDto
     * @return
     */
    @PostMapping("/getAttrName")
    public Map<String,Object> getAttrName(@RequestBody EnterpriseAxisDto enterpriseAxisDto){
        return priorityOrderDataService.getAttrName(enterpriseAxisDto);
    }


    /**
     * @param attrList eg: {company_cd}_{class}_{header_sys(column 3)}
     * @return
     */
    @PostMapping("/uploadPriorityOrderData")
    public Map<String,Object> uploadPriorityOrderData(@RequestParam("file") MultipartFile file,
          @RequestParam("mode") String mode,@RequestParam("company") String company,
          @RequestParam("attrList") String attrList,@RequestParam("taiCd") String taiCd,
                                                      @RequestParam("tanaCd") String tanaCd,
                                                      @RequestParam("priorityOrderCd") Integer priorityOrderCd,
                                                      @RequestParam("productPowerCd")Integer productPowerCd){
        return priorityOrderDataService.uploadPriorityOrderData(file, mode,company, taiCd, tanaCd, priorityOrderCd, attrList,productPowerCd);
    }

    @PostMapping("/downloadForCsv")
    public void downloadForCsv(@RequestBody DownloadSortDto downloadDto, HttpServletResponse response) throws IOException {
       priorityOrderDataService.downloadForCsv(downloadDto,response);
    }


    @GetMapping("getPatternAndName")
    public Map<String,Object> getPatternAndName(Integer productPowerCd) {
       return priorityOrderDataService.getPatternAndName(productPowerCd);
    }

    @GetMapping("/getPriorityOrderListInfo")
    Map<String,Object> getPriorityOrderListInfo(String companyCd,Integer priorityOrderCd){
        return priorityOrderDataService.getPriorityOrderListInfo(companyCd,priorityOrderCd);
    }

}
