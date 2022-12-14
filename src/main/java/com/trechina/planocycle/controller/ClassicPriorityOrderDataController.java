package com.trechina.planocycle.controller;

import com.trechina.planocycle.entity.dto.DownloadSortDto;
import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.entity.dto.PriorityOrderDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderUpdDto;
import com.trechina.planocycle.service.ClassicPriorityOrderDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planoCycleApi/priority/PriorityOrderData")
public class ClassicPriorityOrderDataController {

    @Autowired
    private ClassicPriorityOrderDataService priorityOrderDataService;

    /**
     * 初期取得優先順位テーブルデータ
     *
     * @param priorityOrderDataDto
     * @return
     */
    @PostMapping("/getPriorityOrderData")
    public Map<String, Object> getPriorityOrderData(@RequestBody PriorityOrderDataDto priorityOrderDataDto) {
        return priorityOrderDataService.getPriorityOrderData(priorityOrderDataDto);
    }

    /**
     * 取得優先順位テーブルデータの編集
     *
     * @param priorityOrderDataDto
     * @return
     */
    @PostMapping("/editPriorityOrderData")
    public Map<String, Object> editPriorityOrderData(@RequestBody PriorityOrderDataDto priorityOrderDataDto) {
        return priorityOrderDataService.editPriorityOrderData(priorityOrderDataDto);
    }

    /**
     * rank属性ソート+優先順位表反応ボタン抽出データ
     *
     * @param priorityOrderUpdDto
     * @return
     */
    @PostMapping("/getPriorityOrderDataUpd")
    public Map<String, Object> getPriorityOrderDataUpd(@RequestBody PriorityOrderUpdDto priorityOrderUpdDto) {
        String colName = priorityOrderUpdDto.getColNameList();
        List<String> colNameList = Arrays.asList(colName.split(","));
        Integer priorityOrderCd = priorityOrderUpdDto.getPriorityOrderCd();
        String companyCd = priorityOrderUpdDto.getCompanyCd();
        Integer delFlg = priorityOrderUpdDto.getDelFlg();
        return priorityOrderDataService.getPriorityOrderDataUpd(colNameList, priorityOrderCd, companyCd, delFlg);
    }

    /**
     * ptsアップロード後のソート+優先順位表反応ボタン抽出データ
     *
     * @return
     */
    @GetMapping("/getUploadPriorityOrderData")
    public Map<String, Object> getUploadPriorityOrderData(String companyCd, Integer priorityOrderCd) {
        return priorityOrderDataService.getUploadPriorityOrderData(companyCd, priorityOrderCd);
    }

    /**
     * 属性列名の名前を取得
     *
     * @param enterpriseAxisDto
     * @return
     */
    @PostMapping("/getAttrName")
    public Map<String, Object> getAttrName(@RequestBody EnterpriseAxisDto enterpriseAxisDto) {
        return priorityOrderDataService.getAttrName(enterpriseAxisDto);
    }


    /**
     * @param attrList eg: {company_cd}_{class}_{header_sys(column 3)}
     * @return
     */
    @PostMapping("/uploadPriorityOrderData")
    public Map<String, Object> uploadPriorityOrderData(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("taiCd") String taiCd, @RequestParam("tanaCd") String tanaCd,
                                                       @RequestParam("company") String company, @RequestParam("attrList") String attrList, @RequestParam("priorityOrderCd") Integer priorityOrderCd) {
        return priorityOrderDataService.uploadPriorityOrderData(taiCd, tanaCd, file, company, priorityOrderCd, attrList);
    }

    @PostMapping("/downloadForCsv")
    public void downloadForCsv(@RequestBody DownloadSortDto downloadDto, HttpServletResponse response) throws IOException {
        priorityOrderDataService.downloadForCsv(downloadDto, response);
    }


    @GetMapping("getPatternAndName")
    public Map<String, Object> getPatternAndName(Integer productPowerCd) {
        return priorityOrderDataService.getPatternAndName(productPowerCd);
    }

    @PostMapping("/getBranchNum")
    Map<String, Object> getBranchNum(@RequestBody List<Map<String, Object>> map) {
        return priorityOrderDataService.getBranchNum(map);
    }

    /**
     * pattern比較
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @GetMapping("getPatternCompare")
    Map<String, Object> getPatternCompare(String companyCd ,Integer priorityOrderCd) {
        return priorityOrderDataService.getPatternCompare(companyCd,priorityOrderCd);
    }

    /**
     * 属性の比較
     * @param companyCd
     * @param priorityOrderCd
     * @param attrList
     * @return
     */
    @GetMapping("getAttrCompare")
    Map<String, Object> getAttrCompare(String companyCd ,Integer priorityOrderCd,String attrList) {
        return priorityOrderDataService.getAttrCompare(companyCd,priorityOrderCd,attrList);
    }

    /**
     * 新pts詳細
     * @param companyCd
     * @param priorityOrderCd
     * @param shelfPatternCd
     * @return
     */
    @GetMapping("getPriorityOrderNewPts")
    Map<String, Object> getPriorityOrderNewPts(String companyCd ,Integer priorityOrderCd,Integer shelfPatternCd) {
        return priorityOrderDataService.getPriorityOrderNewPts(companyCd,priorityOrderCd,shelfPatternCd,1);
    }

    /**
     * 旧pts詳細
     * @param companyCd
     * @param priorityOrderCd
     * @param shelfPatternCd
     * @return
     */
    @GetMapping("getPriorityOrderOldPts")
    Map<String, Object> getPriorityOrderOldPts(String companyCd ,Integer priorityOrderCd,Integer shelfPatternCd) {
        return priorityOrderDataService.getPriorityOrderNewPts(companyCd,priorityOrderCd,shelfPatternCd,0);
    }

}
