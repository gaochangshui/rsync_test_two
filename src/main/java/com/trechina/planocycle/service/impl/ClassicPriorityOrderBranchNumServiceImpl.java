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
     *smart処理後の必須+不可商品の結菓セットを取得し、保存
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
        logger.info("priority_を呼び出します。jan_storecntのパラメータ" + priorityOrderDataForCgiDto);

        String result = cgiUtil.postCgi(pathInfo, priorityOrderDataForCgiDto, tokenInfo);
        logger.info("priority_jan_storecnt処理結菓"+result);
        String queryPath = resourceBundle.getString("TaskQuery");
        // 带着taskid，再次请求cgi获取运行状态/数据
        Map<String,Object> Data = cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
        logger.info("priority_を呼び出します。jan_storecntの結菓" + Data);
        JSONArray jsonArray = JSONArray.parseArray(String.valueOf(Data.get("data")));
        logger.info("jsonを回した後："+jsonArray.toString());
        if (jsonArray.size()>0){
            priorityOrderCommodityMustMapper.deletePriorityBranchNum(companyCd,priorityOrderCd);

            priorityOrderCommodityMustMapper.insertPriorityBranchNum(jsonArray,companyCd,priorityOrderCd);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 必須商品リストの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCommodityMust(String companyCd, Integer priorityOrderCd) {
        logger.info("必須商品リストのパラメータを取得する："+companyCd+","+priorityOrderCd);
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityMustMapper
                                                    .selectMystInfo(companyCd,priorityOrderCd);
        logger.info("必須商品リストの戻り値を取得する："+priorityOrderCommodityVOList);

        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderCommodityVOList);
    }

    /**
     * 不可商品リストの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCommodityNot(String companyCd, Integer priorityOrderCd) {
        logger.info("不可商品リストのパラメータを取得する："+companyCd+","+priorityOrderCd);
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityNotMapper
                                                    .selectNotInfo(companyCd,priorityOrderCd);
        logger.info("不可商品リストの戻り値を取得する："+priorityOrderCommodityVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderCommodityVOList);
    }

    /**
     * 保存必須商品リスト
     *
     * @param priorityOrderCommodityMust
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCommodityMust(List<PriorityOrderCommodityMust> priorityOrderCommodityMust) {
        logger.info("必須商品リストパラメータを保存する："+priorityOrderCommodityMust);
        // 拿到的参数只有第一行有企业和顺位表cd，需要遍历参数，给所有行都赋值
        try{
            String companyCd = priorityOrderCommodityMust.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderCommodityMust.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 调用共同方法，处理数据
            List<PriorityOrderCommodityMust> mustList =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityMust.class,priorityOrderCommodityMust,
                    companyCd ,priorityOrderCd);

            logger.info("必須商品リストの処理後のパラメータを保存する："+mustList);
            // 查询企业的店cd是几位数
            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd);
            Integer branchLen = branchCd.length();
            // 遍历not 给店cd补0
            mustList.forEach(item->{
                if (item.getBranch() != null){
                    item.setBranch(String.format("%0"+branchLen+"d",Integer.valueOf(item.getBranch())));
                }
            });
            logger.info("必須商品の店補0後の結菓を保存する"+mustList);
            //削除
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
                //データベースへの書き込み
                priorityOrderCommodityMustMapper.insert(exists);
            }
           if (notExists.length()>0){
               return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));
           }else{
                return ResultMaps.result(ResultEnum.SUCCESS);
           }
        } catch (Exception e) {
            logger.error("保存必須商品リスト："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }


    /**
     * 不可商品リストの保存
     *
     * @param priorityOrderCommodityNot
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCommodityNot(List<PriorityOrderCommodityNot> priorityOrderCommodityNot) {
        logger.info("不可商品リストパラメータの保存："+priorityOrderCommodityNot);
        // 拿到的参数只有第一行有企业和顺位表cd，需要遍历参数，给所有行都赋值
        try{
            String companyCd = priorityOrderCommodityNot.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderCommodityNot.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 调用共同方法，处理数据
            List<PriorityOrderCommodityNot> not =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityNot.class,priorityOrderCommodityNot,
                    companyCd ,priorityOrderCd);
            logger.info("不可商品リスト処理後のパラメータを保存する："+not);
            // 查询企业的店cd是几位数
            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd);
            Integer branchLen = branchCd.length();
            // 遍历not 给店cd补0
            not.forEach(item->{
                if (item.getBranch() !=null) {
                    item.setBranch(String.format("%0"+branchLen+"d",Integer.valueOf(item.getBranch())));
                }
            });
            logger.info("不可商品list店が0を補充した結菓を保存します"+not);
            //削除
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
            logger.error("不可商品リストの保存："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 必須商品を削除する
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
     * 不可商品を削除する
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
     * 必須の中間テーブルを削除
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
