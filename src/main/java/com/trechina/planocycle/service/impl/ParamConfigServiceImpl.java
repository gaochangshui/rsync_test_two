package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.CompanyConfigureDto;
import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.entity.po.Company;
import com.trechina.planocycle.entity.po.Group;
import com.trechina.planocycle.entity.po.MstKiGyoCore;
import com.trechina.planocycle.entity.vo.CommonPartsDataVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.CompanyConfigMapper;
import com.trechina.planocycle.mapper.ParamConfigMapper;
import com.trechina.planocycle.mapper.SkuNameConfigMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.ParamConfigService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

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
        CompanyConfigureDto companyConfigureDto = new CompanyConfigureDto();
        Map<String,Object> mstkigyocore = companyConfigMapper.getMstkigyocore(companyCd);
        if (mstkigyocore != null) {
            companyConfigureDto.setProdIsCore(mstkigyocore.get(MagicString.PROD_IS_CORE).toString());
            companyConfigureDto.setDateIsCore(mstkigyocore.get(MagicString.DATE_IS_CORE).toString());
            companyConfigureDto.setStoreIsCore(mstkigyocore.get(MagicString.STORE_IS_CORE).toString());
            companyConfigureDto.setIntageFlag(Integer.valueOf(mstkigyocore.get("intage").toString()));
            companyConfigureDto.setKokyakuFlag(Integer.valueOf(mstkigyocore.get("kokyaku").toString()));
            companyConfigureDto.setBasketPriceFlag(Integer.valueOf(mstkigyocore.get("basketPrice").toString()));
            companyConfigureDto.setIsIdPos(Integer.valueOf(mstkigyocore.get("isIdPos").toString()));
        }

        List<Map<String, Object>> companyConfig = companyConfigMapper.getCompanyConfig(companyCd);
        List<Map<String, String>> janUnit = sysConfigMapper.selectByPrefix("jan_unit_");

        List companyCol = this.getCompanyCol(companyCd);
        Map<String,Object> attrMap = (Map<String,Object>)companyCol.get(0);
        List<Map<String,Object>> prodClassMap = (List<Map<String,Object>>)companyCol.get(1);
        prodClassMap.forEach(classMap-> attrMap.entrySet().forEach(attr->{
            if (attr.getKey().equals("table_"+classMap.get("classCd"))){
                classMap.put("value",attr.getValue());
            }
        }));
        companyConfigureDto.setCompanyColMap(prodClassMap);
        return ResultMaps.result(ResultEnum.SUCCESS,companyConfigureDto);
    }

    private List<Map<String,Object>> getStoreForSmt(String subkigyo){
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        ResourceBundle resource = ResourceBundle.getBundle("pathConfig");
        String paths = resource.getString("CompanyStoreHeaderData");
        Map<String,Object> cgiMap = new HashMap<>();
        cgiMap.put("subkigyo",subkigyo);
        String result = cgiUtil.postCgiCompany(paths, cgiMap, tokenInfo);
        List resultList = new ArrayList();
        List<String> storeList = Arrays.asList(result.split("@"));
        List<Map<String,Object>> list = new ArrayList<>();
        for (String attr : storeList) {
            Map<String,Object> storeMap = new HashMap<>();
            String[] value = attr.split(" ");
            storeMap.put("value",value[0]);
            storeMap.put("label",value[1]);
            list.add(storeMap);
        }
        return list;
    }

    private List getCompanyCol(String subkigyo) {
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        ResourceBundle resource = ResourceBundle.getBundle("pathConfig");
        String paths = resource.getString("CompanyPordHeaderData");
        Map<String,Object> cgiMap = new HashMap<>();
        cgiMap.put("subkigyo",subkigyo);
        String result = cgiUtil.postCgiCompany(paths, cgiMap, tokenInfo);
        List resultList = new ArrayList();
        List<String> attrList = Arrays.asList(result.split("@"));
        List<Map<String,Object>> list = new ArrayList<>();
        for (String attr : attrList) {
            Map<String,Object> attrMap = new HashMap<>();
            String[] value = attr.split(" ");
            attrMap.put("colCd",value[4]);
            attrMap.put("colName",value[3]);
            attrMap.put("classCd",value[0]);
            attrMap.put("className",value[1]);
            list.add(attrMap);
        }
        List<Map<String, Object>> prodList = list.stream().map(map -> {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("label", map.get("classCd").toString());
            resultMap.put("value", map.get("className").toString());
            return resultMap;
        }).distinct().collect(Collectors.toList());
        Map<Object,List<Map<String, Object>>> classCd = list.stream().collect(Collectors.groupingBy(map -> map.get("classCd")));
        Map<String,Object> resultMap = new HashMap<>();
        for (Map.Entry<Object, java.util.List<Map<String, Object>>> objectListEntry : classCd.entrySet()) {
            List<Map<String, Object>> collect = objectListEntry.getValue().stream().map(map -> {
                Map<String, Object> attr = new HashMap<>();
                attr.put("label", map.get("colName").toString());
                attr.put("value", map.get("colCd").toString());
                return attr;
            }).filter(map->!map.get("label").toString().contains("CD"))
                    .sorted(Comparator.comparing(map->MapUtils.getInteger(map, "value"))).collect(Collectors.toList());
            resultMap.put("table_"+objectListEntry.getKey(),collect);
        }
        resultList.add(resultMap);
        resultList.add(prodList);
        resultList.add(list);
        return resultList;
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

    @Override
    public Map<String, Object> getCompanyList() {
        Map<String,Object> map = new HashMap<>();
        List<Company> companyList2 = companyConfigMapper.getCompanyList();
        List<Group> groupList = companyConfigMapper.getGroupList();
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        for (Company company : companyList2) {
            if (company.getCompanyCd().equals(coreCompany)){
                company.setFlag(true);
            }
        }
        map.put("companyList",companyList2);
        map.put("groupList",groupList);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }

    @Override
    public Map<String, Object> getCompanyParam(String companyCd) {
        List<Map<String, String>> janUnit = sysConfigMapper.selectByPrefix("jan_unit_");
        List companyCol = this.getCompanyCol(companyCd);
        Map<String,Object> attrMap = (Map<String,Object>)companyCol.get(0);
        List<Map<String,Object>> prodClassMap = (List<Map<String,Object>>)companyCol.get(1);
        Map<String,Object> maps = new HashMap<>();
        List<Map<String, Object>> storeForSmt = this.getStoreForSmt(companyCd);
        prodClassMap.forEach(classMap->{
            List<Map<String,Object>> optionList = new ArrayList<>();
            janUnit.forEach(option->{
                Map<String,Object> optionMap = new HashMap<>();
                optionMap.put("value",option.get("item_name"));
                optionMap.put("label",option.get("item_value"));
                optionList.add(optionMap);
            });
            classMap.put("option",optionList);
            attrMap.entrySet().forEach(attr->{
                if (attr.getKey().equals("table_"+classMap.get("label"))){
                    List<Map<String, Object>> value = (List<Map<String, Object>>) attr.getValue();
                    List<Map<String, Object>> collect = value.stream().map(map -> {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("label", map.get("label"));
                        resultMap.put("value", map.get("value"));
                        resultMap.put("isUse", false);
                        resultMap.put("isNumber", false);
                        resultMap.put("isInterval", false);
                        return resultMap;
                    }).collect(Collectors.toList());
                    classMap.put("attrList",collect);
                }
            });
        });
        prodClassMap.forEach(classMap->((List)classMap.get("option")).forEach(unit->{
            attrMap.entrySet().forEach(attr->{
                if (attr.getKey().equals("table_"+classMap.get("label"))){
                    if (((Map<String,Object>)unit).get("value").equals("jan_unit_1")){
                        ((Map<String,Object>)unit).put("option",null);
                    }else {
                        ((Map<String,Object>)unit).put("option",attr.getValue());
                    }
                }
            });
        }));
        maps.put("storeList",storeForSmt);
        maps.put("prodList",prodClassMap);
        return ResultMaps.result(ResultEnum.SUCCESS,maps);
    }
}
