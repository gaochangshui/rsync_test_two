package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.vo.ProductPowerMstVo;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import com.trechina.planocycle.enums.CustomerLabelEnum;
import com.trechina.planocycle.enums.PosLabelEnum;
import com.trechina.planocycle.enums.ProductPowerHeaderEnum;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityAllMstMapper;
import com.trechina.planocycle.mapper.PriorityOrderMstMapper;
import com.trechina.planocycle.mapper.ProductPowerDataMapper;
import com.trechina.planocycle.mapper.ProductPowerMstMapper;
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

@Service
public class ProductPowerMstServiceImpl implements ProductPowerMstService {
    @Autowired
    ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    PriorityAllMstMapper priorityAllMstMapper;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
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
        Map<String,Object> tableNameMap = new HashMap<>();
        tableNameMap.put("commodityData",commodityData);
        tableNameMap.put("basicPtsData",basicPtsData);
        tableNameMap.put("wholePtsData",wholePtsData);
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
        //顧客グループ
        String customerValue = param.getCustomerValue();
        //予備項目
        String prepareValue = param.getPrepareValue();
        //POS項目
        String posValue = param.getPosValue();
        String RankWeight = param.getRankWeight();
        Set<String> weightKeys = JSON.parseObject(RankWeight).keySet();

        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, productPowerCd);
        List<ProductPowerMstData> allData = productPowerDataMapper.getAllData(companyCd, productPowerCd);

        //表示するカラムに対応するフィールド名
        Map<String, List<String>> columnsByClassify = new LinkedHashMap<>(10);
        columnsByClassify.put(ProductPowerHeaderEnum.BASIC.getName(), Lists.newArrayList("jan", "skuName"));
        columnsByClassify.put(ProductPowerHeaderEnum.CLASSIFY.getName(), Lists.newArrayList("classifyBig", "classifyMiddle", "classifySmall", "classifyFine"));
        columnsByClassify.put(ProductPowerHeaderEnum.POS.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.CUSTOMER.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.PREPARE.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.POS_RANK.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.CUSTOMER_RANK.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.PREPARE_RANK.getName(), Lists.newArrayList());
        columnsByClassify.put(ProductPowerHeaderEnum.RANK.getName(), Lists.newArrayList("rankResult"));
        //表示する列に対応するヘッダー
        Map<String, List<String>> headersByClassify = new LinkedHashMap<>(10);
        headersByClassify.put(ProductPowerHeaderEnum.BASIC.getName(), Lists.newArrayList("JANコード", "商品名"));
        headersByClassify.put(ProductPowerHeaderEnum.CLASSIFY.getName(), Lists.newArrayList("大分類", "中分類", "小分類", "細分類"));
        headersByClassify.put(ProductPowerHeaderEnum.POS.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.CUSTOMER.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.PREPARE.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.POS_RANK.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.CUSTOMER_RANK.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.PREPARE_RANK.getName(), Lists.newArrayList());
        headersByClassify.put(ProductPowerHeaderEnum.RANK.getName(), Lists.newArrayList("Rank"));

        if(!Strings.isNullOrEmpty(customerValue)){
            String[] customerValues = customerValue.split(",");

            for (String code : customerValues) {
                CustomerLabelEnum customerLabelByCode = CustomerLabelEnum.getCustomerLabelByCode(Integer.valueOf(code));
                List<String> customer = headersByClassify.get(ProductPowerHeaderEnum.CUSTOMER.getName());
                customer.add(customerLabelByCode.getLable());

                List<String> customerColumn = columnsByClassify.get(ProductPowerHeaderEnum.CUSTOMER.getName());
                customerColumn.add(customerLabelByCode.getColumnName());

                if(weightKeys.contains(customerLabelByCode.getColumnName())){
                    List<String> customerRank = headersByClassify.get(ProductPowerHeaderEnum.CUSTOMER_RANK.getName());
                    customerRank.add(customerLabelByCode.getLable()+"Rank");

                    List<String> customerRankColumn = columnsByClassify.get(ProductPowerHeaderEnum.CUSTOMER_RANK.getName());
                    customerRankColumn.add(customerLabelByCode.getColumnRankName());
                }
            }
        }

        if(!Strings.isNullOrEmpty(posValue)){
            String[] posValues = posValue.split(",");

            for (String code : posValues) {
                PosLabelEnum posLabelByCode = PosLabelEnum.getPosLabelByCode(Integer.valueOf(code));
                List<String> customer = headersByClassify.get(ProductPowerHeaderEnum.POS.getName());
                customer.add(posLabelByCode.getLable());

                List<String> customerColumn = columnsByClassify.get(ProductPowerHeaderEnum.POS.getName());
                customerColumn.add(posLabelByCode.getColumnName());

                if(weightKeys.contains(posLabelByCode.getColumnName())){
                    List<String> customerRank = headersByClassify.get(ProductPowerHeaderEnum.POS_RANK.getName());
                    customerRank.add(posLabelByCode.getLable()+"Rank");

                    List<String> customerRankColumn = columnsByClassify.get(ProductPowerHeaderEnum.POS_RANK.getName());
                    customerRankColumn.add(posLabelByCode.getColumnRankName());
                }
            }
        }

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

        ServletOutputStream outputStream = null;
        try {
            String fileName = MessageFormat.format("{0}.xlsx", productPowerInfo.getProductPowerName());
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
}
