package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.ProductPowerParamMst;
import com.trechina.planocycle.entity.po.WorkProductPowerReserveData;
import com.trechina.planocycle.entity.vo.PriorityOrderMstVO;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

@Service
public class CommodityScoreParaServiceImpl implements CommodityScoreParaService {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ProductPowerShowMstMapper productPowerShowMstMapper;
    @Autowired
    private ProductPowerParamMstMapper productPowerParamMstMapper;
    @Autowired
    private ProductPowerWeightMapper productPowerWeightMapper;
    @Autowired
    private ProductPowerReserveMstMapper productPowerReserveMstMapper;
    @Autowired
    private ProductPowerParamAttributeMapper productPowerParamAttributeMapper;
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ClassicPriorityOrderMstService classicPriorityOrderMstService;

    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private ShelfPatternMstMapper shelfPatternMstMapper;
    @Autowired
    private IDGeneratorService idGeneratorService;
    @Autowired
    private VehicleNumCache vehicleNumCache;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private cgiUtils cgiUtil;
    ///**
    // * 表示項目のすべてのパラメータを取得
    // * @param conpanyCd
    // * @param productPowerCd
    // * @return
    // */
    //@Override
    //public Map<String, Object> getCommodityScorePara(String conpanyCd, Integer productPowerCd) {
    //    List<ProductPowerShowMst> productPowerShowMstList = productPowerShowMstMapper.selectByPrimaryKey(productPowerCd,conpanyCd);
    //    logger.info("つかむ取表示プロジェクト参数：{}",productPowerShowMstList);
    //    ProductOrderParamAttrVO productOrderParamAttrVO = productPowerParamAttributeMapper.selectByPrimaryKey(conpanyCd,productPowerCd);
    //    logger.info("つかむ取動態列参数：{}",productOrderParamAttrVO);
    //    //フロントエンドを作成するためのデータフォーマット
    //    List<String> marketList = new ArrayList<>();
    //    List<String> posList = new ArrayList<>();
    //    //データベースの戻り値のすべてのローを巡り、市場のlistとposのlistに組み合わせる
    //    productPowerShowMstList.forEach(item -> {
    //        if (item.getMarketPosFlag() == 1) {
    //            marketList.add(item.getDataCd());
    //        } else {
    //            posList.add(item.getDataCd());
    //        }
    //    });
    //   try {
    //       //動的列の遍歴
    //       String[] attrList= productOrderParamAttrVO.getAttr().split(",");
    //       String[] attrKey;
    //       Map<String,Object> result = new HashMap<>();
    //       Map<String,Object> attrMap = new HashMap<>();
    //       result.put("conpanyCd",productPowerShowMstList.get(0).getConpanyCd());
    //       result.put("productPowerCd",productPowerShowMstList.get(0).getProductPowerCd());
    //       result.put("MarketData",marketList);
    //       result.put("PosData",posList);
    //       JSONArray jsonArray = new JSONArray();
    //       for (String s : attrList) {
    //           attrKey=s.split(":");
    //           attrMap.put("attr"+attrKey[0],attrKey[1]);
    //       }
    //       jsonArray.add(result);
    //       jsonArray.add(attrMap);
    //       logger.info("動態列返回：{}", jsonArray);
    //       return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
    //   }catch (Exception e) {
    //       logger.info(e.toString());
    //       return ResultMaps.result(ResultEnum.FAILURE);
    //   }
    //}

