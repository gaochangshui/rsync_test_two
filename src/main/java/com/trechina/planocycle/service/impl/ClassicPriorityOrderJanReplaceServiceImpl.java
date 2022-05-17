package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanCard;
import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanReplace;
import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.entity.vo.PriorityOrderJanReplaceVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.dataConverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderJanReplaceServiceImpl implements ClassicPriorityOrderJanReplaceService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    private ClassicPriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private ClassicPriorityOrderCommodityNotMapper priorityOrderCommodityNotMapper;
    /**
     * 获取jan变的信息
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanInfo(String companyCd, Integer priorityOrderCd) {
        logger.info("获取jan变的信息参数："+companyCd+","+priorityOrderCd);
        List<PriorityOrderJanReplaceVO> priorityOrderJanReplaceVOList = priorityOrderJanReplaceMapper
                .selectJanInfo(companyCd,priorityOrderCd);
        logger.info("获取jan变的信息返回值："+priorityOrderJanReplaceVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanReplaceVOList);
    }

    /**
     * 保存jan变的信息
     *
     * @param priorityOrderJanReplace
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanInfo(List<PriorityOrderJanReplace> priorityOrderJanReplace) {
        logger.info("保存jan变的信息参数："+priorityOrderJanReplace);
        // 拿到的参数只有第一行有企业和顺位表cd，需要遍历参数，给所有行都赋值
        try{
            String companyCd = priorityOrderJanReplace.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanReplace.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 调用共同方法，处理数据
            List<PriorityOrderJanReplace> jan =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderJanReplace.class,priorityOrderJanReplace,
                    companyCd ,priorityOrderCd);

            logger.info("保存jan变的信息处理完后的参数："+jan);
            // check
            String resuleJanDistinct =getJanInfo();
            logger.info("checkjan的返回值"+resuleJanDistinct);
            //删除

            priorityOrderJanReplaceMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            List<PriorityOrderJanReplace> exists = new ArrayList<>();
            String notExists = "";
            List<String> list= Arrays.asList(resuleJanDistinct.split(","));
            for (int i = 0; i < jan.size(); i++) {
                if(jan.get(i).getJanNew()==null && jan.get(i).getJanOld()==null){
                    continue;
                }
                if (!list.contains(jan.get(i).getJanNew())){
                    notExists += jan.get(i).getJanNew()+",";
                }else if (!list.contains(jan.get(i).getJanOld())){
                    notExists += jan.get(i).getJanOld()+",";
                }else {
                    exists.add(jan.get(i));
                }
            }
            logger.info("存在的jan信息"+exists.toString());
            logger.info("不存在的jan信息"+notExists);
            //写入数据库
            if (exists.size()>0) {
                priorityOrderJanReplaceMapper.insert(exists);
                //修改主表
                //priorityOrderDataMapper.updatePriorityOrderDataForJanNew(companyCd,priorityOrderCd,
                //        "public.priorityorder"+session.getAttribute("aud").toString());
            }
            if (notExists.length()>0) {
                return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));
            } else {
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("保存jan变信息出错："+e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    private List<String> checkIsJanOldReplace(List<ClassicPriorityOrderJanReplace> priorityOrderJanReplace, String companyCd, Integer priorityOrderCd){
        List<ClassicPriorityOrderJanCard> priorityOrderJanCardList = priorityOrderJanReplace.stream().map(jan -> {
            ClassicPriorityOrderJanCard priorityOrderJanCard = new ClassicPriorityOrderJanCard();
            BeanUtils.copyProperties(jan, priorityOrderJanCard);
            return priorityOrderJanCard;
        }).collect(Collectors.toList());
        List<String> janOldList = priorityOrderJanReplace.stream().map(ClassicPriorityOrderJanReplace::getJanOld).collect(Collectors.toList());

        List<String> existJanNew = priorityOrderJanCardMapper.checkJanNew(priorityOrderJanCardList);
        List<String> existJanReplace =  priorityOrderJanReplaceMapper.existJanNew(janOldList, companyCd);
        List<String> existJanMust = priorityOrderJanCardMapper.checkJanMust(priorityOrderJanCardList);

        List<String> existJan = new ArrayList<>();
        existJan.addAll(existJanNew);
        existJan.addAll(existJanReplace);
        existJan.addAll(existJanMust);

        return existJan;
    }

    private List<String> checkIsJanNewReplace(List<PriorityOrderJanReplace> priorityOrderJanReplace, String companyCd, Integer priorityOrderCd){
        List<String> janNewList = priorityOrderJanReplace.stream().map(PriorityOrderJanReplace::getJanNew).collect(Collectors.toList());

        List<String> existJanNew = priorityOrderJanCardMapper.existJan(janNewList, companyCd, priorityOrderCd);
        List<String> existJanReplace =  priorityOrderJanReplaceMapper.existJanNew(janNewList, companyCd);
        List<String> existJanNot = priorityOrderCommodityNotMapper.existJan(janNewList, companyCd, priorityOrderCd);

        List<String> existJan = new ArrayList<>();
        existJan.addAll(existJanNew);
        existJan.addAll(existJanReplace);
        existJan.addAll(existJanNot);

        return existJan;
    }
    @Override
    public String getJanInfo() {
        return priorityOrderJanReplaceMapper.selectJanDistinct();
    }

    /**
     * 删除jan变list
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delJanReplaceInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanReplaceMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }
}
