package com.trechina.planocycle.service.impl;

import com.google.api.client.util.Strings;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.EnterpriseAxisDto;
import com.trechina.planocycle.entity.vo.CommonPartsDataVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.CompanyConfigMapper;
import com.trechina.planocycle.mapper.SkuNameConfigMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.SysConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysConfigServiceImpl implements SysConfigService {
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private SkuNameConfigMapper skuNameConfigMapper;
    @Autowired
    private CompanyConfigMapper companyConfigMapper;
    @Override
    public Map<String, Object> getShowJanSku(EnterpriseAxisDto enterpriseAxisDto) {
        Map<String, Object> resultMap = new HashMap<>();
        //
        String companyCd = enterpriseAxisDto.getCompanyCd();
        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        String isCompanyCd = null;
            isCompanyCd = companyCd;

        String groupName = sysConfigMapper.getGroupName(companyCd);
        if (!Strings.isNullOrEmpty(groupName)){
            isCompanyCd = coreCompany;
        }
        Map<String, Object> companyList = companyConfigMapper.getCompanyList1(companyCd);
        List<Map<String, String>> janUnit = sysConfigMapper.selectByPrefix("jan_unit_");
            List<Map<String, Object>> janUnitList = new ArrayList<>();
            Map<String, Object> itemJanUnit = null;
            for (Map<String, String> s : janUnit) {
                itemJanUnit = new HashMap<>();
                String itemName = MapUtils.getString(s, "item_name");
                String[] sArray = itemName.split("_");
                itemJanUnit.put("label", MapUtils.getString(s, "item_value"));
                itemJanUnit.put("value", Integer.parseInt(sArray[sArray.length-1]));
                janUnitList.add(itemJanUnit);
            }
            resultMap.put("showJanSkuFlag", 1);
            resultMap.put("janUnit", janUnitList);

        Map<String, Object> kokyakuShow = companyConfigMapper.getKokyakuShow(isCompanyCd);
        String intageFlag = sysConfigMapper.selectSycConfig(MagicString.ITEM_NAME_SHOW_INTAGE);

        resultMap.put("intageFlag", Objects.equals(intageFlag, "1")?1:0);

        if (kokyakuShow == null || kokyakuShow.isEmpty()){
            resultMap.put("kokyakuFlag",0);
            resultMap.put("basketPrice",0);
        }else {
            resultMap.put("kokyakuFlag",Integer.parseInt(kokyakuShow.get("kokyaku").toString())==1?1:0);
            resultMap.put("basketPriceFlag",Integer.parseInt(kokyakuShow.get("basketPrice").toString())==1?1:0);
        }
        CommonPartsDataVO commonPartsDataVO = new CommonPartsDataVO();
        commonPartsDataVO.setProdIsCore(String.valueOf(companyList.get("prod_is_core")).equals("2")?"1":String.valueOf(companyList.get("prod_is_core")));
        commonPartsDataVO.setStoreIsCore(String.valueOf(companyList.get("store_is_core")));
        commonPartsDataVO.setDateIsCore(String.valueOf(companyList.get("date_is_core")));
        resultMap.put("commonPartsData",commonPartsDataVO);

        String level = sysConfigMapper.selectSycConfig(MagicString.LEVEL);
        resultMap.put("level", level);
        return ResultMaps.result(ResultEnum.SUCCESS, resultMap);
    }

    @Override
    public Map<String, Object> getCompanySettings(String companyCd) {


        String groupName = sysConfigMapper.getGroupName(companyCd);
        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        if (!Strings.isNullOrEmpty(groupName)){
            companyCd = coreCompany;
        }
        Map<String, Object> companyList = companyConfigMapper.getCompanyList1(companyCd);

        CommonPartsDataVO commonPartsDataVO = new CommonPartsDataVO();
        commonPartsDataVO.setProdIsCore(String.valueOf(companyList.get("prod_is_core")).equals("2")?"1":String.valueOf(companyList.get("prod_is_core")));
        commonPartsDataVO.setStoreIsCore(String.valueOf(companyList.get("store_is_core")));
        commonPartsDataVO.setDateIsCore(String.valueOf(companyList.get("date_is_core")));
        String prodClass ="";
        String storeClass ="";
        if (commonPartsDataVO.getProdIsCore().equals("1")){
             prodClass = companyConfigMapper.getCompanyProdClass(coreCompany);
        }else {
             prodClass = companyConfigMapper.getCompanyProdClass(companyCd);
        }
        if (commonPartsDataVO.getStoreIsCore().equals("1")){
             storeClass = companyConfigMapper.getCompanyStoreClass(coreCompany);
        }else {
             storeClass = companyConfigMapper.getCompanyStoreClass(companyCd);
        }

        commonPartsDataVO.setProdMstClass(prodClass);
        commonPartsDataVO.setStoreMstClass(storeClass);
        return ResultMaps.result(ResultEnum.SUCCESS,commonPartsDataVO);
    }
}
