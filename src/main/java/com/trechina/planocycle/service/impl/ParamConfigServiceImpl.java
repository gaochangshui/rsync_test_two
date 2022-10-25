package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.po.MstKiGyoCore;
import com.trechina.planocycle.entity.vo.CommonPartsDataVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.CompanyConfigMapper;
import com.trechina.planocycle.mapper.ParamConfigMapper;
import com.trechina.planocycle.mapper.SkuNameConfigMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.MstCommodityService;
import com.trechina.planocycle.service.ParamConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class ParamConfigServiceImpl implements ParamConfigService {
    @Autowired
    private ParamConfigMapper paramConfigMapper;
    @Autowired
    private CompanyConfigMapper companyConfigMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private SkuNameConfigMapper skuNameConfigMapper;
    @Autowired
    private MstCommodityService mstCommodityService;
    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private HttpSession session;
    @Override
    public Map<String, Object> getParamConfigList(Integer showItemCheck) {
        List<ParamConfigDto> paramConfig = paramConfigMapper.getParamConfig(showItemCheck);
        return ResultMaps.result(ResultEnum.SUCCESS,paramConfig);
    }

    @Override
    public Map<String, Object> getCompanyConfig(Map<String, Object> map) {
        String companyCd = MapUtils.getString(map, "F3");
        String companyName = MapUtils.getString(map,"F4");
        String groupCd = MapUtils.getString(map,"F1");
        String groupName = MapUtils.getString(map,"F2");

        MstKiGyoCore mstkigyocore = companyConfigMapper.getMstkigyocore(companyCd);
        CommonPartsDataVO commonPartsDataVO = this.mstKigyocoreChange(mstkigyocore);
        String company = "1000";
        if (commonPartsDataVO.getProdIsCore().equals("0")){
            company = companyCd;
        }

        Map<String, Object> companyConfig = companyConfigMapper.getCompanyConfig(company, commonPartsDataVO.getProdMstClass());
        Integer colNum = skuNameConfigMapper.getJanName2colNum(company,commonPartsDataVO.getProdMstClass());
        Map<String,Object> resultMap  = new HashMap<>();
        resultMap.put("commonPartsData",commonPartsDataVO);
        resultMap.put("basketPriceFlag", MapUtils.getInteger(companyConfig,"basket_price"));
        resultMap.put("intageFlag", MapUtils.getInteger(companyConfig,"intage"));
        resultMap.put("kokyakuFlag", MapUtils.getInteger(companyConfig,"kokyaku"));
        resultMap.put("showJanSkuFlag", MapUtils.getString(companyConfig,"kokyaku"));
        resultMap.put("showJanSkuFlag", MapUtils.getString(companyConfig,"kokyaku"));
        resultMap.put("janName2colNum", MapUtils.getString(companyConfig,"jan_name2col_num"));
        resultMap.put("janItem2colNum", MapUtils.getString(companyConfig,"jan_item2col_num"));
        resultMap.put("maker", MapUtils.getString(companyConfig,"maker"));
        String convertNumbers = MapUtils.getString(companyConfig, "convert_numbers");
        JSONArray jsonArray = new JSONArray();
        if (!Strings.isNullOrEmpty(convertNumbers)){
            jsonArray = JSON.parseArray(convertNumbers);
        }
        if (colNum != null){
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
            resultMap.put("janUnit",janUnitList);
            resultMap.put("showJanSkuFlag", 1);
        }else {
            resultMap.put("showJanSkuFlag", 0);
        }
        //String existTable = mstJanMapper.selectTableExist(companyCd);
        List<CommoditySyncSet> commoditySyncSetList = mstCommodityService.getCommodityList(companyCd);
        Map<String,Object> headerMap = new HashMap<>();
        for (CommoditySyncSet commoditySyncSet : commoditySyncSetList) {
            String tableAttrHeader = String.format(MagicString.PROD_JAN_ATTR_HEADER_SYS, "1000",commoditySyncSet.getProdMstClass());
            List<Map<String, Object>> attrHeader = sysConfigMapper.getAttrHeader(tableAttrHeader);
            headerMap.put("table1_"+commoditySyncSet.getProdMstClass(),attrHeader);
        }
        String uuid = UUID.randomUUID().toString();
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        ResourceBundle resource = ResourceBundle.getBundle("pathConfig");
        String paths = resource.getString("CompanyHeaderData");
        Map<String,Object> cgiMap = new HashMap<>();
        cgiMap.put("subkigyo","1000");
        String result = cgiUtil.postCgiCompany(paths, cgiMap, tokenInfo);
        String[] attrList = result.split("@");
        List<Map<String,Object>> List = new ArrayList<>();
        for (String attr : attrList) {
            Map<String,Object> attrMap = new HashMap<>();
            String[] value = attr.split(" ");
            attrMap.put("colCd",value[2]);
            attrMap.put("colName",value[1]);
            List.add(attrMap);
        }

        resultMap.put("headerMap",headerMap);
        return ResultMaps.result(ResultEnum.SUCCESS,resultMap);
    }

    private CommonPartsDataVO mstKigyocoreChange(MstKiGyoCore mstkigyocore) {
        CommonPartsDataVO commonPartsDataVO = new CommonPartsDataVO();
        if (mstkigyocore.getTenpoKaisouMst().equals("tenpo_kaisou_core_mst")){
            commonPartsDataVO.setStoreIsCore("1");
        }else {
            commonPartsDataVO.setStoreIsCore("0");
        }
        if (mstkigyocore.getShouhinKaisouMst().equals("shouhin_kaisou_core_mst")){
            commonPartsDataVO.setProdIsCore("1");
        }else {
            commonPartsDataVO.setProdIsCore("0");
        }
        commonPartsDataVO.setProdMstClass(mstkigyocore.getSelectedShouhin());
        commonPartsDataVO.setStoreMstClass(mstkigyocore.getSelectedTenpo());
        return commonPartsDataVO;
    }

    @Override
    public Map<String, Object> setCompanyConfig(Map<String, Object> map) {
        String companyCd=map.get(MagicString.COMPANY_CD).toString();
        String companyName=map.get("companyName").toString();
        String groupCd=map.get("groupCd").toString();
        String groupName=map.get("groupName").toString();
        String classCd="";
        Integer janName2colNum = null;
        Integer janItem2colNum = null;

        companyConfigMapper.setCompany(companyCd,companyName);
        companyConfigMapper.setCompanyConfig();
        String syncCompanyList = sysConfigMapper.selectSycConfig("sync_company_list");
        if (!Arrays.asList(syncCompanyList.split(",")).contains(companyCd)){
            companyConfigMapper.setSyncCompany(syncCompanyList+","+companyCd);
        }
        if (!companyCd.equals(companyName)){
            companyConfigMapper.setGroupCompany(companyCd,companyName,groupCd,groupName);
        }

        return null;
    }
}
