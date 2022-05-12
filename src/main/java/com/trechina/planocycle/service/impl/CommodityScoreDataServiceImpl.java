package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ProductPowerGroupDataForCgiDto;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.JanClassifyMapper;
import com.trechina.planocycle.mapper.ParamConfigMapper;
import com.trechina.planocycle.mapper.ProductPowerDataMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private cgiUtils cgiUtil;


    /**
     * smartによる商品力点数表基本posデータの取得
     *
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreData(String taskID, String companyCd,String commonPartsData) {
        commonPartsData = "{\"dateIsCore\":\"1\",\"storeLevel\":\"3\",\"storeIsCore\":\"1\",\"storeMstClass\":\"0000\",\"prodIsCore\":\"2\",\"prodMstClass\":\"0001\"}";
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        String authorCd = session.getAttribute("aud").toString();
        String[] split = taskID.split(",");
        Map<String, Object> data;
        for (String s : split) {
            data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), s, tokenInfo);
            if ("9".equals(data.get("data")) || data.get("data") == null || data.get("data") == "") {
                return data;
            }
        }
        
        JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        String isCompanyCd = null;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = "1000";
        } else {
            isCompanyCd = "87c6f4";
        }
        String tableName = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass);
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, "0001");
        List<Map<String, Object>> janClassifyList = janClassifyMapper.selectJanClassify(tableName);
        Map<String, String> attrMap = janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("attr_val").toString()));
        Map<String, String> attrColumnMap = janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("sort").toString()));
        List<Map<String, Object>> allData = productPowerDataMapper.getSyokikaAllData(companyCd,
                janInfoTableName, "\"" + attrColumnMap.get("jan_cd") + "\"", janClassifyList, authorCd);
        logger.info("返回pos基本情報はい{}", allData);
        return ResultMaps.result(ResultEnum.SUCCESS, null);


    }



    /**
     * 顧客グループデータの取得
     *
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreGroupData(String taskID, String companyCd,String commonPartsData) {
         commonPartsData = null;

        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        //ユーザーIDの取得
        String authorCd = session.getAttribute("aud").toString();

            if (!"1".equals(taskID)) {
                Map<String, Object> data =null;
                if (vehicleNumCache.get(taskID)==null) {

                    // taskIdを持って、再度cgiに運転状態/データの取得を要求する
                     data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), taskID, tokenInfo);
                    if ("9".equals(data.get("data")) || data.get("data") == null || data.get("data") == "") {
                        return data;
                    }else {
                        Map<String, Object> finalData = data;
                        executor.execute(() -> {
                            vehicleNumCache.put(taskID, "1");
                            List strList = new ArrayList();
                            logger.info("商品力点数表web版cgi返回数据：{}", finalData);
                            // 返されるデータは文字列で、2 D配列に処理されます。
                            String[] strResult = finalData.get("data").toString().split("@");
                            String[] strSplit = null;
                            String[] arr;
                            int a = 1;

                            for (int i = 0; i < strResult.length; i++) {
                                strSplit = strResult[i].split(" ");
                                arr = new String[strSplit.length + 1];
                                for (int j = strSplit.length - 1; j >= a; j--) {
                                    arr[j + 1] = strSplit[j];
                                }
                                arr[a] = authorCd;
                                System.arraycopy(strSplit, 0, arr, 0, a);
                                //データが大きすぎて1回1000保存
                                if (i % 1000 == 0 && i >= 1000) {
                                    productPowerDataMapper.insertGroup(strList);
                                    strList.clear();
                                }
                                strList.add(arr);
                            }
                            if (!strList.isEmpty()) {
                                productPowerDataMapper.insertGroup(strList);
                            }

                            vehicleNumCache.put(taskID, "2");
                        });
                    }

                }
                }
        if ("2".equals(vehicleNumCache.get(taskID)) || "1".equals(taskID)) {
            vehicleNumCache.remove(taskID);
            List<ProductPowerMstData> kokyakuList = productPowerDataMapper.selectWKKokyaku(authorCd, companyCd);
            logger.info("pos基本情報和顧客情報：{}", kokyakuList);
           /* List<WKYobiiiternData> wkYobiiiternDataList = productPowerDataMapper.selectWKYobiiiternData(authorCd, companyCd);
            logger.info("準備プロジェクト：{}", kokyakuList);
            if (wkYobiiiternDataList.isEmpty()) {
                return ResultMaps.result(ResultEnum.SUCCESS, kokyakuList);
            }
            kokyakuList.forEach(item -> {
                for (WKYobiiiternData wkYobiiiternData : wkYobiiiternDataList) {
                    Class w = item.getClass();
                    for (int i = 1; i <= 10; i++) {
                        if (wkYobiiiternData.getJan().equals(item.getJan()) && Integer.valueOf("3100" + i).equals(wkYobiiiternData.getDataCd())) {
                            try {
                                Field field = w.getDeclaredField("item" + i);
                                field.setAccessible(true);
                                field.set(item, wkYobiiiternData.getDataValue());
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                logger.error("", e);
                            }

                        }
                    }

                }
            });*/
            return ResultMaps.result(ResultEnum.SUCCESS, kokyakuList);
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
        String uuid = UUID.randomUUID().toString();
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get("company").toString();
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
        map.put("tableName","work_product_power_kokyaku");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("調用cgiつかむ取data的参数：{}", map);
        //yobi
        productPowerDataMapper.deleteWKYobiiitern(authorCd, companyCd,productPowerCd);
        productPowerDataMapper.deleteWKYobiiiternData(authorCd, companyCd,productPowerCd);
        //顧客データ

        productPowerDataMapper.deleteWKKokyaku(companyCd, authorCd,productPowerCd);
        String groupResult = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), map, tokenInfo);
        uuid = UUID.randomUUID().toString();
        map.put("mode","shoki_data");
        map.put("guid",uuid);
        //市場データ
        String intergeResult = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), map, tokenInfo);
        uuid = UUID.randomUUID().toString();
        map.put("mode","shoki_data");
        map.put("guid",uuid);
        map.remove("customerCondition");
        //posデータ
        productPowerDataMapper.deleteWKSyokika(companyCd, authorCd,productPowerCd);
        String posResult = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), map, tokenInfo);
        String result = groupResult+","+intergeResult + "," + posResult;
        logger.info("taskId返回：{}", result);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    /**
     * 商品パワーポイントリストを取得するお客様Grouptaskid
     *
     * @param productPowerDataForCgiDto
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreGroupTaskId(ProductPowerGroupDataForCgiDto productPowerDataForCgiDto) {
        String aud = session.getAttribute("aud").toString();
        Integer paramCount = productPowerDataMapper.getParamCount(productPowerDataForCgiDto);

        if (paramCount > 0) {
            productPowerDataForCgiDto.setChangeFlag("1");
        } else {
            productPowerDataForCgiDto.setChangeFlag("0");
        }
        String uuid = UUID.randomUUID().toString();
        productPowerDataForCgiDto.setGuid(uuid);
        productPowerDataForCgiDto.setMode("shoki_data");
        if (productPowerDataForCgiDto.getSeasonFlag().equals("0")) {
            productPowerDataForCgiDto.setSeasonFlag("MONTH");
        } else {
            productPowerDataForCgiDto.setSeasonFlag("WEEK");
        }
        if (productPowerDataForCgiDto.getRecentlyFlag().equals("0")) {
            productPowerDataForCgiDto.setRecentlyFlag("MONTH");
        } else {
            productPowerDataForCgiDto.setRecentlyFlag("WEEK");
        }
        productPowerDataForCgiDto.setUsercd(aud);
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("調用cgiつかむ取data的参数：{}", productPowerDataForCgiDto);
        String result = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), productPowerDataForCgiDto, tokenInfo);
        logger.info("taskId返回：{}", result);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
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
