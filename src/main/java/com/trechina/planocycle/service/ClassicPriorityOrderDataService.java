package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.dto.DownloadDto;
import com.trechina.planocycle.entity.dto.DownloadSortDto;
import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.entity.dto.PriorityOrderDataDto;
import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ClassicPriorityOrderDataService {
    /**
     * 优先顺位表初期设定数据
     * @param priorityOrderDataDto
     * @return
     */
    Map<String,Object> getPriorityOrderData(PriorityOrderDataDto priorityOrderDataDto);

    Map<String, String> checkIsJanNew(List<String> janList, String company, Integer priorityOrderCd, String tableName);

    Map<String, Object> getUploadPriorityOrderData();

    @Transactional(rollbackFor = Exception.class)
    Map<String, Object> uploadPriorityOrderData(MultipartFile file, String company, Integer priorityOrderCd,
                                                String attrList, String allAttrList);

    void doJanCut(List<DownloadDto> cutJanList, String company, Integer priorityOrderCd);

    Map<String, Object> doJanNew(List<DownloadDto> newJanList, String company, Integer priorityOrderCd,
                                 String attrList, String allAttrList, List<PriorityOrderAttributeClassify> classifyList);

    /**
     * 优先顺位表反应按钮抽出数据
     * @param colNameList
     * @return
     */
    Map<String,Object> getPriorityOrderDataUpd(List<String> colNameList,Integer priorityOrderCd,String companyCd);

    Map<String,Object> getPriorityOrderListInfo(String companyCd,Integer priorityOrderCd);

    /**
     * 获取属性名
     * @param enterpriseAxisDto
     * @return
     */
    Map<String,Object> getAttrName(EnterpriseAxisDto enterpriseAxisDto);

    void downloadForCsv(DownloadSortDto downloadDto, HttpServletResponse response) throws IOException;

    Map<String, Object> getPriorityOrderDataForSmt(String [] jans,String companyCd,Integer priorityOrderCd,Integer productPowerCd);

    Map<String, Object> getPriorityOrderDataForDB(String[] jans, String companyCd, String attrList, Integer priorityOrderCd);

    Map<String, Object> getPatternAndName(Integer productPowerCd);
}
