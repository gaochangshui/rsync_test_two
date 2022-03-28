package com.trechina.planocycle.service.Impl;

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
import java.io.IOException;
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
     * 获取企业信息
     * @return
     */
    @Override
    public Map<String,Object> getEnterpriseInfo() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("CompanyList");
        Object resultInfo = "";
        try {
            String result = cgiUtil.getCgi(path+session.getAttribute("inCharge")+"&mode=kigyolist",(String) session.getAttribute("MSPACEDGOURDLP"));
            logger.info(result);
            resultInfo = JSON.parse(result);
        } catch (IOException e) {
            logger.info("报错:",e);
        }

        logger.info("获取企业信息：{}",resultInfo);

        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * 获取企业cd关联的商品力点数List
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
     * 获取商品力点数的参数
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
        //遍历动态列
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
        //遍历动态列
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
     * 获取商品力点数表的属性和品目信息，用于优先顺位表
     *
     * @param productOrderCd
     * @return
     */
    @Override
    public ProductOrderAttrAndItemVO getAttrAndItmemInfo(String companyCd,Integer productOrderCd) {
        return productPowerParamMstMapper.selectAttrAndItem(companyCd,productOrderCd);
    }


    /**
     * 保存商品力点数表模板名
     * @param productPowerName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setCommodityList(ProductCdAndNameDto productPowerName) {
        logger.info("商品力点数名保存参数：{}", productPowerName);
        // 判断名字是否重复

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
     * 保存商品力点数list模板的参数
     * @param productPowerParamMst
     * @return
     */
    @Override
    public Map<String, Object> setCommodityParam(ProductPowerParamMst productPowerParamMst) {
        logger.info("商品力点数list模板的参数：{}",productPowerParamMst);
        String aud = session.getAttribute("aud").toString();
        productPowerParamMstMapper.insert(productPowerParamMst,aud);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 获取chanel信息
     * @return
     */
    @Override
    public Map<String, Object> getChanelInfo() {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String pathInfo = resourceBundle.getString("ChannelList");
            String result=cgiUtil.getCgi(pathInfo,(String) session.getAttribute("MSPACEDGOURDLP"));
            return ResultMaps.result(ResultEnum.SUCCESS, JSON.parse(result));
        } catch (IOException e) {
            logger.info("报错:",e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 获取都道府县
     * @return
     */
    @Override
    public Map<String, Object> getPrefectureInfo() {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String pathInfo = resourceBundle.getString("PlaceList");
            String result=cgiUtil.getCgi(pathInfo,(String) session.getAttribute("MSPACEDGOURDLP"));
            return ResultMaps.result(ResultEnum.SUCCESS, JSON.parse(result));
        } catch (IOException e) {
            logger.info("报错:",e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 如果由改动会删除所有保存过的参数
     * @param productPowerParamMst
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delCommodityParam(ProductPowerParamMst productPowerParamMst) {
        logger.info("如果有改动就删除商品力点数list模板的参数：{}",productPowerParamMst);

        Integer resuleNum = productPowerParamMstMapper.deleteCommofityParamForChange(productPowerParamMst);
        logger.info("改动结果：{}",resuleNum);
        // 删除了step1参数才删除表示项目和weight设定
        if (resuleNum>0){
            // 重置为1，给前台使用，1代表有改动，0代表没改动
            resuleNum = 1;
            boolean result = delSmartData(productPowerParamMst);
            if (!result) {
                return ResultMaps.result(ResultEnum.FAILURE);
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS,resuleNum);
    }

    @Override
    public boolean delSmartData(ProductPowerParamMst productPowerParamMst) {
        String authorCd = session.getAttribute("aud").toString();
        String authorName = (String) session.getAttribute("aud");
        logger.info("参数为:{}",productPowerParamMst);
        String uuid = UUID.randomUUID().toString();
        //商品力点数mst表删除
        productPowerMstMapper.delete(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd,authorName);
      //参数删除
    productPowerParamMstMapper.deleteCommofityParam(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
        //基本数据删除
        productPowerDataMapper.deleteSyokika(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
        //顾客group删除
        productPowerDataMapper.deleteGroup(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
         //预备项目Data删除
        productPowerDataMapper.deleteYobiiitern(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
        productPowerDataMapper.deleteYobiiiternData(productPowerParamMst.getConpanyCd(), productPowerParamMst.getProductPowerCd(),authorCd);
        //rank总表删除
        productPowerDataMapper.deleteRankData(productPowerParamMst.getConpanyCd(),productPowerParamMst.getProductPowerCd(),authorCd);


        ProductPowerDataForCgiDto productPowerDataForCgiDto = new ProductPowerDataForCgiDto();
        productPowerDataForCgiDto.setMode("data_delete");
        productPowerDataForCgiDto.setCompany(productPowerParamMst.getConpanyCd());
        productPowerDataForCgiDto.setProductPowerNo(productPowerParamMst.getProductPowerCd());
        productPowerDataForCgiDto.setGuid(uuid);


        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");

        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String path = resourceBundle.getString("ProductPowerData");
            String result = null;
            result = cgiUtil.postCgi(path,productPowerDataForCgiDto,tokenInfo);
            logger.info("taskid返回：{}",result);
            String queryPath = resourceBundle.getString("TaskQuery");
            // 带着taskid，再次请求cgi获取运行状态/数据
            Map<String,Object> Data = cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
            return true;
        } catch (IOException e) {
            logger.info("报错:",e);
            return false;
        }

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
