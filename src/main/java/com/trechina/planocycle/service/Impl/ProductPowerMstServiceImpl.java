package com.trechina.planocycle.service.Impl;

import com.google.common.collect.Lists;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.vo.ProductPowerMstVo;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import com.trechina.planocycle.enums.CustomerLabelEnum;
import com.trechina.planocycle.enums.PosLabelEnum;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ProductPowerMstService;
import com.trechina.planocycle.utils.ExcelUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.ServerRequest;
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

    @Override
    public Map<String, Object> getProductPowerTable(String companyCd) {
        String aud = session.getAttribute("aud").toString();
        List<TableNameDto> commodityData = productPowerMstMapper.getTableNameByCompanyCd(companyCd,aud);
        return ResultMaps.result(ResultEnum.SUCCESS,commodityData);

    }

    @Override
    public Map<String, Object> getProductPowerInfo(String companyCd, Integer productPowerCd,Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
        //将productPowerCd存到order_mst表中
        productPowerMstMapper.setProductPowerCdForMst(productPowerCd,companyCd,authorCd,priorityOrderCd);
        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, productPowerCd);
        Integer skuNum = productPowerMstMapper.getSkuNum(companyCd, productPowerCd);
        productPowerInfo.setSku(skuNum);

        return ResultMaps.result(ResultEnum.SUCCESS,productPowerInfo);
    }

    @Override
    public void downloadProductPowerInfo(String companyCd, Integer productPowerCd, HttpServletResponse response) {
        //查询出所需要的表头
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, productPowerCd);
        String customerValue = param.getCustomerValue();
        String prepareValue = param.getPrepareValue();
        String posValue = param.getPosValue();

        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, productPowerCd);
        List<ProductPowerMstData> allData = productPowerDataMapper.getAllData(companyCd, productPowerCd);

        Map<String, List<String>> columnsByClassify = new LinkedHashMap<>(4);
        columnsByClassify.put("", Lists.newArrayList("jan", "skuName"));
        columnsByClassify.put("商品分類", Lists.newArrayList("classifyBig", "classifyMiddle", "classifySmall", "classifyFine"));
        columnsByClassify.put("POS项目", Lists.newArrayList());
        columnsByClassify.put("POS项目Rank", Lists.newArrayList());
        columnsByClassify.put("顧客グループ", Lists.newArrayList());
        columnsByClassify.put("顧客グループRank", Lists.newArrayList());
        columnsByClassify.put("予備项目", Lists.newArrayList());
        columnsByClassify.put("rank", Lists.newArrayList("rankResult"));

        Map<String, List<String>> headersByClassify = new LinkedHashMap<>(4);
        headersByClassify.put("", Lists.newArrayList("JANコード", "商品名"));
        headersByClassify.put("商品分類", Lists.newArrayList("大分類", "中分類", "小分類", "細分類"));
        headersByClassify.put("POS项目", Lists.newArrayList());
        headersByClassify.put("POS项目Rank", Lists.newArrayList());
        headersByClassify.put("顧客グループ", Lists.newArrayList());
        headersByClassify.put("顧客グループRank", Lists.newArrayList());
        headersByClassify.put("予備项目", Lists.newArrayList());
        headersByClassify.put("予備项目Rank", Lists.newArrayList());
        headersByClassify.put("rank", Lists.newArrayList("Rank"));

        String[] customerValues = customerValue.split(",");
        String[] prepareValues = prepareValue.split(",");
        String[] posValues = posValue.split(",");

        if(!"".equals(customerValue) && customerValues.length>0){
            for (String code : customerValues) {
                CustomerLabelEnum customerLabelByCode = CustomerLabelEnum.getCustomerLabelByCode(Integer.valueOf(code));
                List<String> customer = headersByClassify.get("顧客グループ");
                customer.add(customerLabelByCode.getLable());

                List<String> customerRank = headersByClassify.get("顧客グループRank");
                customerRank.add(customerLabelByCode.getLable()+"Rank");

                List<String> customerColumn = columnsByClassify.get("顧客グループ");
                customerColumn.add(customerLabelByCode.getColumnName());

                List<String> customerRankColumn = columnsByClassify.get("顧客グループRank");
                customerRankColumn.add(customerLabelByCode.getColumnRankName());
            }
        }

        if(!"".equals(posValue) && posValues.length>0){
            for (String code : posValues) {
                PosLabelEnum posLabelByCode = PosLabelEnum.getPosLabelByCode(Integer.valueOf(code));
                List<String> customer = headersByClassify.get("POS项目");
                customer.add(posLabelByCode.getLable());

                List<String> customerRank = headersByClassify.get("POS项目Rank");
                customerRank.add(posLabelByCode.getLable()+"Rank");

                List<String> customerColumn = columnsByClassify.get("POS项目");
                customerColumn.add(posLabelByCode.getColumnName());

                List<String> customerRankColumn = columnsByClassify.get("POS项目Rank");
                customerRankColumn.add(posLabelByCode.getColumnRankName());
            }
        }

        if(!"".equals(prepareValue) && prepareValues.length>0){
            List<ReserveMstVo> reserve = productPowerDataMapper.getReserve(productPowerCd, companyCd);
            for (ReserveMstVo reserveMstVo : reserve) {
                List<String> customer = headersByClassify.get("予備项目");
                customer.add(reserveMstVo.getDataName());
                List<String> customerRank = headersByClassify.get("予備项目Rank");
                customerRank.add(reserveMstVo.getDataName());

                String valueCd = reserveMstVo.getValueCd()+"";
                String valueNo = valueCd.substring(valueCd.length() - 3);

                List<String> customerColumn = columnsByClassify.get("予備项目");
                customerColumn.add("item"+Integer.parseInt(valueNo));
                List<String> customerRankColumn = columnsByClassify.get("予備项目Rank");
                customerRankColumn.add("item"+Integer.parseInt(valueNo)+"Rank");
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
            e.printStackTrace();
        } finally {
            if(Objects.nonNull(outputStream)){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
