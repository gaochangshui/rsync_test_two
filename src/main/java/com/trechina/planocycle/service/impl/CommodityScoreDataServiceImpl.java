package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommodityScoreDataService;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import com.trechina.planocycle.utils.cgiUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
    private MstJanMapper mstJanMapper;
    @Autowired
    private cgiUtils cgiUtil;

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
        String commonPartsData = taskIdMap.get("commonPartsData").toString();
        Integer productPowerCd = Integer.valueOf(taskIdMap.get("productPowerCd").toString());
        String companyCd = taskIdMap.get("companyCd").toString();
        String authorCd = session.getAttribute("aud").toString();
        final Map<String, Object>[] returnMap = new Map[]{null};

        Future future = executor.submit(()->{
            while (true){
                if (taskID.equals("")){
                    returnMap[0] = ResultMaps.result(ResultEnum.FAILURE);
                    break;
                }

                if("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskID)))){
                    returnMap[0] = ResultMaps.result(ResultEnum.SUCCESS);
                    break;
                }

                if (vehicleNumCache.get(taskIdMap.get(MagicString.TASK_ID).toString()+"Exception")!=null){
                    returnMap[0] = ResultMaps.result(ResultEnum.CGIERROR);
                    break;
                }
                if (vehicleNumCache.get(taskIdMap.get(MagicString.TASK_ID).toString())==null){
                    returnMap[0] = ResultMaps.result(ResultEnum.SUCCESS,"9");
                    break;
                }

                if ("ok".equals(vehicleNumCache.get(taskIdMap.get(MagicString.TASK_ID).toString()).toString())){
                    Date date = new Date();
                    log.info("taskID state:{}",vehicleNumCache.get(taskIdMap.get(MagicString.TASK_ID).toString()));
                    vehicleNumCache.remove(taskIdMap.get(MagicString.TASK_ID).toString());
                    String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
                    JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
                    String prodMstClass = jsonObject.get("prodMstClass").toString();
                    String prodIsCore = jsonObject.get("prodIsCore").toString();
                    String isCompanyCd = null;
                    if ("1".equals(prodIsCore)) {
                        isCompanyCd = coreCompany;
                    } else {
                        isCompanyCd = companyCd;
                    }
                    int janName2colNum = Integer.parseInt(taskIdMap.get("janName2colNum").toString());
                    int colNum = 2;
                    if (janName2colNum == 2){
                        colNum = skuNameConfigMapper.getJanName2colNum(isCompanyCd, jsonObject.get("prodMstClass").toString());
                    }else if(janName2colNum==3){
                        colNum = skuNameConfigMapper.getJanItem2colNum(isCompanyCd, jsonObject.get("prodMstClass").toString());
                    }

                    String tableName = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass);
                    String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass);
                    List<Map<String, Object>> janClassifyList = janClassifyMapper.getJanClassify(tableName);
                    for (Map<String, Object> map : janClassifyList) {
                        if ("jan_name".equals(map.get("attr"))) {
                            map.put("sort",colNum);
                        }
                    }
                    Map<String, Object> colMap =janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("attr_val").toString(),(k1,k2)->k1, LinkedHashMap::new));
                    colMap.put("branchNum","定番店舗数");
                    Map<String, Object> attrColumnMap = janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("sort").toString(),(k1,k2)->k1, LinkedHashMap::new));

                    ProductPowerParam workParam = productPowerParamMstMapper.getWorkParam(companyCd, productPowerCd);
                    List<String> storeCd = Arrays.asList(workParam.getStoreCd().split(","));
                    List<Integer> shelfPts = shelfPatternMstMapper.getShelfPts(storeCd, companyCd);
                    Date date2 = new Date();
                    List<Map<String, Object>> allData = productPowerDataMapper.getSyokikaAllData(companyCd,
                            janInfoTableName, "\"" + attrColumnMap.get("jan") + "\"", janClassifyList, authorCd,productPowerCd,shelfPts,storeCd);
                    List<Map<String, Object>> resultData = new ArrayList<>();
                    if (allData.isEmpty()){
                        returnMap[0] = ResultMaps.result(ResultEnum.SIZEISZERO);
                        return;
                    }
                    resultData.add(colMap);
                    resultData.addAll(allData);

                    log.info("返回pos基本情報はい{}", resultData.size());
                    Date date1 = new Date();
                    String format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
                    String format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date1);
                    String format2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date2);
                    log.info("開始時間{}",format);
                    log.info("時間を割く{}",format2);
                    log.info("終了時間{}",format1);
                    returnMap[0] = ResultMaps.result(ResultEnum.SUCCESS, resultData);
                }
            }
        });

        try {
            future.get(MagicString.TASK_TIME_OUT_LONG, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            logger.error("", e);
            return ResultMaps.result(ResultEnum.FAILURE);
        } catch (TimeoutException e) {
            logger.error("", e);
            return ResultMaps.result(ResultEnum.SUCCESS,"9");
        }

        if(returnMap[0]!=null){
            return returnMap[0];
        }

        return ResultMaps.result(ResultEnum.SUCCESS,"9");
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
        Integer paramCount = productPowerDataMapper.getParamCount(map);
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get("company").toString();
        String commonPartsData = map.get("commonPartsData").toString();
        Integer productPowerCd = Integer.valueOf(map.get("productPowerNo").toString());
        //mst
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        productPowerMstMapper.deleteWork(companyCd,productPowerCd);
        productPowerMstMapper.setWork(productPowerCd,companyCd,authorCd,simpleDateFormat.format(date));
        //param
        String customerConditionStr = map.get("customerCondition").toString();
        String prodAttrData = map.get("prodAttrData").toString();
        String singleJan = new Gson().toJson(map.get("singleJan"));
        productPowerParamMstMapper.deleteWork(companyCd,productPowerCd);
        productPowerParamMstMapper.setWork(map,authorCd,customerConditionStr,prodAttrData,singleJan);
        if (paramCount >0){
            map.put("changeFlag","1");
        }else {
            map.put("changeFlag","0");
        }
        if ("".equals(map.get("seasonEndTime"))) {
            map.put("seasonEndTime","_");
        }
        if ("".equals(map.get("seasonStTime"))) {
            map.put("seasonStTime","_");
        }
        String uuid1 = UUID.randomUUID().toString();
        String attrCondition =  this.attrList(map);
        map.put("attrCondition",attrCondition);
        Map<String,Object> janList =  this.janList(map);
        map.put("filterJanlist",janList.get("listDisparitStr"));
        map.put("excjanFlg",janList.get("janExclude"));
        map.remove("singleJan");
        String company_kokigyou = planocycleKigyoListMapper.getGroupInfo(companyCd);
        if (map.get("prdCd").equals("")) {
            map.put("prdCd",null);
        }
        //グループ企業かどうかを判断する
        if (company_kokigyou!=null){
            map.put("company_kokigyou",company_kokigyou);
        }else {
            map.put("company_kokigyou",companyCd+"_"+companyCd);
        }
        //マスタ設定
        JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
        String storeIsCore = jsonObject.get("storeIsCore").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        String dateIsCore  = jsonObject.get("dateIsCore").toString();

        map.put("selected_tenpo",jsonObject.get("storeMstClass").toString());
        map.put("selected_shouhin",jsonObject.get("prodMstClass").toString());
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
        map.remove("commonPartsData");

        //選択した品名を判断する
        Integer janName2colNum = Integer.valueOf(map.get("janName2colNum").toString());
        if (janName2colNum == 2){
            Integer prodMstClass = skuNameConfigMapper.getJanName2colNum(isCompanyCd, jsonObject.get("prodMstClass").toString());
            map.put("janName2colNum",prodMstClass);
        }else if(janName2colNum == 3){
            Integer prodMstClass = skuNameConfigMapper.getJanItem2colNum(isCompanyCd, jsonObject.get("prodMstClass").toString());
            map.put("janName2colNum",prodMstClass);
        }else {
            map.put("janName2colNum","_");
        }
        map.put("guid",uuid1);
        map.put("mode","shoki_data");
        map.put("usercd",authorCd);
        if ("0".equals(map.get("seasonFlag"))) {
            map.put("seasonFlag","MONTH");
        } else {
            map.put("seasonFlag","WEEK");
        }
        if ("0".equals(map.get("recentlyFlag"))) {
            map.put("recentlyFlag","MONTH");
        } else {
            map.put("recentlyFlag","WEEK");
        }
        map.put("tableName","planocycle.work_product_power_kokyaku");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("調用cgiつかむ取data的参数：{}", map);

        //yobi
        productPowerDataMapper.deleteWKYobiiitern(authorCd, companyCd,productPowerCd);
        productPowerDataMapper.deleteWKYobiiiternData(authorCd, companyCd,productPowerCd);
        //顧客データ
        productPowerDataMapper.deleteWKKokyaku(companyCd, authorCd,productPowerCd);
        productPowerDataMapper.deleteWKSyokika(companyCd, authorCd,productPowerCd);
        productPowerDataMapper.deleteWKIntage(companyCd, authorCd,productPowerCd);

        uuid1 = UUID.randomUUID().toString();
        Map<String,Object> posMap = new HashMap();
        posMap.putAll(map);
        posMap.put("mode","shoki_data");
        posMap.put("guid",uuid1);
        posMap.remove("customerCondition");
        posMap.put("tableName","planocycle.work_product_power_syokika");
        //posデータ
        logger.info("posパラメータ{}",posMap);
        String taskQuery = cgiUtil.setPath("TaskQuery");
        String productPowerData = cgiUtil.setPath("ProductPowerData");
        String posResult = cgiUtil.postCgi(productPowerData, posMap, tokenInfo,smartPath);
        String smartPath = this.smartPath;

        Future future = executor.submit(() -> {
                List<String> taskIdList = new ArrayList<>();
                taskIdList.add(posResult);
                String uuid = "";
                Map<String, Object> map1 = null;
                logger.info("pos開始時間：{}",new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_CGI, posResult), Joiner.on(",").join(taskIdList));
                    while (true) {
                        map1 = cgiUtil.postCgiOfWeb(taskQuery, posResult, tokenInfo,smartPath);
                        if (!"9".equals(map1.get("data"))) {
                            if (map1.get("data")==null){
                                vehicleNumCache.put(posResult+"Exception",map1.get("msg"));
                            }
                            break;
                        }
                    }
                logger.info("pos終了時間：{}",new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                    if (map1.get("data")!=null) {
                        Map<Object, Object> customerCondition = (Map<Object, Object>) map.get("customerCondition");
                        if (!customerCondition.isEmpty()) {
                            logger.info("顧客パラメータ{}", map);
                            logger.info("顧客開始時間：{}",new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                            String groupResult = cgiUtil.postCgi(productPowerData, map, tokenInfo, smartPath);
                            taskIdList.add(groupResult);
                            vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_CGI, posResult), Joiner.on(",").join(taskIdList));
                            while (true) {
                                Map<String, Object> map2 = cgiUtil.postCgiOfWeb(taskQuery, groupResult, tokenInfo, smartPath);
                                if (!"9".equals(map2.get("data"))) {
                                    if (map2.get("data") == null) {
                                        vehicleNumCache.put(posResult + "Exception", "error");
                                    }
                                    break;
                                }
                            }
                            logger.info("顧客終了時間：{}",new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                        }
                        //市場データ
                        if (map.get("channelNm") != null && !"".equals(map.get("channelNm"))) {
                            uuid = UUID.randomUUID().toString();
                            map.put("mode", "market_data");
                            map.put("guid", uuid);
                            map.put("tableName", "planocycle.work_product_power_intage");
                            logger.info("市場パラメータ{}", map);
                            String intergeResult = cgiUtil.postCgi(productPowerData, map, tokenInfo, smartPath);
                            taskIdList.add(intergeResult);
                            vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_CGI, posResult), Joiner.on(",").join(taskIdList));
                            logger.info("市場開始時間：{}",new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                            while (true) {
                                Map<String, Object> map2 = cgiUtil.postCgiOfWeb(taskQuery, intergeResult, tokenInfo, smartPath);
                                if (!"9".equals(map2.get("data"))) {
                                    if (map2.get("data") == null) {
                                        vehicleNumCache.put(posResult + "Exception", "error");
                                    }
                                    break;
                                }
                            }
                            logger.info("市場終了時間：{}",new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                        }
                    }
            vehicleNumCache.put(posResult,"ok");
                });
        String result =  posResult;

        vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_FUTURE, posResult), future);

        logger.info("taskId返回：{}", result);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    private Map<String, Object> janList(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String,Object> singleJan = (Map<String,Object>) map.get("singleJan");
        if (!singleJan.isEmpty()) {
            List<String> filterJanList = (List<String>) singleJan.get("filterJanList");
            List<String> noSelectedJanListAll = (List<String>) singleJan.get("noSelectedJanListAll");
            boolean janExclude = (boolean) singleJan.get("janExclude");
            List<String> listDisparitStr = ListDisparityUtils.getListDisparitStr(filterJanList, noSelectedJanListAll);

            resultMap.put("janExclude", janExclude ? 1 : 0);
            resultMap.put("listDisparitStr", Joiner.on(",").join(listDisparitStr));
        }else {
            resultMap.put("janExclude", null);
            resultMap.put("listDisparitStr", null);
        }
        return resultMap;
    }

    private String attrList(Map<String,Object> map) {
        String finalValue = "";

        List list = (ArrayList) map.get("prodAttrData");

        if (!list.isEmpty()){
            for (Object o : list) {
                Map<String,Object> proMap = (Map<String, Object>) o;
                List value = (List)proMap.get("value");
                if (!value.isEmpty()){
                    String id = proMap.get("id").toString().split("_")[2];

                    String join = Joiner.on("$|^").join(value);
                    Boolean flag = (Boolean) proMap.get("flag");
                    String eq = flag ? "!~":"~";
                    if (finalValue.equals("")){
                        finalValue += "$"+id+eq+"/^"+join+"$/ ";
                    }else {
                        finalValue +="&& $"+id+eq+"/^"+join+"$/ ";
                    }

                }
            }
        }

        return finalValue.equals("")?null:finalValue;
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
                    .peek(map->map.put("data_cd", "item"+map.get("data_cd")))
                    .filter(map->Arrays.asList(prepareCd).contains(map.get("data_cd").toString())).collect(Collectors.toList());

            List<Map<String, String>> productPowerMstData = productPowerDataMapper.selectShowData(productPowerCd, paramConfigVOS,reserveMst,
                    customerCd, Arrays.asList(prepareCd), intageCd);
            List<Map<String, String>> returnData = new ArrayList<>();
            Map<String, String> colName = paramConfigVOS.stream()
                    .collect(Collectors.toMap(ParamConfigVO::getItemCd, ParamConfigVO::getItemName, (key1, key2) -> key1, LinkedHashMap::new));

            Map<String, String> preparedColName = reserveMst.stream().collect(Collectors.toMap(map -> map.get("data_cd").toString(),
                    map -> map.get("data_name").toString(), (key1, key2) -> key1, LinkedHashMap::new));
            colName.putAll(preparedColName);
            returnData.add(colName);
            returnData.addAll(productPowerMstData);

            return ResultMaps.result(ResultEnum.SUCCESS, returnData);
        } catch (Exception e) {
            logger.error("",e);
            return ResultMaps.result(ResultEnum.FAILURE);

        }

    }

    @Override
    public Map<String, Object> getJanAttrValueList(String attrList) {
       List<String> attrLists= Arrays.asList( attrList.split(","));
       String company = attrLists.get(0).split("_")[0];
       String classCd = attrLists.get(0).split("_")[1];

        List<Map<String,Object>> lists = new ArrayList<>();
        for (String list : attrLists) {
            Map<String,Object> map = new HashMap<>();
            String attrNameForId = mstJanMapper.getAttrNameForId(list.split("_")[2], company, classCd);
            map.put("title",attrNameForId);
            map.put("id",list);
            map.put("select","");
            map.put("value",new Object[]{});
            map.put("flag",false);
            List<String> attrValueList = mstJanMapper.getAttrValueList(list.split("_")[2], company, classCd);
            map.put("option",attrValueList);
            lists.add(map);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,lists);
    }


}