    /**
     * 保存期間、表示項目、weightのすべてのパラメータ
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setCommodityScorePare(ProductPowerParam productPowerParam) {
        String authorCd = session.getAttribute("aud").toString();

        logger.info("保存期間、表示プロジェクト、weight所有参数:{}",productPowerParam);

        String companyCd = productPowerParam.getCompany();
        Integer productPowerCd = productPowerParam.getProductPowerNo();
        Integer newProductPowerCd = productPowerCd;

        //テンポラリ・テーブルの最終テーブルへの保存
        //pos基本データ
            //物理削除挿入の保存の変更
            productPowerDataMapper.phyDeleteSyokika(companyCd,newProductPowerCd,authorCd);
            productPowerDataMapper.endSyokikaForWK(companyCd, productPowerCd, authorCd,newProductPowerCd);

            //物理削除挿入の保存の変更
            productPowerDataMapper.phyDeleteGroup(companyCd,newProductPowerCd,authorCd);
            productPowerDataMapper.endGroupForWK(companyCd, productPowerCd, authorCd,newProductPowerCd);

            //物理削除挿入の保存の変更
            productPowerDataMapper.phyDeleteYobiiitern(companyCd,newProductPowerCd,authorCd);
            productPowerDataMapper.phyDeleteYobiiiternData(companyCd,newProductPowerCd,authorCd);
            productPowerDataMapper.endYobiiiternForWk(companyCd,productPowerCd,authorCd,newProductPowerCd);
            productPowerDataMapper.endYobiiiternDataForWk(companyCd,productPowerCd,authorCd,newProductPowerCd);
            //物理削除挿入の保存の変更
            productPowerDataMapper.phyDeleteIntage(companyCd,newProductPowerCd,authorCd);
            productPowerDataMapper.endIntageForWK(companyCd,productPowerCd,authorCd,newProductPowerCd);
            //物理削除挿入の保存の変更
        
            productPowerDataMapper.deleteData(companyCd,newProductPowerCd);
            productPowerDataMapper.setData(productPowerCd,companyCd,newProductPowerCd);
            //期間パラメータ削除挿入{{きかんぱらめーた:さくじょそうにゅう}}
            String customerCondition = productPowerParam.getCustomerCondition().toJSONString();
        String prodAttrData = productPowerParam.getProdAttrData().toString();
        productPowerParamMstMapper.deleteParam(companyCd,newProductPowerCd);
        String singleJan =new Gson().toJson(productPowerParam.getSingleJan());
        productPowerParamMstMapper.insertParam(productPowerParam,customerCondition,authorCd,newProductPowerCd
                ,prodAttrData, singleJan);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }




    /**
     * 商品力点表のすべての情報+優先順位表のすべての情報を削除
     *
     * @param primaryKeyVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delCommodityScoreAllInfo(ProductPowerPrimaryKeyVO primaryKeyVO) {
        logger.info("参数はい：{}",primaryKeyVO);
        String companyCd = primaryKeyVO.getCompanyCd();
        Integer productPowerCd = primaryKeyVO.getProductPowerCd();
        ProductPowerParamMst productPowerParamMst = new ProductPowerParamMst();
        productPowerParamMst.setConpanyCd(companyCd);
        productPowerParamMst.setProductPowerCd(productPowerCd);
        commodityScoreMasterService.delSmartData(productPowerParamMst);
        List<Integer> basicListList = productPowerMstMapper.getBasicList(companyCd, productPowerCd);
        for (Integer priorityOrderCd : basicListList) {
            PriorityOrderMstVO priorityOrderMstVO = new PriorityOrderMstVO();
            priorityOrderMstVO.setCompanyCd(companyCd);
            priorityOrderMstVO.setPriorityOrderCd(priorityOrderCd);
            priorityOrderMstService.deletePriorityOrderAll(priorityOrderMstVO);
        }
        List<Integer> priorityOrderList = productPowerMstMapper.getPriorityOrderList(companyCd, productPowerCd);
        for (Integer priorityOrderCd : priorityOrderList) {
            PriorityOrderPrimaryKeyVO priorityOrderPrimaryKeyVO = new PriorityOrderPrimaryKeyVO();
            priorityOrderPrimaryKeyVO.setCompanyCd(companyCd);
            priorityOrderPrimaryKeyVO.setPriorityOrderCd(priorityOrderCd);
            classicPriorityOrderMstService.delPriorityOrderAllInfo(priorityOrderPrimaryKeyVO);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    ///**
    // * 調用cgi削除準備プロジェクト
    // *
    // * @param productPowerReserveMst
    // * @return
    // */
    //@Transactional(rollbackFor = Exception.class)
    //@Override
    //public Map<String, Object> delYoBi(ProductPowerReserveMst productPowerReserveMst) {
    //    //ProductPowerReservの処理
    //        ProductPowerDataForCgiDto productPowerDataForCgiDto = new ProductPowerDataForCgiDto();
    //        productPowerDataForCgiDto.setMode("yobi_delete");
    //        productPowerDataForCgiDto.setCompany(productPowerReserveMst.getConpanyCd());
    //
    //
    //
    //        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
    //        //cgiを呼び出して予備表示項目を削除
    //        //再帰的にcgiを呼び出し、まずtaskidを取得する
    //    ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
    //    String path = resourceBundle.getString("ProductPowerData");
    //    cgiUtils cgiUtil = new cgiUtils();
    //    String result = null;
    //    result = cgiUtil.postCgi(path, productPowerDataForCgiDto, tokenInfo);
    //    logger.info("taskid返回削除 yobi：{}", result);
    //    String queryPath = resourceBundle.getString("TaskQuery");
    //    // taskidを持って、再度cgiに運転状態/データの取得を要求する
    //    Map<String, Object> data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
    //    return ResultMaps.result(ResultEnum.SUCCESS);
    //}

