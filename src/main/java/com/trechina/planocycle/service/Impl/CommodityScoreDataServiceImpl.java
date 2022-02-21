package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.po.ProductPowerKokyaku;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.entity.po.ProductPowerSyokika;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CommodityScoreDataServiceImpl implements CommodityScoreDataService {
    @Autowired
    private HttpSession session;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;


    /**
     * 通过smart获取商品力点数表基本pos数据
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreData(String taskID) {
        String tokenInfo =(String) session.getAttribute("MSPACEDGOURDLP");
        cgiUtils cgiUtil = new cgiUtils();
        List arrList = new ArrayList();
        List strList = new ArrayList();
        // 带着taskId，再次请求cgi获取运行状态/数据
        Map<String,Object> Data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), taskID,tokenInfo);
        logger.info("商品力点数表web版cgi返回数据："+Data);

        // 返回的数据是字符串，处理成二维数组
        if (Data.get("data") !=null) {
            String[] strResult = Data.get("data").toString().split("@");
            String[] strSplit = null;
            productPowerDataMapper.deleteWKSyokika();
            for (int i = 0; i < strResult.length; i++) {
                strSplit = strResult[i].split(" ");
                //数据量过大，一次存500条
                 if (i%500==0 &&i>=500){
                    productPowerDataMapper.insert(strList);
                    strList.clear();
                }
                strList.add(strSplit);
            }
            productPowerDataMapper.insert(strList);
        } else {
            return Data;
        }
        List<ProductPowerSyokika> syokikaList = productPowerDataMapper.selectWKSyokika();

//            logger.info("商品力点数表cgi返回数据："+arrList+"&"+Data);
        return ResultMaps.result(ResultEnum.SUCCESS,syokikaList);
    }

    /**
     * 获取顾客group数据
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreGroupData(String taskID) {
        String tokenInfo =(String) session.getAttribute("MSPACEDGOURDLP");
        cgiUtils cgiUtil = new cgiUtils();
        List arrList = new ArrayList();
        List strList = new ArrayList();
        // 带着taskId，再次请求cgi获取运行状态/数据
        Map<String,Object> Data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), taskID,tokenInfo);
        logger.info("商品力点数表web版cgi返回数据："+Data);
        //获取用户id
        String tableName = "public.wk_product_power_syokika"+session.getAttribute("aud").toString();
        // 返回的数据是字符串，处理成二维数组
        if (Data.get("data") !=null) {
            String[] strResult = Data.get("data").toString().split("@");
            String[] strSplit = null;
            productPowerDataMapper.deleteWKKokyaku();
            for (int i = 0; i < strResult.length; i++) {
                strSplit = strResult[i].split(" ");
                if (i%500==0 &&i>=500){
                    productPowerDataMapper.insertGroup(strList);
                    strList.clear();
                }
                strList.add(strSplit);
            }
            productPowerDataMapper.insert(strList);
        } else {
            return Data;
        }
        List<ProductPowerKokyaku> kokyakuList = productPowerDataMapper.selectWKKokyaku();
//            logger.info("商品力点数表cgi返回数据："+arrList+"&"+Data);
        return ResultMaps.result(ResultEnum.SUCCESS,kokyakuList);
    }
    /**
     * 保存文件的时候调用cgi
     *
     * @param companyCd
     * @param filename
     * @param datacd
     * @return
     */
    @Override
    public Map<String, Object> getAttrFileSaveForCgi(String companyCd, String filename, String datacd,
                                                     Integer productPowerNo,String dataNm) {
        // 从cgi获取数据
        String uuid = UUID.randomUUID().toString();
        ProductPowerDataForCgiDto productPowerDataForCgiDto = new ProductPowerDataForCgiDto();
        productPowerDataForCgiDto.setCompany(companyCd);
        productPowerDataForCgiDto.setMode("yobi_shori");
        productPowerDataForCgiDto.setGuid(uuid);
        //productPowerDataForCgiDto.setDataCd(datacd);
        //productPowerDataForCgiDto.setFileNm(filename);
        productPowerDataForCgiDto.setProductPowerNo(productPowerNo);
       // productPowerDataForCgiDto.setDataNm(dataNm);
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        cgiUtils cgiUtils = new cgiUtils();
        logger.info("保存文件的时候调用cgi的参数"+productPowerDataForCgiDto);
        try {
            //递归调用cgi，首先去taskid
            String result = cgiUtils.postCgi(cgiUtils.setPath("ProductPowerData"),productPowerDataForCgiDto,tokenInfo);
            logger.info("taskId返回："+result);
            //带着taskId，再次请求cgi获取运行状态/数据
            Map<String,Object> Data =cgiUtils.postCgiLoop(cgiUtils.setPath("TaskQuery"),result,tokenInfo);
            logger.info("保存文件的时候调用cgi返回的数据："+Data);
        }catch (IOException e) {
            logger.info("保存文件的时候调用cgi报错：" + e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
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
        cgiUtils cgiUtil = new cgiUtils();
        String tokenInfo =(String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("调用cgi获取data的参数："+productPowerDataForCgiDto);
        try {
            String result = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), productPowerDataForCgiDto, tokenInfo);
            logger.info("taskId返回："+result);
            return ResultMaps.result(ResultEnum.SUCCESS,result);
        } catch (IOException e) {
            logger.info("获取商品力点数表taskid数据报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }
    /**
     * 获取商品力点数表顾客Grouptaskid
     *
     * @param productPowerDataForCgiDto
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreGroupTaskId(ProductPowerDataForCgiDto productPowerDataForCgiDto) {
        String uuid = UUID.randomUUID().toString();
        productPowerDataForCgiDto.setGuid(uuid);
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
        cgiUtils cgiUtil = new cgiUtils();
        String tokenInfo =(String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("调用cgi获取data的参数："+productPowerDataForCgiDto);
        try {
            String result = cgiUtil.postCgi(cgiUtil.setPath("ProductPowerData"), productPowerDataForCgiDto, tokenInfo);
            logger.info("taskId返回："+result);
            return ResultMaps.result(ResultEnum.SUCCESS,result);
        } catch (IOException e) {
            logger.info("获取商品力点数表taskid数据报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }
    @Override
    public Map<String, Object> updateCommodityScoreData(ProductPowerDataForCgiDto productPowerDataForCgiDto) {

        return null;
    }

    @Override
    public Map<String, Object> getDBCommodityScoreData(String companyCd, Integer productPowerCd) {
        productPowerDataMapper.deleteWKSyokika();
        productPowerDataMapper.deleteWKKokyaku();
        productPowerDataMapper.deleteWKYobiiitern();
        productPowerDataMapper.deleteWKYobiiiternData();
        List<ProductPowerMstData> productPowerMstData = productPowerDataMapper.getProductPowerMstData(companyCd, productPowerCd);

        return ResultMaps.result(ResultEnum.SUCCESS,productPowerMstData);
    }
}
