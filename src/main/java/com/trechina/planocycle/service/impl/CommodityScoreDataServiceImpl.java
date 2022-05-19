package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommodityScoreDataService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
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
    private cgiUtils cgiUtil;


    /**
     * smartによる商品力点数表基本posデータの取得
     *
     * @param
     * @return  String taskID, String companyCd,String commonPartsData,Integer productPowerCd
     */
    @Override
    public Map<String, Object> getCommodityScoreData(Map<String,Object> taskIdMap) {
        String taskID = taskIdMap.get("taskID").toString();
        String commonPartsData = taskIdMap.get("commonPartsData").toString();
        Integer productPowerCd = Integer.valueOf(taskIdMap.get("productPowerCd").toString());
        String companyCd = taskIdMap.get("companyCd").toString();
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        String authorCd = session.getAttribute("aud").toString();
        String[] split = taskID.split(",");
        Map<String, Object> data;
        for (String s : split) {
            if ("".equals(s)){
                continue;
            }
            data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), s, tokenInfo);
            if ("9".equals(data.get("data")) || data.get("data") == null || data.get("data") == "") {
                return data;
            }
            if ("ok".equals(data.get("data"))){
                continue;
            }
        }
        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        String isCompanyCd = null;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        Integer janName2colNum = Integer.valueOf(taskIdMap.get("janName2colNum").toString());
        Integer colNum = 2;
        if (janName2colNum == 1){
             colNum = skuNameConfigMapper.getJanName2colNum(isCompanyCd, jsonObject.get("prodMstClass").toString());
        }
        String tableName = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass);
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", isCompanyCd, prodMstClass);
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass);
        List<Map<String, Object>> janClassifyList = janClassifyMapper.getJanClassify(tableName,tableNameAttr,colNum);
        Map<String, Object> colMap =janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("attr_val").toString(),(k1,k2)->k1, LinkedHashMap::new));
        Map<String, Object> attrColumnMap = janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("sort").toString(),(k1,k2)->k1, LinkedHashMap::new));

        List<Map<String, Object>> allData = productPowerDataMapper.getSyokikaAllData(companyCd,
                janInfoTableName, "\"" + attrColumnMap.get("jan") + "\"", janClassifyList, authorCd,productPowerCd);
        List<Map<String, Object>> resultData = new ArrayList<>();
        resultData.add(colMap);
        resultData.addAll(allData);

        logger.info("返回pos基本情報はい{}", resultData);
        return ResultMaps.result(ResultEnum.SUCCESS, resultData);


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
        if (paramCount >0){
            map.put("changFlag","1");
        }else {
            map.put("changFlag","0");
        }
        String uuid = UUID.randomUUID().toString();
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get("company").toString();
        String commonPartsData = map.get("commonPartsData").toString();
        String company_kokigyou = planocycleKigyoListMapper.getGroupInfo(companyCd);
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
            map.put(":date_mst","date_core_mst");
        }else {
            map.put(":date_mst","date_kigyomst");
        }
        String isCompanyCd = companyCd;
        if ("1".equals(prodIsCore)){
            isCompanyCd = "1000";
            map.put("shouhin_kaisou_mst","shouhin_kaisou_core_mst");
        }else {
            map.put("shouhin_kaisou_mst","shouhin_kaisou_kigyomst");
        }
        if ("1".equals(storeIsCore)){
            map.put(":tenpo_kaisou_mst","tenpo_kaisou_core_mst");
        }else {
            map.put(":tenpo_kaisou_mst","tenpo_kaisou_kigyomst");
        }
        map.remove("commonPartsData");
        Integer productPowerCd = Integer.valueOf(map.get("productPowerNo").toString());
        //選択した品名を判断する
        Integer janName2colNum = Integer.valueOf(map.get("janName2colNum").toString());
        if (janName2colNum == 1){
            Integer prodMstClass = skuNameConfigMapper.getJanName2colNum(isCompanyCd, jsonObject.get("prodMstClass").toString());
            map.put("janName2colNum",prodMstClass);
        }else {
            map.put("janName2colNum","_");
        }
        map.put("guid",uuid);
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
        List<String> getjan = productPowerDataMapper.getjan();
        productPowerDataMapper.selectWKKokyaku(authorCd,companyCd,productPowerCd,getjan);
        productPowerDataMapper.selectWKKIntager(authorCd,companyCd,productPowerCd,getjan);
        String groupResult ="";
        String intergeResult= "";
        if (map.get("channelNm")!=null &&!"".equals(map.get("channelNm"))) {
             groupResult = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), map, tokenInfo);
        }

        //市場データ
        Map<Object,Object> customerCondition = (Map<Object, Object>) map.get("customerCondition");

        if (!customerCondition.isEmpty()) {
            uuid = UUID.randomUUID().toString();
            map.put("mode","shoki_data");
            map.put("guid",uuid);
            map.put("tableName","planocycle.work_product_power_intage");
             intergeResult = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), map, tokenInfo);

        }
        uuid = UUID.randomUUID().toString();
        map.put("mode","shoki_data");
        map.put("guid",uuid);
        map.remove("customerCondition");
        map.put("tableName","planocycle.work_product_power_syokika");
        //posデータ

        String posResult = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), map, tokenInfo);
        String result = groupResult+","+intergeResult + "," + posResult;
        logger.info("taskId返回：{}", result);

        productPowerDataMapper.selectWKSyokika(companyCd,authorCd,productPowerCd,getjan);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }


    @Override
    public Map<String, Object> getCommodityScoreDataFromDB(Integer productPowerCd, String companyCd, String[] posCd,
                                                           String[] prepareCd, String[] intageCd, String[] customerCd) {
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
    }


}
