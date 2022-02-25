package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.ProductPowerDataForCgiDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.ProductOrderParamAttrVO;
import com.trechina.planocycle.entity.vo.ProductPowerPrimaryKeyVO;
import com.trechina.planocycle.entity.vo.RankCalculateVo;
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
import java.lang.reflect.Field;
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
    public Map<String, Object> setCommodityScorePare(ProductPowerParam productPowerParam) {
        String authorCd = session.getAttribute("aud").toString();

        logger.info("保存期间、表示项目、weight所有参数"+productPowerParam);
        String conpanyCd = productPowerParam.getCompany();
        Integer productPowerCd = productPowerParam.getProductPowerNo();

        //将临时表里的保存到最终表里
        //pos基本数据
            //修改保存  物理删除插入
            productPowerDataMapper.phyDeleteSyokika(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endSyokikaForWK(conpanyCd, productPowerCd, authorCd);

            //修改保存  物理删除插入
            productPowerDataMapper.phyDeleteGroup(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endGroupForWK(conpanyCd, productPowerCd, authorCd);

            //修改保存  物理删除插入
            productPowerDataMapper.phyDeleteYobiiitern(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.phyDeleteYobiiiternData(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endYobiiiternForWk(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.endYobiiiternDataForWk(conpanyCd,productPowerCd,authorCd);
            //修改保存  物理删除插入
            productPowerDataMapper.deleteData(conpanyCd,productPowerCd,authorCd);
            productPowerDataMapper.setData(productPowerCd,conpanyCd,authorCd);
            //期间参数删除 插入
            productPowerParamMstMapper.deleteParam(conpanyCd,productPowerCd);
            productPowerParamMstMapper.insertParam(productPowerParam,authorCd);


        String uuid = UUID.randomUUID().toString();
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        //cgi保存
        ProductPowerDataForCgiDto productPowerDataForCgiSave = new ProductPowerDataForCgiDto();
        productPowerDataForCgiSave.setMode("jan_rank");
        productPowerDataForCgiSave.setCompany(productPowerParam.getCompany());
        productPowerDataForCgiSave.setGuid(uuid);
        productPowerDataForCgiSave.setProductPowerNo(productPowerCd);

        logger.info("保存jan rank"+productPowerDataForCgiSave);
        //递归调用cgi，首先获取taskid
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String path = resourceBundle.getString("ProductPowerData");
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
        logger.info("参数为："+primaryKeyVO);
        ProductPowerParamMst productPowerParamMst = new ProductPowerParamMst();
        productPowerParamMst.setConpanyCd(primaryKeyVO.getCompanyCd());
        productPowerParamMst.setProductPowerCd(primaryKeyVO.getProductPowerCd());
        commodityScoreMasterService.delSmartData(productPowerParamMst);
       /* // 根据productpowercd查询相关联的优先顺位表，循环把优先顺位表全删掉
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
        }*/
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
     * rank计算
     * @param rankCalculateVo
     * @return
     */
    @Override
    public Map<String, Object> rankCalculate(RankCalculateVo rankCalculateVo) {
        String authorCd = session.getAttribute("aud").toString();
        productPowerDataMapper.deleteWKData(rankCalculateVo.getCompanyCd(),authorCd);


        List<ProductPowerMstData> productPowerMstDataList = productPowerDataMapper.selectWKKokyaku(authorCd,rankCalculateVo.getCompanyCd());
        List<WKYobiiiternData> wkYobiiiternDataList = productPowerDataMapper.selectWKYobiiiternData(authorCd,rankCalculateVo.getCompanyCd());

        productPowerMstDataList.stream().forEach(item->{
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
        //
        productPowerMstDataList.stream().forEach(item->{
            BigDecimal rankNum = item.getpPosAmount().multiply(BigDecimal.valueOf(rankCalculateVo.getpPosAmount()))
                    .add(item.getpPosNum().multiply(BigDecimal.valueOf(rankCalculateVo.getpPosNum())))
                    .add(item.getpBranchAmount().multiply(BigDecimal.valueOf(rankCalculateVo.getpBranchAmount())))
                    .add(item.getpBranchNum().multiply(BigDecimal.valueOf(rankCalculateVo.getpBranchNum())))
                    .add(item.getpCompareAmount().multiply(BigDecimal.valueOf(rankCalculateVo.getgCompareAmount())))
                    .add(item.getpCompareNum().multiply(BigDecimal.valueOf(rankCalculateVo.getpCompareNum())))
                    .add(item.getpBranchCompareAmount().multiply(BigDecimal.valueOf(rankCalculateVo.getpBranchCompareAmount())))
                    .add(item.getpBranchCompareNum().multiply(BigDecimal.valueOf(rankCalculateVo.getpBranchCompareNum())))
                    .add(item.getgPosAmount().multiply(BigDecimal.valueOf(rankCalculateVo.getgPosAmount())))
                    .add(item.getgPosNum().multiply(BigDecimal.valueOf(rankCalculateVo.getgPosNum())))
                    .add(item.getgBranchAmount().multiply(BigDecimal.valueOf(rankCalculateVo.getgBranchAmount())))
                    .add(item.getgBranchNum().multiply(BigDecimal.valueOf(rankCalculateVo.getgBranchNum())))
                    .add(item.getgCompareAmount().multiply(BigDecimal.valueOf(rankCalculateVo.getgCompareAmount())))
                    .add(item.getgCompareNum().multiply(BigDecimal.valueOf(rankCalculateVo.getgCompareNum())))
                    .add(item.getgBranchCompareAmount().multiply(BigDecimal.valueOf(rankCalculateVo.getgBranchCompareAmount())))
                    .add(item.getgBranchCompareNum().multiply(BigDecimal.valueOf(rankCalculateVo.getgBranchCompareNum())))
                    .add(item.getItem1().multiply(BigDecimal.valueOf(rankCalculateVo.getItem1())))
                    .add(item.getItem2().multiply(BigDecimal.valueOf(rankCalculateVo.getItem2())))
                    .add(item.getItem3().multiply(BigDecimal.valueOf(rankCalculateVo.getItem3())))
                    .add(item.getItem4().multiply(BigDecimal.valueOf(rankCalculateVo.getItem4())))
                    .add(item.getItem5().multiply(BigDecimal.valueOf(rankCalculateVo.getItem5())))
                    .add(item.getItem6().multiply(BigDecimal.valueOf(rankCalculateVo.getItem6())))
                    .add(item.getItem7().multiply(BigDecimal.valueOf(rankCalculateVo.getItem7())))
                    .add(item.getItem8().multiply(BigDecimal.valueOf(rankCalculateVo.getItem8())))
                    .add(item.getItem9().multiply(BigDecimal.valueOf(rankCalculateVo.getItem9())))
                    .add(item.getItem10().multiply(BigDecimal.valueOf(rankCalculateVo.getItem10())));
                item.setRankNum(rankNum);




        });

                Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                    @Override
                    public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                        return o2.getpPosAmount().compareTo(o1.getpPosAmount());
                    }
                });


                int i = 1;
                for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                    productPowerMstData.setgPosAmountRank(i++);
                }
        for (int j = 0; j < 26; j++) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getRankNum().compareTo(o1.getRankNum());
                }
            });
        }



        i=1;
        if (productPowerMstDataList.size()>0) {
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setRankResult(i++);
            }
            int o = 0;
            List<ProductPowerMstData> list = new ArrayList<ProductPowerMstData>();
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                o++;
                list.add(productPowerMstData);
                if (o % 200 == 0 && o > 200) {
                    productPowerDataMapper.setWKData(list, authorCd, rankCalculateVo.getCompanyCd());
                    list.clear();
                }

            }
            if (list.size()>0) {
                productPowerDataMapper.setWKData(list, authorCd, rankCalculateVo.getCompanyCd());
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS,productPowerMstDataList);
    }

    /**
     * 物理删除期间参数
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delParam(String conpanyCd, Integer productPowerCd){
        productPowerParamMstMapper.delete(conpanyCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 物理删除表示项目
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delShowMst(String conpanyCd, Integer productPowerCd){
        productPowerShowMstMapper.delete(productPowerCd,conpanyCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 物理删除预备表示项目
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String,Object> delPrePara(String conpanyCd,Integer productPowerCd) {
        productPowerReserveMstMapper.delete(conpanyCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 物理删除weight
     * @param conpanyCd
     * @param productPowerCd
     * @return
     */
    public Map<String, Object> delWeight(String conpanyCd, Integer productPowerCd){
        productPowerWeightMapper.delete(conpanyCd,productPowerCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


    /**
     * getpPosNum排序
     */
    public void getpPosNum(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getpPosNum()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getpPosNum().compareTo(o1.getpPosNum());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setpPosNumRank(i++);
            }
        }
    }

    /**
     * getpBranchAmount排序
     * @param rankCalculateVo
     * @param productPowerMstDataList
     */
    public void getpBranchAmount(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getpBranchAmount()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getpBranchAmount().compareTo(o1.getpBranchAmount());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setpBranchAmountRank(i++);
            }
        }
    }

    /**
     * getpBranchNum排序
     * @param rankCalculateVo
     * @param productPowerMstDataList
     */
    public void getpBranchNum(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getpBranchNum()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getpBranchNum().compareTo(o1.getpBranchNum());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setpBranchNumRank(i++);
            }
        }
    }

    /**
     * getpCompareAmount排序
     * @param rankCalculateVo
     * @param productPowerMstDataList
     */
    public void getpCompareAmount(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getpCompareAmount()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getpCompareAmount().compareTo(o1.getpCompareAmount());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setpCompareAmountRank(i++);
            }
        }
    }

    public void getpCompareNum(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getpCompareNum()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getpCompareNum().compareTo(o1.getpCompareNum());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setpCompareNumRank(i++);
            }
        }
    }

    public void getgPosAmount(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getgPosAmount()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getgPosAmount().compareTo(o1.getgPosAmount());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setgPosAmountRank(i++);
            }
        }
    }

    public void getgPosNum(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getgPosNum()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getgPosNum().compareTo(o1.getgPosNum());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setgPosNumRank(i++);
            }
        }
    }

    public void getgBranchNum(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getgBranchNum()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getgBranchNum().compareTo(o1.getgBranchNum());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setgBranchNumRank(i++);
            }
        }
    }


    public void getgBranchAmount(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getgBranchAmount()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getgBranchAmount().compareTo(o1.getgBranchAmount());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setgBranchAmountRank(i++);
            }
        }
    }

    public void getgCompareNum(RankCalculateVo rankCalculateVo,List<ProductPowerMstData> productPowerMstDataList){
        if (rankCalculateVo.getgCompareNum()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getgCompareNum().compareTo(o1.getgCompareNum());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setgCompareAmountRank(i++);
            }
        }
    }


}
