package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.entity.vo.PriorityOrderJanReplaceVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderDataMapper;
import com.trechina.planocycle.mapper.PriorityOrderJanReplaceMapper;
import com.trechina.planocycle.mapper.WorkPriorityOrderMstMapper;
import com.trechina.planocycle.service.BasicPatternMstService;
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
public class PriorityOrderJanReplaceServiceImpl implements PriorityOrderJanReplaceService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    private PriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    /**
     * jan変の情報を取得する
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanInfo(String companyCd, Integer priorityOrderCd) {
        logger.info("つかむ取jan変的信息参数：{}{}{}",companyCd,",",priorityOrderCd);
        String authorCd = session.getAttribute("aud").toString();
        WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd,priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderMst.getCommonPartsData(), companyCd);
        List<PriorityOrderJanReplaceVO> priorityOrderJanReplaceVOList = priorityOrderJanReplaceMapper.selectJanInfo(companyCd,priorityOrderCd,commonTableName);
        logger.info("つかむ取jan変的信息返回値：{}",priorityOrderJanReplaceVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanReplaceVOList);
    }

    /**
     * jan変の情報を保存
     *
     * @param priorityOrderJanReplace
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanInfo(List<PriorityOrderJanReplace> priorityOrderJanReplace) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = null;
        Integer priorityOrderCd =null;
        for (PriorityOrderJanReplace orderJanReplace : priorityOrderJanReplace) {
           companyCd = orderJanReplace.getCompanyCd();
            priorityOrderCd = orderJanReplace.getPriorityOrderCd();
        }

        priorityOrderJanReplaceMapper.workDelete(companyCd,authorCd,priorityOrderCd);
        if (priorityOrderJanReplace.get(0).getJanNew()!=null) {
            priorityOrderJanReplaceMapper.insert(priorityOrderJanReplace, authorCd);
        }
                return ResultMaps.result(ResultEnum.SUCCESS);

    }

    @Override
    public String getJanInfo() {
        return priorityOrderJanReplaceMapper.selectJanDistinct();
    }

}
