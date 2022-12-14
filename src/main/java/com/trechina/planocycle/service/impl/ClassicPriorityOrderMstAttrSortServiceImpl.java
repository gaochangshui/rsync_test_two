package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderMstAttrSortMapper;
import com.trechina.planocycle.service.ClassicPriorityOrderMstAttrSortService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassicPriorityOrderMstAttrSortServiceImpl implements ClassicPriorityOrderMstAttrSortService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    /**
     * 既存のデータのソートの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityAttrSort(String companyCd, Integer priorityOrderCd) {
        List<PriorityOrderMstAttrSort> resultInfo = priorityOrderMstAttrSortMapper.selectByPrimaryKey(companyCd,priorityOrderCd);
        List<Map<String,Object>> result = new ArrayList<>();
        resultInfo.forEach(item->{
            Map<String,Object> maps = new HashMap<>();
                maps.put("value",item.getValue());
            maps.put("cd",item.getCd().toString());
            maps.put("sort",item.getSort());
            result.add(maps);
        });
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }

    /**
     * 削除データのソート
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityAttrSortInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderMstAttrSortMapper.deleteAttrFinal(companyCd,priorityOrderCd);
    }
}
