package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.JanClassifyMapper;
import com.trechina.planocycle.mapper.ParamConfigMapper;
import com.trechina.planocycle.mapper.ProductPowerDataMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        String tableName = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass);
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass);
        List<Map<String, Object>> janClassifyList = janClassifyMapper.getJanClassify(tableName);
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
        String uuid = UUID.randomUUID().toString();
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get("company").toString();
        String commonPartsData = map.get("commonPartsData").toString();
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
        if ("1".equals(prodIsCore)){
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
        List<Map<String, String>> productPowerMstData = productPowerDataMapper.selectShowData(productPowerCd, paramConfigVOS, customerCd, prepareCd, intageCd);
        List<Map<String, String>> returnData = new ArrayList<>();
        Map<String, String> colName = paramConfigVOS.stream()
                .collect(Collectors.toMap(ParamConfigVO::getItemCd, ParamConfigVO::getItemName, (key1, key2) -> key1, LinkedHashMap::new));

        returnData.add(colName);
        returnData.addAll(productPowerMstData);

        return ResultMaps.result(ResultEnum.SUCCESS, returnData);
    }


    /**
     * マルチスレッド挿入pos基本データ
     */
    class MyThread implements Runnable {
        private List list;
        private CountDownLatch begin;
        private CountDownLatch end;

        public MyThread(List list, CountDownLatch begin, CountDownLatch end) {
            this.list = list;
            this.begin = begin;
            this.end = end;

        }

        @Override
        public void run() {

            try {


                productPowerDataMapper.insert(list);
                begin.await();

            } catch (InterruptedException e) {
                logger.error("", e);
                Thread.currentThread().interrupt();
            } finally {
                end.countDown();

            }

        }
    }

    public void exec(List<String> list) throws InterruptedException {
        int count = 1000;
        int listSize = list.size();
        int runSize = (listSize / count) + 1;
        List<String> newList = null;
        ExecutorService executor = Executors.newFixedThreadPool(runSize);
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(runSize);
        //ループスレッド
        for (int i = 0; i < runSize; i++) {
            if ((i + 1) == runSize) {
                int startIndex = i * count;
                int endIndex = list.size();
                newList = list.subList(startIndex, endIndex);

            } else {
                int startIndex = i * count;
                int endIndex = (i + 1) * count;
                newList = list.subList(startIndex, endIndex);
            }

            MyThread myThread = new MyThread(newList, begin, end);

            executor.execute(myThread);

        }
        begin.countDown();
        end.await();
        executor.shutdown();
    }

}
