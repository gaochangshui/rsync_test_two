package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.BasicPatternMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
@Service
public class BasicPatternMstServiceImpl implements BasicPatternMstService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public  GetCommonPartsDataDto getCommonTableName(String commonPartsData, String companyCd ) {
        //{"dateIsCore":"1","storeLevel":"3","storeIsCore":"1","storeMstClass":"0000","prodIsCore":"1","prodMstClass":"0000"}

        JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();

        GetCommonPartsDataDto getCommonPartsDataDto = new GetCommonPartsDataDto();
        getCommonPartsDataDto.setProdMstClass(prodMstClass);

        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        String isCompanyCd = null;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        getCommonPartsDataDto.setProdIsCore(isCompanyCd);
        //if (jsonObject.get("storeIsCore").toString() !=null) {
        //    String storeIsCore = jsonObject.get("storeIsCore").toString();
        //    String storeMstClass = jsonObject.get("storeMstClass").toString();
        //    String storeIsCompanyCd = null;
        //    if ("1".equals(storeIsCore)) {
        //        storeIsCompanyCd = coreCompany;
        //    } else {
        //        storeIsCompanyCd = companyCd;
        //    }
        //    getCommonPartsDataDto.setStoreInfoTable(MessageFormat.format("\"{0}\".ten_{1}_ten_info", storeIsCompanyCd, storeMstClass));
        //    getCommonPartsDataDto.setStoreKaisouTable(MessageFormat.format("\"{0}\".ten_{1}_ten_kaisou_header_sys", storeIsCompanyCd, storeMstClass));
        //}


        getCommonPartsDataDto.setProKaisouTable(MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProAttrTable(MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProInfoTable(MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProZokuseiDataTable(MessageFormat.format("\"{0}\".zokusei_{1}_mst_data", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProZokuseiMstTable(MessageFormat.format("\"{0}\".zokusei_{1}_mst", isCompanyCd, prodMstClass));

        return getCommonPartsDataDto;
    }
}
