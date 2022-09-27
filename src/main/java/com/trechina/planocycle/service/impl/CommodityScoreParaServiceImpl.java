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

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
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
            //期間パラメータ削除挿入きかんぱらめーた:さくじょそうにゅう
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
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> rankCalculate(Map<String,Object> map) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get(MagicString.COMPANY_CD).toString();
        Integer productPowerCd = Integer.valueOf(map.get(MagicString.PRODUCT_POWER_CD).toString());
        map.remove(MagicString.COMPANY_CD);
        map.remove(MagicString.PRODUCT_POWER_CD);

        String uuid = UUID.randomUUID().toString();
        Future<?> future = executor.submit(() -> {
        try {
            productPowerDataMapper.deleteWKData(companyCd,authorCd,productPowerCd);
            List<Map<String, Object>> rankCalculate = productPowerDataMapper.getProductRankCalculate(map, companyCd, productPowerCd,authorCd);
            ProductPowerParam workParam = productPowerParamMstMapper.getWorkParam(companyCd, productPowerCd);
            List<String> storeCd = Arrays.asList(workParam.getStoreCd().split(","));
                storeCd.add("");
            List<Integer> shelfPts = shelfPatternMstMapper.getShelfPts(storeCd, companyCd);
                shelfPts.add(0);
            productPowerDataMapper.setWKData(authorCd,companyCd,productPowerCd,shelfPts,storeCd);
            List<Map<String, Object>> list = new ArrayList<>();
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

            vehicleNumCache.put(uuid+MagicString.DATA_STR, rankCalculate);
        } catch (Exception e) {
            logger.error("rank計算失敗",e);
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
        String companyCd = String.valueOf(((Map) jsonObject.get(MagicString.PARAM)).get(MagicString.COMPANY_CD));
        Integer valueCd = Integer.valueOf(String.valueOf(((Map) jsonObject.get(MagicString.PARAM)).get("valueCd")));
        Integer productPowerCd = Integer.valueOf(String.valueOf(((Map) jsonObject.get(MagicString.PARAM)).get(MagicString.PRODUCT_POWER_CD)));
        productPowerDataMapper.deleteWKYobiiiternCd(aud,companyCd,valueCd,productPowerCd);
        productPowerDataMapper.deleteWKYobiiiternDataCd(aud,companyCd,valueCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> rankCalculateForTaskId(String taskID) {
        LocalDateTime now = LocalDateTime.now();

        while (true){
            if ("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskID)))) {
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
            if (vehicleNumCache.get(taskID+MagicString.DATA_STR) != null){
                Object o = vehicleNumCache.get(taskID + MagicString.DATA_STR);
                vehicleNumCache.remove(taskID+MagicString.DATA_STR);
                return ResultMaps.result(ResultEnum.SUCCESS,o);
            }

            if(Duration.between(now, LocalDateTime.now()).getSeconds() >MagicString.TASK_TIME_OUT_LONG){
                return ResultMaps.result(ResultEnum.SUCCESS,"9");
            }
        }

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
