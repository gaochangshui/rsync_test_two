package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanNewVO;
import com.trechina.planocycle.entity.vo.ProductOrderAttrAndItemVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.CacheUtil;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderDataServiceImpl implements ClassicPriorityOrderDataService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private ClassicPriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private ClassicPriorityOrderCommodityNotMapper priorityOrderCommodityNotMapper;
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ProductPowerParamMstMapper productPowerParamMstMapper;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private ClassicPriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    private ClassicPriorityOrderCatepakAttributeMapper priorityOrderCatepakAttributeMapper;
    @Autowired
    private PriorityOrderCatepakMapper priorityOrderCatepakMapper;
    @Autowired
    private ClassicPriorityOrderJanProposalMapper priorityOrderJanProposalMapper;
    @Autowired
    private PriorityOrderPtsJandataMapper priorityOrderPtsJandataMapper;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private ClassicPriorityOrderResultDataMapper priorityOrderResultDataMapper;
    @Autowired
    private ClassicPriorityOrderDataService priorityOrderDataService;
    @Autowired
    private ClassicPriorityOrderJanAttributeMapper priorityOrderJanAttributeMapper;
    @Autowired
    private ClassicPriorityOrderAttributeClassifyService priorityOrderAttributeClassifyService;
    @Autowired
    private ClaasicPriorityOrderAttributeClassifyMapper priorityOrderClassifyMapper;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ClaasicPriorityOrderAttributeClassifyMapper priorityOrderMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private ClassicPriorityOrderJanCardService priorityOrderJanCardService;
    /**
     * 优先顺位表初期设定数据
     *
     * @param priorityOrderDataForCgiDto
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderData(PriorityOrderDataForCgiDto priorityOrderDataForCgiDto) {
        // 从cgi获取数据
        logger.info("优先顺位表初期设定数据参数" + priorityOrderDataForCgiDto);
        Map<String, Object> Data = new HashMap<>();
        //没有下面这俩参数代表直接跳转画面，有既存数据
        if (priorityOrderDataForCgiDto.getMode().equals("priority_data")) {

            PriorityOrderMstDto priorityOrderMstDto = new PriorityOrderMstDto();
            priorityOrderMstDto.setCompanyCd(priorityOrderDataForCgiDto.getCompany());
            priorityOrderMstDto.setPriorityOrderCd(priorityOrderDataForCgiDto.getPriorityNO());
            // 抽出attrbutecd
            String attrcd = priorityOrderDataMapper.selectPriorityAttrCd(priorityOrderDataForCgiDto.getCompany(),
                    priorityOrderDataForCgiDto.getPriorityNO());
            String attrValue = priorityOrderDataMapper.selectPriorityAttrValue(priorityOrderDataForCgiDto.getCompany(),
                    priorityOrderDataForCgiDto.getPriorityNO());
            //最終表をテンポラリ・テーブルに戻す
            priorityOrderJanCardMapper.deleteByPrimaryKey(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());
            priorityOrderJanCardMapper.setWorkForFinal(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());

            priorityOrderJanNewMapper.delete(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());
            priorityOrderJanNewMapper.setWorkForFinal(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());

            priorityOrderJanAttributeMapper.deleteByPrimaryKey(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());
            priorityOrderJanAttributeMapper.setWorkForFinal(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());

            priorityOrderJanProposalMapper.deleteByPrimaryKey(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());
            priorityOrderJanProposalMapper.setWorkForFinal(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());

            priorityOrderCatepakMapper.deleteByPrimaryKey(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());
            priorityOrderCatepakMapper.setWorkForFinal(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());

            priorityOrderCatepakAttributeMapper.deleteByPrimaryKey(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());
            priorityOrderCatepakAttributeMapper.setWorkForFinal(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());

            priorityOrderMapper.delete(priorityOrderDataForCgiDto.getPriorityNO());
            priorityOrderMapper.insertWork(priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getPriorityNO());

            logger.info("抽出的优先顺位表属性cd" + attrcd);

            priorityOrderMstDto.setAttributeCd(attrValue);
//            Data = priorityOrderMstService.priorityDataWRFlag(priorityOrderMstDto, new String[0], "read");
            String[] colName = attrcd.split(",");
            for (int i = 0; i < colName.length; i++) {
                colName[i] = "attr" +colName[i];
                if (i==colName.length-1){
                    colName[i] = "mulit_attr";
                }
            }

            List<PriorityOrderMstAttrSort> attrSort = priorityOrderDataMapper.getAttrSort(priorityOrderDataForCgiDto.getCompany(), priorityOrderDataForCgiDto.getPriorityNO());
            String [] c =new String[attrSort.size()];
            for (int i = 0; i < attrSort.size(); i++) {
                if (attrSort.get(i).getValue() == colName.length){
                    c[i] = "mulit_attr";
                }else {
                    c[i] = "attr"+attrSort.get(i).getValue();
                }

            }
            List<String> col = Arrays.asList(c);
            priorityOrderDataForCgiDto.setOrderCol(col);
            List<Map<String, Object>> dataList = priorityOrderResultDataMapper.selectFinalData(priorityOrderMstDto.getPriorityOrderCd(), priorityOrderMstDto.getCompanyCd(),col);
            JSONArray jsonArray =  new JSONArray();
            JSONObject itemObj = null;
            for (Map<String, Object> itemMap : dataList) {
                itemObj = new JSONObject();
                for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                    if(!entry.getKey().startsWith("attr") || Arrays.stream(colName).anyMatch(s -> s.equals(entry.getKey()))){
                        itemObj.put(entry.getKey(), entry.getValue());
                    }
                }
                jsonArray.add(itemObj);
            }
            Data.put("data", jsonArray.toString());
            logger.info("colName",colName);

            logger.info("优先顺位表既存cgi返回数据：" + Data);
        } else {
            // 初始化数据
            String uuid = UUID.randomUUID().toString();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String path = resourceBundle.getString("PriorityOrderData");
            priorityOrderDataForCgiDto.setGuid(uuid);
            // 获取品名和商品cd
//            ProductOrderAttrAndItemVO productOrderAttrAndItemVO = commodityScoreMasterService.getAttrAndItmemInfo(
//                    priorityOrderDataForCgiDto.getCompany(),priorityOrderDataForCgiDto.getProductPowerNo()
//            );
            String company = priorityOrderDataForCgiDto.getCompany();
            Integer priorityNO = priorityOrderDataForCgiDto.getPriorityNO();
            ProductOrderAttrAndItemVO productOrderAttrAndItemVO = productPowerParamMstMapper.selectAttrAndValue(
                    priorityOrderDataForCgiDto.getCompany(), priorityOrderDataForCgiDto.getProductPowerNo());
            priorityOrderDataForCgiDto.setAttributeCd(productOrderAttrAndItemVO.getAttrStr());
            priorityOrderDataForCgiDto.setItemFlg(productOrderAttrAndItemVO.getItemFlg());
            priorityOrderDataForCgiDto.setProductNmFlag(productOrderAttrAndItemVO.getItemFlg());
            priorityOrderDataForCgiDto.setWriteReadFlag("read");
            priorityOrderDataForCgiDto.setMode("priority_shoki");

            String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
            logger.info("调用cgi获取优先顺位表的参数：" + priorityOrderDataForCgiDto);
            //递归调用cgi，首先去taskid
            String result = cgiUtil.postCgi(path, priorityOrderDataForCgiDto, tokenInfo);
            logger.info("taskId返回：" + result);
            String queryPath = resourceBundle.getString("TaskQuery");
            //带着taskId，再次请求cgi获取运行状态/数据
            Data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
            logger.info("优先顺位表cgi返回数据：" + Data);
            //delete old data
            priorityOrderJanAttributeMapper.deleteByPrimaryKey(company, priorityNO);
            priorityOrderCatepakMapper.deleteByPrimaryKey(company, priorityNO);
            priorityOrderCatepakAttributeMapper.deleteByPrimaryKey(company, priorityNO);
            priorityOrderJanCardMapper.deleteByPrimaryKey(company, priorityNO);
            priorityOrderJanProposalMapper.deleteByPrimaryKey(company, priorityNO);
            priorityOrderJanNewMapper.delete(company, priorityNO);
            priorityOrderMstAttrSortMapper.deleteByPrimaryKey(company, priorityNO);
        }

        if (Data.get("data") != null) {
            JSONArray datas = (JSONArray) JSONArray.parse(Data.get("data").toString());
            this.commParseJsonArray(datas, priorityOrderDataForCgiDto.getCompany(), priorityOrderDataForCgiDto.getPriorityNO());
            //janProposalData(priorityOrderDataForCgiDto, path, cgiUtils, tokenInfo, queryPath);
            //查询
            List<Map<String, Object>> results = priorityOrderDataMapper.selectTempData(priorityOrderDataForCgiDto.getOrderCol(),
                        "public.priorityorder" + session.getAttribute("aud").toString(),priorityOrderDataForCgiDto.getMode());

       /*     results.forEach(item -> {
                if (item.get("rank_prop").toString().equals("99999999")) {
                    item.put("rank_prop", "カット");
                }
                if (item.get("rank_prop").toString().equals("99999998")) {
                    item.put("rank_prop", "_");
                }
                if (item.get("rank_upd").toString().equals("99999999")) {
                    item.put("rank_upd", "カット");
                }
                if (item.get("rank_upd").toString().equals("99999998")) {
                    item.put("rank_upd", "_");
                }
                if (item.get("rank").toString().equals("-1")) {
                    item.put("rank", "新規");
                }
                if (item.get("rank").toString().equals("99999998")) {
                    item.put("rank", "_");
                }
            });*/
            String a =null;
            Double b =0.0;
            for (Map<String, Object> stringObjectMap : results) {
                if ("_".equals(stringObjectMap.get("pos_before_rate").toString()) || "".equals(stringObjectMap.get("pos_before_rate").toString())){
                    continue;
                }
                a=stringObjectMap.get("pos_before_rate").toString();
                String[] split = a.split("%");
                DecimalFormat format=new DecimalFormat("#0.00");
                a = format.format(Double.parseDouble(split[0]))+"%";//保留2为小数

                stringObjectMap.put("pos_before_rate",a);
            }
            Map<String, Object> colMap = new HashMap<>();
            colMap.put("col", ((JSONObject) datas.get(0)).keySet().stream().sorted());
            results.add(0, colMap);
            if(!priorityOrderDataForCgiDto.getMode().equals("priority_shoki")){

            }
            return ResultMaps.result(ResultEnum.SUCCESS, results);
        } else {
            return Data;
        }
    }

    private void commParseJsonArray(JSONArray datas, String company, Integer priorityNO){
        List<ClassicPriorityOrderJanNew> janNewList = priorityOrderJanNewMapper.selectJanNameFromJanNewByCompanyAndCd(company, priorityNO);
        // 有新规Jan的时候，将新规JAN中名字为'_'的替换成新规JAN表中的数据。
        if (!janNewList.isEmpty()) {
            for (int i = 0; i < datas.size(); i++) {
                JSONObject obj = datas.getJSONObject(i);
                for (ClassicPriorityOrderJanNew janObj : janNewList) {
                    if (janObj.getJanNew().equals(obj.get("jan_new")) && "_".equals(obj.get("sku"))) {
                        obj.put("sku", janObj.getNameNew());
                    }
                }
            }
        }
        priorityOrderData(datas);
    }

    // 查询属性名
    @Override
    public List<Map<String, Object>> getAttrName(Integer productPowerCd) {
        List<Map<String, Object>> attrName = priorityOrderDataMapper.selectPriorityAttrName(productPowerCd);
        return attrName;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void downloadForCsv(DownloadSortDto downloadDto, HttpServletResponse response) throws IOException {
        String companyCd = downloadDto.getCompanyCd();
        Integer priorityOrderCd = downloadDto.getPriorityOrderCd();
        downloadDto.getList().stream().forEach(item->{
            item.setPriorityOrderCd(downloadDto.getPriorityOrderCd());
            item.setCompanyCd(downloadDto.getCompanyCd());
        });
            //修改jan属性cd
        if (!downloadDto.getList().isEmpty()) {
            priorityOrderAttributeClassifyService.setClassifyList(downloadDto.getList());
        }
            String [] version = {"共通棚割情報","V1.0","NS"};
            String [] headers = {"棚台番号","棚段番号","棚位置","商品コード","フェース数","フェース面","フェース回転","積上数","陳列種別"};
            String  fileName = "品揃えPTS_20220401"+System.currentTimeMillis()+".csv";
            String tablename = "public.priorityorder" + session.getAttribute("aud").toString();

        String mode = downloadDto.getMode();
        List<DownloadDto> datas = null;
//        if("priority_data".equals(mode)){
//            datas = priorityOrderDataMapper.downloadSavedForCsv(downloadDto.getTaiCd(), downloadDto.getTanaCd(), companyCd, priorityOrderCd);
//        }else{
        datas = priorityOrderDataMapper.downloadForCsv(downloadDto.getTaiCd(), downloadDto.getTanaCd(), tablename,downloadDto.getPriorityOrderCd());
//        }

        datas.stream().forEach(item->{
            item.setCompanyCd(downloadDto.getCompanyCd());
            item.setPriorityOrderCd(downloadDto.getPriorityOrderCd());
        });

        priorityOrderPtsJandataMapper.delete(companyCd,priorityOrderCd);
            priorityOrderPtsJandataMapper.insert(datas);
            response.setHeader(HttpHeaders.CONTENT_TYPE, "text/csv;charset=utf-8");
            OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
            String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(fileName, "utf-8"));
            response.setHeader("Content-Disposition", format);
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            writer.write(new String(bom));
            writer.flush();
            try(CsvWriter csvWriter = CsvWriter.builder().build(writer)) {
                csvWriter.writeRow(version);
                csvWriter.writeRow("");
                csvWriter.writeRow(headers);
                List<String> janData = null;
                for (DownloadDto data : datas) {
                    janData = Lists.newArrayList(String.valueOf(data.getTaiCd()),String.valueOf(data.getTanaCd()),String.valueOf(data.getTanapositionCd()), data.getJan()
                    ,"1","1","0","1","1");
                    csvWriter.writeRow(janData);

                }
            }



    }

    @Override
    public Map<String, Object> getPriorityOrderDataForSmt(String [] jans,String companyCd,Integer priorityOrderCd,Integer productPowerCd) {


        String uuid = UUID.randomUUID().toString();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        ClassicPriorityOrderJanCgiDto priorityOrderJanCgiDto = new ClassicPriorityOrderJanCgiDto();
        priorityOrderJanCgiDto.setMode("priority_janzokusei");
        priorityOrderJanCgiDto.setCompany(companyCd);
        priorityOrderJanCgiDto.setGuid(uuid);
        priorityOrderJanCgiDto.setDataArray(jans);
        priorityOrderJanCgiDto.setUsercd(session.getAttribute("aud").toString());
        ProductOrderAttrAndItemVO productOrderAttrAndItemVO = productPowerParamMstMapper.selectAttrAndValue(
                companyCd, productPowerCd);
        priorityOrderJanCgiDto.setAttributeCd(productOrderAttrAndItemVO.getAttrStr());
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        Map<String,Object> Data = null;
        logger.info("获取新jan信息，传给smt的参数为：{}",priorityOrderJanCgiDto);
        String result = cgiUtil.postCgi(path, priorityOrderJanCgiDto, tokenInfo);
        logger.info("taskId返回：" + result);
        String queryPath = resourceBundle.getString("TaskQuery");
        //带着taskId，再次请求cgi获取运行状态/数据
        Data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);

        return Data;
    }

    @Override
    public Map<String, Object> getPatternAndName(Integer productPowerCd) {
        GetPatternForProductPowerCd patternAndName = productPowerMstMapper.getPatternAndName(productPowerCd);
        if (patternAndName == null){
            return ResultMaps.result(ResultEnum.SUCCESS,patternAndName);
        }
        if (!"".equals(patternAndName.getShelfPatternCd()) && patternAndName.getShelfPatternCd()!=null) {
            Integer[] integers = Arrays.stream(String.valueOf(patternAndName.getShelfPatternCd()).split(",")).map(Integer::valueOf).toArray(Integer[]::new);
            patternAndName.setShelfPatternCd(integers);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,patternAndName);
    }

    @Override
    public Map<String, String> checkIsJanNew(List<String> janList, String company, Integer priorityOrderCd, String tableName) {
        Map<String, String> janMsg = new HashMap<>(2);
        if(janList.isEmpty()){
            return janMsg;
        }
        List<String> existJanOld = priorityOrderDataMapper.existJanOld(janList, tableName);
        List<String> existJanNew = priorityOrderDataMapper.existJanNew(janList, tableName);

        if(!existJanOld.isEmpty() || !existJanNew.isEmpty()){
            existJanOld.addAll(existJanNew);
            existJanOld = existJanOld.stream().distinct().collect(Collectors.toList());
            for (String old : existJanOld) {
                if(Strings.isNullOrEmpty(janMsg.getOrDefault(old, ""))){
                    janMsg.put(old, "現状棚に並んでいる可能性がありますので削除してください。");
                }
            }
        }

        List<String> replaceExistJanNew = priorityOrderJanReplaceMapper.existJanNew(janList, company);
        List<String> replaceExistJanOld = priorityOrderJanReplaceMapper.existJanOld(janList, company);

        if(!replaceExistJanNew.isEmpty() || !replaceExistJanOld.isEmpty()){
            replaceExistJanOld.addAll(replaceExistJanNew);
            replaceExistJanOld = replaceExistJanOld.stream().distinct().collect(Collectors.toList());
            for (String old : replaceExistJanOld) {
                if(Strings.isNullOrEmpty(janMsg.getOrDefault(old, ""))) {
                    janMsg.put(old, "すでにJAN変商品として入力済みです。");
                }
            }
        }

        List<String> proposalExistJanNew = priorityOrderJanProposalMapper.existJanNew(janList, company, priorityOrderCd);
        List<String> proposalExistJanOld = priorityOrderJanProposalMapper.existJanOld(janList, company, priorityOrderCd);
        if(!proposalExistJanNew.isEmpty() || !proposalExistJanOld.isEmpty()){
            proposalExistJanOld.addAll(proposalExistJanNew);
            proposalExistJanOld = proposalExistJanOld.stream().distinct().collect(Collectors.toList());
            for (String old : proposalExistJanOld) {
                if(Strings.isNullOrEmpty(janMsg.getOrDefault(old, ""))) {
                    janMsg.put(old, "すでにJAN変商品として入力済みです。");
                }
            }
        }

        return janMsg;
    }


    @Override
    public Map<String, Object> getUploadPriorityOrderData() {
        List<Map<String, Object>> result = new ArrayList<>();
        String authorCd = session.getAttribute("aud").toString();
        String tableName = "priorityorder"+authorCd;

        List<String> colNameList = priorityOrderDataMapper.selectTempColName(tableName);
        String attrList = cacheUtil.get(authorCd).toString();
        cacheUtil.remove(authorCd);
        List<String> attrSort = Arrays.stream(attrList.split(",")).collect(Collectors.toList());
        result.add(ImmutableMap.of("col", colNameList.stream().sorted()));
        List<Map<String, Object>> tempData = priorityOrderDataMapper.selectTempDataByRankUpd(attrSort, tableName);
        tempData.forEach(item -> {
            if (item.get("rank").toString().equals("-1")) {
                item.put("rank", "新規");
            }
            if (item.get("rank_upd").toString().equals("99999999")) {
                item.put("rank_upd", "カット");
            }
        });
        result.addAll(tempData);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> uploadPriorityOrderData(MultipartFile file, String mode, String company, String taiCd,
                                                       String tanaCd, Integer priorityOrderCd, String attrList,Integer productPowerCd) {
        Map<String, Object> resultMap = Maps.newHashMap();

        try {
            InputStream inputStream = file.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("Shift_JIS"));
            CsvReader csvReader = CsvReader.builder().skipEmptyRows(false).build(reader);

            Map<String, Object> checkResult = this.checkPTS(csvReader, priorityOrderCd, company);
            if(checkResult.get("code") != ResultEnum.SUCCESS.getCode()){
                return checkResult;
            }

            List<DownloadDto> uploadJanList = (List<DownloadDto>) checkResult.get("data");
            List<PriorityOrderAttributeClassify> classifyList = priorityOrderClassifyMapper.getClassifyList(company, priorityOrderCd);

            List<DownloadDto> cutJanList = new ArrayList<>(priorityOrderPtsJandataMapper.selectCutJan(company, priorityOrderCd, uploadJanList));
            if(!cutJanList.isEmpty()){
                ClassicPriorityOrderDataService dataService = applicationContext.getBean(ClassicPriorityOrderDataService.class);
                dataService.doJanCut(cutJanList, company, priorityOrderCd);
            }

            List<DownloadDto> newJanList = new ArrayList<>(priorityOrderPtsJandataMapper.selectNewJan(company, priorityOrderCd, uploadJanList));
            List<ClassicPriorityOrderJanNew> priorityOrderJanNews = null;
            if(!newJanList.isEmpty()){
                ClassicPriorityOrderDataService dataService = applicationContext.getBean(ClassicPriorityOrderDataService.class);
                resultMap = dataService.doJanNew(newJanList, company, priorityOrderCd, productPowerCd, attrList, classifyList);
                priorityOrderJanNews = (List<ClassicPriorityOrderJanNew>) resultMap.getOrDefault("data", Lists.newArrayList());
            }
            String authorCd = session.getAttribute("aud").toString();
            String tableName = "public.priorityorder"+authorCd;

            List<String> newJanCdList = newJanList.stream().map(DownloadDto::getJan).collect(Collectors.toList());
            List<String> cutJanCdList = cutJanList.stream().map(DownloadDto::getJan).collect(Collectors.toList());
            Map<String, String> janNewError = this.checkIsJanNew(newJanCdList, company, priorityOrderCd, tableName);
            List<ClassicPriorityOrderJanCard> priorityOrderJanCardList = cutJanCdList.stream().map(jan -> {
                ClassicPriorityOrderJanCard priorityOrderJanCard = new ClassicPriorityOrderJanCard();
                priorityOrderJanCard.setJanOld(jan);
                priorityOrderJanCard.setCompanyCd(company);
                priorityOrderJanCard.setPriorityOrderCd(priorityOrderCd);
                return priorityOrderJanCard;
            }).collect(Collectors.toList());
            Map<String, String> janCutError = priorityOrderJanCardService.checkIsJanCut(priorityOrderJanCardList);

            List<DownloadDto> needJanNewList = newJanList.stream().filter(jan -> !janNewError.containsKey(jan.getJan())).collect(Collectors.toList());
            List<DownloadDto> needJanCutList = cutJanList.stream().filter(jan -> !janCutError.containsKey(jan.getJan())).collect(Collectors.toList());

            if(!needJanCutList.isEmpty()){
                priorityOrderPtsJandataMapper.updateCutByJan(company, priorityOrderCd, cutJanList);
                priorityOrderDataMapper.updateCutJanByJanList(tableName, needJanCutList);
            }

            List<String> colName = priorityOrderDataMapper.selectTempColName(tableName.replace("public.", ""));
            List<Map<String, String>> keyNameLists = colName.stream().map(col -> {
                Map<String, String> map = new HashMap<>(1);
                map.put("name", col);
                return map;
            }).collect(Collectors.toList());

            if(!needJanNewList.isEmpty()){
                List<Map<String, Object>> datas = new ArrayList<>();
                List<PriorityOrderJanAttribute> attrs = priorityOrderJanAttributeMapper.selectAttributeByJan(company, priorityOrderCd, needJanNewList);
                for (DownloadDto downloadDto : needJanNewList) {
                    Map<String, Object> dataMap = new HashMap<>(16);

                    if(priorityOrderJanNews!=null){
                        Optional<ClassicPriorityOrderJanNew> firstOpt = priorityOrderJanNews.stream().filter(janNew -> janNew.getJanNew().equals(downloadDto.getJan())).findFirst();
                        firstOpt.ifPresent(priorityOrderJanNew -> dataMap.put("sku", priorityOrderJanNew.getNameNew()));
                    }

                    dataMap.put("jan_old","_");
                    dataMap.put("jan_new",downloadDto.getJan());
                    dataMap.put("rank",-1);
                    dataMap.put("rank_prop",downloadDto.getTanapositionCd());
                    dataMap.put("rank_upd",downloadDto.getTanapositionCd());
                    dataMap.put("branch_amount","_");
                    dataMap.put("unit_before_diff","_");
                    dataMap.put("sale_forecast","_");
                    dataMap.put("branch_num",0);
                    dataMap.put("branch_num_upd","_");
                    dataMap.put("unit_price","_");
                    dataMap.put("pos_amount_upd","_");
                    dataMap.put("pos_before_rate","_");
                    dataMap.put("difference","_");
                    dataMap.put("pos_amount","_");
                    dataMap.put("goods_rank",downloadDto.getTanapositionCd());
                    List<PriorityOrderJanAttribute> attributes = attrs.stream().filter(attr -> attr.getJanNew().equals(downloadDto.getJan())).collect(Collectors.toList());
                    for (int i = 0; i < attributes.size(); i++) {
                        if(i==(attributes.size()-1)){
                            dataMap.put("mulit_attr", attributes.get(i).getAttrValue());
                        }else{
                            dataMap.put("attr"+attributes.get(i).getAttrCd(), attributes.get(i).getAttrValue());
                        }
                    }
                    datas.add(dataMap);
                }
                priorityOrderDataMapper.insert(JSON.parseArray(new Gson().toJson(datas)), keyNameLists, tableName);
            }

            int batchNum = BigDecimal.valueOf(uploadJanList.size() / 1000.0).setScale(0, RoundingMode.CEILING).intValue();
            for (int i = 0; i < batchNum; i++) {
                int startIndex = i*1000;
                int endIndex = startIndex+1001;
                int finalEndIndex = Math.min(endIndex, uploadJanList.size());

                List<DownloadDto> subUploadJanList = uploadJanList.subList(startIndex, finalEndIndex);
                priorityOrderPtsJandataMapper.updatePtsJanRank(company, priorityOrderCd, subUploadJanList);
            }

            List<String> attrSort = Arrays.stream(attrList.split(",")).collect(Collectors.toList());
            List<DownloadDto> newRankList = null;
//            if("priority_data".equals(mode)){
//                //edit
//                newRankList = priorityOrderPtsJandataMapper.selectSavedJanRank(company, priorityOrderCd, attrSort);
//                priorityOrderPtsJandataMapper.updateSavedRankUpd(company, priorityOrderCd, newRankList);
//            }else{
            String[] attrArray = attrList.split(",");
            List<String> taiTana = Arrays.asList(attrArray).subList(0, 2);

            uploadJanList.stream().peek(jan->{
                Optional<PriorityOrderAttributeClassify> attr1Opt = classifyList.stream()
                        .filter(classify -> classify.getTaiCd().equals(jan.getTaiCd())).findFirst();
                attr1Opt.ifPresent(priorityOrderAttributeClassify -> jan.setAttr1(priorityOrderAttributeClassify.getAttr1()));

                Optional<PriorityOrderAttributeClassify> attr2Opt = classifyList.stream()
                        .filter(classify -> classify.getTanaCd().equals(jan.getTanaCd()) && classify.getTaiCd().equals(jan.getTaiCd())).findFirst();
                attr2Opt.ifPresent(priorityOrderAttributeClassify -> jan.setAttr2(priorityOrderAttributeClassify.getAttr2()));
            }).collect(Collectors.toList());
            priorityOrderPtsJandataMapper.updateAttr(uploadJanList, tableName, taiTana.get(0), taiTana.get(1));

            newRankList = priorityOrderPtsJandataMapper.selectJanRank(company, priorityOrderCd, attrSort, tableName);
            priorityOrderPtsJandataMapper.updateRankUpd(newRankList, tableName, taiTana.get(0), taiTana.get(1));
            cacheUtil.put(authorCd, attrList);
//            }
        } catch (IOException e) {
            logger.error("", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        if (Objects.equals(ResultEnum.SUCCESS_BUT_NEW_JAN.getCode(), resultMap.getOrDefault("code", ""))) {
            return ResultMaps.result(ResultEnum.SUCCESS_BUT_NEW_JAN);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public void doJanCut(List<DownloadDto> cutJanList, String company, Integer priorityOrderCd){
        List<String> existJanCut = priorityOrderJanCardMapper.selectExistJanCut(company, priorityOrderCd, cutJanList);
        List<DownloadDto> notExistJanCut = cutJanList.stream().filter(jan -> !existJanCut.contains(jan.getJan())).collect(Collectors.toList());

        List<ClassicPriorityOrderJanCard> notExistJanCutList = notExistJanCut.stream().map(jan -> {
            ClassicPriorityOrderJanCard janCard = new ClassicPriorityOrderJanCard();
            janCard.setJanOld(jan.getJan());
            janCard.setCompanyCd(company);
            janCard.setPriorityOrderCd(priorityOrderCd);
            return janCard;
        }).collect(Collectors.toList());
        if(!notExistJanCutList.isEmpty()){
            priorityOrderJanCardMapper.insert(notExistJanCutList);
        }
    }

    @Override
    public Map<String, Object> doJanNew(List<DownloadDto> newJanList, String company, Integer priorityOrderCd, Integer productPowerCd,
                         String attrList,List<PriorityOrderAttributeClassify> classifyList){
        priorityOrderJanNewMapper.deleteByJan(company, priorityOrderCd, newJanList);

//        List<String> newJanCdList = newJanList.stream().map(DownloadDto::getJan).collect(Collectors.toList());
//        Map<String, String> errorJan = this.checkIsJanNew(newJanCdList, company, priorityOrderCd, tableName);
//        if(!errorJan.isEmpty()){
//            return ResultEnum.JANANDOTHERCONFLICTS;
//        }
        List<String> newJanExistCdList = newJanList.stream().map(DownloadDto::getJan).collect(Collectors.toList());

        if(newJanExistCdList.isEmpty()){
            return Maps.newHashMap();
        }

        List<DownloadDto> notExistNewJan = newJanList.stream().filter(jan -> newJanExistCdList.contains(jan.getJan())).collect(Collectors.toList());
        priorityOrderPtsJandataMapper.insertNewJan(notExistNewJan);

        Map<String, Object> cgiData = getPriorityOrderDataForSmt(newJanExistCdList.toArray(new String[0]), company, priorityOrderCd,productPowerCd);
        Object data = cgiData.get("data");

        if(data==null){
            logger.error("cgi error, {}", cgiData);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        JSONArray datas = (JSONArray) JSONArray.parse(cgiData.get("data").toString());
        Map<String, Object> resultMap = Maps.newHashMap();

        //新規jan
        List<Map> maps = datas.toJavaList(Map.class);
        List<PriorityOrderJanAttribute> janAttrs = new ArrayList<>();

        String tableName = "priorityorder"+session.getAttribute("aud").toString();
        List<String> attrColumn = priorityOrderDataMapper.selectTempColName(tableName).stream()
                .filter(col->col.startsWith("attr") || col.equals("mulit_attr")).collect(Collectors.toList());

        String[] attrSort = attrList.split(",");
        attrColumn.removeAll(Arrays.asList(attrSort));
        List<String> attrSortList = Arrays.asList(attrSort);
        List<String> allAttrSortList = Lists.newArrayList(attrSortList);
        allAttrSortList.addAll( attrColumn);
        List<String> taiTana = allAttrSortList.subList(0, 2);

        List<String> janMstList = maps.stream().map(map -> map.get("jan_new").toString()).collect(Collectors.toList());
        newJanList.stream().filter(newJan->!janMstList.contains(newJan.getJan())).forEach(newJan->{
            Map<String, Object> item = new HashMap<>(16);
            item.put("jan_old",newJan.getJan());
            item.put("sku","");
            item.put("jan_new",newJan.getJan());
            item.put("rank",-1);
            item.put("rank_prop",newJan.getTanapositionCd());
            item.put("branch_amount","_");
            item.put("unit_before_diff","_");
            item.put("sale_forecast","0");
            item.put("branch_num",0);
            item.put("branch_num_upd","_");
            item.put("unit_price","_");
            item.put("pos_amount_upd","0");
            item.put("pos_before_rate","_");
            item.put("difference","_");
            item.put("pos_amount","_");
            item.put("goods_rank",newJan.getTanapositionCd());
            item.put(taiTana.get(0), newJan.getAttr1());
            item.put(taiTana.get(1), newJan.getAttr2());

            for (int i = 2; i < allAttrSortList.size(); i++) {
                item.put(allAttrSortList.get(i), "");
            }

            datas.add(JSON.parseObject(new Gson().toJson(item)));
            maps.add(item);
            resultMap.put("code", ResultEnum.SUCCESS_BUT_NEW_JAN.getCode());
        });

        maps.forEach(item->{
            for (int i = 0; i < newJanList.size(); i++) {
                DownloadDto downloadDto = newJanList.get(i);
                if (item.get("jan_new").equals(downloadDto.getJan())){
                    item.put("jan_old",downloadDto.getJan());
                    item.put("rank",-1);
                    item.put("rank_prop",downloadDto.getTanapositionCd());
                    item.put("branch_amount","_");
                    item.put("unit_before_diff","_");
                    item.put("sale_forecast","_");
                    item.put("branch_num",0);
                    item.put("branch_num_upd","_");
                    item.put("unit_price","_");
                    item.put("pos_amount_upd","_");
                    item.put("pos_before_rate","_");
                    item.put("difference","_");
                    item.put("pos_amount","_");
                    item.put("goods_rank",downloadDto.getTanapositionCd());
                    downloadDto.setName(item.get("sku").toString());
                    newJanList.set(i, downloadDto);
                }
            }
            List<Object> attrs = (List<Object>) item.keySet().stream()
                    .filter(k -> k.toString().startsWith("attr") || k.equals("mulit_attr")).collect(Collectors.toList());
            for (Object attr : attrs) {
                PriorityOrderJanAttribute janAttr = new PriorityOrderJanAttribute();

                if(taiTana.contains(attr.toString())){
                    Optional<DownloadDto> janOpt = newJanList.stream().filter(downloadDto -> downloadDto.getJan().equals(item.get("jan_new"))).findFirst();
                    if(janOpt.isPresent()){
                        DownloadDto jan = janOpt.get();
                        Optional<PriorityOrderAttributeClassify> attrOpt = classifyList.stream()
                                .filter(classify -> classify.getTanaCd().equals(jan.getTanaCd()) && classify.getTaiCd().equals(jan.getTaiCd())).findFirst();

                        if (taiTana.indexOf(attr.toString())==0) {
                            //taiTana's first element is tai attr
                            attrOpt.ifPresent(priorityOrderAttributeClassify -> janAttr.setAttrValue(attrOpt.get().getAttr1()));
                        }else if(taiTana.indexOf(attr.toString())==1){
                            //taiTana's second element is tana attr
                            attrOpt.ifPresent(priorityOrderAttributeClassify -> janAttr.setAttrValue(attrOpt.get().getAttr2()));
                        }
                    }
                }else{
                    janAttr.setAttrValue(item.get(attr).toString());
                }
                String attrCd = attr.toString();
                if(attr.toString().equals("mulit_attr")){
                    attrCd = "attr"+attrs.size();
                }
                janAttr.setAttrCd(Integer.parseInt(attrCd.replace("attr", "")));
                janAttr.setJanNew(item.get("jan_new").toString());
                janAttr.setPriorityOrderCd(priorityOrderCd);
                janAttr.setCompanyCd(company);
                janAttrs.add(janAttr);
            }
        });
        if (!janAttrs.isEmpty()) {
            priorityOrderJanAttributeMapper.deleteByPrimaryKey(company,priorityOrderCd);
            priorityOrderJanAttributeMapper.insert(janAttrs);
        }

        List<ClassicPriorityOrderJanNew> priorityOrderJanNewList = newJanList.stream().map(jan -> {
            ClassicPriorityOrderJanNew janNew = new ClassicPriorityOrderJanNew();
            janNew.setJanNew(jan.getJan());
            janNew.setCompanyCd(company);
            janNew.setRank(jan.getTanapositionCd());
            janNew.setPriorityOrderCd(priorityOrderCd);
            janNew.setBranchAccount(BigDecimal.ZERO);
            janNew.setBranchnum(0);
            janNew.setNameNew(jan.getName());
            return janNew;
        }).collect(Collectors.toList());

        priorityOrderJanNewMapper.insert(priorityOrderJanNewList);
        resultMap.put("data", priorityOrderJanNewList);
        return resultMap;
    }

    private  Map<String,Object> checkPTS(CsvReader csvReader, Integer priorityOrderCd, String company){
        DownloadDto downloadDto = null;
        int rowIndex = 0;
        int tanaPositionColIndex = 2;
        int taiCdColIndex = 0;
        int tanaCdColIndex = 1;
        int janCdColIndex = 3;
        final String numberRegex = "\\d+";
        int startRowIndex = 3;
        int colCount = 9;
        //Check whether the column of CSV file is numeric
        Pattern numPattern = Pattern.compile(numberRegex);
        List<DownloadDto> uploadJanList = new ArrayList<>();

        for (CsvRow csvRow : csvReader) {
            if(csvRow.isEmpty() || csvRow.getFields().stream().allMatch(field->"".equals(field))){
                rowIndex++;
                continue;
            }

            if(rowIndex<startRowIndex){
                if(rowIndex==0){
                    String mode = csvRow.getField(1);
                    if(!"V1.0".equals(mode)){
                        return ResultMaps.result(ResultEnum.VERSION_ERROR);
                    }
                }
                rowIndex++;
                continue;
            }
            int fieldCount = csvRow.getFieldCount();
            //The PTS header is not check
            if(fieldCount!=colCount){
                logger.warn("列数エラー");
                return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
            }

            //The tanaposition column contains non numbers, error
            String tanaPosition = csvRow.getField(tanaPositionColIndex);
            if(!numPattern.matcher(tanaPosition).matches()){
                logger.warn("rankには非数値が含まれています");
                return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
            }

            downloadDto = new DownloadDto();
            downloadDto.setPriorityOrderCd(priorityOrderCd);
            downloadDto.setCompanyCd(company);

            String taiField = csvRow.getField(taiCdColIndex);
            if(!numPattern.matcher(taiField).matches()){
                logger.warn("taiには非数値が含まれています");
                return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
            }

            String tanaField = csvRow.getField(tanaCdColIndex);
            if(!numPattern.matcher(tanaField).matches()){
                logger.warn("tanaには非数値が含まれています");
                return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
            }
            String janField = csvRow.getField(janCdColIndex).trim();
            if(Strings.isNullOrEmpty(janField)){
                logger.warn("janには空が含まれています");
                return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
            }

            downloadDto.setTaiCd(Integer.parseInt(taiField));
            downloadDto.setTanaCd(Integer.parseInt(tanaField));
            downloadDto.setTanapositionCd(Integer.parseInt(tanaPosition));
            downloadDto.setJan(janField);
            uploadJanList.add(downloadDto);
            rowIndex++;
        }

        Map<String, List<DownloadDto>> janCount = uploadJanList.stream().collect(Collectors.groupingBy(DownloadDto::getJan));
        boolean isExistRepeatJan = janCount.values().removeIf(data -> data.size() > 1);

        if(isExistRepeatJan){
            logger.warn("duplicate Jan exists");
            return ResultMaps.result(40003, "jan repeat");
        }

        Set<String> allAttrList = uploadJanList.stream().map(jan -> jan.getTaiCd() + "_" + jan.getTanaCd()).collect(Collectors.toSet());
        Set<String> ptsAllAttrList = priorityOrderClassifyMapper.selectDiffJanTaiTana(company, priorityOrderCd);
        //upload csv file have tai and tana but download file have not
        Sets.SetView<String> difference = Sets.difference(allAttrList, ptsAllAttrList);
        if (!difference.isEmpty()) {
            // There is an undefined tai and tana in the CSV file
            logger.warn("tai_tana not exist,{}", difference);
            return ResultMaps.result(ResultEnum.CLASSIFY_NOT_EXIST);
        }

        return ResultMaps.result(ResultEnum.SUCCESS, uploadJanList);
    }

    private JSONArray priorityOrderData(JSONArray datas) {
        // 保存数据为临时表
//        JSONArray datas = new JSONArray(Data);
        List<Map<String, String>> keyNameList = new ArrayList<>();
        //拿到表头
        colNameList(datas, keyNameList);
        logger.info("打印创建临时表前的表头" + keyNameList.toString());
        logger.info(keyNameList.toString());
        String name = "";
        Integer nameId = 0;
        List<Map<String, String>> finalKeyName = new ArrayList<>();

        //临时存数据的实体表 priorityorder+社员号
        String tablename = "public.priorityorder" + session.getAttribute("aud").toString();
        logger.info("创建的表名" + tablename);
        //初始化建表
        priorityOrderDataMapper.dropTempData(tablename);
        priorityOrderDataMapper.updateTempData(keyNameList, tablename);
        //写数据

        priorityOrderDataMapper.insert(datas, keyNameList, tablename);

        return datas;
    }


    /**
     * 优先顺位表反应按钮抽出数据
     *
     * @param colNameList
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderDataUpd(List<String> colNameList, Integer priorityOrderCd,Integer delFlg,String companyCd) {
        List<String> attribute = priorityOrderJanAttributeMapper.getAttribute(priorityOrderCd);
        System.out.println(attribute);
        logger.info("优先顺位表反应按钮抽出数据参数" + colNameList);
        // 获取所有列名
        List<String> colName = priorityOrderDataMapper.selectTempColName("priorityorder" + session.getAttribute("aud").toString());
        Iterator<String> iterator = colName.iterator();
        while ( iterator.hasNext()){
            String next = iterator.next();
            if (next.equals("goods_rank")){
                iterator.remove();
            }
        }
        logger.info("优先顺位表临时表抽出表头" + colName);
        List<Map<String, Object>> result;
        if (colNameList.size() > 0) {
            if (delFlg == 0) {
                result = priorityOrderDataMapper.selectTempDataAndMst(colNameList, colName, priorityOrderCd,
                        "public.priorityorder" + session.getAttribute("aud").toString());
            }else {
                priorityOrderDataService.getPriorityOrderListInfo(companyCd,priorityOrderCd);
                result = priorityOrderDataMapper.getTempDataAndMst(colNameList,colName,priorityOrderCd,
                        "public.priorityorder" + session.getAttribute("aud").toString());
            }
        } else {
            if (delFlg == 0) {
                result = priorityOrderDataMapper.selectTempDataCol(null, priorityOrderCd,
                        "public.priorityorder" + session.getAttribute("aud").toString());
            }else {
                priorityOrderDataService.getPriorityOrderListInfo(companyCd,priorityOrderCd);
                result = priorityOrderDataMapper.getTempDataAndMst(null,colName,priorityOrderCd,
                        "public.priorityorder" + session.getAttribute("aud").toString());
            }
        }
        String a=null;
        Double b =0.0;
        for (Map<String, Object> stringObjectMap : result) {
            if ("_".equals(stringObjectMap.get("pos_before_rate").toString()) || "".equals(stringObjectMap.get("pos_before_rate").toString())){
                continue;
            }
            a=stringObjectMap.get("pos_before_rate").toString();
            String[] split = a.split("%");
            DecimalFormat format=new DecimalFormat("#0.00");
            a = format.format(Double.parseDouble(split[0]))+"%";//保留2为小数

            stringObjectMap.put("pos_before_rate",a);
        }
        logger.info("优先顺位表临时表抽出数据" + result);

        priorityOrderDataMapper.setRank(result,"public.priorityorder" + session.getAttribute("aud").toString());

        //if(!colNameList.isEmpty()){
        //    result.forEach(item -> {
        //        if (item.get("rank").toString().equals("-1")) {
        //            item.put("rank", "新規");
        //        }
        //        if (item.get("rank_prop").toString().equals("99999999") || item.get("rank_upd").toString().equals("99999999")) {
        //            item.put("rank_prop", "カット");
        //            item.put("rank_upd", "カット");
        //        }
        //    });
        //}

        Map<String, Object> colMap = new HashMap<>();
        colMap.put("col", Arrays.stream(colName.stream().toArray()).sorted());
        result.add(0, colMap);

            priorityOrderMapper.delete(priorityOrderCd);

        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getPriorityOrderListInfo(String companyCd, Integer priorityOrderCd) {
        List<String> colName = priorityOrderDataMapper.selectTempColName("priorityorder" + session.getAttribute("aud").toString());
        String tableName = "public.priorityorder" + session.getAttribute("aud").toString();
        List<String> janCutList = priorityOrderJanCardMapper.getExistOtherMst(companyCd, priorityOrderCd);
        priorityOrderDataMapper.updateRevivification( tableName);
        if (!janCutList.isEmpty()) {

            priorityOrderDataMapper.updateUPdRank(janCutList, tableName);
        }
        List<ClassicPriorityOrderJanNewVO> janNewList = priorityOrderJanNewMapper.getExistOtherMst(companyCd, priorityOrderCd,tableName);
        priorityOrderJanNewMapper.deleteJanNew(tableName);
        if (!janNewList.isEmpty()) {
            JSONArray jsonArray = new JSONArray();

            // 遍历结果集，拆分动态列
            if (!janNewList.isEmpty()) {
                janNewList.forEach(item -> {
                    Map<String, Object> result = new HashMap<>();
                    String[] attrList = item.getAttr().split(",");
                    String[] valList;
                    result.put("jan_new", item.getJanNew());
                    result.put("jan_old", "_");
                    result.put("sku", item.getJanName());
                    for (int i = 0; i < attrList.length; i++) {
                        valList = attrList[i].split(":");
                        if (valList.length < 2) {
                            if (attrList.length == Integer.parseInt(valList[0])) {
                                result.put("mulit_attr", "");
                            } else {
                                result.put("attr" + valList[0], "");
                            }

                        } else {
                            if (attrList.length == Integer.parseInt(valList[0])) {
                                result.put("mulit_attr", valList[1]);
                            } else {
                                result.put("attr" + valList[0], valList[1]);
                            }


                        }

                    }
                    result.put("unit_before_diff", "_");
                    result.put("pos_amount", 0);
                    result.put("difference", "_");
                    result.put("pos_before_rate", "_");
                    result.put("pos_amount_upd", "_");
                    result.put("unit_price", "_");
                    result.put("branch_num_upd", "_");
                    result.put("goods_rank", 0);
                    result.put("sale_forecast", "_");
                    result.put("rank_prop", item.getRank());
                    result.put("rank_upd", item.getRank());
                    result.put("branch_num", item.getBranchNum());
                    result.put("branch_amount", item.getBranchAccount());
                    //写入jsonArray
                    jsonArray.add(result);
                });

            } else {
                //获取列头
                ClassicPriorityOrderJanNewVO colResult = priorityOrderJanNewMapper.selectColName(companyCd, priorityOrderCd);
                logger.info("获取新规商品list返回结果集e：" + colResult);
                String[] attrList = colResult.getAttr().split(",");
                String[] valList;
                List<String> results = new ArrayList<>();
                for (int i = 0; i < attrList.length; i++) {
                    valList = attrList[i].split(":");

                    if (i == attrList.length - 1) {
                        results.add("mulit_attr");
                    } else {
                        results.add("attr" + valList[0]);
                    }
                }
                results.add("unit_before_diff");
                results.add("pos_amount");
                results.add("difference");
                results.add("pos_before_rate");
                results.add("pos_amount_upd");
                results.add("unit_price");
                results.add("branch_num_upd");
                results.add("goods_rank");
                results.add("sale_forecast");
                results.add("jan_new");
                results.add("jan_old");
                results.add("sku");
                results.add("rank_prop");
                results.add("rank_upd");
                results.add("branch_num");
                results.add("branch_amount");
                jsonArray.add(results);
            }
            System.out.println(jsonArray);

            priorityOrderDataMapper.insertJanNew(jsonArray, tableName);
        }
        List<Map<String, Object>> dataList = priorityOrderDataMapper.getTmpTable(tableName,  colName, priorityOrderCd, companyCd);
        for (Map<String, Object> datas : dataList) {
            if (!datas.get("jan_new").equals(datas.get("jan_old")) && !"_".equals(datas.get("jan_old"))){
                datas.put("rank_upd",datas.get("rank_prop"));
            }
        }
        priorityOrderDataMapper.truncateTable(tableName);
        priorityOrderDataMapper.insertTmpTable(tableName,dataList);
        return ResultMaps.result(ResultEnum.SUCCESS,dataList);
    }


    public void colNameList(JSONArray datas, List<Map<String, String>> keyNameList) {
        boolean isGoodsExist=false;
        for (int i = 0; i < ((JSONObject) datas.get(0)).keySet().toArray().length; i++) {
            Map<String, String> maps = new HashMap<>();
            maps.put("name", (String) ((JSONObject) datas.get(0)).keySet().toArray()[i]);
            String col=(String) ((JSONObject) datas.get(0)).keySet().toArray()[i];
            if (col.equals("goods_rank")){
                isGoodsExist = true;
            }

            keyNameList.add(maps);
        }
        if (!isGoodsExist){
            Map<String, String> maps = new HashMap<>();
            maps.put("name","goods_rank");
            keyNameList.add(maps);

        }

    }
}