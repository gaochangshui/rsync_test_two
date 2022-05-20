package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.entity.vo.ProductPowerMstVo;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import com.trechina.planocycle.enums.ProductPowerHeaderEnum;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ProductPowerMstService;
import com.trechina.planocycle.utils.ExcelUtils;
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
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 企業cdによる商品力点数表一覧の取得
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getTableName(String companyCd) {

        String aud = session.getAttribute("aud").toString();
        List<TableNameDto> commodityData = productPowerMstMapper.getTableNameByCompanyCd(companyCd,aud);
        List<TableNameDto> basicPtsData = priorityOrderMstMapper.getTableNameByCompanyCd(companyCd,aud);
        List<TableNameDto> wholePtsData = priorityAllMstMapper.getTableNameByCompanyCd(companyCd,aud);
        List<TableNameDto> priorityData = classicPriorityOrderMstMapper.getTableNameByCompanyCd(companyCd,aud);
        Map<String,Object> tableNameMap = new HashMap<>();
        tableNameMap.put("commodityData",commodityData);
        tableNameMap.put("basicPtsData",basicPtsData);
        tableNameMap.put("wholePtsData",wholePtsData);
        tableNameMap.put("priorityData",priorityData);
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
            Set<String> finalWeightKeys = weightKeys;
            weightArray.stream().forEach(o->{
                JSONObject jsonObj = (JSONObject) o;
                finalWeightKeys.add(jsonObj.getString("colName"));
            });
            weightKeys.addAll(finalWeightKeys);
        }

        String tableName = "";
        String janInfoTableName = "";

        ProductPowerParamVo productPowerParam = productPowerDataMapper.getParam(companyCd, productPowerCd);
        JSONObject productPowerParamJson = JSON.parseObject(productPowerParam.getCommonPartsData());
        //1-自设，0-企業
        String prodIsCore = productPowerParamJson.getString("prodIsCore");
        //第数セット
        String prodMstClass = productPowerParamJson.getString("prodMstClass");

        if("0".equals(prodIsCore)){
            //0-企業
            tableName = String.format("\"%s\".prod_%s_jan_kaisou_header_sys", companyCd, prodMstClass);
            janInfoTableName = String.format("\"%s\".prod_%s_jan_info", companyCd, prodMstClass);
        }else{
            //自设company_cd
            String coreCompany = sysConfigMapper.selectSycConfig("core_company");
            //1-自设
            tableName = String.format("\"%s\".prod_%s_jan_kaisou_header_sys", coreCompany, prodMstClass);
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
        if(janName2colNum==null || Objects.equals(janName2colNum, 2)){
            //product name（品名2）
            janNameColIndex = Integer.parseInt(janName.get(0).get("sort").toString());
        }else{
            //品名1
            janNameColIndex = skuNameConfigMapper.getJanName2colNum(companyCd, prodMstClass);
        }

        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, productPowerCd);
        List<Map<String, Object>> allData = productPowerDataMapper.getDynamicAllData(companyCd, productPowerCd,
                janInfoTableName, "\""+attrColumnMap.get("jan_cd")+"\"", classify, project, janNameColIndex);

        //表示するカラムに対応するフィールド名
        List<String> attr = classify.stream().map(map -> map.get("attr").toString()).collect(Collectors.toList());
        Map<String, List<String>> columnsByClassify = this.initColumnClassify(attr);
        //表示する列に対応するヘッダー
        List<String> attrName = classify.stream().map(map -> map.get("attr_val").toString()).collect(Collectors.toList());
        Map<String, List<String>> headersByClassify = this.initHeaderClassify(attrName);

        this.fillParamData(ProductPowerHeaderEnum.POS.getName(),
                posValue,paramListByGroup.get(ProductPowerHeaderEnum.POS.getCode()), headersByClassify, columnsByClassify, weightKeys);
        this.fillParamData(ProductPowerHeaderEnum.CUSTOMER.getName(),
                customerValue,paramListByGroup.get(ProductPowerHeaderEnum.CUSTOMER.getCode()), headersByClassify, columnsByClassify, weightKeys);
        this.fillParamData(ProductPowerHeaderEnum.INTAGE.getName(),
                intageValue,paramListByGroup.get(ProductPowerHeaderEnum.INTAGE.getCode()), headersByClassify, columnsByClassify, weightKeys);
        this.fillPrepareParamData(prepareValue, productPowerCd, companyCd, headersByClassify, columnsByClassify, weightKeys);

        ServletOutputStream outputStream = null;
        try {
            String productPowerName = productPowerInfo.getProductPowerName();
            String fileName = String.format("%s.xlsx", productPowerName);
            String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(fileName, "utf-8"));
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            response.setHeader("Content-Disposition", format);
            outputStream = response.getOutputStream();
            ExcelUtils.generateExcel(headersByClassify, columnsByClassify, allData, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if(Objects.nonNull(outputStream)){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("io閉じる異常", e);
                }
            }
        }
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

    private Map<String, List<String>> initHeaderClassify(List<String> attrName){
        Map<String, List<String>> headersByClassify = new LinkedHashMap<>(10);
        headersByClassify.put(ProductPowerHeaderEnum.BASIC.getName(), Lists.newArrayList("JANコード", "商品名"));
        headersByClassify.put(ProductPowerHeaderEnum.CLASSIFY.getName(), attrName);
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

    private Map<String, List<String>> initColumnClassify(List<String> attr){
        Map<String, List<String>> columnsByClassify = new LinkedHashMap<>(10);
        columnsByClassify.put(ProductPowerHeaderEnum.BASIC.getName(), Lists.newArrayList("jan", "jan_name"));
        columnsByClassify.put(ProductPowerHeaderEnum.CLASSIFY.getName(), attr);
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
}
