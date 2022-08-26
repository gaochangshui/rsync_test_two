package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictSet;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrListVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrList(priorityOrderAttrDto.getCompanyCd(), priorityOrderAttrDto.getPriorityOrderCd());
        List<String> attrSortList = priorityOrderMstAttrSortMapper.getAttrSortList(priorityOrderAttrDto.getCompanyCd(), priorityOrderAttrDto.getPriorityOrderCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderAttrDto.getCommonPartsData(), priorityOrderAttrDto.getCompanyCd());
        List<String> mainColor = priorityOrderColorMapper.getMainColor();
        Map<String, Object> newTanaWidth = shelfPtsDataMapper.getNewTanaWidth(priorityOrderAttrDto.getPriorityOrderCd());
        Integer id = Integer.parseInt(newTanaWidth.get("id").toString());
        Integer width = Integer.parseInt(newTanaWidth.get("width").toString());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List list = new ArrayList();
        for (int j = 0; j  < attrCol.size(); j++) {
            String prodMstClass = commonTableName.getProdMstClass();
            String prodIsCore = commonTableName.getProdIsCore();


            List<Map<String, Object>> attrDistinct = priorityOrderMstAttrSortMapper.getAttrDistinct(Integer.parseInt(attrCol.get(j).get("value").toString())<104?prodMstClass:"0000"
                    ,Integer.parseInt(attrCol.get(j).get("value").toString())<104? prodIsCore :"9999",priorityOrderAttrDto.getPriorityOrderCd(),attrCol.get(j).get("value").toString() ,id,width, attrSortList.get(j)
                    ,attrCol.get(j).get("zokusei_colname").toString(),attrCol.get(j).get("zokusei_colcd").toString());
            for (int i = 0; i < attrDistinct.size(); i++) {
                attrDistinct.get(i).put("color",mainColor.get(i));
            }
            list.add(attrDistinct);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,list);
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
            newObjectMap.put("color", objectMap.getOrDefault("color", "#ffffff"));
            newObjectMap.put("restrictCd", objectMap.get("restrictCd"));
            for (Map<String, Object> map : attrName) {
                for (int i = 0; i < attrCol.size(); i++) {

                    String sort = attrSortList.get(i);
                    if (objectMap.getOrDefault("zokusei"+sort, "未登録").equals(map.get("val"))){
                        newObjectMap.put(attrCol.get(i).get("zokusei_colname").toString(),map.getOrDefault("nm","未登録"));
                    }
                    newObjectMap.put(attrCol.get(i).get("zokusei_colcd").toString(), objectMap.get("zokusei"+sort));
                    newObjectMap.putIfAbsent(attrCol.get(i).get("zokusei_colname").toString(),"未登録");
                }
            }
            newRestrictList.add(newObjectMap);
        }
        attrCol = attrCol.stream().sorted(Comparator.comparing(map-> MapUtils.getInteger(map,"sort"))).collect(Collectors.toList());
        String groupHeader = "";
        String groupColumns = "";
        for (Map<String, Object> map : attrCol) {
            if (groupHeader.equals("")){
                groupHeader += map.get("zokusei_nm");
                groupColumns += map.get("zokusei_colname");
            }else {
                groupHeader += ","+map.get("zokusei_nm");
                groupColumns += ","+map.get("zokusei_colname");
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("groupHeader",groupHeader);
        map.put("groupColumns",groupColumns);
        map.put("data",newRestrictList);
        return ResultMaps.result(ResultEnum.SUCCESS, map);
    }




    private List<WorkPriorityOrderRestrictSet> packageRestrict(int begin, int end, Integer[] tanaCdArray, Integer taiCd, WorkPriorityOrderRestrictSet tmpRestrictSet) {
        List<WorkPriorityOrderRestrictSet> restrictSetList = new ArrayList<>();
        WorkPriorityOrderRestrictSet restrictSet = null;
        if (begin < end) {
            for (int i = begin; i < end; i++) {
                restrictSet = new WorkPriorityOrderRestrictSet();
                BeanUtils.copyProperties(tmpRestrictSet, restrictSet);
                restrictSet.setTaiCd(taiCd);
                restrictSet.setTanaCd(tanaCdArray[i]);
                restrictSetList.add(restrictSet);
            }
        } else {
            restrictSet = new WorkPriorityOrderRestrictSet();
            BeanUtils.copyProperties(tmpRestrictSet, restrictSet);
            restrictSet.setTaiCd(taiCd);
            restrictSet.setTanaCd(0);
            restrictSetList.add(restrictSet);
        }
        return restrictSetList;
    }


}
