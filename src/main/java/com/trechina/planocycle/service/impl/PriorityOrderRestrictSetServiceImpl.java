package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.PriorityOderAttrSet;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderRestrictSetMapper;
import com.trechina.planocycle.service.PriorityOrderRestrictSetService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class PriorityOrderRestrictSetServiceImpl implements PriorityOrderRestrictSetService {
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private HttpSession session;

    /**
     * テーブル/セグメント対応属性の追加削除
     * @param priorityOderAttrSet
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderRestrict(PriorityOderAttrSet priorityOderAttrSet) {
        String authorCd = session.getAttribute("aud").toString();

        priorityOrderRestrictSetMapper.setPriorityOrderRestrict(priorityOderAttrSet, authorCd);
        priorityOrderRestrictSetMapper.delete(priorityOderAttrSet,authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

}
