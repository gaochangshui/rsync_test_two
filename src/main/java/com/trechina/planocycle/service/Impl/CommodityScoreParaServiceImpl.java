package com.trechina.planocycle.service.Impl;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;
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
import java.io.IOException;
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

    /**
     * 获取表示项目所有参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScorePara(String conpanyCd, Integer productPowerCd) {
        List<ProductPowerShowMst> productPowerShowMstList = productPowerShowMstMapper.selectByPrimaryKey(productPowerCd,conpanyCd);
        logger.info("获取表示项目参数："+productPowerShowMstList);
//        ProductPowerParamMst productPowerParamMst = productPowerParamMstMapper.selectCommodityParam(conpanyCd,productPowerCd);
        ProductOrderParamAttrVO productOrderParamAttrVO = productPowerParamAttributeMapper.selectByPrimaryKey(conpanyCd,productPowerCd);
        logger.info("获取动态列参数："+productOrderParamAttrVO);
        //构造前端用的数据格式
        List<String> marketList = new ArrayList<>();
        List<String> posList = new ArrayList<>();
        //遍历数据库返回值所有的行，组合成市场的list和pos的list
        productPowerShowMstList.forEach(item -> {
            if (item.getMarketPosFlag() == 1) {
                marketList.add(item.getDataCd().toString());
            } else {
                posList.add(item.getDataCd().toString());
            }
        });
       try {
           //遍历动态列
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
           logger.info("动态列返回："+jsonArray.toString());
           //返回
           return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
       }catch (Exception e) {
           logger.info(e.toString());
           return ResultMaps.result(ResultEnum.FAILURE);
       }
    }

    /**
     * 保存期间、表示项目、weight所有参数
     * @param commodityScorePara
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setCommodityScorePare(CommodityScorePara commodityScorePara) {
        logger.info("保存期间、表示项目、weight所有参数"+commodityScorePara);
        String conpanyCd = commodityScorePara.getProductPowerParamMst().getConpanyCd();
        Integer productPowerCd = commodityScorePara.getProductPowerParamMst().getProductPowerCd();
        String authorCd = session.getAttribute("aud").toString();
        //期间参数先删再插
        delParam(conpanyCd,productPowerCd,authorCd);
        productPowerParamMstMapper.insert(commodityScorePara.getProductPowerParamMst(),authorCd);

        //表示项目需要先删再插
        delShowMst(conpanyCd,productPowerCd,authorCd);
        if(commodityScorePara.getProductPowerShowMst().size()>0) {
            productPowerShowMstMapper.insert(commodityScorePara.getProductPowerShowMst(),authorCd);
        }
        //预备表示项目需要先删再插
        delPrePara(conpanyCd,productPowerCd,authorCd);
        if(commodityScorePara.getProductPowerReserveMst().size()>0) {
            productPowerReserveMstMapper.insert(commodityScorePara.getProductPowerReserveMst(),authorCd);
        }

        //weight需要先删再插
        delWeight(conpanyCd,productPowerCd,authorCd);
        productPowerWeightMapper.insert(commodityScorePara.getProductPowerWeight(),authorCd);
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        //cgi保存
        String uuidSave = UUID.randomUUID().toString();
        ProductPowerDataForCgiDto productPowerDataForCgiSave = new ProductPowerDataForCgiDto();
        productPowerDataForCgiSave.setMode("jan_rank");
        productPowerDataForCgiSave.setCompany(commodityScorePara.getProductPowerParamMst().getConpanyCd());

        logger.info("保存jan rank"+productPowerDataForCgiSave);
        //递归调用cgi，首先获取taskid
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String path = resourceBundle.getString("ProductPowerData");
            cgiUtils cgiUtil = new cgiUtils();
            String result = null;
            result = cgiUtil.postCgi(path, productPowerDataForCgiSave, tokenInfo);
            logger.info("taskid返回--保存jan rank：" + result);
            String queryPath = resourceBundle.getString("TaskQuery");
            // 带着taskid，再次请求cgi获取运行状态/数据
            Map<String, Object> Data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
            logger.info("保存jan rank"+Data);
        } catch (IOException e) {
            logger.info("保存期间、表示项目、weight所有参数报错--保存jan rank" + e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 获取weight参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreWeight(String conpanyCd, Integer productPowerCd) {
        List<ProductPowerWeight> productPowerWeights = productPowerWeightMapper.selectByPrimaryKey(conpanyCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS,productPowerWeights);
    }

    /**
     * 获取表示项目的预备项目参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScorePrePara(String conpanyCd, Integer productPowerCd) {
        List<ProductPowerReserveMst> productPowerReserveMsts = productPowerReserveMstMapper.selectByPrimaryKey(conpanyCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS,productPowerReserveMsts);
    }

    /**
     * 删除商品力点数表所有信息+优先顺位表所有信息
     *
     * @param primaryKeyVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delCommodityScoreAllInfo(ProductPowerPrimaryKeyVO primaryKeyVO) {
        ProductPowerParamMst productPowerParamMst = new ProductPowerParamMst();
        productPowerParamMst.setConpanyCd(primaryKeyVO.getCompanyCd());
        productPowerParamMst.setProductPowerCd(primaryKeyVO.getProductPowerCd());
        commodityScoreMasterService.delSmartData(productPowerParamMst);
        // 根据productpowercd查询相关联的优先顺位表，循环把优先顺位表全删掉
        String result = priorityOrderMstService.selPriorityOrderCdForProdCd(primaryKeyVO.getCompanyCd(),primaryKeyVO.getProductPowerCd());
        if (result!=null) {
            String[] resultArr = result.split(",");
            if (resultArr.length > 0) {
                for (int i = 0; i < resultArr.length; i++) {
                    PriorityOrderPrimaryKeyVO priorityOrderPrimaryKeyVO = new PriorityOrderPrimaryKeyVO();
                    priorityOrderPrimaryKeyVO.setCompanyCd(primaryKeyVO.getCompanyCd());
                    priorityOrderPrimaryKeyVO.setPriorityOrderCd(Integer.valueOf(resultArr[i]));
                    priorityOrderMstService.delPriorityOrderAllInfo(priorityOrderPrimaryKeyVO);
                }
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 调用cgi删除预备项目
     *
     * @param productPowerReserveMst
     * @return
     */
    @Override
    public Map<String, Object> delYoBi(ProductPowerReserveMst productPowerReserveMst) {
        //处理ProductPowerReserv
            String uuid = UUID.randomUUID().toString();
            ProductPowerDataForCgiDto productPowerDataForCgiDto = new ProductPowerDataForCgiDto();
            productPowerDataForCgiDto.setMode("yobi_delete");
            productPowerDataForCgiDto.setCompany(productPowerReserveMst.getConpanyCd());



            String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
            //调用cgi 删除预备表示项目
            //递归调用cgi，首先获取taskid
            try {
                ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
                String path = resourceBundle.getString("ProductPowerData");
                cgiUtils cgiUtil = new cgiUtils();
                String result = null;
                result = cgiUtil.postCgi(path, productPowerDataForCgiDto, tokenInfo);
                logger.info("taskid返回删除 yobi：" + result);
                String queryPath = resourceBundle.getString("TaskQuery");
                // 带着taskid，再次请求cgi获取运行状态/数据
                Map<String, Object> Data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
            } catch (IOException e) {
                logger.info("保存期间、表示项目、weight所有参数报错--删除 yobi" + e);
                return ResultMaps.result(ResultEnum.FAILURE);
            }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 删除期间参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delParam(String conpanyCd, Integer productPowerCd,String authorCd){
        productPowerParamMstMapper.deleteCommofityParam(conpanyCd,productPowerCd, authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 删除表示项目
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delShowMst(String conpanyCd, Integer productPowerCd,String authorCd){
        productPowerShowMstMapper.deleteByPrimaryKey(productPowerCd,conpanyCd, authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 删除预备表示项目
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String,Object> delPrePara(String conpanyCd,Integer productPowerCd,String authorCd) {
        productPowerReserveMstMapper.deleteByPrimaryKey(conpanyCd,productPowerCd,authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 删除weight
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delWeight(String conpanyCd, Integer productPowerCd,String authorCd){
        productPowerWeightMapper.deleteByPrimaryKey(conpanyCd,productPowerCd, authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }




}
