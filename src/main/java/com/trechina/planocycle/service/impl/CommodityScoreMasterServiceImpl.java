package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ProductCdAndNameDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.ParamConfigVO;
import com.trechina.planocycle.entity.vo.ProductOrderAttrAndItemVO;
import com.trechina.planocycle.entity.vo.ProductOrderParamAttrVO;
import com.trechina.planocycle.entity.vo.ReserveMstVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.CommodityScoreMasterService;
import com.trechina.planocycle.service.IDGeneratorService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    private ChannelListMapper channelListMapper;
    @Autowired
    private PlaceListMapper placeListMapper;
    @Autowired
    private ParamConfigMapper paramConfigMapper;
    @Autowired
    private PlanocycleKigyoListMapper planocycleKigyoListMapper;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private  SkuNameConfigMapper skuNameConfigMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ShelfPatternMstMapper shelfPatternMstMapper;
    @Autowired
    private IDGeneratorService idGeneratorService;
    @Autowired
    private cgiUtils cgiUtil;

    /**
     * 企業情報の取得
     * @return
     */
    @Override
    public Map<String,Object> getEnterpriseInfo() {
        String authorCd = session.getAttribute("aud").toString();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("CompanyList");

        String companys = session.getAttribute("inCharge").toString();
        List<String> companyList = Arrays.asList(companys.split(","));
        List<Map<String,Object>> resultInfo = planocycleKigyoListMapper.getCompanyList(companyList,authorCd);

        logger.info("つかむ取企業信息：{}",resultInfo);

        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    ///**
    // * 企業cd関連商品力点数リスト取得
    // * @param conpanyCd
    // * @return
    // */
    //@Override
    //public Map<String, Object> getCommodityListInfo(String conpanyCd) {
    //    List<CommodityListInfoVO> resultInfo = productPowerMstMapper.selectCommodityList(conpanyCd);
    //    logger.info("つかむ取企業cd関連付け的商品力点数List：{}",resultInfo);
    //    return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    //}

    ///**
    // * 商品力点数のパラメータを取得する
    // * @param conpanyCd
    // * @param productPowerCd
    // * @return
    // */
    //@Override
    //public Map<String, Object> getCommodityParam(String conpanyCd, Integer productPowerCd) {
    //    JSONArray jsonArray = new JSONArray();
    //    Map<String, Object> result = new HashMap<>();
    //    ProductPowerParamMst resultInfo = productPowerParamMstMapper.selectCommodityParam(conpanyCd,productPowerCd);
    //    logger.info("つかむ取商品力点数参数返回値：{}",resultInfo);
    //    productPowerParamAttr(conpanyCd, productPowerCd, result);
    //
    //    jsonArray.add(resultInfo);
    //    jsonArray.add(result);
    //    return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
    //}

    @Override
    public void productPowerParamAttr(String conpanyCd, Integer productPowerCd, Map<String, Object> result) {
        ProductOrderParamAttrVO productOrderParamAttrVO = productPowerParamAttributeMapper.selectByPrimaryKey(conpanyCd, productPowerCd);
        logger.info("つかむ取動態列返回値：{}",productOrderParamAttrVO);
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
        logger.info("つかむ取動態列返回値：{}",productOrderParamAttrVO);
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
        logger.info("商品力点数名保存パラメータ：{}", productPowerName);
        // 名前が重複しているかどうかを判断する
        Integer resultName = productPowerMstMapper.selectExistsName(productPowerName.getProductPowerName(),
                productPowerName.getCompanyCd(),productPowerName.getProductPowerNo());
        Integer resultNum = productPowerMstMapper.selectUpdExistsName(productPowerName.getCompanyCd(), productPowerName.getProductPowerNo());
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
        productPowerMst.setConpanyCd(productPowerName.getCompanyCd());
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
        productPowerMst.setConpanyCd(productPowerName.getCompanyCd());
        productPowerMst.setProductPowerCd(productPowerName.getProductPowerNo());
        productPowerMst.setProductPowerName(productPowerName.getProductPowerName());

        productPowerMst.setMaintainerCd((session.getAttribute("aud").toString()));
        productPowerMst.setMaintainerName((String) session.getAttribute("aud"));
        logger.info("商品力点数名保存：{}", productPowerMst);
        productPowerMstMapper.update(productPowerMst);
    }




    @Override
    public boolean delSmartData(ProductPowerParamMst productPowerParamMst) {
        String authorCd = session.getAttribute("aud").toString();
        String authorName = (String) session.getAttribute("aud");
        logger.info("参数はい:{}",productPowerParamMst);

        try {
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
        } catch (Exception e) {
            logger.error("削除に失敗しました");
        }
        return true;

    }

    @Override
    public Map<String, Object> getAllDataOrParam(String companyCd, Integer productPowerNo,Integer isCover) {
        Integer newProductPowerCd = productPowerNo;
        if (isCover == 1){
            Map<String, Object> productPowerID = idGeneratorService.productPowerNumGenerator();
            ProductPowerNumGenerator prod = (ProductPowerNumGenerator)productPowerID.get("data");
            newProductPowerCd = prod.getId();
        }

        List<String> dataCol = productPowerDataMapper.getDataCol();
        dataCol.remove("product_power_cd");
        String aud = session.getAttribute("aud").toString();
        productPowerMstMapper.deleteWork(companyCd,newProductPowerCd);
        productPowerDataMapper.deleteWKSyokika(companyCd,aud,newProductPowerCd);
        productPowerDataMapper.deleteWKKokyaku(companyCd,aud,newProductPowerCd);
        productPowerDataMapper.deleteWKYobiiitern(aud,companyCd,newProductPowerCd);
        productPowerDataMapper.deleteWKYobiiiternData(aud,companyCd,newProductPowerCd);
        productPowerDataMapper.deleteWKData(companyCd,aud,newProductPowerCd);
        productPowerDataMapper.deleteWKIntage(companyCd,aud,newProductPowerCd);
        productPowerParamMstMapper.deleteWork(companyCd,newProductPowerCd);

        productPowerDataMapper.setWkSyokikaForFinally(companyCd,productPowerNo,aud,newProductPowerCd);
         productPowerDataMapper.setWkGroupForFinally(companyCd,productPowerNo,aud,newProductPowerCd);
         productPowerDataMapper.setWkYobilitemForFinally(companyCd,productPowerNo,aud,newProductPowerCd);
        productPowerDataMapper.setWkYobilitemDataForFinally(companyCd,productPowerNo,aud,newProductPowerCd);
        productPowerDataMapper.setWkDataForFinally(companyCd,productPowerNo,aud,newProductPowerCd,dataCol);
        productPowerDataMapper.setWKIntageForFinally(companyCd,productPowerNo,aud,newProductPowerCd);
        productPowerParamMstMapper.setWorkForFinal(companyCd,productPowerNo,newProductPowerCd);

        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, productPowerNo);
        List<String> cdList = new ArrayList<>();
         cdList.addAll(Arrays.asList(param.getProject().split(",")));
        List<String> yobi = productPowerDataMapper.getYobi(companyCd, productPowerNo, aud);
        cdList.addAll(yobi);
        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        JSONObject jsonObject = JSON.parseObject(param.getCommonPartsData());
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        String isCompanyCd = null;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        Integer janName2colNum = param.getJanName2colNum();
        Integer colNum = 2;
        if (janName2colNum == 1){
            colNum = skuNameConfigMapper.getJanName2colNum(isCompanyCd, jsonObject.get("prodMstClass").toString());
        }
        String tableName = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass);
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass);
        List<Map<String, Object>> janClassifyList = janClassifyMapper.getJanClassify(tableName);
        for (Map<String, Object> map : janClassifyList) {
            if ("jan_name".equals(map.get("attr"))) {
                map.put("sort",colNum);
            }
        }
        LinkedHashMap<String, Object> colMap =janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("attr_val").toString(),(k1, k2)->k1, LinkedHashMap::new));
        Map<String, Object> attrColumnMap = janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("sort").toString(),(k1,k2)->k1, LinkedHashMap::new));

        List<LinkedHashMap<String, Object>> returnDataAttr = new ArrayList<>();
        ProductPowerParam workParam = productPowerParamMstMapper.getWorkParam(companyCd, productPowerNo);
        List<String> storeCd = Arrays.asList(workParam.getStoreCd().split(","));
        List<Integer> shelfPts = shelfPatternMstMapper.getShelfPts(storeCd, companyCd);
        List<LinkedHashMap<String, Object>> allDataAttr = productPowerDataMapper.getAllDataAttr(companyCd, productPowerNo
                , cdList,"\"" + attrColumnMap.get("jan") + "\"",janClassifyList,janInfoTableName,shelfPts,storeCd);
        colMap.put("branchNum","定番店舗数");
        returnDataAttr.add(colMap);
        returnDataAttr.addAll(allDataAttr);
        List<LinkedHashMap<String, Object>> returnDataItem = new ArrayList<>();
        List<LinkedHashMap<String, Object>> allDataItem = productPowerDataMapper.getAllDataItem(companyCd, productPowerNo, cdList,"\"" + attrColumnMap.get("jan") + "\"",janClassifyList,janInfoTableName);
        List<ParamConfigVO> paramConfigVOS = paramConfigMapper.selectParamConfigByCd(cdList);
        LinkedHashMap<String, Object> colName = paramConfigVOS.stream()
                .collect(Collectors.toMap(ParamConfigVO::getItemCd, ParamConfigVO::getItemName, (key1, key2) -> key1, LinkedHashMap::new));
        List<Map<String, Object>> yobiHeader = productPowerDataMapper.getYobiHeader(companyCd, productPowerNo, aud);
        for (Map<String, Object> map : yobiHeader) {
            colName.put((String) map.get("col"),map.get("val"));
        }
        returnDataItem.add(colName);
        returnDataItem.addAll(allDataItem);
        List<LinkedHashMap<String, Object>> allDataRank = productPowerDataMapper.getAllDataRank(companyCd, productPowerNo, cdList,"\"" + attrColumnMap.get("jan") + "\"",janClassifyList,janInfoTableName);
        ProductPowerParam powerParam = new ProductPowerParam();
        JSONObject jsonObject1 = JSON.parseObject(param.getCustomerCondition());

        powerParam.setCustomerCondition(jsonObject1);
        powerParam.setStoreCd(param.getStoreCd());
        powerParam.setRankWeight(param.getRankWeight());
        powerParam.setPrdCd(param.getPrdCd());
        powerParam.setRecentlyFlag(param.getRecentlyFlag());
        powerParam.setRecentlyEndTime(param.getRecentlyEndTime());
        powerParam.setRecentlyStTime(param.getRecentlyStTime());
        powerParam.setSeasonFlag(param.getSeasonFlag());
        powerParam.setSeasonEndTime(param.getSeasonEndTime());
        powerParam.setSeasonStTime(param.getSeasonStTime());
        powerParam.setYearFlag(param.getYearFlag());
        powerParam.setCommonPartsData(param.getCommonPartsData());
        powerParam.setProject(param.getProject());
        powerParam.setPlaceNm(param.getPlaceNm());
        powerParam.setChannelNm(param.getChannelNm());
        powerParam.setShelfPatternCd(param.getShelfPatternCd());
        powerParam.setJanName2colNum(param.getJanName2colNum());
        powerParam.setProductPowerNo(newProductPowerCd);
        powerParam.setCompany(companyCd);
        List<ReserveMstVo> reserve = productPowerDataMapper.getReserve(productPowerNo, companyCd);
        List<Object> dataAll = new ArrayList();
        dataAll.add(returnDataAttr);
        dataAll.add(returnDataItem);
        dataAll.add(allDataRank);
        List<Object> list = new ArrayList();
        list.add(dataAll);
        list.add(powerParam);
        list.add(reserve);

        return ResultMaps.result(ResultEnum.SUCCESS,list);
    }

    @Override
    public Map<String, Object> getPrefectureAndChanelInfo() {
        List list = new ArrayList();
        List<String> placeList = placeListMapper.getPlaceList();
        List<String> channelList = channelListMapper.getChannelList();
        list.add(placeList);
        list.add(channelList);
        return ResultMaps.result(ResultEnum.SUCCESS,list);
    }


}
