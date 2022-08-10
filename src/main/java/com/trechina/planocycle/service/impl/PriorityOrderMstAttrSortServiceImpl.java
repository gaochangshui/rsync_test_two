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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrList(priorityOrderAttrDto.getCompanyCd(), priorityOrderAttrDto.getPriorityOrderCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderAttrDto.getCommonPartsData(), priorityOrderAttrDto.getCompanyCd());
        List<String> mainColor = priorityOrderColorMapper.getMainColor();
        Map<String, Object> newTanaWidth = shelfPtsDataMapper.getNewTanaWidth(priorityOrderAttrDto.getPriorityOrderCd());
        Integer id = Integer.parseInt(newTanaWidth.get("id").toString());
        Integer width = Integer.parseInt(newTanaWidth.get("width").toString());
        List list = new ArrayList();
        for (String s : attrList) {
            List<Map<String, Object>> attrDistinct = priorityOrderMstAttrSortMapper.getAttrDistinct(commonTableName.getProdMstClass()
                    , commonTableName.getProdIsCore(),priorityOrderAttrDto.getPriorityOrderCd(), s,id,width);
            for (int i = 0; i < attrDistinct.size(); i++) {
                attrDistinct.get(i).put("color",mainColor.get(i));
            }
            list.add(attrDistinct);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,list);
    }






    @Override
    public Map<String, Object> getAttrGroup(PriorityOrderAttrDto priorityOrderAttrDto) {

        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrList(priorityOrderAttrDto.getCompanyCd(), priorityOrderAttrDto.getPriorityOrderCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderAttrDto.getCommonPartsData(), priorityOrderAttrDto.getCompanyCd());
        List<Integer> attrs = attrList.stream().map(Integer::parseInt).collect(Collectors.toList());
        List<Map<String, Object>> attrName = priorityOrderMstAttrSortMapper.getAttrName(commonTableName.getProdMstClass(), commonTableName.getProdIsCore(), attrs);
        List<Map<String, Object>> restrictList = basicPatternResultMapper.getAttrComposeList(priorityOrderAttrDto.getCompanyCd()
                , priorityOrderAttrDto.getPriorityOrderCd(), attrList,commonTableName.getProdMstClass(),commonTableName.getProdIsCore());
        for (Map<String, Object> objectMap : restrictList) {

            for (Map<String, Object> map : attrName) {
                for (String s : attrList) {
                    if (objectMap.get("zokusei"+s).equals(map.get("val"))){
                        objectMap.put("zokuseiName"+s,map.getOrDefault("nm",""));
                    }
                    objectMap.putIfAbsent("zokuseiName"+s,"");
                }
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS, restrictList);
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
