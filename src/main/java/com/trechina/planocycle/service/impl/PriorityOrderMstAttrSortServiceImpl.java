package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrListVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PriorityOrderMstAttrSortServiceImpl implements PriorityOrderMstAttrSortService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private WorkPriorityOrderSpaceMapper workPriorityOrderSpaceMapper;
    @Autowired
    private WorkPriorityOrderRestrictSetMapper workPriorityOrderRestrictSetMapper;
    @Autowired
    private BasicPatternMstServiceImpl basicPatternMstService;
    @Autowired
    private BasicPatternResultMapper basicPatternResultMapper;
    @Autowired
    private PriorityOrderColorMapper priorityOrderColorMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;



    /**
     * データのソートの保存
     *
     * @param priorityOrderMstAttrSort
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityAttrSort(List<PriorityOrderMstAttrSort> priorityOrderMstAttrSort) {
        logger.info("保存優先順位表排序的参数{}", priorityOrderMstAttrSort);
        if (!priorityOrderMstAttrSort.isEmpty()) {
            priorityOrderMstAttrSortMapper.deleteByPrimaryKey(priorityOrderMstAttrSort.get(0).getCompanyCd(), priorityOrderMstAttrSort.get(0).getPriorityOrderCd());
            priorityOrderMstAttrSortMapper.insert(priorityOrderMstAttrSort);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * データのソートの削除
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityAttrSortInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderMstAttrSortMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);
    }

    /**
     * 属性1と属性2の取得
     */
    @Override
    public Map<String, Object> getAttribute(PriorityOrderAttrDto priorityOrderAttrDto) {
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderAttrDto.getCommonPartsData(),priorityOrderAttrDto.getCompanyCd());
        List<PriorityOrderAttrListVo> attributeList = priorityOrderMstAttrSortMapper.getAttribute(commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        return ResultMaps.result(ResultEnum.SUCCESS, attributeList);
    }



    /**
     * 属性の分類および商品分類リストの取得
     */
    @Override
    public Map<String, Object> getAttributeList(PriorityOrderAttrDto priorityOrderAttrDto) {
        String companyCd = priorityOrderAttrDto.getCompanyCd();
        Integer priorityOrderCd = priorityOrderAttrDto.getPriorityOrderCd();
        List<String> attrSortList = priorityOrderMstAttrSortMapper.getAttrSortList(priorityOrderAttrDto.getCompanyCd(), priorityOrderAttrDto.getPriorityOrderCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderAttrDto.getCommonPartsData(), priorityOrderAttrDto.getCompanyCd());
        List<String> mainColor = priorityOrderColorMapper.getMainColor();
        Map<String, Object> newTanaWidth = shelfPtsDataMapper.getNewTanaWidth(priorityOrderAttrDto.getPriorityOrderCd());
        Integer id = Integer.parseInt(newTanaWidth.get("id").toString());
        Integer width = Integer.parseInt(newTanaWidth.get("width").toString());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        Map<String,Object> map = new HashMap<>();
        List<Object> list = new ArrayList<>();
        for (int j = 0; j  < attrCol.size(); j++) {
            String prodMstClass = commonTableName.getProdMstClass();
            String prodIsCore = commonTableName.getProdIsCore();
            List<Map<String, Object>> attrDistinct = priorityOrderMstAttrSortMapper.getAttrDistinct(Integer.parseInt(attrCol.get(j).get(MagicString.VALUE).toString())<104?prodMstClass:"0000"
                    ,Integer.parseInt(attrCol.get(j).get(MagicString.VALUE).toString())<104? prodIsCore :"9999",priorityOrderAttrDto.getPriorityOrderCd()
                    ,attrCol.get(j).get(MagicString.VALUE).toString() ,id,width, attrSortList.get(j)
                    ,attrCol.get(j).get(MagicString.ZOUKUSEI_COLNAME).toString(),attrCol.get(j).get(MagicString.ZOUKUSEI_COLCD).toString(),attrCol.get(j).get(MagicString.ZOUKUSEI_NM).toString());
            for (int i = 0; i < attrDistinct.size(); i++) {
                attrDistinct.get(i).put(MagicString.COLOR,mainColor.get(i));
            }
            list.add(attrDistinct);
        }
        List<String> attrName = attrCol.stream().map(attrMap -> MapUtils.getString(attrMap,MagicString.ZOUKUSEI_COLNAME)).collect(Collectors.toList());
        map.put("data",list);
        map.put("attrName",attrName);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }






    @Override
    public Map<String, Object> getAttrGroup(PriorityOrderAttrDto priorityOrderAttrDto) {
        String companyCd = priorityOrderAttrDto.getCompanyCd();
        Integer priorityOrderCd = priorityOrderAttrDto.getPriorityOrderCd();
        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrList(priorityOrderAttrDto.getCompanyCd(), priorityOrderAttrDto.getPriorityOrderCd());
        List<String> attrSortList = priorityOrderMstAttrSortMapper.getAttrSortList(priorityOrderAttrDto.getCompanyCd(), priorityOrderAttrDto.getPriorityOrderCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderAttrDto.getCommonPartsData(), priorityOrderAttrDto.getCompanyCd());
        List<Integer> attrs = attrList.stream().map(Integer::parseInt).collect(Collectors.toList());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> attrName = priorityOrderMstAttrSortMapper.getAttrName(commonTableName.getProdMstClass(), commonTableName.getProdIsCore(), attrs);
        List<Map<String, Object>> restrictList = basicPatternResultMapper.getAttrComposeList(priorityOrderAttrDto.getCompanyCd()
                , priorityOrderAttrDto.getPriorityOrderCd(), attrSortList,commonTableName.getProdMstClass(),commonTableName.getProdIsCore());
        List<Map<String, Object>> newRestrictList = new ArrayList<>();
        for (Map<String, Object> objectMap : restrictList) {
            Map<String, Object> newObjectMap = new HashMap<>();
            newObjectMap.put(MagicString.COLOR, objectMap.getOrDefault(MagicString.COLOR, "#ffffff"));
            newObjectMap.put(MagicString.RESTRICT_CD, objectMap.get(MagicString.RESTRICT_CD));
            for (Map<String, Object> map : attrName) {
                for (int i = 0; i < attrCol.size(); i++) {

                    String sort = attrSortList.get(i);
                    if (objectMap.get(MagicString.ZOKUSEI_PREFIX+sort ).equals(map.get("val"))){
                        newObjectMap.put(attrCol.get(i).get(MagicString.ZOUKUSEI_COLNAME).toString(),map.get("nm"));
                    }
                    newObjectMap.put(attrCol.get(i).get(MagicString.ZOUKUSEI_COLCD).toString(), objectMap.get(MagicString.ZOKUSEI_PREFIX+sort));
                    newObjectMap.putIfAbsent(attrCol.get(i).get(MagicString.ZOUKUSEI_COLNAME).toString(),objectMap.get(MagicString.ZOKUSEI_PREFIX+sort));
                }
            }
            newRestrictList.add(newObjectMap);
        }
        attrCol = attrCol.stream().sorted(Comparator.comparing(map-> MapUtils.getInteger(map,"sort"))).collect(Collectors.toList());
        StringBuilder groupHeader = new StringBuilder();
        StringBuilder groupColumns = new StringBuilder();
        for (Map<String, Object> map : attrCol) {
            if (groupHeader.toString().equals("")){
                groupHeader.append(map.get(MagicString.ZOUKUSEI_NM));
                groupColumns.append(map.get(MagicString.ZOUKUSEI_COLNAME));
            }else {
                groupHeader.append(",").append(map.get(MagicString.ZOUKUSEI_NM));
                groupColumns.append(",").append(map.get(MagicString.ZOUKUSEI_COLNAME));
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("groupHeader", groupHeader.toString());
        map.put("groupColumns", groupColumns.toString());
        map.put("data",newRestrictList);
        return ResultMaps.result(ResultEnum.SUCCESS, map);
    }




}
