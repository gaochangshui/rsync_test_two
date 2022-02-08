package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.PriorityOrderJanCard;
import com.trechina.planocycle.entity.vo.PriorityOrderJanCardVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderDataMapper;
import com.trechina.planocycle.mapper.PriorityOrderJanCardMapper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class PriorityOrderJanCardServiceImpl implements PriorityOrderJanCardService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;

    @Autowired
    private PriorityOrderDataMapper priorityOrderDataMapper;
    /**
     * 获取card商品list
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanCard(String companyCd, Integer priorityOrderCd) {
        logger.info("获取card商品list参数:"+companyCd+","+priorityOrderCd);
        List<PriorityOrderJanCardVO> priorityOrderJanCardVOS = priorityOrderJanCardMapper.selectJanCard(companyCd,priorityOrderCd);
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
    public Map<String, Object> setPriorityOrderJanCard(List<PriorityOrderJanCard> priorityOrderJanCard) {
        logger.info("保存card商品list的参数："+priorityOrderJanCard);
        try {
            dataConverUtils dataConverUtil = new dataConverUtils();
            String companyCd = priorityOrderJanCard.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanCard.get(0).getPriorityOrderCd();
            // 处理参数
            List<PriorityOrderJanCard> cards = dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderJanCard.class,
                    priorityOrderJanCard,companyCd,priorityOrderCd);
            logger.info("保存card商品list的处理完的参数："+cards);
            //全删
            priorityOrderJanCardMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // jancheck
            String janInfo = priorityOrderJanReplaceService.getJanInfo();
            List<String> list= Arrays.asList(janInfo.split(","));
            String notExists = "";
            List<PriorityOrderJanCard> exists = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++) {
                if (list.indexOf(cards.get(i).getJanOld())==-1){
                    notExists += cards.get(i).getJanOld()+",";
                } else {
                    exists.add(cards.get(i));
                }
            }
            if (exists.size()>0) {
                //全插
                priorityOrderJanCardMapper.insert(exists);

                // 修改优先顺位存在的jan信息
                priorityOrderDataMapper.updatePriorityOrderDataForCard(companyCd, priorityOrderCd,
                        "public.priorityorder" + session.getAttribute("aud").toString());
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
}
