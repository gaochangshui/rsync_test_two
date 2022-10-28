package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.CompanyConfigureDto;
import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.AllParamConfigVO;
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
import org.springframework.transaction.annotation.Transactional;

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
            attrMap.put("flag",value[5]);
            list.add(attrMap);


        }
        List<Map<String, Object>> prodList = list.stream().map(map -> {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("value", map.get("classCd").toString());
            resultMap.put("label", map.get("className").toString());
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
                    .sorted(Comparator.comparing(map->MapUtils.getInteger(map, "value"))).distinct().collect(Collectors.toList());
            resultMap.put("table_"+objectListEntry.getKey(),collect);
        }

        Map<String, Object> attrMap = new HashMap<>();
        for (Map.Entry<Object, java.util.List<Map<String, Object>>> objectListEntry : classCd.entrySet()) {
            List<Map<String, Object>> collect = objectListEntry.getValue().stream()
                    .filter(map->map.get("flag").toString().equals("0")&&!map.get("colCd").equals("1") && !map.get("colCd").equals("2"))
                    .map(map -> {
                        Map<String, Object> attr = new HashMap<>();
                        attr.put("label", map.get("colName").toString());
                        attr.put("value", map.get("colCd").toString());
                        return attr;
                    })
                    .sorted(Comparator.comparing(map->MapUtils.getInteger(map, "value"))).distinct().collect(Collectors.toList());
            attrMap.put("table_"+objectListEntry.getKey(),collect);
        }
        resultList.add(resultMap);
        resultList.add(prodList);
        resultList.add(attrMap);
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
        CompanyList companyList = new CompanyList();
        String companyCd=map.get(MagicString.COMPANY_CD).toString();
        String companyName=map.get("companyName").toString();
        String authorCd = session.getAttribute("aud").toString();
        companyList.setCompanyCd(companyCd);
        companyList.setCompanyName(companyName);
        companyList.setKokyaku((Boolean)map.get("kokyaku")?1:0);
        companyList.setBasketPrice((Boolean)map.get("basket")?1:0);
        companyList.setDateIsCore(Integer.parseInt(map.get("dateIsCore").toString()));
        companyList.setStoreIsCore(Integer.parseInt(map.get("storeIsCore").toString()));
        companyList.setIsIdPos((Boolean)map.get("companyType")?1:0);
       int prodIsCore = ((List<String>)map.get("prodIsCore")).size()>1?2:Integer.parseInt(((List<String>)map.get("prodIsCore")).get(0));
        companyList.setIsUse("1");
        companyList.setProdIsCore(prodIsCore);
        companyList.setUpdater(authorCd);
        companyConfigMapper.setCompany(companyList);
        //store
        List<String> storeMstClass = (List<String>) map.get("storeMstClass");
        companyConfigMapper.delStore(companyCd);
        companyConfigMapper.setStoreMstClass(companyCd,storeMstClass);
        //prod
        Map<String,Object> prodMstOption = (Map<String,Object>) map.get("prodMstOption");
        List<SkuNameConfig> resultProd = new ArrayList<>();
        List<CompanyAttrConfig> prodClass = new ArrayList<>();
        for (Map.Entry<String, Object> stringObjectEntry : prodMstOption.entrySet()) {
            SkuNameConfig skuNameConfig = new SkuNameConfig();
            Map<String, Object> value = (Map<String, Object>) stringObjectEntry.getValue();
            Map<String, Object> option = (Map<String, Object>)value.get("option");
            List<Map<String,Object>> attrList = (List<Map<String,Object>>)value.get("attrList");
            skuNameConfig.setCompanyCd(companyCd);
            skuNameConfig.setClassCd(stringObjectEntry.getKey().split("_")[1]);
            skuNameConfig.setJanItem2colNum(option.get("jan_unit_3").toString());
            skuNameConfig.setJanName2colNum(option.get("jan_unit_2").toString());
            resultProd.add(skuNameConfig);

            attrList.forEach(attr->{
                Map<String,Object> attrMap = attr;
                CompanyAttrConfig companyAttrConfig = new CompanyAttrConfig();
                companyAttrConfig.setCompanyCd(companyCd);
                companyAttrConfig.setClassCd(stringObjectEntry.getKey().split("_")[1]);
                companyAttrConfig.setColName(attrMap.get("label").toString());
                companyAttrConfig.setColCd(attrMap.get("value").toString());
                companyAttrConfig.setNumberUnit(attrMap.get("unit").toString());
                companyAttrConfig.setIsNumber(((Boolean)attrMap.get("isNumber"))?1:0);
                companyAttrConfig.setIsRange(((Boolean)attrMap.get("isInterval"))?1:0);
                companyAttrConfig.setIsShow(((Boolean)attrMap.get("isUse"))?1:0);
                prodClass.add(companyAttrConfig);
            });
        }

        companyConfigMapper.delProdClass(companyCd);
        companyConfigMapper.delcompanyConfig(companyCd);
        companyConfigMapper.delMstKigyocore(companyCd);
        companyConfigMapper.setProdClass(prodClass);
        companyConfigMapper.setCompanyConfig(resultProd);
        companyConfigMapper.setMstKigyocore(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
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
        Map<String,Object> attrHeader = (Map<String,Object>)companyCol.get(2);
        Map<String,Object> maps = new HashMap<>();

        Map<String, Object> companyList1 = companyConfigMapper.getCompanyList1(companyCd);
        List<Map<String, Object>> companyConfigForCompany= new ArrayList<>();
        List<Map<String, Object>> prodList= new ArrayList<>();
        List<String> storeList = new ArrayList<>();
        List<String> prodMstClass = new ArrayList<>();
        if (companyList1 != null) {
            companyConfigForCompany = companyConfigMapper.getCompanyConfigForCompany(companyCd);
             prodList = companyConfigMapper.getProdList(companyCd);
             storeList = companyConfigMapper.getStoreList(companyCd);
             prodMstClass = companyConfigForCompany.stream().map(map -> map.get("class").toString()).collect(Collectors.toList());
            maps.put("storeMstClass", storeList);
            maps.put("prodMstClass", prodMstClass);
            maps.put("companyType", companyList1.get("is_id_pos").equals("1") ? true : false);
            maps.put("kokyaku", companyList1.get("kokyaku").equals("1") ? true : false);
            maps.put("basket", companyList1.get("basket_price").equals("1") ? true : false);
            maps.put("dateIsCore", companyList1.get("date_is_core").toString());
            maps.put("storeIsCore", companyList1.get("store_is_core").toString());
            List<String> prodIsCore = new ArrayList<>();
            if (companyList1.get("prod_is_core").toString().equals("2")) {
                prodIsCore.add("1");
                prodIsCore.add("2");

            } else {
                prodIsCore.add(companyList1.get("prod_is_core").toString());
            }
            maps.put("prodIsCore", prodIsCore);
        }
        List<Map<String, Object>> storeForSmt = this.getStoreForSmt(companyCd);
        List<Map<String, Object>> finalCompanyConfigForCompany = companyConfigForCompany;
        List<Map<String, Object>> finalProdList = prodList;
        prodClassMap.forEach(classMap->{
            List<Map<String,Object>> optionList = new ArrayList<>();
            janUnit.forEach(option->{

                if (!option.get("item_name").equals("jan_unit_1")) {
                    Map<String, Object> optionMap = new HashMap<>();
                    finalCompanyConfigForCompany.forEach(unit->{
                        if (unit.get("class").equals(classMap.get("value"))){
                            if (option.get("item_name").equals("jan_unit_2")){
                                optionMap.put("col", String.valueOf(unit.getOrDefault("jan_name2col_num","")));
                            }else {
                                optionMap.put("col", String.valueOf(unit.getOrDefault("jan_item2col_num","")));
                            }

                        }
                    });
                    optionMap.put("value", option.get("item_name"));
                    optionMap.put("label", option.get("item_value"));
                    optionList.add(optionMap);
                }



            });
            classMap.put("option",optionList);
            attrHeader.entrySet().forEach(attr->{
                if (attr.getKey().equals("table_"+classMap.get("value"))){
                    List<Map<String, Object>> value = (List<Map<String, Object>>) attr.getValue();
                    List<Map<String, Object>> collect = value.stream().map(map -> {

                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("label", map.get("label"));
                        resultMap.put("value", map.get("value"));
                        resultMap.put("isUse", true);
                        resultMap.put("isNumber", false);
                        resultMap.put("isInterval", false);
                        resultMap.put("unit", "");
                        for (Map<String, Object> stringObjectMap : finalProdList) {
                            if (stringObjectMap.get("class_cd").equals(classMap.get("value")) && stringObjectMap.get("col_cd").equals(map.get("value"))){
                                resultMap.put("isUse", stringObjectMap.get("is_show").equals(1));
                                resultMap.put("isNumber", stringObjectMap.get("is_number").equals(1));
                                resultMap.put("isInterval", stringObjectMap.get("is_range").equals(1));
                                resultMap.put("unit", stringObjectMap.get("number_unit"));
                            }
                        }
                        return resultMap;
                    }).collect(Collectors.toList());
                    classMap.put("attrList",collect);
                }
            });
        });
        Map<String,Object> option0 = new HashMap<>();
        option0.put("label", "");
        option0.put("value", "0");
        attrMap.entrySet().forEach(attr-> ((List)attr.getValue()).add(0,option0));
        prodClassMap.forEach(classMap->((List)classMap.get("option")).forEach(unit-> attrMap.entrySet().forEach(attr->{
            if (attr.getKey().equals("table_"+classMap.get("value"))){
                ((Map<String,Object>)unit).put("option",attr.getValue());
            }
        })));
        maps.put("storeList",storeForSmt);
        maps.put("prodList",prodClassMap);
        return ResultMaps.result(ResultEnum.SUCCESS,maps);
    }

    @Override
    public AllParamConfigVO getAllParamConfig() {
        List<ParamConfigDto> paramConfigVOS = paramConfigMapper.selectAllParamConfig();
        String intage = sysConfigMapper.selectSycConfig(MagicString.ITEM_NAME_SHOW_INTAGE);

        AllParamConfigVO allParamConfigVO = new AllParamConfigVO();
        allParamConfigVO.setShowIntage(Strings.isNullOrEmpty(intage)?1:Integer.parseInt(intage));
        allParamConfigVO.setParamConfigList(paramConfigVOS);

        List<Map<String, Object>> junitList = sysConfigMapper.selectAllByPrefix(MagicString.JAN_UNIT_PREFIX);
        junitList.forEach(jan->jan.put("show", false));
        allParamConfigVO.setJanUnit(junitList);

        String level = sysConfigMapper.selectSycConfig(MagicString.LEVEL);
        allParamConfigVO.setLevel(JSON.parseObject(level));

        return allParamConfigVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> updateParamConfig(AllParamConfigVO allParamConfigVO){
        Integer showIntage = allParamConfigVO.getShowIntage();
        sysConfigMapper.updateValByName(MagicString.ITEM_NAME_SHOW_INTAGE, showIntage);
        List<ParamConfigDto> paramConfigVOS = allParamConfigVO.getParamConfigList();
        paramConfigMapper.updateParamConfig(paramConfigVOS);

        //String janUnit = allParamConfigVO.getJanUnit();
        List<Map<String, Object>> janUnitList = allParamConfigVO.getJanUnit();
        sysConfigMapper.updateVal(janUnitList);

        sysConfigMapper.updateValByName(MagicString.LEVEL, allParamConfigVO.getLevel().toJSONString());

        return ResultMaps.result(ResultEnum.SUCCESS);
    }
}