    private List<WorkProductPowerReserveData> dataFormat(List<String[]> datas, String companyCd, Integer dataCd,Integer productPowerCd) {
        String authorCd = session.getAttribute("aud").toString();
        List<WorkProductPowerReserveData> result = new ArrayList<>();
        WorkProductPowerReserveData reserveData = null;
        // 最初の行は見出しです
        for (int i = 1; i < datas.size(); i++) {
            String[] data = datas.get(i);
            reserveData = new WorkProductPowerReserveData();
            reserveData.setCompanyCd(companyCd);
            reserveData.setProductPowerCd(productPowerCd);
            reserveData.setDataCd(dataCd);
            reserveData.setAuthorCd(authorCd);
            reserveData.setJan(data[0]);
            reserveData.setDataValue(new BigDecimal(data[1]));
            result.add(reserveData);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveYoBi(List<String[]> data, String companyCd, Integer productPowerCd,String dataName,Integer valueCd) {
        //
        String aud = session.getAttribute("aud").toString();
        Integer sortMax = productPowerDataMapper.getWKYobiiiternSort(companyCd, aud,productPowerCd);
        if (sortMax==null){
            sortMax = 1;
        }else {
            sortMax +=1;
        }

        productPowerDataMapper.insertYobilitem(companyCd,aud,valueCd,dataName,sortMax,productPowerCd);
        if (data==null){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        List<WorkProductPowerReserveData> dataList = dataFormat(data, companyCd, valueCd,productPowerCd);
        if (dataList.isEmpty()) {
            logger.info("csv文件中没有数据");
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        productPowerDataMapper.deleteWKYobiiiternDataCd(aud,companyCd,valueCd,productPowerCd);
        productPowerDataMapper.insertYobilitemData(dataList);
        return ResultMaps.result(ResultEnum.SUCCESS);

    }

    /**
     * rank計算
     * @param
     * @return
     */
    @Override
    public Map<String, Object> rankCalculate(Map<String,Object> map) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get("companyCd").toString();
        Integer productPowerCd = Integer.valueOf(map.get("productPowerCd").toString());
        map.remove("companyCd");
        map.remove("productPowerCd");

        String uuid = UUID.randomUUID().toString();
        Future<?> future = executor.submit(() -> {
        try {
            productPowerDataMapper.deleteWKData(companyCd,authorCd,productPowerCd);
            List<Map<String, Object>> rankCalculate = productPowerDataMapper.getProductRankCalculate(map, companyCd, productPowerCd,authorCd);
            ProductPowerParam workParam = productPowerParamMstMapper.getWorkParam(companyCd, productPowerCd);
            List<String> storeCd = Arrays.asList(workParam.getStoreCd().split(","));
            if (storeCd.isEmpty()){
                storeCd.add("");
            }
            List<Integer> shelfPts = shelfPatternMstMapper.getShelfPts(storeCd, companyCd);
            if (shelfPts.isEmpty()){
                shelfPts.add(0);
            }
            if ("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, uuid)))) {
                return;
            }
            productPowerDataMapper.setWKData(authorCd,companyCd,productPowerCd,shelfPts,storeCd);
            List list = new ArrayList();
            for (int i = 0; i < rankCalculate.size(); i++) {
                list.add(rankCalculate.get(i));
                if (i != 0 && i % 1000 == 0){
                    productPowerDataMapper.insertWkRank(list,authorCd,companyCd,productPowerCd);
                    list.clear();
                }
                if ("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, uuid)))) {
                    return;
                }
            }
            if (!list.isEmpty()) {
                productPowerDataMapper.insertWkRank(list, authorCd, companyCd, productPowerCd);
            }

            vehicleNumCache.put(uuid+",data", rankCalculate);
        } catch (Exception e) {

        }
        });

        vehicleNumCache.put(MessageFormat.format(MagicString.TASK_KEY_FUTURE, uuid),future);
        try {
            return ResultMaps.result(ResultEnum.SUCCESS,uuid);
        } catch (CancellationException e) {
            return ResultMaps.result(202, "canceled");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> deleteReserve(JSONObject jsonObject) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = String.valueOf(((Map) jsonObject.get("param")).get("companyCd"));
        Integer valueCd = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("valueCd")));
        Integer productPowerCd = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("productPowerCd")));
        productPowerDataMapper.deleteWKYobiiiternCd(aud,companyCd,valueCd,productPowerCd);
        productPowerDataMapper.deleteWKYobiiiternDataCd(aud,companyCd,valueCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> rankCalculateForTaskId(String taskID) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        int i = 1;
        while (i<Integer.valueOf(MagicString.TASK_TIME_OUT_LONG)){
            if ("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskID)))) {
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
            if (vehicleNumCache.get(taskID+",data") != null){
                Object o = vehicleNumCache.get(taskID + ",data");
                vehicleNumCache.remove(taskID+",data");
                return ResultMaps.result(ResultEnum.SUCCESS,o);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            i++;
        }

            return ResultMaps.result(ResultEnum.SUCCESS,"9");
    }

    /**
     * 物理削除期間パラメータ
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delParam(String conpanyCd, Integer productPowerCd){
        productPowerParamMstMapper.delete(conpanyCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 物理削除表示項目
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delShowMst(String conpanyCd, Integer productPowerCd){
        productPowerShowMstMapper.delete(productPowerCd,conpanyCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 物理的に予備表示項目を削除
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String,Object> delPrePara(String conpanyCd,Integer productPowerCd) {
        productPowerReserveMstMapper.delete(conpanyCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 物理削除weight
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delWeight(String conpanyCd, Integer productPowerCd){
        productPowerWeightMapper.delete(conpanyCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }



}
