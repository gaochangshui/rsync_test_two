package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.ProductOrderParamAttrVO;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommodityScoreMasterService;
import com.trechina.planocycle.service.CommodityScoreParaService;
import com.trechina.planocycle.service.PriorityOrderMstService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

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
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private cgiUtils cgiUtil;
    /**
     * 表示項目のすべてのパラメータを取得
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScorePara(String conpanyCd, Integer productPowerCd) {
        List<ProductPowerShowMst> productPowerShowMstList = productPowerShowMstMapper.selectByPrimaryKey(productPowerCd,conpanyCd);
        logger.info("つかむ取表示プロジェクト参数：{}",productPowerShowMstList);
        ProductOrderParamAttrVO productOrderParamAttrVO = productPowerParamAttributeMapper.selectByPrimaryKey(conpanyCd,productPowerCd);
        logger.info("つかむ取動態列参数：{}",productOrderParamAttrVO);
        //フロントエンドを作成するためのデータフォーマット
        List<String> marketList = new ArrayList<>();
        List<String> posList = new ArrayList<>();
        //データベースの戻り値のすべてのローを巡り、市場のlistとposのlistに組み合わせる
        productPowerShowMstList.forEach(item -> {
            if (item.getMarketPosFlag() == 1) {
                marketList.add(item.getDataCd());
            } else {
                posList.add(item.getDataCd());
            }
        });
       try {
           //動的列の遍歴
           String[] attrList= productOrderParamAttrVO.getAttr().split(",");
           String[] attrKey;
           Map<String,Object> result = new HashMap<>();
           Map<String,Object> attrMap = new HashMap<>();
           result.put("conpanyCd",productPowerShowMstList.get(0).getConpanyCd());
           result.put("productPowerCd",productPowerShowMstList.get(0).getProductPowerCd());
           result.put("MarketData",marketList);
           result.put("PosData",posList);
           JSONArray jsonArray = new JSONArray();
           for (String s : attrList) {
               attrKey=s.split(":");
               attrMap.put("attr"+attrKey[0],attrKey[1]);
           }
           jsonArray.add(result);
           jsonArray.add(attrMap);
           logger.info("動態列返回：{}", jsonArray);
           return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
       }catch (Exception e) {
           logger.info(e.toString());
           return ResultMaps.result(ResultEnum.FAILURE);
       }
    }

    /**
     * 保存期間、表示項目、weightのすべてのパラメータ
     * @param
     * @return
     */
    //@Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setCommodityScorePare(ProductPowerParam productPowerParam) {
        String authorCd = session.getAttribute("aud").toString();

        logger.info("保存期間、表示プロジェクト、weight所有参数:{}",productPowerParam);

        String conpanyCd = productPowerParam.getCompany();
        Integer productPowerCd = productPowerParam.getProductPowerNo();

        //テンポラリ・テーブルの最終テーブルへの保存
        //pos基本データ
            //物理削除挿入の保存の変更
            productPowerDataMapper.phyDeleteSyokika(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endSyokikaForWK(conpanyCd, productPowerCd, authorCd);

            //物理削除挿入の保存の変更
            productPowerDataMapper.phyDeleteGroup(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endGroupForWK(conpanyCd, productPowerCd, authorCd);

            //物理削除挿入の保存の変更
            productPowerDataMapper.phyDeleteYobiiitern(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.phyDeleteYobiiiternData(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endYobiiiternForWk(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endYobiiiternDataForWk(conpanyCd,productPowerCd,authorCd);
            //物理削除挿入の保存の変更
            productPowerDataMapper.phyDeleteIntage(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endIntageForWK(conpanyCd,productPowerCd,authorCd);
            //物理削除挿入の保存の変更
            productPowerDataMapper.deleteData(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.setData(productPowerCd,authorCd,conpanyCd);
            //期間パラメータ削除挿入{{きかんぱらめーた:さくじょそうにゅう}}
            String customerCondition = productPowerParam.getCustomerCondition().toJSONString();
            String project = productPowerParam.getProject().toJSONString();
        productPowerParamMstMapper.deleteParam(conpanyCd,productPowerCd);
        productPowerParamMstMapper.insertParam(productPowerParam,customerCondition,authorCd,project);



        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        //cgi保存
        String uuidSave = UUID.randomUUID().toString();
        ProductPowerDataForCgiDto productPowerDataForCgiSave = new ProductPowerDataForCgiDto();
        productPowerDataForCgiSave.setMode("data_save");
        productPowerDataForCgiSave.setCompany(productPowerParam.getCompany());
        productPowerDataForCgiSave.setGuid(uuidSave);
        productPowerDataForCgiSave.setProductPowerNo(productPowerParam.getProductPowerNo());

        logger.info("保存jan rank{}",productPowerDataForCgiSave);
        //再帰呼び出しcgi，まずtaskidを取得する
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("ProductPowerData");
        String result = null;
        logger.info("携帯参数：{}", productPowerDataForCgiSave);
        result = cgiUtil.postCgi(path, productPowerDataForCgiSave, tokenInfo);
        logger.info("taskid返回--保存jan rank：{}", result);
        String queryPath = resourceBundle.getString("TaskQuery");
        // 帯着taskid，再度cgiに運転状態/データの取得を要求する
        Map<String, Object> data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
        logger.info("保存jan rank{}",data);
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
        ProductPowerParamMst productPowerParamMst = new ProductPowerParamMst();
        productPowerParamMst.setConpanyCd(primaryKeyVO.getCompanyCd());
        productPowerParamMst.setProductPowerCd(primaryKeyVO.getProductPowerCd());
        commodityScoreMasterService.delSmartData(productPowerParamMst);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 調用cgi削除準備プロジェクト
     *
     * @param productPowerReserveMst
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delYoBi(ProductPowerReserveMst productPowerReserveMst) {
        //ProductPowerReservの処理
            ProductPowerDataForCgiDto productPowerDataForCgiDto = new ProductPowerDataForCgiDto();
            productPowerDataForCgiDto.setMode("yobi_delete");
            productPowerDataForCgiDto.setCompany(productPowerReserveMst.getConpanyCd());



            String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
            //cgiを呼び出して予備表示項目を削除
            //再帰的にcgiを呼び出し、まずtaskidを取得する
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("ProductPowerData");
        cgiUtils cgiUtil = new cgiUtils();
        String result = null;
        result = cgiUtil.postCgi(path, productPowerDataForCgiDto, tokenInfo);
        logger.info("taskid返回削除 yobi：{}", result);
        String queryPath = resourceBundle.getString("TaskQuery");
        // taskidを持って、再度cgiに運転状態/データの取得を要求する
        Map<String, Object> data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    private List<WorkProductPowerReserveData> dataFormat(List<String[]> datas, String companyCd, Integer dataCd) {
        String authorCd = session.getAttribute("aud").toString();
        List<WorkProductPowerReserveData> result = new ArrayList<>();
        WorkProductPowerReserveData reserveData = null;
        // 最初の行は見出しです
        for (int i = 1; i < datas.size(); i++) {
            String[] data = datas.get(i);
            reserveData = new WorkProductPowerReserveData();
            reserveData.setCompanyCd(companyCd);
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
    public Map<String, Object> saveYoBi(List<String[]> data, String companyCd, String dataCd,String dataName,Integer valueCd) {
        //
        String aud = session.getAttribute("aud").toString();
        Integer sortMax = productPowerDataMapper.getWKYobiiiternSort(companyCd, aud);
        if (sortMax==null){
            sortMax = 1;
        }else {
            sortMax +=1;
        }

        productPowerDataMapper.insertYobilitem(companyCd,aud,valueCd,dataName,sortMax);
        if (data==null){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        List<WorkProductPowerReserveData> dataList = dataFormat(data, companyCd, valueCd);
        if (dataList.isEmpty()) {
            logger.info("csv文件中没有数据");
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        productPowerDataMapper.deleteWKYobiiiternDataCd(aud,companyCd,valueCd);
        productPowerDataMapper.insertYobilitemData(dataList);
        return ResultMaps.result(ResultEnum.SUCCESS);

    }

    /**
     * rank計算
     * @param
     * @return
     */
    @Override
    public Map<String, Object> rankCalculate(Map<String,Object> map) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = map.get("companyCd").toString();
        Integer productPowerCd = Integer.valueOf(map.get("productPowerCd").toString());
        productPowerDataMapper.deleteWKData(companyCd,authorCd,productPowerCd);
        map.remove("companyCd");
        map.remove("productPowerCd");
        List<Map<String, Object>> rankCalculate = productPowerDataMapper.getProductRankCalculate(map, companyCd, productPowerCd,authorCd);

        productPowerDataMapper.setWKData(authorCd,companyCd,productPowerCd);
        Set<String> colNames = rankCalculate.get(0).keySet();
        for (String colName : colNames) {
            if (!colName.equals("jan")) {
                productPowerDataMapper.setWkDataRank(rankCalculate, authorCd, companyCd, productPowerCd, colName);
            }
        }


        return ResultMaps.result(ResultEnum.SUCCESS,rankCalculate);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> deleteReserve(JSONObject jsonObject) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = String.valueOf(((Map) jsonObject.get("param")).get("companyCd"));
        Integer valueCd = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("valueCd")));
        productPowerDataMapper.deleteWKYobiiiternCd(aud,companyCd,valueCd);
        productPowerDataMapper.deleteWKYobiiiternDataCd(aud,companyCd,valueCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
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
