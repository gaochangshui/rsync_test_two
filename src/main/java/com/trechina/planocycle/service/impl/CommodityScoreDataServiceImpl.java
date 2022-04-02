package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.dto.ProductPowerGroupDataForCgiDto;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.entity.po.WKYobiiiternData;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ProductPowerDataMapper;
import com.trechina.planocycle.service.CommodityScoreDataService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CommodityScoreDataServiceImpl implements CommodityScoreDataService {
    @Autowired
    private HttpSession session;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private cgiUtils cgiUtil;


    /**
     * smartによる商品力点数表基本posデータの取得
     *
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreData(String taskID, String companyCd) {
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        List strList = new ArrayList();
        // taskIdを持って、再度cgiに運転状態/データの取得を要求する
        Map<String, Object> Data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), taskID, tokenInfo);
        if ("9".equals(Data.get("data"))){
            return Data;
        }
        logger.info("商品力点数表web版cgi返回数据：{}", Data);
        String authorCd = session.getAttribute("aud").toString();
        // 返されるデータは文字列で、2 D配列に処理されます。
        if (Data.get("data") != null) {
            String[] strResult = Data.get("data").toString().split("@");
            String[] strSplit = null;
            String[] arr;
            int a = 1;
            productPowerDataMapper.deleteWKSyokika(companyCd, authorCd);
            productPowerDataMapper.deleteWKKokyaku(companyCd, authorCd);
            productPowerDataMapper.deleteWKYobiiitern(authorCd, companyCd);
            productPowerDataMapper.deleteWKYobiiiternData(authorCd, companyCd);
            for (int i = 0; i < strResult.length; i++) {
                strSplit = strResult[i].split(" ");
                arr = new String[strSplit.length + 1];
                for (int j = strSplit.length - 1; j >= a; j--) {
                    arr[j + 1] = strSplit[j];
                }
                arr[a] = session.getAttribute("aud").toString();
                System.arraycopy(strSplit, 0, arr, 0, a);
                strList.add(arr);
            }
        } else {
            return Data;
        }
        try {
            exec(strList);
        } catch (InterruptedException e) {
            logger.error("", e);
            Thread.currentThread().interrupt();
        }
        List<ProductPowerMstData> syokikaList = productPowerDataMapper.selectWKSyokika(companyCd, authorCd);

        logger.info("返回pos基本信息为{}", syokikaList);
        return ResultMaps.result(ResultEnum.SUCCESS, syokikaList);
    }

    /**
     * 顧客グループデータの取得
     *
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreGroupData(String taskID, String companyCd) {
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        //ユーザーIDの取得
        String authorCd = session.getAttribute("aud").toString();
        if (!taskID.equals("1")) {

            List strList = new ArrayList();
            // taskIdを持って、再度cgiに運転状態/データの取得を要求する
            Map<String, Object> Data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), taskID, tokenInfo);
            if (Data.get("data").equals("9")){
                return Data;
            }
            logger.info("商品力点数表web版cgi返回数据：{}", Data);
            // 返されるデータは文字列で、2 D配列に処理されます。
            if (Data.get("data") != null && Data.get("data") != "") {
                String[] strResult = Data.get("data").toString().split("@");
                String[] strSplit = null;
                String[] arr;
                int a = 1;
                productPowerDataMapper.deleteWKKokyaku(companyCd, authorCd);
                for (int i = 0; i < strResult.length; i++) {
                    strSplit = strResult[i].split(" ");
                    arr = new String[strSplit.length + 1];
                    for (int j = strSplit.length - 1; j >= a; j--) {
                        arr[j + 1] = strSplit[j];
                    }
                    arr[a] = session.getAttribute("aud").toString();
                    System.arraycopy(strSplit, 0, arr, 0, a);
                    //データが大きすぎて1回1000保存
                    if (i % 1000 == 0 && i >= 1000) {
                        productPowerDataMapper.insertGroup(strList);
                        strList.clear();
                    }
                    strList.add(arr);
                }
                if (strList.size() > 0) {
                    productPowerDataMapper.insertGroup(strList);
                }
            } else if ("".equals(Data.get("data"))) {
                productPowerDataMapper.deleteWKKokyaku(companyCd, authorCd);
            } else {

                return Data;
            }


        }
        List<ProductPowerMstData> kokyakuList = productPowerDataMapper.selectWKKokyaku(authorCd, companyCd);
        logger.info("pos基本信息和顾客信息：{}", kokyakuList);
        List<WKYobiiiternData> wkYobiiiternDataList = productPowerDataMapper.selectWKYobiiiternData(authorCd, companyCd);
        logger.info("预备项目：{}", kokyakuList);

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
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }

        });

        return ResultMaps.result(ResultEnum.SUCCESS, kokyakuList);
    }


    /**
     * 获取商品力点数表taskid
     *
     * @param productPowerDataForCgiDto
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreTaskId(ProductPowerDataForCgiDto productPowerDataForCgiDto) {
        String uuid = UUID.randomUUID().toString();
        productPowerDataForCgiDto.setGuid(uuid);
        productPowerDataForCgiDto.setMode("shoki_data");
        productPowerDataForCgiDto.setUsercd(session.getAttribute("aud").toString());
        if ("0".equals(productPowerDataForCgiDto.getSeasonFlag())) {
            productPowerDataForCgiDto.setSeasonFlag("MONTH");
        } else {
            productPowerDataForCgiDto.setSeasonFlag("WEEK");
        }
        if ("0".equals(productPowerDataForCgiDto.getRecentlyFlag())) {
            productPowerDataForCgiDto.setRecentlyFlag("MONTH");
        } else {
            productPowerDataForCgiDto.setRecentlyFlag("WEEK");
        }
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("调用cgi获取data的参数：{}", productPowerDataForCgiDto);
        String result = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), productPowerDataForCgiDto, tokenInfo);
        logger.info("taskId返回：{}", result);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    /**
     * 获取商品力点数表顾客Grouptaskid
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
        logger.info("调用cgi获取data的参数：{}", productPowerDataForCgiDto);
        String result = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), productPowerDataForCgiDto, tokenInfo);
        logger.info("taskId返回：{}", result);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }


    /**
     * 多线程插入pos基本数据
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
        //循环线程
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
