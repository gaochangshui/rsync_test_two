package com.trechina.planocycle.service.impl;

import com.google.api.client.util.Strings;
import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanCard;
import com.trechina.planocycle.entity.po.PriorityOrderJanCard;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanCardVO;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanNewVO;
import com.trechina.planocycle.entity.vo.PriorityOrderJanCardVO;
import com.trechina.planocycle.entity.vo.PriorityOrderJanNewVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderJanCardService;
import com.trechina.planocycle.service.PriorityOrderJanCardService;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ResultMaps;
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
public class ClassicPriorityOrderJanCardServiceImpl implements ClassicPriorityOrderJanCardService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;

    @Autowired
    private ClassicPriorityOrderJanNewMapper priorityOrderJanNewMapper;
    /**
     * 获取card商品list
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanCard(String companyCd, Integer priorityOrderCd) {
        String tableName = "public.priorityorder" + session.getAttribute("aud").toString();
        List<ClassicPriorityOrderJanNewVO> janNewList = priorityOrderJanNewMapper.getExistOtherMst(companyCd, priorityOrderCd,tableName);
        logger.info("获取card商品list参数:"+companyCd+","+priorityOrderCd);
        List<ClassicPriorityOrderJanCardVO> priorityOrderJanCardVOS = priorityOrderJanCardMapper.selectJanCard(companyCd,priorityOrderCd);
        for (ClassicPriorityOrderJanNewVO priorityOrderJanNewVO : janNewList) {
            for (ClassicPriorityOrderJanCardVO priorityOrderJanCardVO : priorityOrderJanCardVOS) {
                if (priorityOrderJanNewVO.getJanNew().equals(priorityOrderJanCardVO.getJanOld())){
                    priorityOrderJanCardVO.setErrMsg("すでに新規商品として入力済みです。");
                }
            }
        }
        logger.info("获取card商品list返回值："+priorityOrderJanCardVOS);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanCardVOS);
    }

    /**
     * 保存card商品list
     *
     * @param priorityOrderJanCard
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanCard(List<ClassicPriorityOrderJanCard> priorityOrderJanCard) {
        logger.info("保存card商品list的参数："+priorityOrderJanCard);
        try {
            dataConverUtils dataConverUtil = new dataConverUtils();
            String companyCd = priorityOrderJanCard.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanCard.get(0).getPriorityOrderCd();
            // 处理参数
            List<ClassicPriorityOrderJanCard> cards = dataConverUtil.priorityOrderCommonMstInsertMethod(ClassicPriorityOrderJanCard.class,
                    priorityOrderJanCard,companyCd,priorityOrderCd);
            logger.info("保存card商品list的处理完的参数："+cards);
            //全删
            priorityOrderJanCardMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // jancheck
            String janInfo = priorityOrderJanReplaceService.getJanInfo();
            List<String> list= Arrays.asList(janInfo.split(","));
            String notExists = "";
            List<ClassicPriorityOrderJanCard> exists = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getJanOld()!=null) {
                    if (list.indexOf(cards.get(i).getJanOld())==-1){
                        notExists += cards.get(i).getJanOld()+",";
                    } else {
                        exists.add(cards.get(i));
                    }
                }
            }
            String tableName = "public.priorityorder" + session.getAttribute("aud").toString();
         //   priorityOrderDataMapper.updateCutJanForProp(tableName);
            if (exists.size()>0) {
                //全插
                priorityOrderJanCardMapper.insert(exists);

                // 修改优先顺位存在的jan信息
            //    priorityOrderDataMapper.updatePriorityOrderDataForCard(companyCd, priorityOrderCd,tableName);
            }
            if (notExists.length()>0){
                return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));

            }else {
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.info("保存card商品list报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }

    }

    /**
     * 删除card商品list
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderJanCardInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanCardMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }

    @Override
    public Map<String, String> checkIsJanCut(List<ClassicPriorityOrderJanCard> priorityOrderJanCard) {
        Map<String, String> errorJan = new HashMap<>();
        if(priorityOrderJanCard.isEmpty()){
            return errorJan;
        }
        List<String> janReplaceList = priorityOrderJanCardMapper.checkJanReplace(priorityOrderJanCard);
        List<String> janReplaceOldList = priorityOrderJanCardMapper.checkJanReplaceOld(priorityOrderJanCard);
        if (!janReplaceList.isEmpty() || !janReplaceOldList.isEmpty()){
            janReplaceList.addAll(janReplaceOldList);
            for (String jan : janReplaceList) {
                if(Strings.isNullOrEmpty(errorJan.getOrDefault(jan, ""))) {
                    errorJan.put(jan, "すでにJAN変商品として入力済みです。");
                }
            }
        }

        List<String> janProposalList = priorityOrderJanCardMapper.checkJanProposal(priorityOrderJanCard);
        List<String> janProposalOldList = priorityOrderJanCardMapper.checkJanProposalOld(priorityOrderJanCard);
        if (!janProposalList.isEmpty() || !janProposalOldList.isEmpty()){
            janProposalList.addAll(janProposalOldList);
            janProposalList = janProposalList.stream().distinct().collect(Collectors.toList());

            for (String jan : janProposalList) {
                if(Strings.isNullOrEmpty(errorJan.getOrDefault(jan, ""))){
                    errorJan.put(jan, "すでにJAN変商品として入力済みです。");
                }
            }
        }

        List<String> janNewList = priorityOrderJanCardMapper.checkJanNew(priorityOrderJanCard);
        if (!janNewList.isEmpty()){
            for (String jan : janNewList) {
                if(Strings.isNullOrEmpty(errorJan.getOrDefault(jan, ""))){
                    errorJan.put(jan, "すでに新規商品として入力済みです。");
                }
            }
        }

        return errorJan;
    }


}
