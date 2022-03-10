package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.PriorityOderAttrSet;
import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrValue;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderRestrictSetMapper;
import com.trechina.planocycle.service.PriorityOrderRestrictSetService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service
public class PriorityOrderRestrictSetServiceImpl implements PriorityOrderRestrictSetService {
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    private HttpSession session;

    @Override
    public Map<String, Object> setPriorityOrderRestrict(PriorityOderAttrSet priorityOderAttrSet) {
        String authorCd = session.getAttribute("aud").toString();

        priorityOrderRestrictSetMapper.setPriorityOrderRestrict(priorityOderAttrSet, authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> getAttrDisplay(String companyCd) {
        //获取社员号
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderRestrictSet> priorityOrderRestrict = priorityOrderRestrictSetMapper.getPriorityOrderRestrict(companyCd, authorCd);
        List<PriorityOrderAttrValue> attrValues = priorityOrderRestrictSetMapper.getAttrValues();
       

        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderRestrict);
    }
}
