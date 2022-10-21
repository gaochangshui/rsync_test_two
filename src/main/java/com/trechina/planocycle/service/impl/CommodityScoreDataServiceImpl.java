package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommodityScoreDataService;
import com.trechina.planocycle.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommodityScoreDataServiceImpl implements CommodityScoreDataService {
    @Autowired
    private HttpSession session;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private VehicleNumCache vehicleNumCache;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private ParamConfigMapper paramConfigMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ProductPowerReserveMstMapper productPowerReserveMstMapper;
    @Autowired
    private PlanocycleKigyoListMapper planocycleKigyoListMapper;
    @Autowired
    private SkuNameConfigMapper skuNameConfigMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private ProductPowerParamMstMapper productPowerParamMstMapper;
    @Autowired
    private ShelfPatternMstMapper shelfPatternMstMapper;
    @Autowired
    private CommodityScoreDataService commodityScoreDataService;
    @Autowired
    private MstJanMapper mstJanMapper;
    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private LogAspect logAspect;

    @Value("${smartUrlPath}")
    public String smartPath;
    /**
     * smartによる商品力点数表基本posデータの取得
     *
     * @param
     * @return  String taskID, String companyCd,String commonPartsData,Integer productPowerCd
     */
    @Override
    public Map<String, Object> getCommodityScoreData(Map<String,Object> taskIdMap) throws InterruptedException {

        String taskID = taskIdMap.get(MagicString.TASK_ID).toString();
        String commonPartsData = taskIdMap.get(MagicString.COMMON_PARTS_DATA).toString();
        Integer productPowerCd = Integer.valueOf(taskIdMap.get("productPowerCd").toString());
        String companyCd = taskIdMap.get("companyCd").toString();
        String authorCd = session.getAttribute("aud").toString();

        if (taskID.equals("")){
            logger.info(MagicString.GET_COMMODITY_SCORE_DATA, 1);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        if ("5".equals(vehicleNumCache.get(taskID + "Exception"))){
            return ResultMaps.result(ResultEnum.DATAISTOOLARGE);
        }
        if("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskID)))){
            logger.info(MagicString.GET_COMMODITY_SCORE_DATA, 2);
            return ResultMaps.result(ResultEnum.SUCCESS);
        }

        if (vehicleNumCache.get(taskIdMap.get(MagicString.TASK_ID).toString()+MagicString.EXCEPTION)!=null){
            logger.info(MagicString.GET_COMMODITY_SCORE_DATA, 3);
            return ResultMaps.result(ResultEnum.CGIERROR);
        }
        if (vehicleNumCache.get(taskIdMap.get(MagicString.TASK_ID).toString())==null){
            logger.info(MagicString.GET_COMMODITY_SCORE_DATA, 4);
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        Future<?> future = (Future<?>) vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_FUTURE, taskID)+"2");
        if(future==null){
            future = executor.submit(()->{

                while (true){
                    if ("ok".equals(vehicleNumCache.get(taskIdMap.get(MagicString.TASK_ID).toString()).toString())) {
                        log.info("taskID state:{}",vehicleNumCache.get(taskIdMap.get(MagicString.TASK_ID).toString()));
                        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
                        JSONObject jsonObject = JSON.parseObject(commonPartsData);
                        String prodMstClass = jsonObject.get(MagicString.PROD_MST_CLASS).toString();
                        String prodIsCore = jsonObject.get("prodIsCore").toString();
                        String isCompanyCd = null;
                        if ("1".equals(prodIsCore)) {
                            isCompanyCd = coreCompany;
                        } else {
                            isCompanyCd = companyCd;
                        }
                        int janName2colNum = Integer.parseInt(taskIdMap.get(MagicString.JAN_NAME2COL_NUM).toString());
                        int colNum = 2;
                        if (janName2colNum == 2){
                            colNum = skuNameConfigMapper.getJanName2colNum(isCompanyCd, jsonObject.get(MagicString.PROD_MST_CLASS).toString());
                        }else if(janName2colNum==3){
                            colNum = skuNameConfigMapper.getJanItem2colNum(isCompanyCd, jsonObject.get(MagicString.PROD_MST_CLASS).toString());
                        }

                        if ("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskID)))) {
                            break;
                        }

                        String tableName = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass);
                        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", isCompanyCd, prodMstClass);
                        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass);
                        List<Map<String, Object>> janClassifyList = janClassifyMapper.getJanClassify(tableName);
                        for (Map<String, Object> map : janClassifyList) {
                            if ("jan_name".equals(map.get("attr"))) {
                                map.put("sort",colNum);
                            }
                        }
                        Map<String, Object> colMap =janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("attr_val").toString(),(k1, k2)->k1, LinkedHashMap::new));


                        Map<String, Object> attrColumnMap = janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("sort").toString(),(k1,k2)->k1, LinkedHashMap::new));

                        ProductPowerParam workParam = productPowerParamMstMapper.getWorkParam(companyCd, productPowerCd);
                        List<Map<String, Object>> prodAttrData = new Gson().fromJson(workParam.getProdAttrData().toString(), new com.google.common.reflect.TypeToken<List<Map<String, Object>>>(){}.getType());
                        List<String> attr = new ArrayList<>();
                        prodAttrData.forEach(map->{
                            if (map.get("showFlag")!=null && (Boolean) map.get("showFlag")) {
                                attr.add(map.get("id").toString().split("_")[2]);
                            }
                        });
                        List<Map<String, Object>> attrColName = productPowerDataMapper.getAttrColName(attr, tableNameAttr);
                        attrColName.forEach(map->{
                            colMap.put(map.get("colCd").toString(),map.get("colName"));
                        });

                        colMap.put("branchNum","定番店舗数");


                        List<String> storeCd = Arrays.asList(workParam.getStoreCd().split(","));
                        List<Integer> shelfPts = shelfPatternMstMapper.getShelfPts(storeCd, companyCd);

                        if ("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskID)))) {
                            break;
                        }
                        productPowerDataMapper.setIntageJanForSyokika(companyCd,productPowerCd);
                        List<Map<String, Object>> allData = productPowerDataMapper.getSyokikaAllData(companyCd,
                                janInfoTableName, "\"" + attrColumnMap.get("jan") + "\"", janClassifyList, authorCd,productPowerCd
                                ,shelfPts,storeCd,attrColName);
                        List<Map<String, Object>> resultData = new ArrayList<>();

                        resultData.add(colMap);
                        resultData.addAll(allData);

                        log.info("返回pos基本情報はい{}", resultData.size());
                        vehicleNumCache.put(taskID, "9");
                        vehicleNumCache.put(taskID+",data", resultData);
                        break;
                    }
                }
            });
            vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_FUTURE, taskID)+"2", future);
        }

        try {
            future.get(MagicString.TASK_TIME_OUT_LONG, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            logger.error("", e);
            logAspect.setTryErrorLog(e,new Object[]{taskIdMap});
            return ResultMaps.result(ResultEnum.FAILURE);
        } catch (TimeoutException e) {
            return ResultMaps.result(ResultEnum.SUCCESS,"9");
        } catch (CancellationException e){
            logger.error("taskId:{} canceled", taskID);
            return ResultMaps.result(ResultEnum.SUCCESS);
        }

        if("9".equals(vehicleNumCache.get(taskID))){
            if ("5".equals(vehicleNumCache.get(taskID + "Exception"))){
                return ResultMaps.result(ResultEnum.DATAISTOOLARGE);
            }

            if(vehicleNumCache.get(taskID + "Exception")!=null){
                return ResultMaps.result(ResultEnum.FAILURE);
            }

            List<Map<String, Object>> o = (List<Map<String, Object>>) vehicleNumCache.get(taskID + ",data");
            if (o.size()<=1){
                return ResultMaps.result(ResultEnum.SIZEISZERO);
            }

            vehicleNumCache.remove(taskIdMap.get(MagicString.TASK_ID).toString());
            vehicleNumCache.remove(taskIdMap.get(MagicString.TASK_ID).toString());
            vehicleNumCache.remove(MessageFormat.format(MagicString.TASK_KEY_FUTURE, taskID));
            vehicleNumCache.remove(MessageFormat.format(MagicString.TASK_KEY_FUTURE, taskID)+"2");
            return ResultMaps.result(ResultEnum.SUCCESS, o);
        }

        return ResultMaps.result(ResultEnum.SUCCESS, "9");
    }



    /**
     * 商品力点数表taskidを取得する
     *
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreTaskId( Map<String,Object> map) {
        //smtデータソースを教える

        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get("company").toString();
        String commonPartsData = map.get(MagicString.COMMON_PARTS_DATA).toString();

        map = this.dateChange(map,companyCd, commonPartsData);
        String uuid1 = UUID.randomUUID().toString();
        String attrCondition =  this.attrList(map);
        map.put("attrCondition",attrCondition);
        Map<String,Object> janList =  this.janList(map);
        map.put("filterJanlist",janList.get(MagicString.LIST_DISPARIT_STR));
        map.put("excjanFlg",janList.get(MagicString.JAN_EXCLUDE));
        map.remove("singleJan");

        map.put("guid",uuid1);
        map.put("mode","shoki_data");
        map.put("usercd",authorCd);

        map.put(MagicString.TABLE_NAME,"planocycle.work_product_power_kokyaku");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("調用cgiつかむ取data的参数：{}", map);

        map = this.companyChange(map,companyCd,commonPartsData);

        uuid1 = UUID.randomUUID().toString();
        Map<String,Object> posMap = new HashMap<>();
        posMap.putAll(map);
        posMap.put("mode","shoki_data");
        posMap.put("guid",uuid1);
        posMap.remove("customerCondition");
        posMap.put(MagicString.TABLE_NAME,"planocycle.work_product_power_syokika");
        //posデータ
        logger.info("posパラメータ{}",posMap);
        String taskQuery = cgiUtil.setPath("TaskQuery");
        String productPowerData = cgiUtil.setPath("ProductPowerData");
        String posResult = cgiUtil.postCgi(productPowerData, posMap, tokenInfo,smartPath);

        Map<String, Object> finalMap = map;
        Future<?> future = executor.submit(() -> {
                List<String> taskIdList = new ArrayList<>();
                taskIdList.add(posResult);

                Map<String, Object> map1 = null;
                vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_CGI, posResult), Joiner.on(",").join(taskIdList));
                    while (true) {
                        map1 = cgiUtil.postCgiOfWeb(taskQuery, posResult, tokenInfo,smartPath);
                        if (!"9".equals(map1.get("data"))) {
                            if (map1.get("data")==null){
                                vehicleNumCache.put(posResult+MagicString.EXCEPTION,map1.get("msg"));
                            }else if("5".equals(map1.get("data"))){
                                vehicleNumCache.put(posResult + "Exception", "5");
                            }
                            break;
                        }
                    }

                    if (map1.get("data")!=null) {
                        //顧客パラメータ
                       this.callKokyaku(finalMap,productPowerData,tokenInfo,taskIdList,taskQuery,posResult,smartPath);
                        //市場データ
                        this.callIntage(finalMap,productPowerData,tokenInfo,taskIdList,taskQuery,posResult,smartPath);
                    }
            vehicleNumCache.put(posResult,"ok");
                });
        String result =  posResult;

        vehicleNumCache.put(posResult, "1");
        vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_FUTURE, posResult), future);

        logger.info("taskId返回：{}", result);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }
    public void callKokyaku(Map<String, Object> finalMap, String productPowerData, String tokenInfo, List<String> taskIdList, String taskQuery
            , String posResult, String smartPath){
        Map<Object, Object> customerCondition = (Map<Object, Object>) finalMap.get("customerCondition");
        if (!customerCondition.isEmpty()) {
            String  uuid = UUID.randomUUID().toString();
            finalMap.put("guid", uuid);
            finalMap.put("basketFlg",0);
            logger.info("顧客パラメータ{}", finalMap);
            String groupResult = cgiUtil.postCgi(productPowerData, finalMap, tokenInfo, smartPath);
            taskIdList.add(groupResult);
            vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_CGI, posResult), Joiner.on(",").join(taskIdList));
            while (true) {
                Map<String, Object> map2 = cgiUtil.postCgiOfWeb(taskQuery, groupResult, tokenInfo,smartPath);
                if (!"9".equals(map2.get("data"))) {
                    if (map2.get("data") == null) {
                        vehicleNumCache.put(posResult + MagicString.EXCEPTION, "error");
                    }else if("5".equals(map2.get("data"))){
                        vehicleNumCache.put(posResult + "Exception", "5");
                    }
                    break;
                }
            }

        }
    }

    public void callIntage(Map<String, Object> finalMap, String productPowerData, String tokenInfo, List<String> taskIdList, String taskQuery
            , String posResult, String smartPath){
        if (!Strings.isNullOrEmpty(MapUtils.getString(finalMap, "channelNm"))) {
            String  uuid = UUID.randomUUID().toString();
            finalMap.put("mode", "market_data");
            finalMap.put("guid", uuid);
            finalMap.put(MagicString.TABLE_NAME, "planocycle.work_product_power_intage");
            logger.info("市場パラメータ{}", finalMap);
            String intergeResult = cgiUtil.postCgi(productPowerData, finalMap, tokenInfo, smartPath);
            taskIdList.add(intergeResult);
            vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_CGI, posResult), Joiner.on(",").join(taskIdList));

            while (true) {
                Map<String, Object> map2 = cgiUtil.postCgiOfWeb(taskQuery, intergeResult, tokenInfo, smartPath);
                if (!"9".equals(map2.get("data"))) {
                    if (map2.get("data") == null) {
                        vehicleNumCache.put(posResult + MagicString.EXCEPTION, "error");
                    }else if("5".equals(map2.get("data"))){
                        vehicleNumCache.put(posResult + "Exception", "5");
                    }
                    break;
                }
            }

        }
    }
    public Map<String,Object> dateChange(Map<String,Object> map,String companyCd, String commonPartsData){
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        JSONObject jsonObject = JSON.parseObject(commonPartsData);
        String basketIsCore = "";
        String basketMstClass = "";
        if(jsonObject.containsKey("basketIsCore")){
            basketIsCore = jsonObject.getString("basketIsCore").equals("1")?coreCompany:companyCd;
            basketMstClass = jsonObject.getString("basketMstClass");
        }

        String basketProdCd = MapUtils.getString(map, "basketProdCd", "");
        map.put("basketFlg",Strings.isNullOrEmpty(basketProdCd)?0:1);
        map.put("basketprdcd", basketProdCd);
        map.put("basketcompany", basketIsCore);
        map.put("basketselected_shouhin", basketMstClass);

        map.remove(MagicString.BASKET_PROD_CD);
        map.remove("showItemCheck");
        //fix: save->change condition-> error
        map.put("changeFlag","0");
        if ("".equals(map.get("seasonEndTime"))) {
            map.put("seasonEndTime","_");
        }
        if ("".equals(map.get("seasonStTime"))) {
            map.put("seasonStTime","_");
        }
        if (map.get("prdCd").equals("")) {
            map.put("prdCd",null);
        }
        String companyKokigyou = planocycleKigyoListMapper.getGroupInfo(companyCd);
        //グループ企業かどうかを判断する
        if (companyKokigyou!=null){
            map.put("company_kokigyou",companyKokigyou);
        }else {
            map.put("company_kokigyou",companyCd+"_"+companyCd);
        }

        if ("0".equals(map.get(MagicString.SEASON_FLAG))) {
            map.put(MagicString.SEASON_FLAG,"MONTH");
        } else {
            map.put(MagicString.SEASON_FLAG,"WEEK");
        }
        if ("0".equals(map.get(MagicString.RECENTLY_FLAG))) {
            map.put(MagicString.RECENTLY_FLAG,"MONTH");
        } else {
            map.put(MagicString.RECENTLY_FLAG,"WEEK");
        }
        return map;
    }

    public Map<String,Object> companyChange(Map<String,Object> map, String companyCd, String commonPartsData){
        //マスタ設定
        JSONObject jsonObject = JSON.parseObject(commonPartsData);
        String storeIsCore = jsonObject.get("storeIsCore").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        String dateIsCore  = jsonObject.get("dateIsCore").toString();

        map.put("selected_tenpo",jsonObject.get("storeMstClass").toString());
        map.put("selected_shouhin",jsonObject.get(MagicString.PROD_MST_CLASS).toString());
        map.put("storeLevel",jsonObject.get("storeLevel").toString());
        if ("1".equals(dateIsCore)){
            map.put("date_mst","date_core_mst");
        }else {
            map.put("date_mst","date_kigyomst");
        }
        String isCompanyCd = companyCd;
        if ("1".equals(prodIsCore)){
            isCompanyCd = "1000";
            map.put("shouhin_kaisou_mst","shouhin_kaisou_core_mst");
        }else {
            map.put("shouhin_kaisou_mst","shouhin_kaisou_kigyomst");
        }
        if ("1".equals(storeIsCore)){
            map.put("tenpo_kaisou_mst","tenpo_kaisou_core_mst");
        }else {
            map.put("tenpo_kaisou_mst","tenpo_kaisou_kigyomst");
        }
        map.remove(MagicString.COMMON_PARTS_DATA);

        //選択した品名を判断する
        Integer janName2colNum = Integer.valueOf(map.get(MagicString.JAN_NAME2COL_NUM).toString());
        if (janName2colNum == 2){
            Integer prodMstClass = skuNameConfigMapper.getJanName2colNum(isCompanyCd, jsonObject.get(MagicString.PROD_MST_CLASS).toString());
            map.put(MagicString.JAN_NAME2COL_NUM,prodMstClass);
        }else if(janName2colNum == 3){
            Integer prodMstClass = skuNameConfigMapper.getJanItem2colNum(isCompanyCd, jsonObject.get(MagicString.PROD_MST_CLASS).toString());
            map.put(MagicString.JAN_NAME2COL_NUM,prodMstClass);
        }else {
            map.put(MagicString.JAN_NAME2COL_NUM,"_");
        }
        return map;
    }

    public void setProductParam(Map<String, Object> map, Integer productPowerCd, String companyCd, String authorCd, String customerConditionStr, String prodAttrData, String singleJan) {
        //mst
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        productPowerMstMapper.deleteWork(companyCd,productPowerCd);
        productPowerMstMapper.setWork(productPowerCd,companyCd,authorCd,simpleDateFormat.format(date));
        productPowerParamMstMapper.deleteWork(companyCd,productPowerCd);
        productPowerParamMstMapper.setWork(map,authorCd,customerConditionStr,prodAttrData,singleJan);
        //yobi
        productPowerDataMapper.deleteWKYobiiitern(authorCd, companyCd,productPowerCd);
        productPowerDataMapper.deleteWKYobiiiternData(authorCd, companyCd,productPowerCd);
        //顧客データ
        productPowerDataMapper.deleteWKKokyaku(companyCd, authorCd,productPowerCd);
        productPowerDataMapper.deleteWKSyokika(companyCd, authorCd,productPowerCd);
        productPowerDataMapper.deleteWKIntage(companyCd, authorCd,productPowerCd);

    }

    private Map<String, Object> janList(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,Object> singleJan = (Map<String,Object>) map.get("singleJan");
        if (!singleJan.isEmpty()) {
            List<String> filterJanList = (List<String>) singleJan.get("filterJanList");
            List<String> noSelectedJanListAll = (List<String>) singleJan.get("noSelectedJanListAll");
            boolean janExclude = (boolean) singleJan.get(MagicString.JAN_EXCLUDE);
            List<String> listDisparitStr = ListDisparityUtils.getListDisparitStr(filterJanList, noSelectedJanListAll);

            resultMap.put(MagicString.JAN_EXCLUDE, janExclude ? 1 : 0);
            resultMap.put(MagicString.LIST_DISPARIT_STR, Joiner.on(",").join(listDisparitStr));
        }else {
            resultMap.put(MagicString.JAN_EXCLUDE, null);
            resultMap.put(MagicString.LIST_DISPARIT_STR, null);
        }
        return resultMap;
    }

    private String attrList(Map<String,Object> map) {
        StringBuilder finalValue = new StringBuilder();

        List<Object> list = (ArrayList<Object>) map.get("prodAttrData");
        list = this.ConvertToNumber(list);
        if (!list.isEmpty()){
            for (Object o : list) {
                Map<String,Object> proMap = (Map<String, Object>) o;
                List<Object> value = (List<Object>)proMap.get("value");
                List<Object> newValue = new ArrayList<>();
                if (!value.isEmpty()){
                    String id = proMap.get("id").toString().split("_")[2];
                    value.forEach(str->{
                        str = str.toString().replace("(","\\(").replace(")","\\)");
                        newValue.add(str);
                    });
                    String join = Joiner.on("$|^").join(newValue);
                    boolean flag = (boolean) proMap.getOrDefault("rmFlag", false);
                    String eq = flag ? "!~":"~";
                    if (finalValue.toString().equals("")){
                        finalValue.append("$").append(id).append(eq).append("/^").append(join).append("$/ ");
                    }else {
                        finalValue.append("&& $").append(id).append(eq).append("/^").append(join).append("$/ ");
                    }

                }
            }
        }

        return CommonUtil.defaultIfEmpty(finalValue.toString(),null);

    }

    private List<Object>  ConvertToNumber( List<Object> list) {
        for (Object o : list) {
            Map<String,Object> proMap = (Map<String, Object>) o;
            String[] split = proMap.get("id").toString().split("_");
            String company = split[0];
            String classCd = split[1];
            String colCd = split[2];
            String convertNumbers = mstJanMapper.getConvertNumbers(company, classCd);
            JSONArray jsonArray = new JSONArray();
            if (!Strings.isNullOrEmpty(convertNumbers)){
                jsonArray = JSON.parseArray(convertNumbers);
            }
            List<String> value = ((List<Object>) proMap.get("value")).stream().map(val -> val instanceof Double ?
                    BigDecimal.valueOf((Double) val).setScale(0, RoundingMode.HALF_UP).toString() : String.valueOf(val)).collect(Collectors.toList());
            if (!value.isEmpty()&&jsonArray.stream().anyMatch(map->((JSONObject)map).get("col").equals(colCd))){
                 value = mstJanMapper.getNewValue(value,company,classCd,colCd);
                proMap.put("value",value);
            }
        }

        return list;
    }

    /**
     * commonはプロジェクトインタフェースの取得を示します。
     * @param productPowerCd
     * @param companyCd
     * @param posCd
     * @param prepareCd
     * @param intageCd
     * @param customerCd
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreDataFromDB(Integer productPowerCd, String companyCd, String[] posCd,
                                                           String[] prepareCd, String[] intageCd, String[] customerCd) {
        try {
            List<String> cdList = new ArrayList<>();

            if(posCd.length>0){
                cdList.addAll(Arrays.asList(posCd));
            }
            if(prepareCd.length>0){
                cdList.addAll(Arrays.asList(prepareCd));
            }
            if(intageCd.length>0){
                cdList.addAll(Arrays.asList(intageCd));
            }
            if(customerCd.length>0){
                cdList.addAll(Arrays.asList(customerCd));
            }

            List<ParamConfigVO> paramConfigVOS = null;
            if(cdList.isEmpty()){
                paramConfigVOS = new ArrayList<>();
            }else{
                paramConfigVOS = paramConfigMapper.selectParamConfigByCd(cdList);
            }

            List<Map<String, Object>> reserveMst = productPowerReserveMstMapper.selectAllPrepared(productPowerCd);
            reserveMst = reserveMst.stream()
                    .peek(map->map.put(MagicString.DATE_CD, "item"+map.get(MagicString.DATE_CD)))
                    .filter(map->Arrays.asList(prepareCd).contains(map.get(MagicString.DATE_CD).toString())).collect(Collectors.toList());

            List<Map<String, String>> productPowerMstData = productPowerDataMapper.selectShowData(productPowerCd, paramConfigVOS,reserveMst,
                    customerCd, Arrays.asList(prepareCd), intageCd);
            productPowerMstData.forEach(map->map.entrySet().forEach(entry->{
                if (entry.getKey().equals("intage_item03")){
                    Double value = Double.valueOf( entry.getValue());
                    String result = String.format("%.1f",value);
                    entry.setValue(result+MagicString.PERCENTAGE);
                }else if (!entry.getKey().equals("jan")){
                    Integer value = Double.valueOf( entry.getValue()).intValue();
                    entry.setValue(value.toString());
                }
            }));
            List<Map<String, String>> returnData = new ArrayList<>();
            Map<String, String> colName = paramConfigVOS.stream()
                    .collect(Collectors.toMap(ParamConfigVO::getItemCd, ParamConfigVO::getItemName, (key1, key2) -> key1, LinkedHashMap::new));

            Map<String, String> preparedColName = reserveMst.stream().collect(Collectors.toMap(map -> map.get(MagicString.DATE_CD).toString(),
                    map -> map.get("data_name").toString(), (key1, key2) -> key1, LinkedHashMap::new));
            colName.putAll(preparedColName);
            returnData.add(colName);
            returnData.addAll(productPowerMstData);

            return ResultMaps.result(ResultEnum.SUCCESS, returnData);
        } catch (Exception e) {
            logger.error("",e);
            logAspect.setTryErrorLog(e,new Object[]{productPowerCd,companyCd});
            return ResultMaps.result(ResultEnum.FAILURE);

        }

    }

    @Override
    public Map<String, Object> getJanAttrValueList(String attrList) {
       List<String> attrLists= Arrays.asList( attrList.split(","));
       String company = attrLists.get(0).split("_")[0];
       String classCd = attrLists.get(0).split("_")[1];

        List<Map<String,Object>> lists = new ArrayList<>();
        String convertNumbers = mstJanMapper.getConvertNumbers(company, classCd);
        JSONArray jsonArray = new JSONArray();
        if (!Strings.isNullOrEmpty(convertNumbers)){
             jsonArray = JSON.parseArray(convertNumbers);
        }
        //List<String> colList  = Arrays.asList(convertNumbers.split(",")) ;

        for (String list : attrLists) {
            boolean flag = false;
            String unit = "";
            if (jsonArray.stream().anyMatch(map->((JSONObject)map).get("col").equals(list.split("_")[2]))) {
                flag = true;
                for (Object jsonObject : jsonArray) {
                    if (((JSONObject)jsonObject).get("col").equals(list.split("_")[2])) {
                        unit = ((JSONObject) jsonObject).getString("unit");
                    }
                }
            }
            Map<String,Object> map = new HashMap<>();
            String attrNameForId = mstJanMapper.getAttrNameForId(list.split("_")[2], company, classCd);
            map.put("title",attrNameForId);
            map.put("id",list);
            map.put("select","");
            map.put("value",new Object[]{});
            map.put("rmFlag",false);
            map.put("showFlag",false);
            map.put("unit",unit);
            map.put("range",false);
            List<String> attrValueList = new ArrayList<>();
            if (flag){
                attrValueList = mstJanMapper.getAttrConvertToNumber(list.split("_")[2], company, classCd);
            }else {
                 attrValueList = mstJanMapper.getAttrValueList(list.split("_")[2], company, classCd);
            }
            map.put("option",attrValueList);
            lists.add(map);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,lists);
    }


}
