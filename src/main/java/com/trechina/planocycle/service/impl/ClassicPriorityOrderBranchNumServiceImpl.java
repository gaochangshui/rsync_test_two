package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.PriorityOrderDataForCgiDto;
import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanCard;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.entity.vo.PriorityOrderCommodityVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderBranchNumService;
import com.trechina.planocycle.service.ClassicPriorityOrderDataService;
import com.trechina.planocycle.service.ClassicPriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import com.trechina.planocycle.utils.dataConverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderBranchNumServiceImpl implements ClassicPriorityOrderBranchNumService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderCommodityMustMapper priorityOrderCommodityMustMapper;
    @Autowired
    private ClassicPriorityOrderCommodityNotMapper priorityOrderCommodityNotMapper;
    @Autowired
    private ClassicPriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private ClassicPriorityOrderBranchNumMapper priorityOrderBranchNumMapper;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private ClassicPriorityOrderDataService priorityOrderDataService;
    @Autowired
    private cgiUtils cgiUtil;
    /**
     * 获取smart处理之后的必须+不可商品的结果集，并保存
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> getPriorityOrderBranchNum(String companyCd, Integer priorityOrderCd,String shelfPatternCd) {
        String uuid = UUID.randomUUID().toString();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String pathInfo = resourceBundle.getString("PriorityOrderData");
        PriorityOrderDataForCgiDto priorityOrderDataForCgiDto = new PriorityOrderDataForCgiDto();
        priorityOrderDataForCgiDto.setCompany(companyCd);
        priorityOrderDataForCgiDto.setShelfPatternNo(shelfPatternCd);
        priorityOrderDataForCgiDto.setPriorityNO(priorityOrderCd);
        priorityOrderDataForCgiDto.setGuid(uuid);
        priorityOrderDataForCgiDto.setMode("priority_jan_storecnt");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("调用priority_jan_storecnt的参数" + priorityOrderDataForCgiDto);

        String result = cgiUtil.postCgi(pathInfo, priorityOrderDataForCgiDto, tokenInfo);
        logger.info("返回priority_jan_storecnt处理结果"+result);
        String queryPath = resourceBundle.getString("TaskQuery");
        // 带着taskid，再次请求cgi获取运行状态/数据
        Map<String,Object> Data = cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
        logger.info("调用priority_jan_storecnt的结果" + Data);
        JSONArray jsonArray = JSONArray.parseArray(String.valueOf(Data.get("data")));
        logger.info("转json后："+jsonArray.toString());
        if (jsonArray.size()>0){
            priorityOrderCommodityMustMapper.deletePriorityBranchNum(companyCd,priorityOrderCd);

            priorityOrderCommodityMustMapper.insertPriorityBranchNum(jsonArray,companyCd,priorityOrderCd);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 获取必须商品list
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCommodityMust(String companyCd, Integer priorityOrderCd) {
        logger.info("获取必须商品list的参数："+companyCd+","+priorityOrderCd);
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityMustMapper
                                                    .selectMystInfo(companyCd,priorityOrderCd);
        logger.info("获取必须商品list的返回值："+priorityOrderCommodityVOList);

        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderCommodityVOList);
    }

    /**
     * 获取不可商品list
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCommodityNot(String companyCd, Integer priorityOrderCd) {
        logger.info("获取不可商品list的参数："+companyCd+","+priorityOrderCd);
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityNotMapper
                                                    .selectNotInfo(companyCd,priorityOrderCd);
        logger.info("获取不可商品list的返回值："+priorityOrderCommodityVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderCommodityVOList);
    }

    /**
     * 保存必须商品list，并传参给smart
     *
     * @param priorityOrderCommodityMust
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCommodityMust(List<PriorityOrderCommodityMust> priorityOrderCommodityMust) {
        logger.info("保存必须商品list参数："+priorityOrderCommodityMust);
        // 拿到的参数只有第一行有企业和顺位表cd，需要遍历参数，给所有行都赋值
        try{
            String companyCd = priorityOrderCommodityMust.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderCommodityMust.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 调用共同方法，处理数据
            List<PriorityOrderCommodityMust> mustList =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityMust.class,priorityOrderCommodityMust,
                    companyCd ,priorityOrderCd);

            logger.info("保存必须商品list处理完后的参数："+mustList);
            // 查询企业的店cd是几位数
            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd);
            Integer branchLen = branchCd.length();
            // 遍历not 给店cd补0
            mustList.forEach(item->{
                if (item.getBranch() != null){
                    item.setBranch(String.format("%0"+branchLen+"d",Integer.valueOf(item.getBranch())));
                }
            });
            logger.info("保存必须商品店补0后的结果"+mustList);
            //删除
            priorityOrderCommodityMustMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // jancheck
            String janInfo = priorityOrderJanReplaceService.getJanInfo();
            List<String> list= Arrays.asList(janInfo.split(","));
            String notExists = "";
            List<PriorityOrderCommodityMust> exists = new ArrayList<>();
            for (int i = 0; i < mustList.size(); i++) {
                if (list.indexOf(mustList.get(i).getJan())==-1){
                    notExists += mustList.get(i).getJan()+",";
                } else {
                    exists.add(mustList.get(i));
                }
            }
            if (exists.size()>0){
                //写入数据库
                priorityOrderCommodityMustMapper.insert(exists);
            }
           if (notExists.length()>0){
               return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));
           }else{
                return ResultMaps.result(ResultEnum.SUCCESS);
           }
        } catch (Exception e) {
            logger.error("保存必须商品list："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }


    /**
     * 保存不可商品list，并传参给smart
     *
     * @param priorityOrderCommodityNot
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCommodityNot(List<PriorityOrderCommodityNot> priorityOrderCommodityNot) {
        logger.info("保存不可商品list参数："+priorityOrderCommodityNot);
        // 拿到的参数只有第一行有企业和顺位表cd，需要遍历参数，给所有行都赋值
        try{
            String companyCd = priorityOrderCommodityNot.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderCommodityNot.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 调用共同方法，处理数据
            List<PriorityOrderCommodityNot> not =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityNot.class,priorityOrderCommodityNot,
                    companyCd ,priorityOrderCd);
            logger.info("保存不可商品list处理完后的参数："+not);
            // 查询企业的店cd是几位数
            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd);
            Integer branchLen = branchCd.length();
            // 遍历not 给店cd补0
            not.forEach(item->{
                if (item.getBranch() !=null) {
                    item.setBranch(String.format("%0"+branchLen+"d",Integer.valueOf(item.getBranch())));
                }
            });
            logger.info("保存不可商品list店补0后的结果"+not);
            //删除
            priorityOrderCommodityNotMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // jancheck
            String janInfo = priorityOrderJanReplaceService.getJanInfo();
            List<String> list= Arrays.asList(janInfo.split(","));
            String notExists = "";
            List<PriorityOrderCommodityNot> exists = new ArrayList<>();
            for (int i = 0; i < not.size(); i++) {
                if (list.indexOf(not.get(i).getJan())==-1){
                    notExists += not.get(i).getJan()+",";
                } else {
                    exists.add(not.get(i));
                }
            }
            if (exists.size()>0){
                //写入数据库
                priorityOrderCommodityNotMapper.insert(exists);
            }
            if (notExists.length()>0){
                return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));
            }else{
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("保存不可商品list："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 删除必须商品
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderCommodityMustInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderCommodityMustMapper.deleteFinal(companyCd,priorityOrderCd);
    }

    /**
     * 删除不可商品
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderCommodityNotInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderCommodityNotMapper.deleteFinal(companyCd,priorityOrderCd);
    }

    /**
     * 删除必须不可的中间表
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderBranchNumInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderBranchNumMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }

    @Override
    public Map<String, Object> checkIsJanCommodityNot(List<PriorityOrderCommodityNot> priorityOrderCommodityNot) {

        List<ClassicPriorityOrderJanCard> priorityOrderJanCard = priorityOrderCommodityNot.stream().map(item -> {
            ClassicPriorityOrderJanCard priorityOrderJanCard1 = new ClassicPriorityOrderJanCard();
            priorityOrderJanCard1.setJanOld(item.getJan());
            priorityOrderJanCard1.setCompanyCd(item.getCompanyCd());
            priorityOrderJanCard1.setPriorityOrderCd(item.getPriorityOrderCd());
            return priorityOrderJanCard1;
        }).collect(Collectors.toList());

        List<String> janNewList = priorityOrderJanCardMapper.checkJanNew(priorityOrderJanCard);
        if (!janNewList.isEmpty()){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS,janNewList);
        }
        List<String> janReplaceList =  priorityOrderJanCardMapper.checkJanReplace(priorityOrderJanCard);
        if (!janReplaceList.isEmpty()){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS,janReplaceList);
        }
        List<String> janMustList =  priorityOrderJanCardMapper.checkJanMust(priorityOrderJanCard);
        if (!janMustList.isEmpty()){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS,janMustList);
        }
        List<String> janProposalList =  priorityOrderJanCardMapper.checkJanProposal(priorityOrderJanCard);
        if (!janProposalList.isEmpty()){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS,janProposalList);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);

    }

    @Override
    public List<String> checkIsJanCommodityMust(List<PriorityOrderCommodityMust> priorityOrderCommodityMust) {
        String tableName = "public.priorityorder" + session.getAttribute("aud").toString();
        String companyCd = priorityOrderCommodityMust.get(0).getCompanyCd();
        Integer priorityOrderCd = priorityOrderCommodityMust.get(0).getPriorityOrderCd();
        List<String> janList = priorityOrderCommodityMust.stream().map(item -> item.getJan()).collect(Collectors.toList());
        List<String> jans = new ArrayList<>(priorityOrderDataService.checkIsJanNew(janList, companyCd, priorityOrderCd, tableName).keySet());
        return jans;
    }
}
