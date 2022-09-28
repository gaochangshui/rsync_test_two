package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.CacheUtil;
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

    /**
     * 初期取得優先順位テーブルデータ
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

        logger.info("優先順位テーブルpattertテーブルが保存するデータを保存するには{}：",priorityOrderPatternList);
        priorityOrderPatternMapper.deleteWork(priorityOrderDataDto.getPriorityOrderCd());
        priorityOrderPatternMapper.insertWork(priorityOrderPatternList);

        // 初期化データ
        List<ShelfPtsData> shelfPtsData = shelfPtsDataMapper.getPtsCdByPatternCd(companyCd, priorityOrderDataDto.getShelfPatternCd());
        //ただの用品名2
        int existTableName = mstBranchMapper.checkTableExist("prod_0000_jan_info", "1000");
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
        mapColHeader.put(MagicString.JAN_OLD,"旧JAN");
        mapColHeader.put(MagicString.JAN_NEW,"新JAN");
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
            mapColHeader.put(MagicString.POS_AMOUNT,"POS金額(円)");
            mapColHeader.put(MagicString.UNIT_PRICE,"単価");
            mapColHeader.put(MagicString.BRANCH_AMOUNT,MagicString.BRANCH_AMOUNT_NAME);
            mapColHeader.put(MagicString.BRANCH_NUM,MagicString.BRANCH_NUM_NAME);
            mapColHeader.put(MagicString.BRANCH_NUM_UPD,MagicString.BRANCH_NUM_NAME);
            mapColHeader.put(MagicString.DIFFERENCE,"配荷差");
            mapColHeader.put(MagicString.SALE_FORECAST,"売上増減 予測(千円)");
            mapColHeader.put(MagicString.RANK,"Rank");
            mapColHeader.put(MagicString.RANK_PROP,"Rank");
            mapColHeader.put(MagicString.RANK_UPD,"Rank");
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
        List<Map<String, Object>> datas = shelfPtsDataMapper.getInitialExtraction(shelfPtsData, tableName
                , priorityOrderDataDto.getProductPowerCd(), listTableName, listAttr,colName);
        if (datas.isEmpty()){
            return ResultMaps.result(ResultEnum.SIZEISZERO);
        }

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
     * 属性列名の名前を取得
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
        downloadDto.getList().stream().forEach(item->{
            item.setPriorityOrderCd(downloadDto.getPriorityOrderCd());
            item.setCompanyCd(downloadDto.getCompanyCd());
        });
        //修改jan属性cd
        if (!downloadDto.getList().isEmpty() && downloadDto.getFlag()!=0) {
            priorityOrderAttributeClassifyService.setClassifyList(downloadDto.getList());
        }
        String shelfName = priorityOrderPatternMapper.getShelfName(priorityOrderCd, companyCd);
        String colName = null;
        if (downloadDto.getFlag() == 0){
            shelfName = "現状_品揃えPTS_"+shelfName;
            colName = "rank";

        }else {
            shelfName = "変更後_品揃えPTS_"+shelfName;
            colName = MagicString.RANK_UPD;
        }
        String [] version = {"共通棚割情報",MagicString.PTS_VERSION,"NS"};
        String [] headers = {"棚台番号","棚段番号","棚位置","商品コード","フェース数","フェース面","フェース回転","積上数","陳列種別"};
        String  fileName = "品揃えPTS_20220401"+System.currentTimeMillis()+".csv";

        List<DownloadDto> datas = null;

        datas = priorityOrderDataMapper.downloadForCsv(downloadDto.getTaiCd(), downloadDto.getTanaCd()
                ,downloadDto.getPriorityOrderCd(),colName);

        datas.stream().forEach(item->{
            item.setCompanyCd(downloadDto.getCompanyCd());
            item.setPriorityOrderCd(downloadDto.getPriorityOrderCd());
        });
        if (!downloadDto.getList().isEmpty() && downloadDto.getFlag()!=0) {
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
                String itemHeaderTableName = String.format("\"%s\".prod_%s_jan_kaisou_header_sys", attrArray[0], attrArray[1]);
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
     * 取得優先順位テーブルデータの編集
     * @param priorityOrderDataDto
     * @return
     */
    @Override
    public Map<String, Object> editPriorityOrderData(PriorityOrderDataDto priorityOrderDataDto) {
        String companyCd = priorityOrderDataDto.getCompanyCd();
        Integer priorityOrderCd = priorityOrderDataDto.getPriorityOrderCd();
            logger.info("取得優先順位テーブルデータの編集パラメータは:{}",priorityOrderDataDto);
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
            //最終表をテンポラリ・テーブルに戻す
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            priorityOrderDataService.deleteWorkData(companyCd,newPriorityOrderCd);

            classicPriorityOrderMstMapper.setWorkForFinal(companyCd, priorityOrderCd,simpleDateFormat.format(date),newPriorityOrderCd);
            priorityOrderPatternMapper.insertWorkForFinal(companyCd,priorityOrderCd,newPriorityOrderCd);
            priorityOrderJanCardMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderJanNewMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderJanAttributeMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderJanProposalMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            Integer workCatepakId = priorityOrderCatepakMapper.getWorkCatepakId();
            priorityOrderCatepakMapper.setWorkForFinal(workCatepakId, companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderCatepakAttributeMapper.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd, workCatepakId);
            priorityOrderMapper.insertWork(companyCd, priorityOrderCd,newPriorityOrderCd);
            priorityOrderDataMapper.insertWorkDataForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            workPriorityOrderPtsClassify.setWorkForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            classicPriorityOrderMstAttrSortMapper.insertAttrForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            classicPriorityOrderMstAttrSortMapper.insertAttrSortForFinal(companyCd, priorityOrderCd,newPriorityOrderCd);
            starReadingTableMapper.insertForFinalByPattern(companyCd, priorityOrderCd,newPriorityOrderCd);
            starReadingTableMapper.insertForFinalByBranch(companyCd, priorityOrderCd,newPriorityOrderCd);
        }
        List<Object> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put(MagicString.JAN_OLD,"旧JAN");
        map.put(MagicString.JAN_NEW,"新JAN");
        map.put("sku","SKU");
        map.put(MagicString.BRANCH_AMOUNT_UPD,MagicString.BRANCH_AMOUNT_NAME);
        map.put(MagicString.POS_AMOUNT,"POS金額(円)");
        map.put(MagicString.UNIT_PRICE,"単価");
        map.put(MagicString.BRANCH_AMOUNT,MagicString.BRANCH_AMOUNT_NAME);
        map.put(MagicString.BRANCH_NUM,MagicString.BRANCH_NUM_NAME);
        map.put(MagicString.BRANCH_NUM_UPD,MagicString.BRANCH_NUM_NAME);
        map.put(MagicString.DIFFERENCE,"配荷差");
        map.put(MagicString.SALE_FORECAST,"売上増減 予測(千円)");
        map.put("rank","Rank");
        map.put(MagicString.RANK_PROP,"Rank");
        map.put(MagicString.RANK_UPD,"Rank");
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
                workData.addAll(priorityOrderDataMapper.getWorkData(companyCd, newPriorityOrderCd, attrList));
        list.add(attrSortList);
        list.add(patternOrProduct);
        list.add(workData);
        list.add(attrValueList);
        logger.info("取得優先順位テーブルデータの編集リターンマッチ:{}",list);
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
    @Transactional(rollbackFor = Exception.class)
    public void setPtsClassify(List<String> colNameList, String shelfPatternCd, String companyCd, Integer priorityOrderCd) {
        logger.info("pts classifyパラメータの保存:{},{},{}",colNameList,shelfPatternCd,priorityOrderCd);
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
            }

            List<Integer> ptsCd = workPriorityOrderPtsClassify.getJanPtsCd(companyCd, priorityOrderCd, objectMap);
            Map<String, Object> janBranchNum =  new HashMap<>();
            janBranchNum.put(MagicString.JAN_OLD,objectMap.get(MagicString.JAN_OLD));
            janBranchNum.put(MagicString.JAN_NEW,objectMap.get(MagicString.JAN_NEW));
            if (ptsCd.isEmpty()){
                Integer difference = -Integer.parseInt(branchNumNow.getOrDefault(MagicString.BRANCH_NUM,0).toString());
                double saleForecast = difference  * Double.parseDouble(branchNumNow.getOrDefault(MagicString.BRANCH_AMOUNT_UPD,0).toString()) / 1000;

                BigDecimal bd = BigDecimal.valueOf(saleForecast);
                saleForecast = bd.setScale(0,RoundingMode.HALF_UP).doubleValue();

                janBranchNum.put(MagicString.BRANCH_NUM_UPD,0);
                janBranchNum.put(MagicString.DIFFERENCE,difference);
                janBranchNum.put(MagicString.SALE_FORECAST,saleForecast);
            }else {
                Integer branchNum = workPriorityOrderPtsClassify.getJanBranchNum(ptsCd, objectMap);
                logger.info("店舗数{}",branchNumNow.get(MagicString.BRANCH_NUM));
                Integer difference = branchNum - Integer.parseInt(branchNumNow.getOrDefault(MagicString.BRANCH_NUM,0).toString());
                double saleForecast = difference  * Double.parseDouble(branchNumNow.getOrDefault(MagicString.BRANCH_AMOUNT_UPD,0).toString()) / 1000;
                BigDecimal bd = BigDecimal.valueOf(saleForecast);
                saleForecast = bd.setScale(0,RoundingMode.HALF_UP).doubleValue();
                janBranchNum.put(MagicString.BRANCH_NUM_UPD,branchNum);
                janBranchNum.put(MagicString.DIFFERENCE,difference);
                janBranchNum.put(MagicString.SALE_FORECAST,saleForecast);
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
        List<String> existJanOld = priorityOrderDataMapper.existJanOld(janList);
        List<String> existJanNew = priorityOrderDataMapper.existJanNew(janList);

        if(this.isEmptyJan(!existJanOld.isEmpty(),!existJanNew.isEmpty())){
            existJanOld.addAll(existJanNew);
            existJanOld = existJanOld.stream().distinct().collect(Collectors.toList());
            existJanOld.forEach(old->{
                if(Strings.isNullOrEmpty(janMsg.getOrDefault(old, ""))){
                    janMsg.put(old, "現状棚に並んでいる可能性がありますので削除してください。");
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
                    janMsg.put(old, "すでにJAN変商品として入力済みです。");
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
                    janMsg.put(old, "すでにJAN変商品として入力済みです。");
                }
            });
        }

        return janMsg;
    }

    public boolean isEmptyJan(boolean a,boolean b){
        return a || b;
    }

    /**
     * ptsアップロード後のソート+優先順位表反応ボタン抽出データ
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
        Map<String, Object> resultMap = Maps.newHashMap();

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
            List<DownloadDto> uploadJanList = (List<DownloadDto>) checkResult.get("data");
            List<PriorityOrderAttributeClassify> classifyList = priorityOrderClassifyMapper.getClassifyList(company, priorityOrderCd);

            List<DownloadDto> cutJanList = new ArrayList<>(priorityOrderPtsJandataMapper.selectCutJan(company, priorityOrderCd, uploadJanList));
            if(!cutJanList.isEmpty()){
                ClassicPriorityOrderDataService dataService = applicationContext.getBean(ClassicPriorityOrderDataService.class);
                dataService.doJanCut(cutJanList, company, priorityOrderCd);
            }

            List<DownloadDto> newJanList = new ArrayList<>(priorityOrderPtsJandataMapper.selectNewJan(company, priorityOrderCd, uploadJanList));
            List<ClassicPriorityOrderJanNew> priorityOrderJanNews = null;
            List<PriorityOrderMstAttrSortDto> attrSorts = priorityOrderMstAttrSortMapper.selectWKAttr(company, priorityOrderCd);
            if(!newJanList.isEmpty()){
                ClassicPriorityOrderDataService dataService = applicationContext.getBean(ClassicPriorityOrderDataService.class);
                resultMap = dataService.doJanNew(newJanList, company, priorityOrderCd, taiCd, tanaCd, attrList, classifyList, attrSorts);
                priorityOrderJanNews = (List<ClassicPriorityOrderJanNew>) resultMap.getOrDefault("data", Lists.newArrayList());
            }
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
                //add 新規jan
                List<Map<String, Object>> datas = new ArrayList<>();
                List<PriorityOrderJanAttribute> attrs = priorityOrderJanAttributeMapper.selectAttributeByJan(company, priorityOrderCd, needJanNewList);
                for (DownloadDto downloadDto : needJanNewList) {
                    Map<String, Object> dataMap = new HashMap<>(16);

                    if(priorityOrderJanNews!=null){
                        Optional<ClassicPriorityOrderJanNew> firstOpt = priorityOrderJanNews.stream().filter(janNew -> janNew.getJanNew().equals(downloadDto.getJan())).findFirst();
                        firstOpt.ifPresent(priorityOrderJanNew -> dataMap.put("sku", priorityOrderJanNew.getNameNew()));
                    }

                    dataMap.put(MagicString.JAN_OLD,"_");
                    dataMap.put(MagicString.JAN_NEW,downloadDto.getJan());
                    dataMap.put("rank",-1);
                    dataMap.put(MagicString.GOODS_RANK,downloadDto.getTanapositionCd());
                    dataMap.put(MagicString.RANK_PROP,downloadDto.getTanapositionCd());
                    dataMap.put(MagicString.RANK_UPD,downloadDto.getTanapositionCd());
                    dataMap.put(MagicString.BRANCH_AMOUNT,"_");
                    dataMap.put(MagicString.BRANCH_NUM,"0");
                    dataMap.put(MagicString.UNIT_PRICE,"_");
                    dataMap.put(MagicString.DIFFERENCE,"_".equals(downloadDto.getBranchNum())?0:Math.round(Double.parseDouble(downloadDto.getBranchNum())));
                    dataMap.put(MagicString.BRANCH_NUM_UPD,"_".equals(downloadDto.getBranchNum())?0:Math.round(Double.parseDouble(downloadDto.getBranchNum())));
                    dataMap.put("pos_amount_upd","_");
                    dataMap.put("pos_before_rate","_");
                    dataMap.put(MagicString.POS_AMOUNT,"_");
                    dataMap.put(MagicString.PRIORITY_ORDER_CD_DB, priorityOrderCd);
                    dataMap.put(MagicString.AUTHOR_CD_DB, authorCd);
                    dataMap.put(MagicString.COMPANY_CD_DB, company);
                    List<PriorityOrderJanAttribute> attributes = attrs.stream().filter(attr -> attr.getJanNew().equals(downloadDto.getJan())).collect(Collectors.toList());
                    for (int i = 0; i < attributes.size(); i++) {
                        dataMap.put("attr"+attributes.get(i).getAttrCd(), attributes.get(i).getAttrValue());
                    }
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

            uploadJanList.stream().forEach(jan->{
                Optional<PriorityOrderAttributeClassify> attr1Opt = classifyList.stream()
                        .filter(classify -> classify.getTaiCd().equals(jan.getTaiCd())).findFirst();
                attr1Opt.ifPresent(priorityOrderAttributeClassify -> jan.setAttr1(priorityOrderAttributeClassify.getAttr1()));

                Optional<PriorityOrderAttributeClassify> attr2Opt = classifyList.stream()
                        .filter(classify -> classify.getTanaCd().equals(jan.getTanaCd()) && classify.getTaiCd().equals(jan.getTaiCd())).findFirst();
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
                String[] attrArr = map.getAttrList().split(",");
                for (String s : attrArr) {
                    String[] attrVal = s.split(":");
                    if(attrVal.length > 1){
                        itemMap.put(attrVal[0], attrVal[1]);
                    }else{
                        itemMap.put(attrVal[0], "");
                    }
                }

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
                downloadDto.setBranchNum(branchNumUpd);
                downloadDto.setDifference(MapUtils.getString(map, MagicString.DIFFERENCE));
                downloadDto.setSaleForecast(MapUtils.getString(map, MagicString.SALE_FORECAST));
                return downloadDto;
            }).collect(Collectors.toList());
            priorityOrderPtsJandataMapper.updateRankUpd(newRankList, taiCd, tanaCd, priorityOrderCd);

            if(!needJanNewList.isEmpty()){
                List<Map<String, Object>> lists = new ArrayList<>();
                for (DownloadDto downloadDto : needJanNewList) {
                    Optional<DownloadDto> first = newRankPoList.stream().filter(dto -> dto.getJan().equals(downloadDto.getJan())).findFirst();
                    if(first.isPresent()){
                        String branchNum = first.get().getBranchNum();
                        Map<String, Object> map = new HashMap<>();
                        map.put(MagicString.JAN_NEW, downloadDto.getJan());
                        map.put(MagicString.BRANCH_NUM, branchNum);
                        map.put(MagicString.COMPANY_CD, company);
                        map.put(MagicString.PRIORITY_ORDER_CD, priorityOrderCd);

                        lists.add(map);
                    }
                }

                priorityOrderJanNewMapper.updateBranchNumByList(priorityOrderCd, lists);
            }

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
    public Map<String, Object> doJanNew(List<DownloadDto> newJanList, String company, Integer priorityOrderCd, String taiCd, String tanaCd,
                                        String attrList, List<PriorityOrderAttributeClassify> classifyList,List<PriorityOrderMstAttrSortDto> attrSorts){

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

        //新規jan
        List<Map> maps = new Gson().fromJson(dbData.get("data").toString(), new TypeToken<List<Map>>(){}.getType());
        List<PriorityOrderJanAttribute> janAttrs = new ArrayList<>();

        List<String> allAttrSortList = new ArrayList<>(attrSortMap.keySet());
        allAttrSortList.removeIf(s->s.equals(taiCd)||s.equals(tanaCd));

        List<String> janMstList = maps.stream().map(map -> map.get(MagicString.JAN_NEW).toString()).collect(Collectors.toList());
        //not in jan master
        newJanList.stream().filter(newJan->!janMstList.contains(newJan.getJan())).forEach(newJan->{
            Map<String, Object> item = new HashMap<>(16);
            item.put("sku","");
            item.put(MagicString.JAN_NEW,newJan.getJan());

            this.fillCommonParam(newJan, item);
            item.put(taiCd, newJan.getAttr1());
            item.put(tanaCd, newJan.getAttr2());

            for (int i = 0; i < allAttrSortList.size(); i++) {
                item.put(allAttrSortList.get(i), "");
            }

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
                    for (String attr : attrList.split(",")) {
                        attrValMap.put(attr, item.getOrDefault(attr, ""));
                    }
                    //The order cannot be changed, select only the checked attribute rank(value in attrList)
                    Integer branchNum = priorityOrderResultDataMapper.selectBranchNumByAttr(priorityOrderCd, company, attrValMap);
                    branchNum = Optional.ofNullable(branchNum).orElse(0);
                    item.put(MagicString.BRANCH_NUM, branchNum);

                    for (String attr : allAttrSortList) {
                        attrValMap.put(attr, item.getOrDefault(attr, ""));
                    }
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
                                .filter(classify -> classify.getTanaCd().equals(jan.getTanaCd()) && classify.getTaiCd().equals(jan.getTaiCd())).findFirst();

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

        for (CsvRow csvRow : csvReader) {
            if(csvRow.isEmpty() || csvRow.getFields().stream().allMatch(""::equals)){
                rowIndex++;
                continue;
            }

            if(rowIndex<startRowIndex){
                Map<String, Object> stringObjectMap = this.checkVersionFormat(rowIndex, csvRow);
                if (stringObjectMap != null){
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
           if (stringObjectMap != null){
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

        return ResultMaps.result(ResultEnum.SUCCESS, uploadJanList);
    }
    public Map<String,Object> checkVersionFormat(int rowIndex, CsvRow csvRow){
        if(rowIndex==0){
            String mode = csvRow.getField(1);
            if(!MagicString.PTS_VERSION.equals(mode)){
                return ResultMaps.result(ResultEnum.VERSION_ERROR);
            }
        }else if(rowIndex==1){
            String modeName = csvRow.getField(0);
            if(!modeName.startsWith("変更後")){
                return ResultMaps.result(ResultEnum.UPDATE_RANK);
            }
        }
        return null;
    }
    public Map<String,Object> checkDataFormat(int fieldCount, int colCount, Pattern numPattern, String tanaField, String tanaPosition, String taiField, String janField){
        if(fieldCount!=colCount){
            logger.warn("列数エラー");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }


        if(!numPattern.matcher(tanaPosition).matches()){
            logger.warn("rankには非数値が含まれています");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }


        if(!numPattern.matcher(taiField).matches()){
            logger.warn("taiには非数値が含まれています");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }


        if(!numPattern.matcher(tanaField).matches()){
            logger.warn("tanaには非数値が含まれています");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }

        if(Strings.isNullOrEmpty(janField)){
            logger.warn("janには空が含まれています");
            return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
        }
        return null;
    }


    /**
     * rank属性ソート+優先順位表反応ボタン抽出データ
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
            //データを保存してください
            priorityOrderDataMapper.deleteWorkData(companyCd,priorityOrderCd);
            priorityOrderDataMapper.insertWorkData(companyCd,priorityOrderCd,datas,authorCd);

            this.branchNumCalculation(datas,priorityOrderCd,colNameList);
            //店舗数の計算
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
                //写入jsonArray
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
                //新規jan rank don't reorder
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
                    objectMap.put(MagicString.BRANCH_NUM_UPD, map.get(MagicString.BRANCH_NUM));
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
        //get 新規jan
        List<Map<String, Object>> janNewList = result.stream().filter(map -> "-1".equals(map.get("rank").toString())).collect(Collectors.toList());
        //新規jan group by 階層
        Map<String, List<Map<String, Object>>> janNewMap = janNewList.stream()
                .collect(Collectors.groupingBy(map -> colNameList.stream().map(col->map.get(col).toString()).collect(Collectors.joining(",")),
                        LinkedHashMap::new, Collectors.toList()));
        //既存Jan
        List<Map<String, Object>> janList = result.stream().filter(map -> !"-1".equals(map.get("rank").toString())).collect(Collectors.toList());
        //既存Jan
        Map<String, List<Map<String, Object>>> janMap = janList.stream()
                .collect(Collectors.groupingBy(map -> colNameList.stream().map(col->map.get(col).toString()).collect(Collectors.joining(",")),
                        LinkedHashMap::new, Collectors.toList()));
        //既存Jan group by 階層
        Map<String, List<Map<String, Object>>> resultJanMap = new HashMap<>();
        for (String group : janNewMap.keySet()) {
            //start index
            int start = 0;
            //既存jan index,record the position traversed by the list(janList)
            int oldIndex = 0;

            List<Map<String, Object>> resultJanList = new ArrayList<>();
            List<Map<String, Object>> janNewTmp = janNewMap.get(group);

            if(!janMap.containsKey(group)){
                resultJanList.addAll(janNewTmp);
                resultJanMap.put(group, resultJanList);
                continue;
            }
            List<Map<String, Object>> janListTmp = janMap.get(group);
            resultJanList = this.calGroupRank(janNewTmp,resultJanList,start,oldIndex,janListTmp);

            if(oldIndex < janListTmp.size()){
                resultJanList.addAll(janListTmp.subList(oldIndex, janListTmp.size()));
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

    public List<Map<String, Object>>  calGroupRank(List<Map<String, Object>> janNewTmp, List<Map<String, Object>> resultJanList, int start, int oldIndex, List<Map<String, Object>> janListTmp){
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
            if((oldIndex + rangeNum) > janListTmp.size()){
                rangeNum = janListTmp.size()-oldIndex;
            }

            int end = Math.min(oldIndex + rangeNum, janListTmp.size());
            if(rangeNum<=janListTmp.size()){
                resultJanList.addAll(janListTmp.subList(oldIndex, end));
                //record the position index
            }
            oldIndex += rangeNum;
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
