package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.PriorityOrderAttrValueDto;
import com.trechina.planocycle.entity.po.PriorityOderAttrSet;
import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderRestrictSetMapper;
import com.trechina.planocycle.service.PriorityOrderRestrictSetService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Service
public class PriorityOrderRestrictSetServiceImpl implements PriorityOrderRestrictSetService {
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private HttpSession session;

    @Override
    public Map<String, Object> setPriorityOrderRestrict(PriorityOderAttrSet priorityOderAttrSet) {
        String authorCd = session.getAttribute("aud").toString();

        priorityOrderRestrictSetMapper.setPriorityOrderRestrict(priorityOderAttrSet, authorCd);
        priorityOrderRestrictSetMapper.delete(priorityOderAttrSet,authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> getAttrDisplay(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //获取社员号
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderRestrictSet> priorityOrderRestrict = priorityOrderRestrictSetMapper.getPriorityOrderRestrict(companyCd, authorCd,priorityOrderCd);
        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues();
        Class clazz = PriorityOrderRestrictSet.class;
        for (int i = 1; i <= 10; i++) {
            Method getMethod = clazz.getMethod("get"+"Zokusei"+i);
            Method setMethod = clazz.getMethod("set"+"ZokuseiName"+i, String.class);
            for (PriorityOrderRestrictSet priorityOrderRestrictSet : priorityOrderRestrict) {
                for (PriorityOrderAttrValueDto attrValue : attrValues) {
                    if (getMethod.invoke(priorityOrderRestrictSet)!=null&&getMethod.invoke(priorityOrderRestrictSet).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
                        setMethod.invoke(priorityOrderRestrictSet,attrValue.getNm());
                    }
                }
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS, priorityOrderRestrict);
    }
}
