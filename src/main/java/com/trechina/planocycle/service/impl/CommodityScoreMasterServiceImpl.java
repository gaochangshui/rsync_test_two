package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ProductCdAndNameDto;
import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.CommodityListInfoVO;
import com.trechina.planocycle.entity.vo.ProductOrderAttrAndItemVO;
import com.trechina.planocycle.entity.vo.ProductOrderParamAttrVO;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommodityScoreMasterService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class CommodityScoreMasterServiceImpl implements CommodityScoreMasterService {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private ProductPowerParamMstMapper productPowerParamMstMapper;
    @Autowired
    private ProductPowerParamAttributeMapper productPowerParamAttributeMapper;
    @Autowired
    private ProductPowerShowMstMapper productPowerShowMstMapper;
    @Autowired
    private ProductPowerWeightMapper productPowerWeightMapper;
    @Autowired
    private ProductPowerReserveMstMapper productPowerReserveMstMapper;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private cgiUtils cgiUtil;
    /**
     * 企業情報の取得
     * @return
     */
    @Override
    public Map<String,Object> getEnterpriseInfo() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("CompanyList");
        Object resultInfo = "";
        String result = cgiUtil.getCgi(path+session.getAttribute("inCharge")+"&mode=kigyolist",(String) session.getAttribute("MSPACEDGOURDLP"));
        logger.info(result);
        resultInfo = JSON.parse(result);

        logger.info("获取企业信息：{}",resultInfo);

        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * 企業cd関連商品力点数リスト取得
     * @param conpanyCd
     * @return
     */
    @Override
    public Map<String, Object> getCommodityListInfo(String conpanyCd) {
        List<CommodityListInfoVO> resultInfo = productPowerMstMapper.selectCommodityList(conpanyCd);
        logger.info("获取企业cd关联的商品力点数List：{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * 商品力点数のパラメータを取得する
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getCommodityParam(String conpanyCd, Integer productPowerCd) {
        JSONArray jsonArray = new JSONArray();
        Map<String, Object> result = new HashMap<>();
        ProductPowerParamMst resultInfo = productPowerParamMstMapper.selectCommodityParam(conpanyCd,productPowerCd);
        logger.info("获取商品力点数参数返回值：{}",resultInfo);
        productPowerParamAttr(conpanyCd, productPowerCd, result);

        jsonArray.add(resultInfo);
        jsonArray.add(result);
        return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
    }

    @Override
    public void productPowerParamAttr(String conpanyCd, Integer productPowerCd, Map<String, Object> result) {
        ProductOrderParamAttrVO productOrderParamAttrVO = productPowerParamAttributeMapper.selectByPrimaryKey(conpanyCd, productPowerCd);
        logger.info("获取动态列返回值：{}",productOrderParamAttrVO);
        //動的列の遍歴
        if (productOrderParamAttrVO !=null && !productOrderParamAttrVO.getAttr().equals("")){
            String[] attrList = productOrderParamAttrVO.getAttr().split(",");
            String[] attrKey;
            for (String s : attrList) {
                attrKey = s.split(":");
                result.put("attr" + attrKey[0], attrKey[1]);
            }
        }
    }

    @Override
    public void productPowerParamAttrName(String conpanyCd, Integer productPowerCd, Map<String, Object> result) {
        ProductOrderParamAttrVO productOrderParamAttrVO = productPowerParamAttributeMapper.selectAttrName(conpanyCd, productPowerCd);
        logger.info("获取动态列返回值：{}",productOrderParamAttrVO);
        //動的列の遍歴
        if (productOrderParamAttrVO !=null && !productOrderParamAttrVO.getAttr().equals("")){
            String[] attrList = productOrderParamAttrVO.getAttr().split(",");
            String[] attrKey;
            String[] attrVal;
            int idx = 0;
            for (String s : attrList) {
                if(idx< attrList.length-1){
                    attrKey = s.split(":");
                    attrVal = attrKey[1].split("-");
                    result.put("attr" + attrKey[0], attrVal);
                    idx +=1;
                }
            }
        }
    }

    /**
     * 商品力点数表の属性と品目情報を取得し、優先順位表に使用する
     *
     * @param productOrderCd
     * @return
     */
    @Override
    public ProductOrderAttrAndItemVO getAttrAndItmemInfo(String companyCd,Integer productOrderCd) {
        return productPowerParamMstMapper.selectAttrAndItem(companyCd,productOrderCd);
    }


    /**
     * 商品力点表テンプレート名の保存
     * @param productPowerName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setCommodityList(ProductCdAndNameDto productPowerName) {
        logger.info("商品力点数名保存参数：{}", productPowerName);
        // 名前が重複しているかどうかを判断する

        Integer resultName = productPowerMstMapper.selectExistsName(productPowerName.getProductPowerName(),
                productPowerName.getConpanyCd(),productPowerName.getProductPowerNo());
        Integer resultNum = productPowerMstMapper.selectUpdExistsName(productPowerName.getConpanyCd(), productPowerName.getProductPowerNo());
        if (resultName == 0 && resultNum < 1){
            insertMasterInfo(productPowerName);
            return ResultMaps.result(ResultEnum.SUCCESS);

        }


        if (resultNum == 1 && resultName==0){
            updateMasterInfo(productPowerName);
            return ResultMaps.result(ResultEnum.SUCCESS);
        }



        return ResultMaps.result(ResultEnum.NAMEISEXISTS);
    }

    private void insertMasterInfo(ProductCdAndNameDto productPowerName) {
        ProductPowerMst productPowerMst = new ProductPowerMst();
        productPowerMst.setConpanyCd(productPowerName.getConpanyCd());
        productPowerMst.setProductPowerCd(productPowerName.getProductPowerNo());
        productPowerMst.setProductPowerName(productPowerName.getProductPowerName());

        productPowerMst.setAuthorCd((session.getAttribute("aud").toString()));
        productPowerMst.setAuthorName((String) session.getAttribute("aud"));
        productPowerMst.setMaintainerCd((session.getAttribute("aud").toString()));
        productPowerMst.setMaintainerName((String) session.getAttribute("aud"));
        logger.info("商品力点数名保存：{}", productPowerMst);
        productPowerMstMapper.insert(productPowerMst);
    }
    private void updateMasterInfo(ProductCdAndNameDto productPowerName) {
        ProductPowerMst productPowerMst = new ProductPowerMst();
        productPowerMst.setConpanyCd(productPowerName.getConpanyCd());
        productPowerMst.setProductPowerCd(productPowerName.getProductPowerNo());
        productPowerMst.setProductPowerName(productPowerName.getProductPowerName());

        productPowerMst.setMaintainerCd((session.getAttribute("aud").toString()));
        productPowerMst.setMaintainerName((String) session.getAttribute("aud"));
        logger.info("商品力点数名保存：{}", productPowerMst);
        productPowerMstMapper.update(productPowerMst);
    }



    /**
     * chanel情報の取得
     * @return
     */
    @Override
    public Map<String, Object> getChanelInfo() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String pathInfo = resourceBundle.getString("ChannelList");
        String result=cgiUtil.getCgi(pathInfo,(String) session.getAttribute("MSPACEDGOURDLP"));
        return ResultMaps.result(ResultEnum.SUCCESS, JSON.parse(result));
    }





    @Override
    public boolean delSmartData(ProductPowerParamMst productPowerParamMst) {
        String authorCd = session.getAttribute("aud").toString();
        String authorName = (String) session.getAttribute("aud");
        logger.info("参数为:{}",productPowerParamMst);
        String uuid = UUID.randomUUID().toString();
        //商品力点数mst表削除
        productPowerMstMapper.delete(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd,authorName);
      //パラメータ削除
    productPowerParamMstMapper.deleteCommofityParam(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
        //基本データ削除
        productPowerDataMapper.deleteSyokika(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
        //顧客グループ削除
        productPowerDataMapper.deleteGroup(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
         //予備項目Data削除
        productPowerDataMapper.deleteYobiiitern(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
        productPowerDataMapper.deleteYobiiiternData(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
        //rankスプレッドシート削除
        productPowerDataMapper.deleteRankData(productPowerParamMst.getConpanyCd(),productPowerParamMst.getProductPowerCd(),authorCd);


        ProductPowerDataForCgiDto productPowerDataForCgiDto = new ProductPowerDataForCgiDto();
        productPowerDataForCgiDto.setMode("data_delete");
        productPowerDataForCgiDto.setCompany(productPowerParamMst.getConpanyCd());
        productPowerDataForCgiDto.setProductPowerNo(productPowerParamMst.getProductPowerCd());
        productPowerDataForCgiDto.setGuid(uuid);


        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");

        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("ProductPowerData");
        String result = null;
        result = cgiUtil.postCgi(path,productPowerDataForCgiDto,tokenInfo);
        logger.info("taskid返回：{}",result);
        String queryPath = resourceBundle.getString("TaskQuery");
        // taskidを持って、再度cgiに運転状態/データの取得を要求する
        Map<String,Object> Data = cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
        return true;

    }
//TODO:
    @Override
    public Map<String, Object> getAllDataOrParam(String companyCd, Integer productPowerNo) {
        String aud = session.getAttribute("aud").toString();
        productPowerDataMapper.deleteWKSyokika(companyCd,aud);
        productPowerDataMapper.deleteWKKokyaku(companyCd,aud);
        productPowerDataMapper.deleteWKYobiiitern(aud,companyCd);
        productPowerDataMapper.deleteWKYobiiiternData(aud,companyCd);
        productPowerDataMapper.deleteWKData(companyCd,aud);

        productPowerDataMapper.setWkSyokikaForFinally(companyCd,productPowerNo,aud);
         productPowerDataMapper.setWkGroupForFinally(companyCd,productPowerNo,aud);
         productPowerDataMapper.setWkYobilitemForFinally(companyCd,productPowerNo,aud);
        productPowerDataMapper.setWkYobilitemDataForFinally(companyCd,productPowerNo,aud);
        productPowerDataMapper.setWkDataForFinally(companyCd,productPowerNo,aud);
        List<ProductPowerMstData> allData = productPowerDataMapper.getAllData(companyCd, productPowerNo);
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, productPowerNo);
        ProductPowerParam powerParam = new ProductPowerParam();
        JSONObject jsonObject = JSON.parseObject(param.getCustomerCondition());

        powerParam.setCustomerCondition(jsonObject);
        powerParam.setPosValue(param.getPosValue());
        powerParam.setStoreCd(param.getStoreCd());
        powerParam.setCustomerValue(param.getCustomerValue());
        powerParam.setPrepareValue(param.getPrepareValue());
        powerParam.setRangWeight(param.getRangWeight());
        powerParam.setPrdCd(param.getPrdCd());
        powerParam.setRecentlyFlag(param.getRecentlyFlag());
        powerParam.setRecentlyEndTime(param.getRecentlyEndTime());
        powerParam.setRecentlyStTime(param.getRecentlyStTime());
        powerParam.setSeasonFlag(param.getSeasonFlag());
        powerParam.setSeasonEndTime(param.getSeasonEndTime());
        powerParam.setSeasonStTime(param.getSeasonStTime());
        powerParam.setYearFlag(param.getYearFlag());
        List<ReserveMstVo> reserve = productPowerDataMapper.getReserve(productPowerNo, companyCd);
        List list = new ArrayList();
        list.add(allData);
        list.add(powerParam);
        list.add(reserve);

        return ResultMaps.result(ResultEnum.SUCCESS,list);
    }

}
