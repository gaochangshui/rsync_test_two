package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.entity.vo.ProductPowerMstVo;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import com.trechina.planocycle.enums.ProductPowerHeaderEnum;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.ProductPowerMstService;
import com.trechina.planocycle.utils.ExcelUtils;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductPowerMstServiceImpl implements ProductPowerMstService {
    @Autowired
    ProductPowerMstMapper productPowerMstMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    PriorityAllMstMapper priorityAllMstMapper;
    @Autowired
    private SkuNameConfigMapper skuNameConfigMapper;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private ParamConfigMapper paramConfigMapper;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private ClassicPriorityOrderMstMapper classicPriorityOrderMstMapper;
    @Autowired
    HttpSession session;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private LogAspect logAspect;
    /**
     * 企業cdによる商品力点数表一覧の取得
     * @param companyCd
     * @return
     */

    @Override
    public Map<String, Object> getTableName(String companyCd) {

        Map<String,Object> tableNameMap =  new HashMap<>();
        try {
            String aud = session.getAttribute("aud").toString();
            List<TableNameDto> commodityData = productPowerMstMapper.getTableNameByCompanyCd(companyCd,aud);
            List<TableNameDto> basicPtsData = priorityOrderMstMapper.getTableNameByCompanyCd(companyCd,aud);
            List<TableNameDto> wholePtsData = priorityAllMstMapper.getTableNameByCompanyCd(companyCd,aud);
            List<TableNameDto> priorityData = classicPriorityOrderMstMapper.getTableNameByCompanyCd(companyCd,aud);
            tableNameMap.put("commodityData",commodityData);
            tableNameMap.put("basicPtsData",basicPtsData);
            tableNameMap.put("wholePtsData",wholePtsData);
            tableNameMap.put("priorityData",priorityData);
        } catch (Exception e) {
            logAspect.setTryErrorLog(e,new Object[]{companyCd});
           return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,tableNameMap);
    }

    /**
     * mst基本情報の取得
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getProductPowerTable(String companyCd) {
        String aud = session.getAttribute("aud").toString();
        List<TableNameDto> commodityData = productPowerMstMapper.getTableNameByCompanyCd(companyCd,aud);
        return ResultMaps.result(ResultEnum.SUCCESS,commodityData);

    }

    /**
     * 商品力点数表一覧データの取得
     * @param companyCd
     * @param productPowerCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getProductPowerInfo(String companyCd, Integer productPowerCd,Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
        //productPowerCdをorder_に保存mstテーブル
        shelfPtsDataMapper.deletePtsJandataByPriorityOrderCd(priorityOrderCd);
        productPowerMstMapper.setProductPowerCdForMst(productPowerCd,companyCd,authorCd,priorityOrderCd);
        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, productPowerCd);
        Integer skuNum = productPowerMstMapper.getSkuNum(companyCd, productPowerCd);
        productPowerInfo.setSku(skuNum);

        return ResultMaps.result(ResultEnum.SUCCESS,productPowerInfo);
    }

    /**
     * 商品力点数表データを取得excel download
     * @param companyCd
     * @param productPowerCd
     * @param response
     */
    @Override
    public void downloadProductPowerInfo(String companyCd, Integer productPowerCd, HttpServletResponse response) {
        //必要なヘッダーを検索
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, productPowerCd);
        if(Strings.isNullOrEmpty(param.getProject())){
            logger.error("no project");
            return;
        }
        String[] project = param.getProject().split(",");
        //顧客グループ
        String customerValue = Stream.of(project).filter(s -> s.startsWith(ProductPowerHeaderEnum.CUSTOMER.getCode())).collect(Collectors.joining(","));
        //予備項目
        String prepareValue = Stream.of(project).filter(s -> s.startsWith("item")).collect(Collectors.joining(","));
        //POS項目
        String posValue = Stream.of(project).filter(s -> s.startsWith(ProductPowerHeaderEnum.POS.getCode())).collect(Collectors.joining(","));
        //市場項目
        String intageValue = Stream.of(project).filter(s -> s.startsWith(ProductPowerHeaderEnum.INTAGE.getCode())).collect(Collectors.joining(","));
        String rankWeight = param.getRankWeight();
        Set<String> weightKeys = new HashSet<>();

        if (!Strings.isNullOrEmpty(rankWeight)) {
            JSONArray weightArray = JSON.parseArray(rankWeight);
            weightArray = weightArray.stream().filter(json->!((JSONObject)json).getString("weight").equals("0")).collect(Collectors.toCollection(JSONArray::new));
            Set<String> finalWeightKeys = weightKeys;
            weightArray.stream().forEach(o->{
                JSONObject jsonObj = (JSONObject) o;
                finalWeightKeys.add(jsonObj.getString("colName"));
            });
            weightKeys.addAll(finalWeightKeys);
        }

        String tableName = "";
        String tableNameAttr = "";
        String janInfoTableName = "";

        ProductPowerParamVo productPowerParam = productPowerDataMapper.getParam(companyCd, productPowerCd);
        JSONObject productPowerParamJson = JSON.parseObject(productPowerParam.getCommonPartsData());
        //1-自設，0-企業
        String prodIsCore = productPowerParamJson.getString("prodIsCore");
        //第数セット
        String prodMstClass = productPowerParamJson.getString("prodMstClass");
        String coreCompany = companyCd;

        if("0".equals(prodIsCore)){
            //0-企業
            tableName = String.format("\"%s\".prod_%s_jan_kaisou_header_sys", companyCd, prodMstClass);
            tableNameAttr = String.format("\"%s\".prod_%s_jan_attr_header_sys", coreCompany, prodMstClass);
            janInfoTableName = String.format("\"%s\".prod_%s_jan_info", companyCd, prodMstClass);
        }else{
            //自設company_cd
            coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
            //1-自設
            tableName = String.format("\"%s\".prod_%s_jan_kaisou_header_sys", coreCompany, prodMstClass);
            tableNameAttr = String.format("\"%s\".prod_%s_jan_attr_header_sys", coreCompany, prodMstClass);
            janInfoTableName = String.format("\"%s\".prod_%s_jan_info", coreCompany, prodMstClass);
        }

        List<ParamConfigVO> paramList = paramConfigMapper.selectParamConfig();
        Map<String, List<ParamConfigVO>> paramListByGroup = paramList.stream()
                .peek(config-> config.setItemName(config.getItemType()+config.getItemName()))
                .collect(Collectors.groupingBy(paramParam -> paramParam.getItemCd().split("_")[0], LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> classify = janClassifyMapper.selectJanClassify(tableName);
        Map<String, String> attrColumnMap = classify.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("sort").toString()));
        List<Map<String, Object>> janName = classify.stream().filter(map -> map.get("attr").equals("jan_name")).collect(Collectors.toList());
        classify = classify.stream().filter(map -> map.get("colSort")!=null).collect(Collectors.toList());

        Integer janNameColIndex = null;
        Integer janName2colNum = param.getJanName2colNum();
        if(janName2colNum==null || Objects.equals(janName2colNum, MagicString.PRODUCT_TYPE_JAN)){
            //product name（品名2）
            janNameColIndex = Integer.parseInt(janName.get(0).get("sort").toString());
        }else if ( Objects.equals(janName2colNum, MagicString.PRODUCT_TYPE_ITEM)){
            janNameColIndex = skuNameConfigMapper.getJanItem2colNum(coreCompany, prodMstClass);
        }else{
            //品名1
            janNameColIndex = skuNameConfigMapper.getJanName2colNum(coreCompany, prodMstClass);
        }
        List<Map<String, Object>> prodAttrData = new Gson().fromJson(param.getProdAttrData().toString(), new com.google.common.reflect.TypeToken<List<Map<String, Object>>>(){}.getType());
        List<String> attrList = new ArrayList<>();
        prodAttrData.forEach(map->{
            if ((Boolean) map.get("showFlag")) {
                attrList.add(map.get("id").toString().split("_")[2]);
            }
        });
        List<Map<String, Object>> attrColName = productPowerDataMapper.getAttrColName(attrList, tableNameAttr);
        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, productPowerCd);
        List<Map<String, Object>> allData = productPowerDataMapper.getDynamicAllData(companyCd, productPowerCd,
                janInfoTableName, "\""+attrColumnMap.get("jan_cd")+"\"", classify, project, janNameColIndex,attrColName);
        allData.forEach(map->map.entrySet().forEach(entry->{
            if (entry.getKey().equals("intage_item03")) {
                Double value = Double.valueOf(entry.getValue().toString());
                entry.setValue(value);
            }
        }));
        //表示するカラムに対応するフィールド名
        List<String> attr = classify.stream().map(map -> map.get("attr").toString()).collect(Collectors.toList());
        List<String> attrColCdList = attrColName.stream().map(map -> map.get("colCd").toString()).collect(Collectors.toList());
        attr.add("branchNum");
        Map<String, List<String>> columnsByClassify = this.initColumnClassify(attr, attrColCdList);
        //表示する列に対応するヘッダー

        List<String> attrName = classify.stream().map(map -> map.get("attr_val").toString()).collect(Collectors.toList());
        List<String> attrColNameList = attrColName.stream().map(map -> map.get("colName").toString()).collect(Collectors.toList());
        attrName.add("定番店鋪数");
        Map<String, List<String>> headersByClassify = this.initHeaderClassify(attrName, attrColNameList);

        this.fillParamData(ProductPowerHeaderEnum.POS.getName(),
                posValue,paramListByGroup.get(ProductPowerHeaderEnum.POS.getCode()), headersByClassify, columnsByClassify, weightKeys);
        this.fillParamData(ProductPowerHeaderEnum.CUSTOMER.getName(),
                customerValue,paramListByGroup.get(ProductPowerHeaderEnum.CUSTOMER.getCode()), headersByClassify, columnsByClassify, weightKeys);
        this.fillParamData(ProductPowerHeaderEnum.INTAGE.getName(),
                intageValue,paramListByGroup.get(ProductPowerHeaderEnum.INTAGE.getCode()), headersByClassify, columnsByClassify, weightKeys);
        this.fillPrepareParamData(prepareValue, productPowerCd, companyCd, headersByClassify, columnsByClassify, weightKeys);

        Map<String, Object> productParam = this.getProductParam(companyCd, productPowerCd);
        ServletOutputStream outputStream = null;
        try {
            String productPowerName = productPowerInfo.getProductPowerName();
            String fileName = String.format("%s.xlsx", productPowerName);
            String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(fileName, "utf-8"));
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            response.setHeader("Content-Disposition", format);
            outputStream = response.getOutputStream();
            ExcelUtils.generateExcel(headersByClassify, columnsByClassify, allData, outputStream,productParam);
            outputStream.flush();
        } catch (IOException e) {
            logger.error("", e);
            logAspect.setTryErrorLog(e,new Object[]{companyCd,productPowerCd});
        } finally {
            if(Objects.nonNull(outputStream)){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("io閉じる異常", e);
                    logAspect.setTryErrorLog(e,new Object[]{companyCd,productPowerCd});
                }
            }
        }
    }

    @Override
    public Map<String, Object> getPatternForBranch(String companyCd,Integer productPowerCd) {
        String branchs = productPowerDataMapper.getBranch(productPowerCd);
        List<String> branchList = Arrays.asList(branchs.split(","));
        String patternList = productPowerDataMapper.getPatternList(branchList);
        return ResultMaps.result(ResultEnum.SUCCESS,patternList);
    }

    private void fillPrepareParamData(String prepareValue, Integer productPowerCd, String companyCd,
                                      Map<String, List<String>> headersByClassify,
                                      Map<String, List<String>> columnsByClassify, Set<String> weightKeys){
        if(!Strings.isNullOrEmpty(prepareValue)){
            String[] prepareValues = prepareValue.split(",");
            List<ReserveMstVo> reserve = productPowerDataMapper.getCheckedReserve(productPowerCd, companyCd, prepareValues);
            for (ReserveMstVo reserveMstVo : reserve) {
                List<String> customer = headersByClassify.get(ProductPowerHeaderEnum.PREPARE.getName());
                customer.add(reserveMstVo.getDataName());

                String valueCd = reserveMstVo.getValueCd()+"";
                String valueNo = valueCd.substring(valueCd.length() - 2);

                List<String> customerColumn = columnsByClassify.get(ProductPowerHeaderEnum.PREPARE.getName());
                customerColumn.add("item"+Integer.parseInt(valueNo));

                if(weightKeys.contains("item"+Integer.parseInt(valueNo))){
                    List<String> customerRank = headersByClassify.get(ProductPowerHeaderEnum.PREPARE_RANK.getName());
                    customerRank.add(reserveMstVo.getDataName()+"Rank");

                    List<String> customerRankColumn = columnsByClassify.get(ProductPowerHeaderEnum.PREPARE_RANK.getName());
                    customerRankColumn.add("item"+Integer.parseInt(valueNo)+"Rank");
                }
            }
        }
    }
    private void fillParamData(String paramNameType, String paramVal, List<ParamConfigVO> paramList,
                               Map<String, List<String>> headersByClassify,
                               Map<String, List<String>> columnsByClassify, Set<String> weightKeys){
        if(!Strings.isNullOrEmpty(paramVal)) {
            String paramTypeRank = String.format("%sRank", paramNameType);
            String[] paramsValueArr = paramVal.split(",");
            List<ParamConfigVO> paramConfigChecked = paramList.stream()
                    .filter(config -> Arrays.stream(paramsValueArr).anyMatch(s->s.equals(config.getItemCd()))).collect(Collectors.toList());
            for (ParamConfigVO paramConfigVO : paramConfigChecked) {
                List<String> customer = headersByClassify.get(paramNameType);
                customer.add(paramConfigVO.getItemName());

                List<String> column = columnsByClassify.get(paramNameType);
                column.add(paramConfigVO.getItemCd());

                if (weightKeys.contains(paramConfigVO.getItemCd())) {
                    List<String> rank = headersByClassify.get(paramTypeRank);
                    String rankName = String.format("%sRank", paramConfigVO.getItemName());
                    rank.add(rankName);

                    List<String> customerRankColumn = columnsByClassify.get(paramTypeRank);
                    customerRankColumn.add(String.format("%s_rank", paramConfigVO.getItemCd()));
                }
            }
        }
    }

    private Map<String, List<String>> initHeaderClassify(List<String> kaisouName, List<String> attrName){
        Map<String, List<String>> headersByClassify = new LinkedHashMap<>(10);
        headersByClassify.put(ProductPowerHeaderEnum.BASIC.getName(), Lists.newArrayList("JANコード", "商品名"));
        headersByClassify.put(ProductPowerHeaderEnum.CLASSIFY.getName(), kaisouName);
        headersByClassify.put(ProductPowerHeaderEnum.ATTR.getName(), attrName);
        headersByClassify.put(ProductPowerHeaderEnum.POS.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.CUSTOMER.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.PREPARE.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.INTAGE.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.POS_RANK.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.CUSTOMER_RANK.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.PREPARE_RANK.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.INTAGE_RANK.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.RANK.getName(), Lists.newArrayList("Rank"));
        return headersByClassify;
    }

    private Map<String, List<String>> initColumnClassify(List<String> kaisou, List<String> attr){
        Map<String, List<String>> columnsByClassify = new LinkedHashMap<>(10);
        columnsByClassify.put(ProductPowerHeaderEnum.BASIC.getName(), Lists.newArrayList("jan", "jan_name"));
        columnsByClassify.put(ProductPowerHeaderEnum.CLASSIFY.getName(), kaisou);
        columnsByClassify.put(ProductPowerHeaderEnum.ATTR.getName(), attr);
        columnsByClassify.put(ProductPowerHeaderEnum.POS.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.CUSTOMER.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.PREPARE.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.INTAGE.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.POS_RANK.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.CUSTOMER_RANK.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.PREPARE_RANK.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.INTAGE_RANK.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.RANK.getName(), Lists.newArrayList("rank_result"));
        return columnsByClassify;
    }




    public Map<String,Object> getProductParam(String companyCd,Integer productPowerCd){
        Map<String,Object> mapResult = new HashMap<>();
        //エンタープライズ
        String company = productPowerMstMapper.getCompanyName(companyCd);
        mapResult.put("company",company);
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, productPowerCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
        JSONObject basket = JSON.parseObject(param.getCommonPartsData());
        String basketTableName = "";
        String basketKaisouTableName = "";
        if (basket.getOrDefault("basketIsCore","2").equals("0")){
            basketTableName = MessageFormat.format(MagicString.PROD_JAN_KAISOU,companyCd,basket.get("basketMstClass"));
            basketKaisouTableName = MessageFormat.format(MagicString.PROD_JAN_KAISOU_HEADER_SYS,companyCd,basket.get("basketMstClass"));
        }else if (basket.getOrDefault("basketIsCore","2").equals("1")){
            basketTableName = MessageFormat.format(MagicString.PROD_JAN_KAISOU,MagicString.SELF_SERVICE,basket.get("basketMstClass"));
            basketKaisouTableName = MessageFormat.format(MagicString.PROD_JAN_KAISOU_HEADER_SYS,MagicString.SELF_SERVICE,basket.get("basketMstClass"));
        }
        //商品出力粒度
        String granularity = param.getJanName2colNum() == 1 ?"JAN単位":param.getJanName2colNum() == 2 ?"SKU単位":"Item単位";
        mapResult.put("granularity",granularity);
        //期間設定
        mapResult = this.setDateRang(param, mapResult);
        //店舗条件
        List<String> storeList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(param.getStoreCd())) {
            storeList = productPowerDataMapper.getStoreName(Arrays.asList(param.getStoreCd().split(",")),commonTableName.getStoreInfoTable());
        }
        mapResult.put("storeList",storeList);
        //商品条件
        mapResult = this.setSkuRang(param,mapResult,commonTableName);
            //jan
        Map<String, Object> singleJan = new Gson().fromJson(param.getSingleJan().toString(),
                new com.google.common.reflect.TypeToken<Map<String, Object>>(){}.getType());
        List<String> janList = new ArrayList<>();
        String janFlag = "";
        if (!singleJan.isEmpty()){
           List<String>filterJanList =  (List<String>) singleJan.get("filterJanList");
           List<String>noSelectedJanListAll =  (List<String>) singleJan.get("noSelectedJanListAll");
           boolean janExclude =  (boolean) singleJan.get("janExclude");
             janList = ListDisparityUtils.getListDisparitStr(filterJanList, noSelectedJanListAll);
             janFlag =janExclude?"除外":"対象";
        }
        mapResult.put("janList",janList);
        mapResult.put("janFlag",janFlag);

        //顧客条件
        JSONObject jsonObject = JSON.parseObject(param.getCustomerCondition());
        List<String> groupNames = new ArrayList<>();
        if (!jsonObject.isEmpty()) {
            JSONObject customerGroup = JSON.parseObject(jsonObject.get("customerGroup").toString());
            JSONArray jsonArray = customerGroup.getJSONArray("groupNames");
            for (Object o : jsonArray) {
                groupNames.add(o.toString());
            }
        }
        mapResult.put("groupNames",groupNames);
        //市場条件
        List<String> channelNm = new ArrayList<>();
        if (!Strings.isNullOrEmpty(param.getChannelNm())){
            channelNm = Arrays.asList(param.getChannelNm().split(","));
        }
        mapResult.put("channelNm",channelNm);
        List<String> placeNm = new ArrayList<>();
        if (!Strings.isNullOrEmpty(param.getPlaceNm())){
            placeNm = Arrays.asList(param.getPlaceNm().split(","));
        }
        mapResult.put("placeNm",placeNm);
        //バスケット単価
        this.getBasketCondition(param,mapResult,basketTableName,basketKaisouTableName);

        return mapResult;
    }

    private Map<String,Object> getBasketCondition(ProductPowerParamVo param, Map<String, Object> mapResult, String basketTableName,String basketAttrTableName) {
        if (basketTableName.equals("")){
            mapResult.put("basketJanClassify",new ArrayList<>());
            mapResult.put("basketJanClassifyHeader",new ArrayList<>());
            return mapResult;
        }
        List<Map<String,Object>> janClassify =  new ArrayList<>();
        List<LinkedHashMap<String, Object>> classifyHeader = productPowerDataMapper.getClassifyHeader(basketAttrTableName);
        String basketCondition = param.getBasketCondition();

        if (!Strings.isNullOrEmpty(basketCondition)){
            JSONObject basketJson = JSON.parseObject(basketCondition);
            List<String> list = Arrays.asList(basketJson.get(MagicString.BASKET_PROD_CD).toString().split(","));
            List<Map<String,Object>>  janClassifyList = new ArrayList<>();
            for (String s : list) {
                Map<String,Object> janClassCd = new HashMap<>();
                String s1 = s.split("-")[1];
                String[] s2 = s1.split("_");
                for (int i = 0; i < s2.length; i++) {
                    janClassCd.put(2*i+1+"",s2[i]);
                }

                janClassifyList.add(janClassCd);
            }
            janClassify = productPowerDataMapper.getJanClassify(janClassifyList,basketTableName,classifyHeader);
        }
        mapResult.put("basketJanClassify",janClassify);
        mapResult.put("basketJanClassifyHeader",classifyHeader);
        return mapResult;
    }

    public Map<String,Object> setDateRang(ProductPowerParamVo param, Map<String, Object> mapResult){
        String dateRange1 = "";
        String dateRange2 = "";
        if (!Strings.isNullOrEmpty(param.getRecentlyFlag())) {
            dateRange1 = param.getRecentlyFlag().equals("0")?"年月":"年週";
            dateRange1 += param.getRecentlyStTime()+"~"+param.getRecentlyEndTime();
        }
        if (!Strings.isNullOrEmpty(param.getSeasonFlag())) {
            dateRange2 = param.getSeasonFlag().equals("0")?"年月":"年週";
            dateRange2 += param.getSeasonStTime()+"~"+param.getSeasonEndTime();
        }else {
            dateRange2 +="昨対";
        }
        mapResult.put("dateRange1",dateRange1);
        mapResult.put("dateRange2",dateRange2);
        return mapResult;
    }


    public Map<String,Object> setSkuRang(ProductPowerParamVo param, Map<String, Object> mapResult, GetCommonPartsDataDto commonTableName){
        //classify
        List<LinkedHashMap<String, Object>> classifyHeader = productPowerDataMapper.getClassifyHeader(commonTableName.getProKaisouTable());
        List<Map<String,Object>> janClassify =  new ArrayList<>();
        if (!Strings.isNullOrEmpty(param.getPrdCd())) {
            List<String> list = Arrays.asList(param.getPrdCd().split(","));

            List<Map<String,Object>>  janClassifyList = new ArrayList<>();
            for (String s : list) {
                Map<String,Object> janClassCd = new HashMap<>();
                String s1 = s.split("-")[1];
                String[] s2 = s1.split("_");
                for (int i = 0; i < s2.length; i++) {
                    janClassCd.put(2*i+1+"",s2[i]);
                }

                janClassifyList.add(janClassCd);
            }
            janClassify = productPowerDataMapper.getJanClassify(janClassifyList,commonTableName.getProKaisouInfoTable(),classifyHeader);
        }

        mapResult.put("janClassify",janClassify);
        mapResult.put("classifyHeader",classifyHeader);
        //attr
        Map<String,Object> janAttr =  new LinkedHashMap<>();
        Map<String,Object> janAttrFlag =  new LinkedHashMap<>();
        List<Map<String, Object>> janAttrList = new Gson().fromJson(param.getProdAttrData().toString(),
                new com.google.common.reflect.TypeToken<List<Map<String, Object>>>(){}.getType());

        if(!janAttrList.isEmpty()){
            for (Map<String,Object> objectMap : janAttrList) {
                List<String> value = ((List<Object>) objectMap.get("value")).stream().map(val -> val instanceof Double ?
                        BigDecimal.valueOf((Double) val).setScale(0, RoundingMode.HALF_UP).toString() : String.valueOf(val)).collect(Collectors.toList());
                if (!value.isEmpty()) {
                    boolean flag = (boolean) objectMap.getOrDefault("rmFlag", false);
                    String attrName = productPowerDataMapper.getAttrName(objectMap.get("id").toString().split("_")[2], commonTableName.getProAttrTable());
                    janAttr.put(attrName, value);
                    janAttrFlag.put(attrName + "区分", flag ? "除外" : "対象");
                }
            }
        }
        mapResult.put("janAttr",janAttr);
        mapResult.put("janAttrFlag",janAttrFlag);
        return mapResult;
    }

}
