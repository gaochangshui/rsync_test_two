package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.CompanyConfigureDto;
import com.trechina.planocycle.entity.dto.ParamConfigDto;
import com.trechina.planocycle.entity.dto.SysConfigDto;
import com.trechina.planocycle.entity.po.Company;
import com.trechina.planocycle.entity.po.Group;
import com.trechina.planocycle.entity.po.MstKiGyoCore;
import com.trechina.planocycle.entity.vo.AllParamConfigVO;
import com.trechina.planocycle.entity.vo.CommonPartsDataVO;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
        CompanyConfigureDto companyConfigureDto = new CompanyConfigureDto();
        Map<String, Object> companyConfig = companyConfigMapper.getCompanyConfig(company, commonPartsDataVO.getProdMstClass());
        Integer colNum = skuNameConfigMapper.getJanName2colNum(company,commonPartsDataVO.getProdMstClass());
        if (!companyConfig.isEmpty()) {
            companyConfigureDto.setCommonPartsData(commonPartsDataVO);
            companyConfigureDto.setBasketPriceFlag(MapUtils.getInteger(companyConfig, "basket_price"));
            companyConfigureDto.setIntageFlag(MapUtils.getInteger(companyConfig, "intage"));
            companyConfigureDto.setKokyakuFlag(MapUtils.getInteger(companyConfig, "kokyaku"));
            companyConfigureDto.setJanName2colNum(MapUtils.getString(companyConfig, "jan_name2col_num"));
            companyConfigureDto.setJanItem2colNum(MapUtils.getString(companyConfig, "jan_item2col_num"));
            String convertNumbers = MapUtils.getString(companyConfig, "convert_numbers");

            JSONArray jsonArray = new JSONArray();
            if (!Strings.isNullOrEmpty(convertNumbers)) {
                jsonArray = JSON.parseArray(convertNumbers);
            }
            if (colNum != null) {
                List<Map<String, String>> janUnit = sysConfigMapper.selectByPrefix("jan_unit_");
                List<Map<String, Object>> janUnitList = new ArrayList<>();
                Map<String, Object> itemJanUnit = null;

                for (Map<String, String> s : janUnit) {
                    itemJanUnit = new HashMap<>();
                    String itemName = MapUtils.getString(s, "item_name");
                    String[] sArray = itemName.split("_");
                    itemJanUnit.put("label", MapUtils.getString(s, "item_value"));
                    itemJanUnit.put("value", Integer.parseInt(sArray[sArray.length - 1]));
                    janUnitList.add(itemJanUnit);
                }
                companyConfigureDto.setJanUnit(janUnitList);
                companyConfigureDto.setShowJanSkuFlag(1);
            } else {
                companyConfigureDto.setShowJanSkuFlag(0);
            }
        }
        Map<String,Object> companyColMap = new HashMap<>();
        companyColMap.putAll(this.getCompanyCol(companyCd, "0"));
        companyColMap.putAll(this.getCompanyCol("1000", "1"));
        companyConfigureDto.setCompanyColMap(companyColMap);
        return ResultMaps.result(ResultEnum.SUCCESS,companyConfigureDto);
    }

    private Map<String,Object> getCompanyCol(String subkigyo,String flag) {
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        ResourceBundle resource = ResourceBundle.getBundle("pathConfig");
        String paths = resource.getString("CompanyHeaderData");
        Map<String,Object> cgiMap = new HashMap<>();
        cgiMap.put("subkigyo",subkigyo);
        String result = cgiUtil.postCgiCompany(paths, cgiMap, tokenInfo);
        if (Strings.isNullOrEmpty(result)){
            return new HashMap<>();
        }
        List<String> attrList = Arrays.asList(result.split("@"));
        List<Map<String,Object>> List = new ArrayList<>();
        for (String attr : attrList) {
            Map<String,Object> attrMap = new HashMap<>();
            String[] value = attr.split(" ");
            attrMap.put("colCd",value[3]);
            attrMap.put("colName",value[2]);
            attrMap.put("classCd",value[0]);
            List.add(attrMap);
        }
        Map<Object, java.util.List<Map<String, Object>>> classCd = List.stream().collect(Collectors.groupingBy(map -> map.get("classCd")));
        Map<String,Object> resultMap = new HashMap<>();
        for (Map.Entry<Object, java.util.List<Map<String, Object>>> objectListEntry : classCd.entrySet()) {
            resultMap.put("table"+flag+"_"+objectListEntry.getKey(),objectListEntry.getValue());
        }
        return resultMap;
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
        String companys = session.getAttribute("inCharge").toString();
        List<String> companyList = Arrays.asList(companys.split(","));
        List<String> companyList1 = new ArrayList<>(companyList);
        companyList1.add("1000");
        List<Company> companyList2 = companyConfigMapper.getCompanyList(companyList1);
        List<Group> groupList = companyConfigMapper.getGroupList();
        map.put("companyList",companyList2);
        map.put("groupList",groupList);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }

    @Override
    public AllParamConfigVO getAllParamConfig() {
        List<ParamConfigVO> paramConfigVOS = paramConfigMapper.selectAllParamConfig();
        String intage = sysConfigMapper.selectSycConfig(MagicString.ITEM_NAME_SHOW_INTAGE);

        AllParamConfigVO allParamConfigVO = new AllParamConfigVO();
        allParamConfigVO.setShowIntage(Strings.isNullOrEmpty(intage)?1:Integer.parseInt(intage));
        allParamConfigVO.setParamConfigList(JSON.toJSONString(paramConfigVOS));

        return allParamConfigVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> updateParamConfig(AllParamConfigVO allParamConfigVO){
        Integer showIntage = allParamConfigVO.getShowIntage();
        sysConfigMapper.updateValByName(MagicString.ITEM_NAME_SHOW_INTAGE, showIntage);
        List<ParamConfigVO> paramConfigVOS = new Gson().fromJson(allParamConfigVO.getParamConfigList(), new TypeToken<List<ParamConfigVO>>() {
        }.getType());
        paramConfigMapper.updateParamConfig(paramConfigVOS);

        String janUnit = allParamConfigVO.getJanUnit();
        List<SysConfigDto> janUnitList = new Gson().fromJson(janUnit, new TypeToken<List<SysConfigDto>>(){}.getType());
        sysConfigMapper.updateVal(janUnitList);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }
}
