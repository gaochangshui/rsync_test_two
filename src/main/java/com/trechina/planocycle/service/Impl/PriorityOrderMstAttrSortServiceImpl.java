package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderMstAttrSortMapper;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
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
public class PriorityOrderMstAttrSortServiceImpl implements PriorityOrderMstAttrSortService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    /**
     * 获取既存数据的排序
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
            if (item.getCd()==13 && item.getValue() == resultInfo.size()){
                maps.put("value","mulit_attr");
            } else {
                maps.put("value",item.getValue().toString());
            }
            maps.put("cd",item.getCd().toString());
            maps.put("sort",item.getSort());
            result.add(maps);
        });
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }

    /**
     * 保存数据的排序
     *
     * @param priorityOrderMstAttrSort
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityAttrSort(List<PriorityOrderMstAttrSort> priorityOrderMstAttrSort) {
        logger.info("保存优先顺位表排序的参数"+priorityOrderMstAttrSort);
        if (priorityOrderMstAttrSort.size()>0) {
            priorityOrderMstAttrSortMapper.deleteByPrimaryKey(priorityOrderMstAttrSort.get(0).getCompanyCd(), priorityOrderMstAttrSort.get(0).getPriorityOrderCd());
            priorityOrderMstAttrSortMapper.insert(priorityOrderMstAttrSort);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 删除数据的排序
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityAttrSortInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderMstAttrSortMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }

    /**
     *获取属性1和属性2
     */
    @Override
    public Map<String, Object> getAttribute() {
        List<PriorityOrderAttrListVo> attributeList = priorityOrderMstAttrSortMapper.getAttribute();
        return ResultMaps.result(ResultEnum.SUCCESS,attributeList);
    }
    /**
     * 获取属性的分类及商品分类列表
     */
    @Override
    public Map<String, Object> getAttributeList() {

        List<PriorityOrderAttrValue> goodsAttrTree = priorityOrderMstAttrSortMapper.getGoodsAttrTree();
        PriorityOrderAttrValueVo priorityOrderAttrValueVo = new PriorityOrderAttrValueVo();
        priorityOrderAttrValueVo.setValues(goodsAttrTree);
        priorityOrderAttrValueVo.setAttrName("商品分類");
        priorityOrderAttrValueVo.setAttrCd(0);
        List<PriorityOrderAttrValueVo> attr =new ArrayList<>();
        attr.add(priorityOrderAttrValueVo);
        List<PriorityOrderAttrValueVo> attr1 = priorityOrderMstAttrSortMapper.getAttr();
        for (PriorityOrderAttrValueVo orderAttrValueVo : attr1) {
            attr.add(orderAttrValueVo);
        }

        for (PriorityOrderAttrValueVo priorityOrderAttrListVo : attr) {
            if (priorityOrderAttrListVo.getTableName()!=null) {
                List<PriorityOrderAttrValue> attrValue2 = priorityOrderMstAttrSortMapper.getAttrValues(priorityOrderAttrListVo.getTableName());
                priorityOrderAttrListVo.setValues(attrValue2);
            }
        }



        return ResultMaps.result(ResultEnum.SUCCESS,attr);
    }

    /**
     *获取属性1属性2组合对应的面积
     */
    @Override
    public Map<String, Object> getAttributeArea(Integer patternCd, Integer attr1, Integer attr2) {
        int attrType1 = priorityOrderMstAttrSortMapper.getAttrType(attr1);
        int attrType2 = priorityOrderMstAttrSortMapper.getAttrType(attr2);
        if (attrType1 == 1 && attrType2 ==1){

            int attrSort = priorityOrderMstAttrSortMapper.getAttrSort(attr1);
            int attrSort1 = priorityOrderMstAttrSortMapper.getAttrSort(attr2);
            List<PriorityOrderAttrVO> attrList = new ArrayList<>();
            if (attrSort>attrSort1){
                attrList = priorityOrderMstAttrSortMapper.getAttrValue5(attrSort1, attrSort);
                attrList.forEach(item->{
                 //   item.setAttrAName(priorityOrderMstAttrSortMapper.getfeceNum(attr1,attr2,item.getAttrACd(),item.getAttrBCd()));
                });
            }
            if (attrSort<attrSort1){
                 attrList = priorityOrderMstAttrSortMapper.getAttrValue5(attrSort, attrSort1);
            }

            logger.info("属性所有组合为：{}" ,attrList);
            return ResultMaps.result(ResultEnum.SUCCESS,attrList);

        }else if (attrType1 ==1 && attrType2 ==0){
            String attr2TableName = priorityOrderMstAttrSortMapper.getAttrTableName(attr2);
            List<PriorityOrderAttrListVo> attrValue2 = priorityOrderMstAttrSortMapper.getAttrValue(attr2TableName);
            List<PriorityOrderAttrListVo> attrValue1 = priorityOrderMstAttrSortMapper.getAttrValue1(attr1);
            PriorityOrderAttrVO priorityOrderAttrVO = new PriorityOrderAttrVO();
            List attrList = new ArrayList();
            for (PriorityOrderAttrListVo priorityOrderAttrListVo : attrValue2) {
                for (PriorityOrderAttrListVo orderAttrListVo : attrValue1) {
                    priorityOrderAttrVO.setAttrBCd(priorityOrderAttrListVo.getAttrCd());
                    priorityOrderAttrVO.setAttrBName(priorityOrderAttrListVo.getAttrName());
                    priorityOrderAttrVO.setAttrACd(orderAttrListVo.getAttrCd());
                    priorityOrderAttrVO.setAttrAName(orderAttrListVo.getAttrName());
                    attrList.add(priorityOrderAttrVO);
                }
            }
            logger.info("属性所有组合为：{}" ,attrList);
            return ResultMaps.result(ResultEnum.SUCCESS,attrList);
        }else if (attrType2 ==1 && attrType1 == 0){
            String attr1TableName = priorityOrderMstAttrSortMapper.getAttrTableName(attr1);
            List<PriorityOrderAttrListVo> attrValue2 = priorityOrderMstAttrSortMapper.getAttrValue(attr1TableName);
            List<PriorityOrderAttrListVo> attrValue1 = priorityOrderMstAttrSortMapper.getAttrValue1(attr2);
            PriorityOrderAttrVO priorityOrderAttrVO = new PriorityOrderAttrVO();
            List attrList = new ArrayList();
            for (PriorityOrderAttrListVo priorityOrderAttrListVo : attrValue2) {
                for (PriorityOrderAttrListVo orderAttrListVo : attrValue1) {
                    priorityOrderAttrVO.setAttrBCd(priorityOrderAttrListVo.getAttrCd());
                    priorityOrderAttrVO.setAttrBName(priorityOrderAttrListVo.getAttrName());
                    priorityOrderAttrVO.setAttrACd(orderAttrListVo.getAttrCd());
                    priorityOrderAttrVO.setAttrAName(orderAttrListVo.getAttrName());
                    attrList.add(priorityOrderAttrVO);
                }

            }
            logger.info("属性所有组合为：{}" ,attrList);
            return ResultMaps.result(ResultEnum.SUCCESS,attrList);
        }else {
            //跟据分类id获取对应的表名
            String attr1TableName = priorityOrderMstAttrSortMapper.getAttrTableName(attr1);
            String attr2TableName = priorityOrderMstAttrSortMapper.getAttrTableName(attr2);
            List<PriorityOrderAttrListVo> attrValue1 = priorityOrderMstAttrSortMapper.getAttrValue(attr1TableName);
            List<PriorityOrderAttrListVo> attrValue2 = priorityOrderMstAttrSortMapper.getAttrValue(attr2TableName);
            PriorityOrderAttrVO priorityOrderAttrVO = new PriorityOrderAttrVO();
            List attrList = new ArrayList();
            for (PriorityOrderAttrListVo priorityOrderAttrListVo : attrValue1) {
                for (PriorityOrderAttrListVo orderAttrListVo : attrValue2) {
                    priorityOrderAttrVO.setAttrACd(priorityOrderAttrListVo.getAttrCd());
                    priorityOrderAttrVO.setAttrAName(priorityOrderAttrListVo.getAttrName());
                    priorityOrderAttrVO.setAttrBCd(orderAttrListVo.getAttrCd());
                    priorityOrderAttrVO.setAttrBName(orderAttrListVo.getAttrName());
                    attrList.add(priorityOrderAttrVO);
                }
            }

            logger.info("属性所有组合为：{}", attrList);
            return ResultMaps.result(ResultEnum.SUCCESS,attrList);
        }

    }
}
