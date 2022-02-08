package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.CommodityScoreDataService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Service
public class CommodityScoreDataServiceImpl implements CommodityScoreDataService {
    @Autowired
    private HttpSession session;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 通过smart获取商品力点数表数据
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreData(String taskID) {
        String tokenInfo =(String) session.getAttribute("MSPACEDGOURDLP");
        cgiUtils cgiUtil = new cgiUtils();
        List arrList = new ArrayList();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String queryPath = resourceBundle.getString("TaskQuery");
        // 带着taskId，再次请求cgi获取运行状态/数据
        Map<String,Object> Data = cgiUtil.postCgiOfWeb(queryPath,taskID,tokenInfo);
        logger.info("商品力点数表web版cgi返回数据："+Data);
        // 返回的数据是字符串，处理成二维数组
        if (Data.get("data") !=null) {
            String[] strResult = Data.get("data").toString().split("@");
            String[] strSplit = null;
            for (int i = 0; i < strResult.length; i++) {
                strSplit = strResult[i].split(" ");
                arrList.add(strSplit);
            }
        } else {
            return Data;
        }
//            logger.info("商品力点数表cgi返回数据："+arrList+"&"+Data);
        return ResultMaps.result(ResultEnum.SUCCESS,arrList);
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
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("ProductPowerData");
        ProductPowerDataForCgiDto productPowerDataForCgiDto = new ProductPowerDataForCgiDto();
        productPowerDataForCgiDto.setCompany(companyCd);
        productPowerDataForCgiDto.setMode("yobi_shori");
        productPowerDataForCgiDto.setGuid(uuid);
        productPowerDataForCgiDto.setDataCd(datacd);
        productPowerDataForCgiDto.setFileNm(filename);
        productPowerDataForCgiDto.setProductPowerNo(productPowerNo);
        productPowerDataForCgiDto.setDataNm(dataNm);
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        cgiUtils cgiUtils = new cgiUtils();
        logger.info("保存文件的时候调用cgi的参数"+productPowerDataForCgiDto);
        try {
            //递归调用cgi，首先去taskid
            String result = cgiUtils.postCgi(path,productPowerDataForCgiDto,tokenInfo);
            logger.info("taskId返回："+result);
            String queryPath = resourceBundle.getString("TaskQuery");
            //带着taskId，再次请求cgi获取运行状态/数据
            Map<String,Object> Data =cgiUtils.postCgiLoop(queryPath,result,tokenInfo);
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
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("ProductPowerData");
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
            String result = cgiUtil.postCgi(path,productPowerDataForCgiDto, tokenInfo);
            logger.info("taskId返回："+result);
            return ResultMaps.result(ResultEnum.SUCCESS,result);
        } catch (IOException e) {
            logger.info("获取商品力点数表taskid数据报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }
}
