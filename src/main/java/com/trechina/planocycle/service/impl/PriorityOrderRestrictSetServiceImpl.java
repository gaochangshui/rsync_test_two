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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        PriorityOrderAttrDto priorityOrderAttrDto = new PriorityOrderAttrDto();
        priorityOrderAttrDto.setCompanyCd(companyCd);
        priorityOrderAttrDto.setCommonPartsData(workPriorityOrderMst.getCommonPartsData());
        GetCommonPartsDataDto commonTableName = priorityOrderMstAttrSortService.getCommonTableName(priorityOrderAttrDto);

        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getDynamicAttrValues(companyCd, "0000");
        List<Map<String, Object>> priorityOrderRestrict = priorityOrderRestrictSetMapper.getDynamicPriorityOrderRestrict(companyCd, authorCd,priorityOrderCd, zokuseiList);

        for (Map<String, Object> priorityOrderRestrictSet : priorityOrderRestrict) {
            for (int i = 0; i < zokuseiList.size(); i++) {
                int finalI = i;
                Optional<PriorityOrderAttrValueDto> any = attrValues.stream().filter(attr ->
                        MapUtils.getString(priorityOrderRestrictSet, zokuseiList.get(finalI)) != null &&
                                attr.getVal().equals(MapUtils.getString(priorityOrderRestrictSet, zokuseiList.get(finalI)))
                                && attr.getZokuseiId() == (finalI + 1)).findAny();
                if(any.isPresent()){
                    priorityOrderRestrictSet.put("zokuseiName"+(i+1), any.get().getNm());
                }else{
                    priorityOrderRestrictSet.put("zokuseiName"+(i+1), "");
                }
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS, priorityOrderRestrict);
    }
}
