package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.PriorityOrderJanCard;
import com.trechina.planocycle.entity.vo.PriorityOrderJanCardVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderDataMapper;
import com.trechina.planocycle.mapper.PriorityOrderJanCardMapper;
import com.trechina.planocycle.service.PriorityOrderJanCardService;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
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
     * card商品リストを取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanCard(String companyCd, Integer priorityOrderCd) {
        logger.info("つかむ取card商品list参数:{}{}{}",companyCd,",",priorityOrderCd);
        List<PriorityOrderJanCardVO> priorityOrderJanCardVOS = priorityOrderJanCardMapper.selectJanCard(companyCd, priorityOrderCd);
        logger.info("つかむ取card商品list返回値：{}",priorityOrderJanCardVOS);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanCardVOS);
    }

    /**
     * card商品リストの保存
     *
     * @param priorityOrderJanCard
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanCard(List<PriorityOrderJanCard> priorityOrderJanCard) {
        logger.info("保存card商品list的参数：{}",priorityOrderJanCard);

        String authorCd = session.getAttribute("aud").toString();
        String companyCd=null;
        Integer priorityOrderCd=null;

        for (PriorityOrderJanCard orderJanCard : priorityOrderJanCard) {
           companyCd= orderJanCard.getCompanyCd();
           priorityOrderCd = orderJanCard.getPriorityOrderCd();
        }
                priorityOrderJanCardMapper.workDelete(companyCd,priorityOrderCd,authorCd);
        if (priorityOrderJanCard.get(0).getJanCd()!=null){
                priorityOrderJanCardMapper.insert(priorityOrderJanCard,authorCd);
                }
        return ResultMaps.result(ResultEnum.SUCCESS);

    }


}
