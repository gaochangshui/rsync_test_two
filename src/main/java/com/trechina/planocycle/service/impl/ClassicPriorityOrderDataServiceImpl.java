package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Strings;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.PtsDetailDataVo;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import com.trechina.planocycle.entity.vo.PtsTanaVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.CacheUtil;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.apache.commons.collections4.MapUtils;
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
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private ComparePriorityOrderPatternMapper comparePriorityOrderPatternMapper;
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
    private ClassicPriorityOrderCompareJanDataMapper classicPriorityOrderCompareJanDataMapper;
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
    private ClassicPriorityOrderCommodityMustMapper priorityOrderCommodityMustMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private ClassicPriorityOrderJanCardService priorityOrderJanCardService;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper classicPriorityOrderMstAttrSortMapper;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ClaasicPriorityOrderAttributeClassifyMapper claasicPriorityOrderAttributeClassifyMapper;
    @Autowired
    private ClassicPriorityOrderMstMapper classicPriorityOrderMstMapper;
    @Autowired
    private WorkPriorityOrderPtsClassifyMapper workPriorityOrderPtsClassify;
    @Autowired
    private ClassicPriorityOrderPatternMapper priorityOrderPatternMapper;
    @Autowired
    private ClassicPriorityOrderBranchNumServiceImpl classicPriorityOrderBranchNumService;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private LogAspect logAspect;
    @Autowired
    private IDGeneratorService idGeneratorService;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private StarReadingTableMapper starReadingTableMapper;
    @Autowired
    private MstBranchMapper mstBranchMapper;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;

    /**
     * ?????????????????????????????????????????????
     *
     * @param priorityOrderDataDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getPriorityOrderData(PriorityOrderDataDto priorityOrderDataDto) {
        String companyCd = priorityOrderDataDto.getCompanyCd();
        Integer priorityPowerCd = priorityOrderDataDto.getPriorityOrderCd();
        String authorCd = session.getAttribute("aud").toString();

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        classicPriorityOrderMstMapper.deleteWork(priorityOrderDataDto.getPriorityOrderCd());
        priorityOrderDataService.deleteWorkData(companyCd,priorityPowerCd);
        classicPriorityOrderMstMapper.setWork(priorityOrderDataDto,authorCd,simpleDateFormat.format(date));

        List<PriorityOrderPattern> priorityOrderPatternList = new ArrayList<>();
        String[] shelfPatternList = priorityOrderDataDto.getShelfPatternCd().split(",");
        String[] shelfNameList = priorityOrderDataDto.getShelfCd().split(",");
        List<Map<String, Object>> shelfNameCd = priorityOrderPatternMapper.getShelfNameCd(companyCd, shelfPatternList);
        shelfNameCd.forEach(stringObjectMap->{
            for (int i = 0; i < shelfNameList.length; i++) {
                if (shelfNameList[i].equals(stringObjectMap.get("shelfNameCd").toString())){
                    PriorityOrderPattern priorityOrderPattern = new PriorityOrderPattern();
                    priorityOrderPattern.setCompanyCd(priorityOrderDataDto.getCompanyCd());
                    priorityOrderPattern.setPriorityOrderCd(priorityOrderDataDto.getPriorityOrderCd());
                    priorityOrderPattern.setShelfPatternCd(Integer.valueOf(stringObjectMap.get("shelfPatternCd").toString()));
                    priorityOrderPattern.setSort(i+1);
                    priorityOrderPatternList.add(priorityOrderPattern);
                }
            }
        });

        logger.info("????????????????????????pattert?????????????????????????????????????????????????????????{}???",priorityOrderPatternList);
        priorityOrderPatternMapper.deleteWork(priorityOrderDataDto.getPriorityOrderCd());
        priorityOrderPatternMapper.insertWork(priorityOrderPatternList);
        //?????????????????????
       this.saveReferencePattern(priorityOrderDataDto.getReferenceData(),priorityPowerCd,companyCd);

        List<Integer> comparePtsList = shelfPtsDataMapper.getPtsCdForShelfName(companyCd, priorityPowerCd);
        List<Integer> exceptJanPtsCd = shelfPtsDataMapper.getExceptJanPtsCd(companyCd, priorityPowerCd);
        // ??????????????????
        List<ShelfPtsData> shelfPtsData = shelfPtsDataMapper.getPtsCdByPatternCd(companyCd, priorityOrderDataDto.getShelfPatternCd());
        //??????????????????2
        int existTableName = mstBranchMapper.checkTableExist(MagicString.DEFAULT_TABLE, "1000");
        String tableName ="";
        if (existTableName == 1){
             tableName = "\"1000\".prod_0000_jan_info";
        }else {
            tableName = "\""+companyCd+"\".prod_0000_jan_info";
        }


        Set<Map.Entry<String, Object>> entrySet = priorityOrderDataDto.getAttrList().entrySet();
        List<String> list = new ArrayList<>();
        List<Map<String,Object>> listAll = new ArrayList<>();
        final int[] y = {1};
        entrySet.forEach(stringObjectEntry->{
            LinkedHashMap<String,Object> maps = new LinkedHashMap<>();
            maps.put("value",stringObjectEntry.getKey());
            maps.put("name",stringObjectEntry.getValue());
            maps.put("sort","attr"+ y[0]++);
            list.add(stringObjectEntry.getKey());
            listAll.add(maps);
        });
        classicPriorityOrderMstAttrSortMapper.deleteAttrWk(companyCd,priorityPowerCd);
        classicPriorityOrderMstAttrSortMapper.insertAttrWk(companyCd,priorityPowerCd,listAll);
        List<Map<String,Object>> listAttr = new ArrayList<>();
        Map<String,Object> listTableName = new HashMap<>();
        LinkedHashMap<String,Object> mapColHeader = new LinkedHashMap<>();
        mapColHeader.put(MagicString.JAN_OLD,"???JAN");
        mapColHeader.put(MagicString.JAN_NEW,"???JAN");
        mapColHeader.put("sku","SKU");

        final int[] i = {1};
        list.forEach(s->{
            Map<String,Object> map = new HashMap<>();
            String[] s1 = s.split("_");
            map.put(MagicString.COMPANY_CD,s1[0]);
            map.put("isCore",s1[1]);
            map.put("col","\""+s1[2]+"\"");
            map.put("tableName","\""+s1[0]+s1[1]+"\"");
            map.put("colName","attr"+ i[0]);
            listAttr.add(map);
            String tableNameInfo = MessageFormat.format("\"{0}\".prod_{1}_jan_info", s1[0], s1[1]);
            String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", s1[0], s1[1]);
            String tableNameKaisou = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", s1[0], s1[1]);

            String colName = priorityOrderDataMapper.getColName(tableNameAttr, tableNameKaisou, s1[2],s1[0]);
            mapColHeader.put("attr"+ i[0],colName);
            listTableName.put("\""+s1[0]+s1[1]+"\"",tableNameInfo);
            i[0]++;
        });
            mapColHeader.put(MagicString.BRANCH_AMOUNT_UPD,MagicString.BRANCH_AMOUNT_NAME);
            mapColHeader.put(MagicString.POS_AMOUNT,"POS??????(???)");
            mapColHeader.put(MagicString.UNIT_PRICE,"??????");
            mapColHeader.put(MagicString.BRANCH_AMOUNT,MagicString.BRANCH_AMOUNT_NAME);
            mapColHeader.put(MagicString.BRANCH_NUM,MagicString.BRANCH_NUM_NAME);
            mapColHeader.put(MagicString.BRANCH_NUM_UPD,MagicString.BRANCH_NUM_NAME);
            mapColHeader.put(MagicString.DIFFERENCE,"?????????");
            mapColHeader.put(MagicString.SALE_FORECAST,"???????????? ??????(??????)");
            mapColHeader.put(MagicString.RANK,"??????Rank");
            mapColHeader.put(MagicString.RANK_PROP,"??????Rank");
            mapColHeader.put(MagicString.RANK_UPD,"??????Rank");
            mapColHeader.put(MagicString.ACTUALITY_ALL_NUM,"???????????? ?????????");
            mapColHeader.put(MagicString.ACTUALITY_COMPARE_NUM,"???????????? ?????????");
            mapColHeader.put(MagicString.UPDATE_ALL_NUM,"???????????? ?????????");
            List<Map<String, Object>> initialExtraction = new ArrayList<>();
            initialExtraction.add(mapColHeader);
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderDataDto.getProductPowerCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
        String colName = "";
        if (param.getJanName2colNum() == 1){
            colName = "1";
        }else if (param.getJanName2colNum() == 2){
            colName = "2";
        }else {
            colName = productPowerDataMapper.getItemCol(commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        }
        comparePtsList.add(0);
        exceptJanPtsCd.add(0);
        List<Map<String, Object>> datas = shelfPtsDataMapper.getInitialExtraction(shelfPtsData, tableName
                , priorityOrderDataDto.getProductPowerCd(), listTableName, listAttr,colName,comparePtsList,exceptJanPtsCd);

        if (datas.isEmpty()){
            return ResultMaps.result(ResultEnum.SIZEISZERO);
        }
        datas = this.allBranchNum(datas);
        for (Map<String, Object> data : datas) {
            BigDecimal branchAmountUpd = BigDecimal.valueOf(Double.parseDouble(data.get(MagicString.BRANCH_AMOUNT_UPD).toString()));
            branchAmountUpd = branchAmountUpd.setScale(0,RoundingMode.HALF_UP);
            data.put(MagicString.BRANCH_AMOUNT_UPD,branchAmountUpd);

            BigDecimal branchAmount = BigDecimal.valueOf(Double.parseDouble(data.get(MagicString.BRANCH_AMOUNT).toString()));
            branchAmount = branchAmount.setScale(0,RoundingMode.HALF_UP);
            data.put(MagicString.BRANCH_AMOUNT,branchAmount);

            BigDecimal unitPrice = BigDecimal.valueOf(Double.parseDouble(data.get(MagicString.UNIT_PRICE).toString()));
            unitPrice = unitPrice.setScale(0,RoundingMode.HALF_UP);
            data.put(MagicString.UNIT_PRICE,unitPrice);
        }

        priorityOrderDataMapper.deleteWorkData(companyCd,priorityPowerCd);
        priorityOrderDataMapper.insertWorkData(companyCd,priorityPowerCd,datas,authorCd);
        for (Map<String, Object> data : datas) {
            data.remove(MagicString.GOODS_RANK);
            if (Integer.parseInt(data.get("rank").toString())==99999998){
                data.put(MagicString.RANK,"_");
                data.put(MagicString.RANK_UPD,"_");
                data.put(MagicString.RANK_PROP,"_");
            }
        }
        initialExtraction.addAll(datas);
            //delete old data
    return ResultMaps.result(ResultEnum.SUCCESS,initialExtraction);

    }

    /**
     *
     * @param referenceData
     * @param priorityPowerCd
     * @param companyCd
     */
    private void saveReferencePattern(List<Map<String, Object>> referenceData, Integer priorityPowerCd, String companyCd) {
        List<ComparePriorityOrderPattern> comparePriorityOrderPatternList = new ArrayList<>();
        for (Map<String, Object> referenceDatum : referenceData) {
            ComparePriorityOrderPattern comparePriorityOrderPattern = new ComparePriorityOrderPattern();
            comparePriorityOrderPattern.setPriorityOrderCd(priorityPowerCd);
            comparePriorityOrderPattern.setCompanyCd(companyCd);
            comparePriorityOrderPattern.setShelfNameCd(Integer.parseInt(referenceDatum.get("shelfCd").toString()));
            boolean reference = (boolean)referenceDatum.get("reference");
            boolean flag = (boolean)referenceDatum.get("flag");
            comparePriorityOrderPattern.setCompareFlag(reference?1:0);
            comparePriorityOrderPattern.setRepeatFlag(flag?1:0);
            comparePriorityOrderPatternList.add(comparePriorityOrderPattern);
        }
        comparePriorityOrderPatternMapper.delWk(priorityPowerCd);
        if (!comparePriorityOrderPatternList.isEmpty()) {
            comparePriorityOrderPatternMapper.insertWK(comparePriorityOrderPatternList);
        }
    }

    private List<Map<String, Object>> allBranchNum(List<Map<String, Object>> datas) {
        datas.stream().forEach(map->{
            String branch = map.getOrDefault("branch","").toString();
            String compareBranch = map.getOrDefault("actuality_compare_branch","").toString();
            String exceptBranch = map.getOrDefault("except_branch","").toString();
            List<String> branchList = Arrays.asList(branch.split(","));
            List<String> compareBranchList = Arrays.asList(compareBranch.split(","));
            List<String> exceptBranchList = Arrays.asList(exceptBranch.split(","));
            List<String> list = new ArrayList<>();
            List<String> newBranchList = ListDisparityUtils.getListDisparitStr(branchList == null ? new ArrayList<>() : branchList,
                    exceptBranchList == null ? new ArrayList<>() : exceptBranchList);

            if (compareBranchList != null && !compareBranch.equals("")) {
                list.addAll(compareBranchList);
            }
            if (branchList != null && !branch.equals("")) {
                list.addAll(branchList);
            }
            List<String> AllBranchList = list.stream().distinct().collect(Collectors.toList());
            map.put("actuality_all_num",AllBranchList.size());
            map.put("update_all_num",AllBranchList.size());
            map.put("branch_num",newBranchList.size());
            map.put("branch_num_upd",newBranchList.size());
            map.remove("branch");
        });
        return datas;
    }


    /**
     * ??????????????????????????????
     * @param enterpriseAxisDto
     * @return
     */
    @Override
    public Map<String, Object> getAttrName(EnterpriseAxisDto enterpriseAxisDto) {
        String companyCd = enterpriseAxisDto.getCompanyCd();
        String commonPartsData = enterpriseAxisDto.getCommonPartsData();
        JSONObject jsonObject = JSON.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        String isCompanyCd = null;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        String tableName = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass);
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", isCompanyCd, prodMstClass);

        List<List<Map<String, Object>>> list = new ArrayList<>();
        List<Map<String, Object>> attrName = priorityOrderDataMapper.selectPriorityAttrName(tableNameAttr,isCompanyCd,prodMstClass);
        List<Map<String, Object>> specialName = priorityOrderDataMapper.selectSpecialName(tableNameAttr, isCompanyCd, prodMstClass);
        List<Map<String, Object>> stratumName = priorityOrderDataMapper.selectPriorityStratumName(tableName,isCompanyCd,prodMstClass);
        List<Map<String, Object>> attrName1 = attrName.stream().filter(map->"1".equals(MapUtils.getString(map,"type"))
        || "3".equals(MapUtils.getString(map,"type"))).collect(Collectors.toList());
        specialName.forEach(map->map.put("attr_type",3));
        list.add(stratumName);
        list.add(attrName1);
        list.add(specialName);

        return ResultMaps.result(ResultEnum.SUCCESS,list);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void downloadForCsv(DownloadSortDto downloadDto, HttpServletResponse response) throws IOException {
        String companyCd = downloadDto.getCompanyCd();
        Integer priorityOrderCd = downloadDto.getPriorityOrderCd();
        downloadDto.getList().forEach(item->{
            item.setPriorityOrderCd(downloadDto.getPriorityOrderCd());
            item.setCompanyCd(downloadDto.getCompanyCd());
        });
        //??????jan??????cd
        if (!downloadDto.getList().isEmpty() && downloadDto.getFlag()!=0) {
            priorityOrderAttributeClassifyService.setClassifyList(downloadDto.getList());
        }
        String shelfName = priorityOrderPatternMapper.getShelfName(priorityOrderCd, companyCd);
        String colName = null;
        if (downloadDto.getFlag() == 0){
            shelfName = "??????_?????????PTS_"+shelfName;
            colName = "rank";

        }else {
            shelfName = "?????????_?????????PTS_"+shelfName;
            colName = MagicString.RANK_UPD;
        }
        String [] version = {"??????????????????",MagicString.PTS_VERSION,"NS"};
        String [] headers = {"????????????","????????????","?????????","???????????????","???????????????","???????????????","??????????????????","?????????","????????????"};
        String  fileName = "?????????PTS_20220401"+System.currentTimeMillis()+".csv";

        List<DownloadDto> datas = null;

        datas = priorityOrderDataMapper.downloadForCsv(downloadDto.getTaiCd(), downloadDto.getTanaCd()
                ,downloadDto.getPriorityOrderCd(),colName);

        datas.forEach(item->{
            item.setCompanyCd(downloadDto.getCompanyCd());
            item.setPriorityOrderCd(downloadDto.getPriorityOrderCd());
        });
        if (!downloadDto.getList().isEmpty()) {
            priorityOrderPtsJandataMapper.delete(companyCd, priorityOrderCd);
            priorityOrderPtsJandataMapper.insert(datas);
        }
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/csv;charset=Shift_JIS");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), Charset.forName("Shift_JIS"));
        String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(fileName, "utf-8"));
        response.setHeader("Content-Disposition", format);
        try(CsvWriter csvWriter = CsvWriter.builder().build(writer)) {
            csvWriter.writeRow(version);
            csvWriter.writeRow(shelfName);
            csvWriter.writeRow(headers);
            List<String> janData = null;
            for (DownloadDto data : datas) {
                janData = Lists.newArrayList(String.valueOf(data.getTaiCd()),String.valueOf(data.getTanaCd()),String.valueOf(data.getTanapositionCd()), data.getJan()
                        ,"1","1","0","1","1");
                csvWriter.writeRow(janData);

            }
        }
    }


    public Map<String, Object> getPriorityOrderDataForDb(String [] jans, Map<String, String> attrSortMap) {
        CommonPartsDto commonPartsDto = new CommonPartsDto();
        commonPartsDto.setCoreCompany(sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY));
        commonPartsDto.setProdMstClass(MagicString.FIRST_CLASS_CD);
        String tableName = String.format("\"%s\".prod_%s_jan_info", commonPartsDto.getCoreCompany(), commonPartsDto.getProdMstClass());

        List<AttrHeaderSysDto> attrTableList = new ArrayList<>();
        AttrHeaderSysDto itemDto = null;
        for (Map.Entry<String, String> entry : attrSortMap.entrySet()) {
            String attrValue = entry.getValue();
            int length = attrValue.split("_").length;
            if(length<3){
                logger.warn("error attr data:{}", attrValue);
                continue;
            }

            String[] attrArray = attrValue.split("_");
            String itemTableName = String.format("\"%s\".prod_%s_jan_info", attrArray[0], attrArray[1]);
            String colNum = attrArray[2];

            //classify the data of the same schema
            Optional<AttrHeaderSysDto> any = attrTableList.stream().filter(attrHeaderSysDto -> attrHeaderSysDto.getTableName().equals(itemTableName)).findAny();
            if (any.isPresent()) {
                AttrHeaderSysDto attrHeaderSysDto = any.get();
                attrHeaderSysDto.getColNum().put(colNum, entry.getKey());
            }else{
                if(attrArray[0].equals(MagicString.PLANO_CYCLE_COMPANY_CD)){
                    continue;
                }
                String itemHeaderTableName = MessageFormat.format(MagicString.PROD_JAN_KAISOU_HEADER_SYS, attrArray[0], attrArray[1]);
                List<Map<String, Object>> janClassify = janClassifyMapper.selectJanClassify(itemHeaderTableName);
                if(janClassify.isEmpty()){
                    logger.warn("jan header table {} not exists", itemTableName);
                    continue;
                }

                String cdCol = janClassify.stream().filter(map -> map.get("attr").equals(MagicString.JAN_HEADER_JAN_CD_COL)).map(map -> map.get("sort"))
                        .findAny().orElse(MagicString.JAN_HEADER_JAN_CD_DEFAULT).toString();

                itemDto = new AttrHeaderSysDto();
                itemDto.setTableName(itemTableName);
                Map<String, String> colNumMap = Maps.newHashMap();
                colNumMap.put(colNum, entry.getKey());
                itemDto.setColNum(colNumMap);
                itemDto.setJanCdCol(cdCol);
                attrTableList.add(itemDto);
            }
        }

        List<Map<String, Object>> results = new ArrayList<>(1);
        if(jans.length > 0){
            results = priorityOrderDataMapper.selectDynamicAttr(jans, attrTableList, tableName,
                    MagicString.JAN_HEADER_JAN_CD_DEFAULT, MagicString.JAN_HEADER_JAN_NAME_DEFAULT);
        }
        return ResultMaps.result(ResultEnum.SUCCESS, JSON.toJSON(results));
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

    /**
     * ????????????????????????????????????????????????
     * @param priorityOrderDataDto
     * @return
     */
    @Override
    public Map<String, Object> editPriorityOrderData(PriorityOrderDataDto priorityOrderDataDto) {
        String companyCd = priorityOrderDataDto.getCompanyCd();
        Integer priorityOrderCd = priorityOrderDataDto.getPriorityOrderCd();
            logger.info("??????????????????????????????????????????????????????????????????:{}",priorityOrderDataDto);
            if (priorityOrderDataDto.getFlag()==null){
                priorityOrderDataDto.setFlag(1);
            }
            if (priorityOrderDataDto.getIsCover() == null){
                priorityOrderDataDto.setIsCover(0);
            }
            Integer newPriorityOrderCd = priorityOrderCd;
            if (priorityOrderDataDto.getIsCover()  == 1){
                newPriorityOrderCd = (Integer) idGeneratorService.classPriorityOrderNumGenerator().get("data");
            }
        if (priorityOrderDataDto.getFlag()==0) {
            //???????????????????????????????????????????????????
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            priorityOrderDataService.deleteWorkData(companyCd,newPriorityOrderCd);

            classicPriorityOrderMstMapper.setWorkForFinal(companyCd, priorityOrderCd,simpleDateFormat.format(date),newPriorityOrderCd);
            priorityOrderPatternMapper.insertWorkForFinal(companyCd,priorityOrderCd,newPriorityOrderCd);
            priorityOrderJanCardMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderJanNewMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderJanAttributeMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderJanProposalMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            comparePriorityOrderPatternMapper.delWk(priorityOrderCd);

            priorityOrderCatepakMapper.setWorkForFinal( companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderCatepakAttributeMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderMapper.insertWork(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderDataMapper.insertWorkDataForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            workPriorityOrderPtsClassify.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            classicPriorityOrderMstAttrSortMapper.insertAttrForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            classicPriorityOrderMstAttrSortMapper.insertAttrSortForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            starReadingTableMapper.insertForFinalByPattern(companyCd, priorityOrderCd,newPriorityOrderCd);
            starReadingTableMapper.insertForFinalByBranch(companyCd, priorityOrderCd,newPriorityOrderCd);
            comparePriorityOrderPatternMapper.setWKForFinal(priorityOrderCd);
        }
        List<Object> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put(MagicString.JAN_OLD,"???JAN");
        map.put(MagicString.JAN_NEW,"???JAN");
        map.put("sku","SKU");
        map.put(MagicString.BRANCH_AMOUNT_UPD,MagicString.BRANCH_AMOUNT_NAME);
        map.put(MagicString.POS_AMOUNT,"POS??????(???)");
        map.put(MagicString.UNIT_PRICE,"??????");
        map.put(MagicString.BRANCH_AMOUNT,MagicString.BRANCH_AMOUNT_NAME);
        map.put(MagicString.BRANCH_NUM,MagicString.BRANCH_NUM_NAME);
        map.put(MagicString.BRANCH_NUM_UPD,MagicString.BRANCH_NUM_NAME);
        map.put(MagicString.DIFFERENCE,"?????????");
        map.put(MagicString.SALE_FORECAST,"???????????? ??????(??????)");
        map.put("rank","??????Rank");
        map.put(MagicString.RANK_PROP,"??????Rank");
        map.put(MagicString.RANK_UPD,"??????Rank");
        map.put(MagicString.ACTUALITY_ALL_NUM,"???????????? ?????????");
        map.put(MagicString.ACTUALITY_COMPARE_NUM,"???????????? ?????????");
        map.put(MagicString.UPDATE_ALL_NUM,"???????????? ?????????");
        List<String> attrSortList = classicPriorityOrderMstAttrSortMapper.getAttrSortList(companyCd, newPriorityOrderCd);
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrList(companyCd, newPriorityOrderCd);
        List<Map<String,Object>> allAttrList = classicPriorityOrderMstAttrSortMapper.getAllAttrList(companyCd, newPriorityOrderCd);
        for (Map<String, Object> stringObjectMap : allAttrList) {
            map.put(stringObjectMap.get("sort").toString(),stringObjectMap.get("name"));
        }
        List<String> attrValueList = classicPriorityOrderMstAttrSortMapper.attrValueList(companyCd, newPriorityOrderCd);
        PriorityOrderMstDto patternOrProduct = priorityOrderMstMapper.getPatternOrProduct(companyCd, newPriorityOrderCd);
        patternOrProduct.setPriorityOrderCd(newPriorityOrderCd);
        List<Map<String, Object>> workData = new ArrayList<>();
                workData.add(map);
                workData.addAll(priorityOrderDataMapper.getWorkData(companyCd, newPriorityOrderCd, attrList,attrSortList));
        List<Map<String, Object>> referenceData = comparePriorityOrderPatternMapper.selectWorkReference(companyCd, priorityOrderCd);
        list.add(attrSortList);
        list.add(patternOrProduct);
        list.add(workData);
        list.add(attrValueList);
        list.add(referenceData);
        logger.info("?????????????????????????????????????????????????????????????????????:{}",list);
        return ResultMaps.result(ResultEnum.SUCCESS,list);
    }

    public void deleteWorkData(String companyCd, Integer newPriorityOrderCd) {
        classicPriorityOrderMstMapper.deleteWork(newPriorityOrderCd);
        priorityOrderPatternMapper.deleteWork(newPriorityOrderCd);
        priorityOrderJanCardMapper.deleteByPrimaryKey(companyCd, newPriorityOrderCd);
        priorityOrderJanNewMapper.delete(companyCd, newPriorityOrderCd);
        priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd, newPriorityOrderCd);
        priorityOrderJanProposalMapper.deleteByPrimaryKey(companyCd, newPriorityOrderCd);
        priorityOrderCatepakMapper.deleteByPrimaryKey(companyCd, newPriorityOrderCd);
        priorityOrderCatepakAttributeMapper.deleteByPrimaryKey(companyCd, newPriorityOrderCd);
        priorityOrderMapper.delete(newPriorityOrderCd);
        priorityOrderDataMapper.deleteWorkData(companyCd, newPriorityOrderCd);
        workPriorityOrderPtsClassify.deleteWork(companyCd, newPriorityOrderCd);
        classicPriorityOrderMstAttrSortMapper.deleteAttrWk(companyCd, newPriorityOrderCd);
        classicPriorityOrderMstAttrSortMapper.deleteAttrSortWK(companyCd, newPriorityOrderCd);
        starReadingTableMapper.deleteWorkByPattern(companyCd,newPriorityOrderCd);
        starReadingTableMapper.deleteWorkByBranch(companyCd,newPriorityOrderCd);
    }

    @Override
    public Map<String, Object> getPatternCompare(String companyCd, Integer priorityOrderCd) {
        List<Map<String, Object>> changeJan = classicPriorityOrderCompareJanDataMapper.getChangeJan(companyCd, priorityOrderCd);
        List<Map<String, Object>> changeJanForAll = classicPriorityOrderCompareJanDataMapper.getChangeJanForAll(companyCd, priorityOrderCd);


        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int existTableName = mstBranchMapper.checkTableExist(MagicString.DEFAULT_TABLE, coreCompany);

        if (existTableName == 0){
            coreCompany = companyCd;
        }
        String tableName = MessageFormat.format("\"{0}\".ten_0000_ten_info", coreCompany);
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        List<Map<String, Object>> patternBranchList = classicPriorityOrderCompareJanDataMapper.getPatternBranchList(companyCd, priorityOrderCd,tableName,groupCompany);
        patternBranchList = patternBranchList.stream().filter(map->!MapUtils.getString(map,"branchNum").equals("_")).collect(Collectors.toList());
        List<String> allBranchList = classicPriorityOrderCompareJanDataMapper.getAllBranchList(companyCd, priorityOrderCd,tableName,groupCompany);
        allBranchList = allBranchList.stream().filter(val->!val.equals("_")).collect(Collectors.toList());
        List<PriorityOrderCompareJanData> patternNewCompare = classicPriorityOrderCompareJanDataMapper.getPatternNewCompare(companyCd,priorityOrderCd);
        List<PriorityOrderCompareJanData> patternOldCompare = classicPriorityOrderCompareJanDataMapper.getPatternOldCompare(companyCd,priorityOrderCd);
        List<PriorityOrderCompareJanData> allNewCompare = classicPriorityOrderCompareJanDataMapper.getAllNewCompare(companyCd,priorityOrderCd);
        List<PriorityOrderCompareJanData> allOldCompare = classicPriorityOrderCompareJanDataMapper.getAllOldCompare(companyCd,priorityOrderCd);
        Integer allSaleForecast = classicPriorityOrderCompareJanDataMapper.getAllSaleForecast(companyCd, priorityOrderCd);
        List<Map<String, Object>> oldAmountNum = classicPriorityOrderCompareJanDataMapper.getOldAmountNum(companyCd, priorityOrderCd);
        List<Map<String, Object>> newAmountNum = classicPriorityOrderCompareJanDataMapper.getNewAmountNum(companyCd, priorityOrderCd);
        //oldAmount
        patternOldCompare.forEach(map->{
            oldAmountNum.forEach(oldAmount->{
                if (oldAmount.get(MagicString.SHELF_PATTERN_CD).equals(map.getShelfPatternCd())){
                    map.setOldAmount(Integer.parseInt(oldAmount.get(MagicString.AMOUNT).toString()));
                }
            });
            if (patternNewCompare.stream().noneMatch(oldMap->oldMap.getShelfPatternCd().equals(map.getShelfPatternCd()))) {
                PriorityOrderCompareJanData priorityOrderCompareJanData = new PriorityOrderCompareJanData();
                priorityOrderCompareJanData.setNewAmount(0);
                priorityOrderCompareJanData.setShelfPatternName(map.getShelfPatternName());
                priorityOrderCompareJanData.setShelfPatternCd(map.getShelfPatternCd());
                priorityOrderCompareJanData.setShelfName(map.getShelfName());
                priorityOrderCompareJanData.setSkuNew(0);
                priorityOrderCompareJanData.setFaceNew(0);
                patternNewCompare.add(priorityOrderCompareJanData);
            }
        });
        //amount
        patternNewCompare.forEach(map->{
            newAmountNum.forEach(newAmount->{
                if (newAmount.get(MagicString.SHELF_PATTERN_CD).equals(map.getShelfPatternCd())){
                    map.setNewAmount(Double.valueOf(newAmount.get(MagicString.AMOUNT).toString()).intValue());
                }
            });
        });
        //branch
        List<Map<String, Object>> finalPatternBranchList = patternBranchList;
        patternNewCompare
                .forEach(map -> {
                    finalPatternBranchList.forEach(branch->{
                        if (branch.get(MagicString.SHELF_PATTERN_CD).equals(map.getShelfPatternCd())){
                            String branchs = branch.get(MagicString.BRANCHNUM).toString();
                                map.setBranchNum(Arrays.asList(branchs.split(",")));
                        }
                    });
                    if (map.getBranchNum()==null){
                        map.setBranchNum(new ArrayList<>());
                    }
                    patternOldCompare.forEach(oldPts->{
                        if (oldPts.getShelfPatternCd().equals(map.getShelfPatternCd())){
                            List<String> branchList = (List<String>)(map.getBranchNum());
                            int sale = BigDecimal.valueOf((double) (map.getNewAmount()-oldPts.getOldAmount()) * branchList.size() /1000).setScale(0,BigDecimal.ROUND_UP).intValue();
                            map.setFaceOld(oldPts.getFaceOld());
                            map.setSkuOld(oldPts.getSkuOld());
                            map.setFaceCompare(map.getFaceNew()-oldPts.getFaceOld());
                            map.setSkuCompare(map.getSkuNew()-oldPts.getSkuOld());
                            map.setSaleForecast(sale);
                        }
                    });
                    changeJan.forEach(map1 ->{
                        if(map.getShelfPatternCd().equals(MapUtils.getInteger(map1,MagicString.SHELF_PATTERN_CD)) && MapUtils.getString(map1,MagicString.FLAG).equals("777") ){
                            map.setSkuAdd(MapUtils.getInteger(map1,MagicString.SKU_NUM));
                        }
                        if(map.getShelfPatternCd().equals(MapUtils.getInteger(map1,MagicString.SHELF_PATTERN_CD)) && MapUtils.getString(map1,MagicString.FLAG).equals("888") ){
                            map.setSkuCut(MapUtils.getInteger(map1,MagicString.SKU_NUM));
                        }
                    });
                });
        changeJanForAll.forEach(map->{
            if( MapUtils.getString(map,MagicString.FLAG).equals("777") ){
                allNewCompare.get(0).setSkuAdd(MapUtils.getInteger(map,MagicString.SKU_NUM));
            }
            if( MapUtils.getString(map,MagicString.FLAG).equals("888") ){
                allNewCompare.get(0).setSkuCut(MapUtils.getInteger(map,MagicString.SKU_NUM));
            }
        });
        allNewCompare.get(0).setBranchNum(allBranchList.isEmpty()?new ArrayList<>():allBranchList);
        allNewCompare.get(0).setFaceOld(allOldCompare.get(0).getFaceOld());
        allNewCompare.get(0).setSkuOld(allOldCompare.get(0).getSkuOld());
        allNewCompare.get(0).setFaceCompare(allNewCompare.get(0).getFaceNew()-allOldCompare.get(0).getFaceOld());
        allNewCompare.get(0).setSkuCompare(allNewCompare.get(0).getSkuNew()-allOldCompare.get(0).getSkuOld());
        allNewCompare.get(0).setSaleForecast(allSaleForecast);
        List<List<PriorityOrderCompareJanData>> list = new ArrayList<>();
        list.add(allNewCompare);
        list.add(patternNewCompare);
        return ResultMaps.result(ResultEnum.SUCCESS,list);
    }

    @Override
    public Map<String, Object> getAttrCompare(String companyCd, Integer priorityOrderCd,String attrList) {
        List<String> attr = Arrays.asList(attrList.split(","));
        List<Map<String,Object>> attrResultList = new ArrayList<>();
        Map<String,Object> listTableName = new HashMap<>();
        attr.forEach(map->{
            Map<String,Object> attrMap = new HashMap<>();
            String[] s = map.split("_");
            String tableNameInfo = MessageFormat.format("\"{0}\".prod_{1}_jan_info", s[0], s[1]);
            attrMap.put("col",s[2]);
            attrMap.put(MagicString.NAME,"attr_"+map);
            attrMap.put(MagicString.TABLE_NAME,"\""+s[0]+s[1]+"\"");
            listTableName.put("\""+s[0]+s[1]+"\"",tableNameInfo);
            attrResultList.add(attrMap);
        });

        List<Map<String, Object>> newPtsAttrCompare = classicPriorityOrderCompareJanDataMapper.getNewPtsAttrCompare(attrResultList,listTableName, priorityOrderCd);
        List<Map<String, Object>> oldPtsAttrCompare = classicPriorityOrderCompareJanDataMapper.getOldPtsAttrCompare(attrResultList,listTableName, priorityOrderCd);
        long newSkuSum = newPtsAttrCompare.stream().mapToInt(value -> MapUtils.getInteger(value, MagicString.SKU_NUM)).sum();
        long oldSkuSum = oldPtsAttrCompare.stream().mapToInt(value -> MapUtils.getInteger(value, MagicString.SKU_NUM)).sum();
        int newFaceSum = newPtsAttrCompare.stream().mapToInt(value -> MapUtils.getInteger(value, MagicString.FACE_NUM)).sum();
        int oldFaceSum = oldPtsAttrCompare.stream().mapToInt(value -> MapUtils.getInteger(value, MagicString.FACE_NUM)).sum();
        List<Map<String, Object>> resultAttrCompare = new ArrayList<>();

        newPtsAttrCompare.addAll(oldPtsAttrCompare);
        Map<String, List<Map<String, Object>>> collect = newPtsAttrCompare.stream().collect(Collectors.groupingBy(map -> {
            StringBuilder a = new StringBuilder();
            for (Map<String,Object> s : attrResultList) {
                a.append(map.get(s.get(MagicString.NAME)));
            }
            return a.toString();
        }));
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : collect.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            if (stringListEntry.getValue().size()>1){
                Map<String, Object> pts1 = stringListEntry.getValue().get(0);
                Map<String, Object> pts2 = stringListEntry.getValue().get(1);
                if (pts1.get(MagicString.FLAG).equals("1")){
                    map = this.compareNum(attr,oldSkuSum,oldFaceSum,newSkuSum,newFaceSum,pts1,pts2);
                }else {
                    map = this.compareNum(attr,oldSkuSum,oldFaceSum,newSkuSum,newFaceSum,pts2,pts1);
                }
            }else {
                Map<String, Object> pts1 = stringListEntry.getValue().get(0);
                if (pts1.get(MagicString.FLAG).equals("1")){
                    map = this.compareNum(attr,oldSkuSum,oldFaceSum,newSkuSum,newFaceSum,pts1,new HashMap<>());
                }else {
                    map = this.compareNum(attr,oldSkuSum,oldFaceSum,newSkuSum,newFaceSum,new HashMap<>(),pts1);
                }
            }
            resultAttrCompare.add(map);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,resultAttrCompare);
    }

    @Override
    public Map<String, Object> getPriorityOrderNewPts(String companyCd, Integer priorityOrderCd, Integer shelfPatternCd,Integer flag) {
        List<Map<String, Object>> list = classicPriorityOrderCompareJanDataMapper.getAttrValue(companyCd, priorityOrderCd);

        List<Map<String,Object>> attrList = new ArrayList<>();
        Map<String,Object> listTableName = new HashMap<>();
        list.forEach(map->{
            Map<String,Object> attrMap = new HashMap<>();
            String value = map.get(MagicString.VALUE).toString();
            String name = map.get(MagicString.NAME).toString();
            String sort = map.get(MagicString.SORT).toString();
            String[] valueList = value.split("_");
            String tableNameInfo = MessageFormat.format("\"{0}\".prod_{1}_jan_info", valueList[0], valueList[1]);
            attrMap.put(MagicString.NAME,name);
            attrMap.put(MagicString.SORT,sort);
            attrMap.put("col",valueList[2]);
            attrMap.put(MagicString.TABLE_NAME,"\""+valueList[0]+valueList[1]+"\"");
            listTableName.put("\""+valueList[0]+valueList[1]+"\"",tableNameInfo);
            attrList.add(attrMap);
        });
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(null);
        String name = Joiner.on(",").join(attrList.stream().map(map -> MapUtils.getString(map, "name")).collect(Collectors.toList()));

        PtsDetailDataVo ptsDetailData = shelfPtsDataMapper.getPtsDetailData(shelfPatternCd);
        List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(shelfPatternCd);
        List<PtsTanaVo> tanaData = shelfPtsDataMapper.getTanaData(shelfPatternCd);
        ptsDetailData.setPtsTaiList(taiData);
        ptsDetailData.setPtsTanaVoList(tanaData);
        String janHeader = ptsDetailData.getJanHeader()+",?????????,"+name+",???,???,??????";
        ptsDetailData.setJanHeader(janHeader);
        StringBuilder s = new StringBuilder("taiCd,tanaCd,tanapositionCd,jan,faceCount,faceMen,faceKaiten,tumiagesu,zaikosu");
        if ("V3.0".equals(ptsDetailData.getVersioninfo())){
            s.append(",faceDisplayflg,facePosition,depthDisplayNum");
        }
        s.append(",janName,");
        String col = Joiner.on(",").join(attrList.stream().map(map -> MapUtils.getString(map, "sort")).collect(Collectors.toList()));
        s.append(col+",plano_width,plano_height,plano_depth");
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int existTableName = mstBranchMapper.checkTableExist(MagicString.DEFAULT_TABLE, coreCompany);

        if (existTableName == 0){
            coreCompany = companyCd;
        }
        String tableName = MessageFormat.format("\"{0}\".prod_0000_jan_info", coreCompany);
        List<LinkedHashMap<String,Object>> janData = null;
        if (flag == 0){
             janData = classicPriorityOrderCompareJanDataMapper.getJanOldData(shelfPatternCd,attrList,listTableName
                    ,janSizeCol,tableName);
        }else {
             janData = classicPriorityOrderCompareJanDataMapper.getJanNewData(shelfPatternCd,attrList,listTableName
                    ,janSizeCol,tableName,priorityOrderCd);
        }

        ptsDetailData.setPtsJanDataList(janData);
        ptsDetailData.setJanColumns(s.toString());
        return ResultMaps.result(ResultEnum.SUCCESS,ptsDetailData);
    }


    private Map<String,Object> compareNum(List<String> attr, long oldSkuSum, int oldFaceSum, long newSkuSum, int newFaceSum
            , Map<String, Object> newPts, Map<String, Object> oldPts){
        Map<String,Object> map = new HashMap<>();
        for (String s : attr) {
            map.put("attr_"+s,newPts.get("attr_"+s));
        }
        double skuOldArea = BigDecimal.valueOf(Double.parseDouble(oldPts.getOrDefault(MagicString.SKU_NUM, 0).toString()) / oldSkuSum * 100)
                .setScale(1, BigDecimal.ROUND_UP).doubleValue();
        double skuNewArea =  BigDecimal.valueOf(Double.parseDouble(newPts.getOrDefault(MagicString.SKU_NUM,0).toString())/newSkuSum*100)
                .setScale(1,BigDecimal.ROUND_UP).doubleValue();
        double faceOldArea = BigDecimal.valueOf(Double.parseDouble(oldPts.getOrDefault(MagicString.FACE_NUM,0).toString())/oldFaceSum*100)
                .setScale(1,BigDecimal.ROUND_UP).doubleValue();
        double faceNewArea =  BigDecimal.valueOf(Double.parseDouble(newPts.getOrDefault(MagicString.FACE_NUM,0).toString())/newFaceSum*100)
                .setScale(1,BigDecimal.ROUND_UP).doubleValue();
        map.put("skuNew",newPts.getOrDefault(MagicString.SKU_NUM,0));
        map.put("skuOld",oldPts.getOrDefault(MagicString.SKU_NUM,0));
        map.put("skuCompare",Integer.parseInt(newPts.getOrDefault(MagicString.SKU_NUM,0).toString())-Integer.parseInt(oldPts.getOrDefault(MagicString.SKU_NUM,0).toString()));
        map.put("skuOldArea",skuOldArea+MagicString.PERCENTAGE);
        map.put("skuNewArea",skuNewArea+MagicString.PERCENTAGE);
        map.put("skuCompareArea",BigDecimal.valueOf(skuNewArea-skuOldArea).setScale(1,BigDecimal.ROUND_UP).doubleValue()+MagicString.PERCENTAGE);
        map.put("faceNew",newPts.getOrDefault(MagicString.FACE_NUM,0));
        map.put("faceOld",oldPts.getOrDefault(MagicString.FACE_NUM,0));
        map.put("faceCompare",Integer.parseInt(newPts.getOrDefault(MagicString.FACE_NUM,0).toString()) -Integer.parseInt(oldPts.getOrDefault(MagicString.FACE_NUM,0).toString()) );
        map.put("faceOldArea",faceOldArea+MagicString.PERCENTAGE);
        map.put("faceNewArea",faceNewArea+MagicString.PERCENTAGE);
        map.put("faceCompareArea",BigDecimal.valueOf(faceNewArea-faceOldArea).setScale(1,BigDecimal.ROUND_UP).doubleValue()+MagicString.PERCENTAGE);
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPtsClassify(List<String> colNameList, String shelfPatternCd, String companyCd, Integer priorityOrderCd) {
        logger.info("pts classify????????????????????????:{},{},{}",colNameList,shelfPatternCd,priorityOrderCd);
        List<ShelfPtsData> shelfPtsData = shelfPtsDataMapper.getPtsCdByPatternCd(companyCd, shelfPatternCd);
        workPriorityOrderPtsClassify.deleteWork(companyCd,priorityOrderCd);
        workPriorityOrderPtsClassify.setWorkPtsClassify(companyCd,priorityOrderCd,shelfPtsData,colNameList);
    }

    @Override
    public Map<String, Object> getBranchNum(List<Map<String, Object>> map) {
        String companyCd = map.get(0).get(MagicString.COMPANY_CD).toString();
        Integer priorityOrderCd = Integer.valueOf(map.get(0).get(MagicString.PRIORITY_ORDER_CD).toString());
        //
        List<Map<String, Object>> lists = new ArrayList<>();
        for (Map<String, Object> objectMap : map) {
            Map<String,Object> branchNumNow = priorityOrderDataMapper.getJanBranchNum(priorityOrderCd,objectMap.get(MagicString.JAN_OLD).toString(),objectMap.get(MagicString.JAN_NEW).toString());
            if (branchNumNow == null){
                branchNumNow = new HashMap<>();
                branchNumNow.put(MagicString.BRANCH_NUM,0);
                branchNumNow.put(MagicString.BRANCH_AMOUNT_UPD,0);
                branchNumNow.put(MagicString.EXCEPT_BRANCH,0);
            }

            List<Integer> ptsCd = workPriorityOrderPtsClassify.getJanPtsCd(companyCd, priorityOrderCd, objectMap);
            Map<String, Object> janBranchNum =  new HashMap<>();
            janBranchNum.put(MagicString.JAN_OLD,objectMap.get(MagicString.JAN_OLD));
            janBranchNum.put(MagicString.JAN_NEW,objectMap.get(MagicString.JAN_NEW));
            String compareBranch = MapUtils.getString(branchNumNow, "actuality_compare_branch");

            List<String> compareBranchList = new ArrayList<>();
            if (compareBranch!=null){
                compareBranchList= Arrays.asList(compareBranch.split(","));
            }
            List<String> list = new ArrayList<>();
            if (!compareBranchList.isEmpty() && !compareBranch.equals("")&& !compareBranch.equals("_")){
                list.addAll(compareBranchList);
            }
            if (ptsCd.isEmpty()){
                Integer difference = -Integer.parseInt(branchNumNow.getOrDefault(MagicString.BRANCH_NUM,0).toString());
                double saleForecast = difference  * Double.parseDouble(branchNumNow.getOrDefault(MagicString.BRANCH_AMOUNT_UPD,0).toString()) / 1000;

                BigDecimal bd = BigDecimal.valueOf(saleForecast);
                saleForecast = bd.setScale(0,RoundingMode.HALF_UP).doubleValue();

                janBranchNum.put(MagicString.BRANCH_NUM_UPD,0);
                janBranchNum.put(MagicString.DIFFERENCE,difference);
                janBranchNum.put(MagicString.SALE_FORECAST,saleForecast);
                janBranchNum.put(MagicString.UPDATE_ALL_NUM,list.size());
            }else {
                Map<String,Object> branchList = workPriorityOrderPtsClassify.getJanBranchNum(ptsCd, objectMap);
                String branch = MapUtils.getString(branchList, "branch");
                String exceptBranch = MapUtils.getString(branchNumNow, MagicString.EXCEPT_BRANCH);
                List<String> exceptBranchList = Arrays.asList(exceptBranch.split(","));
                List<String> branchs = Arrays.asList(branch.split(","));
                List<String> newBranchList = ListDisparityUtils.getListDisparitStr(branchs == null ? new ArrayList<>() : branchs,
                        exceptBranchList == null ? new ArrayList<>() : exceptBranchList);
                newBranchList.remove("");
                if (branchs != null && !branch.equals("")&& !branch.equals("_")){
                    list.addAll(newBranchList);
                }
                logger.info("?????????{}",branchNumNow.get(MagicString.BRANCH_NUM));
                Integer difference = newBranchList.size() - Integer.parseInt(branchNumNow.getOrDefault(MagicString.BRANCH_NUM,0).toString());
                double saleForecast = difference  * Double.parseDouble(branchNumNow.getOrDefault(MagicString.BRANCH_AMOUNT_UPD,0).toString()) / 1000;
                BigDecimal bd = BigDecimal.valueOf(saleForecast);
                saleForecast = bd.setScale(0,RoundingMode.HALF_UP).doubleValue();
                janBranchNum.put(MagicString.BRANCH_NUM_UPD,newBranchList.size());
                janBranchNum.put(MagicString.DIFFERENCE,difference);
                janBranchNum.put(MagicString.SALE_FORECAST,saleForecast);
                janBranchNum.put(MagicString.UPDATE_ALL_NUM,list.size());
            }
            lists.add(janBranchNum);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,lists);
    }

    /**
     * check jan's error message
     * @param janList
     * @param company
     * @param priorityOrderCd
     * @param tableName
     * @return
     */
    @Override
    public Map<String, String> checkIsJanNew(List<String> janList, String company, Integer priorityOrderCd, String tableName) {
        Map<String, String> janMsg = new HashMap<>(2);
        if(janList.isEmpty()){
            return janMsg;
        }
        List<String> existJanOld = priorityOrderDataMapper.existJanOld(janList,priorityOrderCd);
        List<String> existJanNew = priorityOrderDataMapper.existJanNew(janList,priorityOrderCd);

        if(this.isEmptyJan(!existJanOld.isEmpty(),!existJanNew.isEmpty())){
            existJanOld.addAll(existJanNew);
            existJanOld = existJanOld.stream().distinct().collect(Collectors.toList());
            existJanOld.forEach(old->{
                if(Strings.isNullOrEmpty(janMsg.getOrDefault(old, ""))){
                    janMsg.put(old, "????????????????????????????????????????????????????????????????????????????????????");
                }
            });
        }

        List<String> replaceExistJanNew = priorityOrderJanReplaceMapper.existJanNew(janList, company);
        List<String> replaceExistJanOld = priorityOrderJanReplaceMapper.existJanOld(janList, company);

        if(!replaceExistJanNew.isEmpty() || !replaceExistJanOld.isEmpty()){
            replaceExistJanOld.addAll(replaceExistJanNew);
            replaceExistJanOld = replaceExistJanOld.stream().distinct().collect(Collectors.toList());
            replaceExistJanOld.forEach(old->{
                if(Strings.isNullOrEmpty(janMsg.getOrDefault(old, ""))) {
                    janMsg.put(old, "?????????JAN???????????????????????????????????????");
                }
            });
        }

        List<String> proposalExistJanNew = priorityOrderJanProposalMapper.existJanNew(janList, company, priorityOrderCd);
        List<String> proposalExistJanOld = priorityOrderJanProposalMapper.existJanOld(janList, company, priorityOrderCd);
        if(!proposalExistJanNew.isEmpty() || !proposalExistJanOld.isEmpty()){
            proposalExistJanOld.addAll(proposalExistJanNew);
            proposalExistJanOld = proposalExistJanOld.stream().distinct().collect(Collectors.toList());
            proposalExistJanOld.forEach(old->{
                if(Strings.isNullOrEmpty(janMsg.getOrDefault(old, ""))) {
                    janMsg.put(old, "?????????JAN???????????????????????????????????????");
                }
            });
        }

        return janMsg;
    }

    public boolean isEmptyJan(boolean a,boolean b){
        return a || b;
    }

    /**
     * pts?????????????????????????????????+?????????????????????????????????????????????
     * @param company
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getUploadPriorityOrderData(String company, Integer priorityOrderCd) {
        List<Map<String, Object>> result = new ArrayList<>();
        String authorCd = session.getAttribute("aud").toString();
        cacheUtil.remove(authorCd);
        List<String> attrValueList = classicPriorityOrderMstAttrSortMapper.getAttrList(company, priorityOrderCd);
        List<PriorityOrderMstAttrSortDto> priorityOrderMstAttrSortDtos = classicPriorityOrderMstAttrSortMapper.selectWKRankSort(company, priorityOrderCd);
        List<String> attrSort = priorityOrderMstAttrSortDtos.stream().map(PriorityOrderMstAttrSortDto::getValue).collect(Collectors.toList());
        List<Map<String, Object>> tempData = priorityOrderDataMapper.selectTempDataByRankUpd(attrSort, priorityOrderCd,company,  attrValueList);
        result.addAll(tempData);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    /**
     *
     * @param file
     * @param company
     * @param priorityOrderCd
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> uploadPriorityOrderData(String taiCd, String tanaCd, MultipartFile file, String company, Integer priorityOrderCd,
                                                       String attrStr) {
        Map<String, Object> resultMap;
        Pattern pattern = Pattern.compile("^_$");

        try {
            InputStream inputStream = file.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("Shift_JIS"));
            CsvReader csvReader = CsvReader.builder().skipEmptyRows(false).build(reader);

            Map<String, Object> checkResult = this.checkPTS(csvReader, priorityOrderCd, company);
            if(checkResult.get("code") != ResultEnum.SUCCESS.getCode()){
                return checkResult;
            }

            List<PriorityOrderMstAttrSortDto> checkedAttrList = priorityOrderMstAttrSortMapper.selectWKRankSort(company, priorityOrderCd);
            String attrList = checkedAttrList.stream().map(PriorityOrderMstAttrSortDto::getValue).collect(Collectors.joining(","));
            Map<String, Object> data = (Map<String, Object>) checkResult.get("data");
            List<DownloadDto> uploadJanList = (List<DownloadDto>) data.get("janList");
            List<PriorityOrderAttributeClassify> classifyList = priorityOrderClassifyMapper.getClassifyList(company, priorityOrderCd);

            List<DownloadDto> cutJanList = new ArrayList<>(priorityOrderPtsJandataMapper.selectCutJan(company, priorityOrderCd, uploadJanList));
            ClassicPriorityOrderDataService dataService = applicationContext.getBean(ClassicPriorityOrderDataService.class);
            dataService.doJanCut(cutJanList, company, priorityOrderCd);

            List<DownloadDto> newJanList = new ArrayList<>(priorityOrderPtsJandataMapper.selectNewJan(company, priorityOrderCd, uploadJanList));
            List<ClassicPriorityOrderJanNew> priorityOrderJanNews = null;
            List<PriorityOrderMstAttrSortDto> attrSorts = priorityOrderMstAttrSortMapper.selectWKAttr(company, priorityOrderCd);

            if(Objects.equals(MapUtils.getString(data, "modeName"), "??????")){
                //??????pts delete jan new, because ??????pts download not have jan new
                priorityOrderJanNewMapper.delete(company, priorityOrderCd);
                priorityOrderJanAttributeMapper.deleteByPrimaryKey(company, priorityOrderCd);
            }

            resultMap = dataService.doJanNew(newJanList, company, priorityOrderCd, taiCd, tanaCd, attrList, classifyList, attrSorts);
            priorityOrderJanNews = (List<ClassicPriorityOrderJanNew>) resultMap.getOrDefault("data", Lists.newArrayList());
            String authorCd = session.getAttribute("aud").toString();

            List<String> newJanCdList = newJanList.stream().map(DownloadDto::getJan).collect(Collectors.toList());
            List<String> cutJanCdList = cutJanList.stream().map(DownloadDto::getJan).collect(Collectors.toList());
            Map<String, String> janNewError = this.checkIsJanNew(newJanCdList, company, priorityOrderCd, "");
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
                priorityOrderDataMapper.updateCutJanByJanList(priorityOrderCd, needJanCutList);
            }

            List<String> colName = priorityOrderDataMapper.selectTempColNameBySchema("work_priority_order_result_data", "priority");
            List<Map<String, String>> keyNameLists = colName.stream().map(col -> {
                Map<String, String> map = new HashMap<>(1);
                map.put("name", col);
                return map;
            }).collect(Collectors.toList());

            if(!needJanNewList.isEmpty()){
                //add ??????jan
                List<Map<String, Object>> datas = new ArrayList<>();
                List<PriorityOrderJanAttribute> attrs = priorityOrderJanAttributeMapper.selectAttributeByJan(company, priorityOrderCd, needJanNewList);
                for (DownloadDto downloadDto : needJanNewList) {
                    Map<String, Object> dataMap = new HashMap<>(16);

                    if(priorityOrderJanNews!=null){
                        Optional<ClassicPriorityOrderJanNew> firstOpt = priorityOrderJanNews.stream().filter(janNew -> janNew.getJanNew().equals(downloadDto.getJan())).findFirst();
                        firstOpt.ifPresent(priorityOrderJanNew -> {
                            dataMap.put("sku", priorityOrderJanNew.getNameNew());
                            dataMap.put(MagicString.ACTUALITY_COMPARE_BRANCH, firstOpt.get().getActualityCompareBranch());
                            dataMap.put(MagicString.EXCEPT_BRANCH, firstOpt.get().getExceptBranch());
                        });
                    }

                    dataMap.put(MagicString.JAN_OLD,"_");
                    dataMap.put(MagicString.JAN_NEW,downloadDto.getJan());
                    dataMap.put("rank",-1);
                    dataMap.put(MagicString.GOODS_RANK,downloadDto.getTanapositionCd());
                    dataMap.put(MagicString.RANK_PROP,downloadDto.getTanapositionCd());
                    dataMap.put(MagicString.RANK_UPD,downloadDto.getTanapositionCd());
                    dataMap.put(MagicString.BRANCH_AMOUNT,"0");
                    dataMap.put(MagicString.BRANCH_NUM,"0");
                    dataMap.put(MagicString.UNIT_PRICE,"0");
                    dataMap.put(MagicString.DIFFERENCE,Math.round(Double.parseDouble(pattern.matcher(downloadDto.getBranchNum()).replaceAll("0"))));
                    dataMap.put(MagicString.BRANCH_NUM_UPD,Math.round(Double.parseDouble(pattern.matcher(downloadDto.getBranchNum()).replaceAll( "0"))));
                    dataMap.put("pos_amount_upd","0");
                    dataMap.put("pos_before_rate","0");
                    dataMap.put(MagicString.POS_AMOUNT,"0");
                    dataMap.put(MagicString.PRIORITY_ORDER_CD_DB, priorityOrderCd);
                    dataMap.put(MagicString.AUTHOR_CD_DB, authorCd);
                    dataMap.put(MagicString.COMPANY_CD_DB, company);
                    List<PriorityOrderJanAttribute> attributes = attrs.stream().filter(attr -> attr.getJanNew().equals(downloadDto.getJan())).collect(Collectors.toList());
                    attributes.forEach(attr-> dataMap.put("attr"+attr.getAttrCd(), attr.getAttrValue()));
                    datas.add(dataMap);
                }
                priorityOrderDataMapper.insertByPriorityOrderCd(JSON.parseArray(new Gson().toJson(datas)), keyNameLists, priorityOrderCd);
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
            String[] attrArray = attrList.split(",");
            List<String> taiTana = Arrays.asList(attrArray).subList(0, 2);

            uploadJanList.forEach(jan->{
                Optional<PriorityOrderAttributeClassify> attr1Opt = classifyList.stream()
                        .filter(classify -> classify.getTaiCd().equals(jan.getTaiCd())).findFirst();
                attr1Opt.ifPresent(priorityOrderAttributeClassify -> jan.setAttr1(priorityOrderAttributeClassify.getAttr1()));

                Optional<PriorityOrderAttributeClassify> attr2Opt = classifyList.stream()
                        .filter(classify -> commonMstService.taiTanaEquals(classify.getTaiCd(), jan.getTaiCd(), classify.getTanaCd(), jan.getTanaCd())).findFirst();
                attr2Opt.ifPresent(priorityOrderAttributeClassify -> jan.setAttr2(priorityOrderAttributeClassify.getAttr2()));
            });
            priorityOrderPtsJandataMapper.updateAttr(uploadJanList, priorityOrderCd, taiTana.get(0), taiTana.get(1));

            newRankList = priorityOrderPtsJandataMapper.selectJanRank(company, priorityOrderCd, Arrays.stream(attrArray).collect(Collectors.toList()));
            List<Map<String, Object>> newRankMapList = newRankList.stream().map(map -> {
                Map<String, Object> itemMap = Maps.newHashMap();
                itemMap.put("jan", map.getJan());
                itemMap.put("rank", map.getRankNow());
                itemMap.put(MagicString.RANK_UPD, map.getRank());
                itemMap.put(MagicString.JAN_OLD, map.getJanOld());
                itemMap.put(MagicString.JAN_NEW, map.getJan());
                itemMap.put(MagicString.COMPANY_CD, company);
                itemMap.put(MagicString.PRIORITY_ORDER_CD, priorityOrderCd);
                itemMap.put(MagicString.BRANCH_NUM, map.getBranchNum());
                itemMap.put(MagicString.BRANCH_NUM_UPD, map.getBranchNumUpd());
                itemMap.put("difference", map.getDifference());
                itemMap.put("sale_forecast", map.getSaleForecast());
                this.splitAttrList(map.getAttrList(), itemMap);

                return itemMap;
            }).collect(Collectors.toList());
            newRankMapList = calRank(newRankMapList, attrSort);
            Map<String, Object> branchNumResult = this.getBranchNum(newRankMapList);
            List<DownloadDto> newRankPoList = newRankMapList.stream().map(map -> {
                DownloadDto downloadDto = new DownloadDto();
                map.put(MagicString.COMPANY_CD, company);
                map.put(MagicString.PRIORITY_ORDER_CD, priorityOrderCd);
                map.put(MagicString.JAN_NEW, map.get(MagicString.JAN_NEW).toString());
                map.put(MagicString.JAN_OLD, map.get(MagicString.JAN_OLD).toString());
                downloadDto.setJan(map.get(MagicString.JAN_NEW).toString());
                downloadDto.setJanOld(map.get(MagicString.JAN_OLD).toString());
                downloadDto.setRankNow(Integer.parseInt(map.get("rank").toString()));
                String branchNumUpd = getBranchNumWrapper(branchNumResult, map);
                downloadDto.setRank(Integer.parseInt(map.get(MagicString.RANK_UPD).toString()));
                downloadDto.setBranchNum(MapUtils.getString(map, MagicString.BRANCH_NUM));
                downloadDto.setDifference(MapUtils.getString(map, "difference"));
                downloadDto.setSaleForecast(MapUtils.getString(map, "sale_forecast"));
                downloadDto.setBranchNumUpd(branchNumUpd);
                return downloadDto;
            }).collect(Collectors.toList());
            priorityOrderPtsJandataMapper.updateRankUpd(newRankPoList, taiCd, tanaCd, priorityOrderCd);

            List<Map<String, Object>> lists = new ArrayList<>();
            needJanNewList.forEach(downloadDto->{
                Optional<DownloadDto> first = newRankPoList.stream().filter(dto -> dto.getJan().equals(downloadDto.getJan())).findFirst();
                first.ifPresent(dto->{
                    String branchNum = dto.getBranchNum();
                    Map<String, Object> map = new HashMap<>();
                    map.put(MagicString.JAN_NEW, downloadDto.getJan());
                    map.put(MagicString.BRANCH_NUM, branchNum);
                    map.put(MagicString.COMPANY_CD, company);
                    map.put(MagicString.PRIORITY_ORDER_CD, priorityOrderCd);

                    lists.add(map);
                });
            });

            priorityOrderJanNewMapper.updateBranchNumByList(priorityOrderCd, lists);
            cacheUtil.put(authorCd, attrList);
        } catch (IOException e) {
            logger.error("", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logAspect.setTryErrorLog(e,new Object[]{company,priorityOrderCd});
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        if (Objects.equals(ResultEnum.SUCCESS_BUT_NEW_JAN.getCode(), resultMap.getOrDefault("code", ""))) {
            return ResultMaps.result(ResultEnum.SUCCESS_BUT_NEW_JAN);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    private void splitAttrList(String attrList, Map<String, Object> itemMap){
        String[] attrArr = attrList.split(",");
        Arrays.stream(attrArr).forEach(s->{
            String[] attrVal = s.split(":");
            if(attrVal.length > 1){
                itemMap.put(attrVal[0], attrVal[1]);
            }else{
                itemMap.put(attrVal[0], "");
            }
        });
    }

    @Override
    public void doJanCut(List<DownloadDto> cutJanList, String company, Integer priorityOrderCd){
        if(cutJanList.isEmpty()){
            return;
        }

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
    public Map<String, Object> doJanNew(List<DownloadDto> newJanList, String company, Integer priorityOrderCd, String taiCd, String tanaCd,
                                        String attrList, List<PriorityOrderAttributeClassify> classifyList,List<PriorityOrderMstAttrSortDto> attrSorts){

        if(newJanList.isEmpty()){
            return Maps.newHashMap();
        }

        priorityOrderJanNewMapper.deleteByJan(company, priorityOrderCd, newJanList);

        List<String> newJanExistCdList = newJanList.stream().map(DownloadDto::getJan).collect(Collectors.toList());

        if(newJanExistCdList.isEmpty()){
            return Maps.newHashMap();
        }

        //eg:attr1:1000_0000_1,attr2:0001_0000_1
        Map<String, String> attrSortMap = attrSorts.stream()
                .collect(Collectors.toMap(PriorityOrderMstAttrSortDto::getSort, PriorityOrderMstAttrSortDto::getValue));

        List<DownloadDto> notExistNewJan = newJanList.stream().filter(jan -> newJanExistCdList.contains(jan.getJan())).collect(Collectors.toList());
        priorityOrderPtsJandataMapper.insertNewJan(notExistNewJan);

        Map<String, Object> dbData = getPriorityOrderDataForDb(newJanExistCdList.toArray(new String[0]), attrSortMap);
        Object data = dbData.get("data");

        if(data==null){
            logger.error("db error, {}", dbData);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        JSONArray datas = (JSONArray) JSON.parse(dbData.get("data").toString());
        Map<String, Object> resultMap = Maps.newHashMap();

        //??????jan
        List<Map> maps = new Gson().fromJson(dbData.get("data").toString(), new TypeToken<List<Map>>(){}.getType());
        List<PriorityOrderJanAttribute> janAttrs = new ArrayList<>();

        List<String> allAttrSortList = new ArrayList<>(attrSortMap.keySet());
        allAttrSortList.removeIf(s->s.equals(taiCd)||s.equals(tanaCd));

        List<String> janMstList = maps.stream().map(map -> map.get(MagicString.JAN_NEW).toString()).collect(Collectors.toList());

        List<Map<String, Object>> compareList = priorityOrderJanNewMapper.getCompareList(priorityOrderCd);
        List<Map<String, Object>> exceptList = priorityOrderJanNewMapper.getExceptList(priorityOrderCd);

        //not in jan master
        newJanList.stream().filter(newJan->!janMstList.contains(newJan.getJan())).forEach(newJan->{
            Map<String, Object> item = new HashMap<>(16);
            item.put("sku","");
            item.put(MagicString.JAN_NEW,newJan.getJan());

            Optional<Map<String, Object>> firstCompare = compareList.stream().filter(compare ->
                    Objects.equals(MapUtils.getString(compare, MagicString.JAN), newJan.getJan())).findFirst();
            firstCompare.ifPresent(stringObjectMap -> newJan.setActualityCompareBranch(MapUtils.getString(stringObjectMap, MagicString.BRANCH)));

            Optional<Map<String, Object>> firstExcept = exceptList.stream().filter(except ->
                    Objects.equals(MapUtils.getString(except, MagicString.JAN), newJan.getJan())).findFirst();
            firstExcept.ifPresent(stringObjectMap -> newJan.setExceptBranch(MapUtils.getString(stringObjectMap, MagicString.BRANCH)));

            this.fillCommonParam(newJan, item);
            item.put(taiCd, newJan.getAttr1());
            item.put(tanaCd, newJan.getAttr2());

            allAttrSortList.forEach(sort->item.put(sort, ""));

            datas.add(JSON.parseObject(new Gson().toJson(item)));
            maps.add(item);
            resultMap.put("code", ResultEnum.SUCCESS_BUT_NEW_JAN.getCode());
        });

        //in jan master
        maps.forEach(item->{
            for (int i = 0; i < newJanList.size(); i++) {
                DownloadDto downloadDto = newJanList.get(i);
                if (item.get(MagicString.JAN_NEW).equals(downloadDto.getJan())){
                    Map<String, Object> attrValMap = new HashMap<>();
                    Arrays.stream(attrList.split(",")).forEach(attr->attrValMap.put(attr, item.getOrDefault(attr, "")));
                    //The order cannot be changed, select only the checked attribute rank(value in attrList)
                    Integer branchNum = priorityOrderResultDataMapper.selectBranchNumByAttr(priorityOrderCd, company, attrValMap);
                    branchNum = Optional.ofNullable(branchNum).orElse(0);
                    item.put(MagicString.BRANCH_NUM, branchNum);

                    allAttrSortList.forEach(attr-> attrValMap.put(attr, item.getOrDefault(attr, "")));
                    this.fillCommonParam(downloadDto, item);
                    downloadDto.setName(item.get("sku").toString());
                    downloadDto.setBranchNum(item.get(MagicString.BRANCH_NUM_UPD).toString());
                    newJanList.set(i, downloadDto);
                }
            }
            List<Object> attrs = (List<Object>) item.keySet().stream()
                    .filter(k -> k.toString().startsWith("attr")).collect(Collectors.toList());
            for (Object attr : attrs) {
                PriorityOrderJanAttribute janAttr = new PriorityOrderJanAttribute();

                if(taiCd.equals(attr.toString()) || tanaCd.equals(attr.toString())){
                    Optional<DownloadDto> janOpt = newJanList.stream().filter(downloadDto -> downloadDto.getJan().equals(item.get(MagicString.JAN_NEW))).findFirst();
                    if(janOpt.isPresent()){
                        DownloadDto jan = janOpt.get();
                        Optional<PriorityOrderAttributeClassify> attrOpt = classifyList.stream()
                                .filter(classify -> commonMstService.taiTanaEquals(classify.getTaiCd(),jan.getTaiCd(),classify.getTanaCd(),jan.getTanaCd())).findFirst();

                        if (taiCd.equals(attr.toString())) {
                            //taiTana's first element is tai attr
                            attrOpt.ifPresent(priorityOrderAttributeClassify -> janAttr.setAttrValue(attrOpt.get().getAttr1()));
                        }else if(tanaCd.equals(attr.toString())){
                            //taiTana's second element is tana attr
                            attrOpt.ifPresent(priorityOrderAttributeClassify -> janAttr.setAttrValue(attrOpt.get().getAttr2()));
                        }
                    }
                }else{
                    janAttr.setAttrValue(item.get(attr).toString());
                }
                String attrCd = attr.toString();
                janAttr.setAttrCd(Integer.parseInt(attrCd.replace("attr", "")));
                janAttr.setJanNew(item.get(MagicString.JAN_NEW).toString());
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
            janNew.setBranchNum(jan.getBranchNum().equals("_")?"0":jan.getBranchNum());
            janNew.setNameNew(jan.getName());
            return janNew;
        }).collect(Collectors.toList());

        priorityOrderJanNewMapper.insert(priorityOrderJanNewList);
        resultMap.put("data", priorityOrderJanNewList);
        return resultMap;
    }

    private void fillCommonParam(DownloadDto downloadDto, Map<String, Object> item){
        item.put(MagicString.JAN_OLD,downloadDto.getJan());
        item.put("rank",-1);
        item.put(MagicString.RANK_PROP,downloadDto.getTanapositionCd());
        item.put(MagicString.BRANCH_AMOUNT,"_");
        item.put("unit_before_diff","_");
        item.put(MagicString.SALE_FORECAST,"_");
        item.put(MagicString.BRANCH_NUM,0);
        item.put(MagicString.BRANCH_NUM_UPD,"_");
        item.put(MagicString.UNIT_PRICE,"_");
        item.put(MagicString.BRANCH_AMOUNT_UPD,"_");
        item.put("pos_before_rate","_");
        item.put(MagicString.DIFFERENCE,"_");
        item.put(MagicString.POS_AMOUNT,"_");
        item.put(MagicString.GOODS_RANK,downloadDto.getTanapositionCd());
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

        String modeName = null;
        for (CsvRow csvRow : csvReader) {
            if(rowIndex==1){
                if (csvRow.getField(0).startsWith("??????")) {
                    modeName = "??????";
                }else{
                    modeName = "?????????";
                }
            }

            if(csvRow.isEmpty() || csvRow.getFields().stream().allMatch(""::equals)){
                rowIndex++;
                continue;
            }

            if(rowIndex<startRowIndex){
                Map<String, Object> stringObjectMap = this.checkVersionFormat(rowIndex, csvRow);
                if (!stringObjectMap.isEmpty()){
                    return stringObjectMap;
                }
                rowIndex++;
                continue;
            }
            int fieldCount = csvRow.getFieldCount();
            //The PTS header is not check
            downloadDto = new DownloadDto();
            downloadDto.setPriorityOrderCd(priorityOrderCd);
            downloadDto.setCompanyCd(company);
            //The tanaposition column contains non numbers, error
            String tanaPosition = csvRow.getField(tanaPositionColIndex);
            String taiField = csvRow.getField(taiCdColIndex);
            String tanaField = csvRow.getField(tanaCdColIndex);
            String janField = csvRow.getField(janCdColIndex).trim();

            Map<String, Object> stringObjectMap = this.checkDataFormat(fieldCount,colCount,numPattern,tanaField,tanaPosition,taiField,janField);
           if (!stringObjectMap.isEmpty()){
               return stringObjectMap;
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

        Map<String, Object> data = new HashMap<>(2);
        data.put("janList", uploadJanList);
        data.put("modeName", modeName);
        return ResultMaps.result(ResultEnum.SUCCESS, data);
    }
    public Map<String,Object> checkVersionFormat(int rowIndex, CsvRow csvRow){
        if(rowIndex==0){
            String mode = csvRow.getField(1);
            if(!MagicString.PTS_VERSION.equals(mode)){
                return ResultMaps.result(ResultEnum.VERSION_ERROR);
            }
        }else if(rowIndex==1){
            //String modeName = csvRow.getField(0);
            //if(!modeName.startsWith("?????????")){
            //    return ResultMaps.result(ResultEnum.UPDATE_RANK);
            //}
        }
        return new HashMap<>();
    }
    public Map<String,Object> checkDataFormat(int fieldCount, int colCount, Pattern numPattern, String tanaField, String tanaPosition, String taiField, String janField){
        if(fieldCount!=colCount){
            logger.warn("???????????????");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }


        if(!numPattern.matcher(tanaPosition).matches()){
            logger.warn("rank???????????????????????????????????????");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }


        if(!numPattern.matcher(taiField).matches()){
            logger.warn("tai???????????????????????????????????????");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }


        if(!numPattern.matcher(tanaField).matches()){
            logger.warn("tana???????????????????????????????????????");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }

        if(Strings.isNullOrEmpty(janField)){
            logger.warn("jan?????????????????????????????????");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }
        return new HashMap<>();
    }


    /**
     * rank???????????????+?????????????????????????????????????????????
     *
     * @param colNameList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getPriorityOrderDataUpd(List<String> colNameList, Integer priorityOrderCd,String companyCd,Integer delFlg) {
        String authorCd = session.getAttribute("aud").toString();
        claasicPriorityOrderAttributeClassifyMapper.delete(priorityOrderCd);
        classicPriorityOrderMstAttrSortMapper.deleteAttrSortWK(companyCd,priorityOrderCd);
        classicPriorityOrderMstAttrSortMapper.insertAttrSortWk(companyCd,priorityOrderCd,colNameList);
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrList(companyCd, priorityOrderCd);

        List<Map<String, Object>> datas = null;
        if (delFlg == 0 ){
            PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
            priorityOrderDataService.setPtsClassify(colNameList,priorityOrderMst.getShelfPatternCd(),companyCd,priorityOrderCd);
             datas = priorityOrderDataMapper.getTempDataAndMst(colNameList,attrList, companyCd, priorityOrderCd);

        }else {
            Map<String, Object> priorityOrderListInfo = priorityOrderDataService.getPriorityOrderListInfo(companyCd, priorityOrderCd, colNameList);
            Object data = priorityOrderListInfo.get("data");
            datas = (List<Map<String, Object>>) data;
            datas = priorityOrderDataService.calRank(datas, colNameList);

        }

        if (!datas.isEmpty()) {
            //????????????????????????????????????
            priorityOrderDataMapper.deleteWorkData(companyCd,priorityOrderCd);
            priorityOrderDataMapper.insertWorkData(companyCd,priorityOrderCd,datas,authorCd);

            this.branchNumCalculation(datas,priorityOrderCd,colNameList);
            //??????????????????
            priorityOrderDataMapper.deleteWorkData(companyCd,priorityOrderCd);
            priorityOrderDataMapper.insertWorkData(companyCd,priorityOrderCd,datas,authorCd);
            datas = priorityOrderDataMapper.getData(priorityOrderCd,colNameList);
            return ResultMaps.result(ResultEnum.SUCCESS,datas);
        }

        return ResultMaps.result(ResultEnum.FAILURE);

    }

    @Override

    public Map<String, Object> getPriorityOrderListInfo(String companyCd, Integer priorityOrderCd,List<String> colNameList) {

        String authorCd = session.getAttribute("aud").toString();

        List<String> janCutList = priorityOrderJanCardMapper.getExistOtherMst(companyCd, priorityOrderCd);
        priorityOrderDataMapper.updateRevivification(companyCd,priorityOrderCd);
        if (!janCutList.isEmpty()) {

            priorityOrderDataMapper.updateUPdRank(janCutList, companyCd,priorityOrderCd);
        }
        priorityOrderJanNewMapper.deleteJanNew(companyCd, priorityOrderCd);
        List<Map<String, Object>> janNews = priorityOrderJanNewMapper.getJanNews(priorityOrderCd, colNameList,companyCd);

        if (!janNews.isEmpty()) {
            JSONArray jsonArray = new JSONArray();

            for (Map<String, Object> priorityOrderJanNewVO : janNews) {
                priorityOrderJanNewVO.put(MagicString.POS_AMOUNT, 0);
                priorityOrderJanNewVO.put(MagicString.BRANCH_AMOUNT, 0);
                priorityOrderJanNewVO.put(MagicString.DIFFERENCE, 0);
                priorityOrderJanNewVO.put(MagicString.UNIT_PRICE, 0);
                priorityOrderJanNewVO.put(MagicString.SALE_FORECAST, 0);
                priorityOrderJanNewVO.put(MagicString.BRANCH_NUM, 0);
                priorityOrderJanNewVO.put(MagicString.PRIORITY_ORDER_CD_DB, priorityOrderCd);
                priorityOrderJanNewVO.put(MagicString.AUTHOR_CD_DB, authorCd);
                priorityOrderJanNewVO.put(MagicString.COMPANY_CD_DB, companyCd);
                priorityOrderJanNewVO.put(MagicString.JAN_OLD, "_");
                priorityOrderJanNewVO.put(MagicString.ACTUALITY_ALL_NUM, priorityOrderJanNewVO.get("actuality_compare_num"));
                //??????jsonArray
                jsonArray.add(priorityOrderJanNewVO);
            }

            priorityOrderDataMapper.insertJanNew(jsonArray);
        }
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrList(companyCd, priorityOrderCd);
        List<Map<String, Object>> dataList = priorityOrderDataMapper.getTmpTable( attrList, priorityOrderCd, companyCd);
        for (Map<String, Object> datas : dataList) {
            if (!datas.get(MagicString.JAN_NEW).equals(datas.get(MagicString.JAN_OLD)) && !"_".equals(datas.get(MagicString.JAN_OLD))){
                datas.put(MagicString.RANK_UPD,datas.get(MagicString.RANK_PROP));
            }
        }
        priorityOrderDataMapper.deleteWorkData(companyCd,priorityOrderCd);
        priorityOrderDataMapper.insertTmpTable(dataList);
        for (Map<String, Object> map : dataList) {
            map.remove(MagicString.PRIORITY_ORDER_CD_DB);
            map.remove(MagicString.COMPANY_CD_DB);
            map.remove(MagicString.AUTHOR_CD_DB);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,dataList);
    }


    private  List<Map<String, Object>> reOrder(List<Map<String, Object>> resultJanList){
        List<Map<String, Object>> finalResultList = new ArrayList<>();

        for (int i = 0; i < resultJanList.size(); i++) {
            Map<String, Object> curMap = resultJanList.get(i);
            if( !"99999999".equals(curMap.get(MagicString.RANK_UPD).toString())){
                //??????jan rank don't reorder
                curMap.put(MagicString.RANK_UPD, i+1);
            }
            finalResultList.add(curMap);
        }

        return finalResultList;
    }
    public void colNameList(JSONArray datas, List<Map<String, String>> keyNameList) {
        boolean isGoodsExist=false;
        for (int i = 0; i < ((JSONObject) datas.get(0)).keySet().toArray().length; i++) {
            Map<String, String> maps = new HashMap<>();
            maps.put("name", (String) ((JSONObject) datas.get(0)).keySet().toArray()[i]);
            String col=(String) ((JSONObject) datas.get(0)).keySet().toArray()[i];
            if (col.equals(MagicString.GOODS_RANK)){
                isGoodsExist = true;
            }

            keyNameList.add(maps);
        }
        if (!isGoodsExist){
            Map<String, String> maps = new HashMap<>();
            maps.put("name",MagicString.GOODS_RANK);
            keyNameList.add(maps);

        }

    }


    public  void  branchNumCalculation(List<Map<String,Object>>datas,Integer priorityOrderCd,List<String> colNameList){
        List<Map<String, Object>> branchNumList = priorityOrderDataMapper.getJanBranchNumList("public.priorityorder" + session.getAttribute("aud").toString(), priorityOrderCd, colNameList);
        datas.forEach(objectMap->{
            branchNumList.forEach(map->{
                if (objectMap.get(MagicString.JAN_NEW).equals(map.get(MagicString.JAN_NEW)) && objectMap.get(MagicString.JAN_OLD).equals(map.get(MagicString.JAN_OLD))) {

                    List<String> list = new ArrayList<>();
                    String compareBranch = objectMap.get(MagicString.ACTUALITY_COMPARE_BRANCH).toString();
                    String exceptBranch = objectMap.get(MagicString.EXCEPT_BRANCH).toString();
                    String branch = map.get(MagicString.BRANCH).toString();
                    List<String> compareBrranchList = Arrays.asList(compareBranch.split(","));
                    List<String> exceptBranchList = Arrays.asList(exceptBranch.split(","));
                    List<String> branchList = Arrays.asList(branch.split(","));
                    if (compareBrranchList != null && !compareBranch.equals("")&&!compareBranch.equals("_")){
                        list.addAll(compareBrranchList);
                    }
                    if (branchList != null && !branch.equals("")){
                        list.addAll(branchList);
                    }
                    list = list.stream().distinct().collect(Collectors.toList());
                    List<String> newBranchList = ListDisparityUtils.getListDisparitStr(branchList == null ? new ArrayList<>() : branchList,
                            exceptBranchList == null ? new ArrayList<>() : exceptBranchList);
                    objectMap.put(MagicString.BRANCH_NUM_UPD, map.get(MagicString.BRANCH_NUM));
                    objectMap.put(MagicString.UPDATE_ALL_NUM, list.size());
                }
            });
            objectMap.entrySet().forEach(stringObjectEntry->{
                if (com.google.common.base.Strings.isNullOrEmpty(MapUtils.getString(objectMap,stringObjectEntry.getKey()))){
                    stringObjectEntry.setValue("_");
                }
            });
        });

            datas.forEach(objectMap->{
                boolean emptyJan = isEmptyJan(objectMap.get(MagicString.BRANCH_NUM_UPD) == null, objectMap.get(MagicString.BRANCH_NUM_UPD).equals("_"));
                if (emptyJan) {
                    objectMap.put(MagicString.BRANCH_NUM_UPD, 0);
                }
                 emptyJan = isEmptyJan(objectMap.get(MagicString.BRANCH_NUM) == null, objectMap.get(MagicString.BRANCH_NUM).equals("_"));
                if ( emptyJan) {
                    objectMap.put(MagicString.BRANCH_NUM, 0);
                }
                emptyJan = isEmptyJan(objectMap.get(MagicString.BRANCH_AMOUNT_UPD) == null, objectMap.get(MagicString.BRANCH_AMOUNT_UPD).equals("_"));
                if (emptyJan) {
                    objectMap.put(MagicString.BRANCH_AMOUNT_UPD, 0);
                }
                int difference = Integer.parseInt(objectMap.get(MagicString.BRANCH_NUM_UPD).toString()) - Integer.parseInt(objectMap.get(MagicString.BRANCH_NUM).toString());
                double saleForecast = difference * Double.parseDouble(objectMap.get(MagicString.BRANCH_AMOUNT_UPD).toString())  / 1000;

                BigDecimal bd = BigDecimal.valueOf(saleForecast);
                saleForecast = bd.setScale(0,RoundingMode.HALF_UP).doubleValue();
                objectMap.put(MagicString.DIFFERENCE, difference);
                objectMap.put(MagicString.SALE_FORECAST, saleForecast);
            });

    }

    @Override
    public List<Map<String, Object>> calRank(List<Map<String, Object>> result, List<String> colNameList) {
        result = result.stream().sorted(Comparator.comparing(map -> MapUtils.getInteger(map, MagicString.RANK_UPD))).collect(Collectors.toList());
        //get ??????jan
        List<Map<String, Object>> janNewList = result.stream().filter(map -> "-1".equals(map.get("rank").toString())).collect(Collectors.toList());
        //??????jan group by ??????
        Map<String, List<Map<String, Object>>> janNewMap = janNewList.stream()
                .collect(Collectors.groupingBy(map -> colNameList.stream().map(col->map.get(col).toString()).collect(Collectors.joining(",")),
                        LinkedHashMap::new, Collectors.toList()));
        //??????Jan
        List<Map<String, Object>> janList = result.stream().filter(map -> !"-1".equals(map.get("rank").toString())).collect(Collectors.toList());
        //??????Jan
        Map<String, List<Map<String, Object>>> janMap = janList.stream()
                .collect(Collectors.groupingBy(map -> colNameList.stream().map(col->map.get(col).toString()).collect(Collectors.joining(",")),
                        LinkedHashMap::new, Collectors.toList()));
        //??????Jan group by ??????
        Map<String, List<Map<String, Object>>> resultJanMap = new HashMap<>();
        for (String group : janNewMap.keySet()) {
            //start index
            Integer start = 0;
            //??????jan index,record the position traversed by the list(janList)
            AtomicInteger oldIndex = new AtomicInteger(0);

            List<Map<String, Object>> resultJanList = new ArrayList<>();
            List<Map<String, Object>> janNewTmp = janNewMap.get(group);

            if(!janMap.containsKey(group)){
                resultJanList.addAll(janNewTmp);
                resultJanMap.put(group, resultJanList);
                continue;
            }
            List<Map<String, Object>> janListTmp = janMap.get(group);
            resultJanList = this.calGroupRank(janNewTmp,resultJanList,start,oldIndex,janListTmp);

            if(oldIndex.get() < janListTmp.size()){
                resultJanList.addAll(janListTmp.subList(oldIndex.get(), janListTmp.size()));
            }
            resultJanMap.put(group, resultJanList);
        }

        List<Map<String, Object>> finalResultList = new ArrayList<>();
        resultJanMap.entrySet().forEach(entry->{
            List<Map<String, Object>> resultJanList = entry.getValue();
            finalResultList.addAll(this.reOrder(resultJanList));
        });
        janMap.entrySet().forEach(entry->{
            if(!resultJanMap.containsKey(entry.getKey())){
                List<Map<String, Object>> resultJanList = entry.getValue();
                finalResultList.addAll(this.reOrder(resultJanList));
            }
        });
        return finalResultList;
    }

    public List<Map<String, Object>>  calGroupRank(List<Map<String, Object>> janNewTmp, List<Map<String, Object>> resultJanList, Integer start, AtomicInteger oldIndex, List<Map<String, Object>> janListTmp){
        for (int i = 0; i < janNewTmp.size(); i++) {
            Map<String, Object> curJan = janNewTmp.get(i);
            Integer curRank = Integer.valueOf(curJan.get(MagicString.RANK_UPD).toString());
            if(curRank.equals(99999999)){
                resultJanList.add(janNewTmp.get(i));
                continue;
            }

            //How many elements can be inserted between two rank
            int rangeNum = 0;
            if(i == 0){
                rangeNum = curRank - start - 1;
            }else{
                Map<String, Object> preJan = janNewTmp.get(i-1);
                Integer preRank = Integer.valueOf(preJan.get(MagicString.RANK_UPD).toString());
                rangeNum = curRank - preRank - 1;
            }

            //Prevent (oldIndex + rangeNum) > janListTmp.length and array out of bounds
            if((oldIndex.get() + rangeNum) > janListTmp.size()){
                rangeNum = janListTmp.size()-oldIndex.get();
            }

            int end = Math.min(oldIndex.get() + rangeNum, janListTmp.size());
            if(rangeNum<=janListTmp.size()){
                resultJanList.addAll(janListTmp.subList(oldIndex.get(), end));
                //record the position index
            }
            oldIndex.set(oldIndex.get()+rangeNum);
            resultJanList.add(janNewTmp.get(i));
        }
        return resultJanList;
    }

    private String getBranchNumWrapper(Map<String, Object> branchNum, Map<String, Object> tmpItem){
        String branchNumUpd = "0";
        if(branchNum.get("code").equals(ResultEnum.SUCCESS.getCode())){
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) branchNum.get("data");
            Optional<Map<String, Object>> first = mapList.stream().filter(map -> map.get(MagicString.JAN_OLD).toString().equals(tmpItem.get(MagicString.JAN_OLD))
                    && map.get(MagicString.JAN_NEW).toString().equals(tmpItem.get(MagicString.JAN_NEW))).findFirst();
            if(first.isPresent()){
                branchNumUpd = MapUtils.getInteger(first.get(), MagicString.BRANCH_NUM_UPD, 0)+"";

                tmpItem.put(MagicString.BRANCH_NUM_UPD, branchNumUpd);
                tmpItem.put(MagicString.DIFFERENCE, MapUtils.getInteger(first.get(), "difference", 0)+"");
                tmpItem.put(MagicString.SALE_FORECAST, MapUtils.getInteger(first.get(), MagicString.SALE_FORECAST, 0)+"");
            }
        }

        return branchNumUpd;
    }

}
