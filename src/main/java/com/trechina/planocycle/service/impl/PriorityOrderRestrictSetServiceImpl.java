package com.trechina.planocycle.service.impl;

import com.google.common.base.Strings;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrValueDto;
import com.trechina.planocycle.entity.po.PriorityOderAttrSet;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderMstMapper;
import com.trechina.planocycle.mapper.PriorityOrderRestrictSetMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.mapper.WorkPriorityOrderMstMapper;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import com.trechina.planocycle.service.PriorityOrderRestrictSetService;
import com.trechina.planocycle.utils.CommonUtil;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class PriorityOrderRestrictSetServiceImpl implements PriorityOrderRestrictSetService {
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;

    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    @Autowired
    private SysConfigMapper sysConfigMapper;
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

    /**
     * 各ステージ/セグメントに対応するプロパティの取得
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getAttrDisplay(String companyCd,Integer priorityOrderCd) {
        //社員番号の取得
        String authorCd = session.getAttribute("aud").toString();
        String zokusei = sysConfigMapper.selectSycConfig(MagicString.ZOKUSEI_COUNT_ITEM);
        if(Strings.isNullOrEmpty(zokusei)){
            zokusei = "33";
        }

        List<String> zokuseiList = CommonUtil.getZokuseiList(Integer.parseInt(zokusei));
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = priorityOrderMstAttrSortService
                .getCommonTableName(workPriorityOrderMst.getCommonPartsData(), companyCd);

        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getDynamicAttrValues(commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<LinkedHashMap<String,Object>> priorityOrderRestrict = priorityOrderRestrictSetMapper.getDynamicPriorityOrderRestrict(companyCd, authorCd,priorityOrderCd, zokuseiList);

        for (LinkedHashMap<String, Object> priorityOrderRestrictSet : priorityOrderRestrict) {
            for (int i = 0; i < zokuseiList.size(); i++) {
                int finalI = i;
                String zokuseiId = zokuseiList.get(finalI);
                Optional<PriorityOrderAttrValueDto> any = attrValues.stream().filter(attr ->
                        MapUtils.getString(priorityOrderRestrictSet, zokuseiId) != null &&
                                attr.getVal().equals(MapUtils.getString(priorityOrderRestrictSet, zokuseiId))
                                && attr.getZokuseiId() == (finalI + 1)).findAny();
                if(Strings.isNullOrEmpty(MapUtils.getString(priorityOrderRestrictSet, MagicString.ZOKUSEI_PREFIX+(i+1)))){
                    priorityOrderRestrictSet.put(MagicString.ZOKUSEI_PREFIX+(i+1), null);
                }

                if(any.isPresent()){
                    priorityOrderRestrictSet.put("zokuseiName"+(i+1), any.get().getNm());
                }else{
                    priorityOrderRestrictSet.put("zokuseiName"+(i+1), null);
                }
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS, priorityOrderRestrict);
    }
}
