package com.trechina.planocycle.service.Impl;

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
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

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
    public Map<String, Object> getCommodityScoreData(String taskID,String companyCd) {
        String tokenInfo =(String) session.getAttribute("MSPACEDGOURDLP");
        cgiUtils cgiUtil = new cgiUtils();
        List strList = new ArrayList();
        // 带着taskId，再次请求cgi获取运行状态/数据
        Map<String,Object> Data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), taskID,tokenInfo);
        /*Map<String,Object> Data =new HashMap<>();

       StringBuilder strs =new StringBuilder();
        for (int i=0;i<=1000;i++){
            strs.append("0001 "+(i)+" 啤酒 酒类 水类 扎啤 黑啤 10.21 10.45 1.14 124.0 14 145 14 70@");
        }
        Data.put("data",strs);*/
        logger.info("商品力点数表web版cgi返回数据："+Data);
        String authorCd = session.getAttribute("aud").toString();
        // 返回的数据是字符串，处理成二维数组
        if (Data.get("data") !=null) {
            String[] strResult = Data.get("data").toString().split("@");
            String[] strSplit = null;
            String[] arr ;
            int a = 1;
            productPowerDataMapper.deleteWKSyokika(companyCd,authorCd);
            for (int i = 0; i < strResult.length; i++) {
                strSplit = strResult[i].split(" ");
                arr = new String[strSplit.length+1];
                for (int j = strSplit.length-1;j>=a ;j--){
                    arr[j+1] = strSplit[j];
                }
                arr[a]=session.getAttribute("aud").toString();
                System.arraycopy(strSplit,0,arr,0,a);
                //数据量过大，一次存500条
                 if (i%500==0 &&i>=500){
                    productPowerDataMapper.insert(strList);
                    strList.clear();
                }
                strList.add(arr);
            }
            productPowerDataMapper.insert(strList);
        } else {
            return Data;
        }
        List<ProductPowerMstData> syokikaList = productPowerDataMapper.selectWKSyokika(companyCd,authorCd);

//            logger.info("商品力点数表cgi返回数据："+arrList+"&"+Data);
        return ResultMaps.result(ResultEnum.SUCCESS,syokikaList);
    }

    /**
     * 获取顾客group数据
     * @param taskID
     * @return
     */
    @Override
    public Map<String, Object> getCommodityScoreGroupData(String taskID,String companyCd) {
        String tokenInfo =(String) session.getAttribute("MSPACEDGOURDLP");
        cgiUtils cgiUtil = new cgiUtils();
        List strList = new ArrayList();
        // 带着taskId，再次请求cgi获取运行状态/数据
        Map<String,Object> Data = cgiUtil.postCgiOfWeb(cgiUtil.setPath("TaskQuery"), taskID,tokenInfo);



        logger.info("商品力点数表web版cgi返回数据："+Data);
        //获取用户id
        String authorCd =session.getAttribute("aud").toString();
        // 返回的数据是字符串，处理成二维数组
        if (Data.get("data") !=null) {
            String[] strResult = Data.get("data").toString().split("@");
            String[] strSplit = null;
            String[] arr ;
            int a = 1;
            productPowerDataMapper.deleteWKKokyaku(companyCd,authorCd);
            for (int i = 0; i < strResult.length; i++) {
                strSplit = strResult[i].split(" ");
                arr = new String[strSplit.length+1];
                for (int j = strSplit.length-1;j>=a ;j--){
                    arr[j+1] = strSplit[j];
                }
                arr[a]=session.getAttribute("aud").toString();
                System.arraycopy(strSplit,0,arr,0,a);
                //数据过大500存一次
                if (i%500==0 &&i>=500){
                    productPowerDataMapper.insertGroup(strList);
                    strList.clear();
                }
                strList.add(arr);
            }
            productPowerDataMapper.insertGroup(strList);
        } else {
            return Data;
        }
        List<ProductPowerMstData> kokyakuList = productPowerDataMapper.selectWKKokyaku(authorCd,companyCd);
        List<WKYobiiiternData> wkYobiiiternDataList = productPowerDataMapper.selectWKYobiiiternData( authorCd, companyCd);

        kokyakuList.forEach(item->{
            for (WKYobiiiternData wkYobiiiternData : wkYobiiiternDataList) {
                Class w =item.getClass();
                for(int i=1;i<=10;i++){
                    if (wkYobiiiternData.getJan().equals(item.getJan()) && wkYobiiiternData.getDataSort()==i){
                        try {
                            Field field = w.getDeclaredField("item"+i);
                            field.setAccessible(true);
                            field.set(item,wkYobiiiternData.getDataValue());
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }

        });

//            logger.info("商品力点数表cgi返回数据："+arrList+"&"+


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
        productPowerDataForCgiDto.setMode("shoki_data");
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
    public Map<String, Object> getCommodityScoreGroupTaskId(ProductPowerGroupDataForCgiDto productPowerDataForCgiDto) {
        String uuid = UUID.randomUUID().toString();
        productPowerDataForCgiDto.setGuid(uuid);
        productPowerDataForCgiDto.setMode("kokyaku_data");
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
        String authorCd =session.getAttribute("aud").toString();
        productPowerDataMapper.deleteWKSyokika(companyCd,authorCd);
        productPowerDataMapper.deleteWKKokyaku(companyCd,authorCd);
        productPowerDataMapper.deleteWKYobiiitern();
        productPowerDataMapper.deleteWKYobiiiternData();
        List<ProductPowerMstData> productPowerMstData = productPowerDataMapper.getProductPowerMstData(companyCd, productPowerCd);

        return ResultMaps.result(ResultEnum.SUCCESS,productPowerMstData);
    }
}
