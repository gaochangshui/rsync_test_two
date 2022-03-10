package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrListVo;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrVO;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrValue;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrValueVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriorityOrderMstAttrSortMapper {
    int deleteByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    int insert(@Param("lists") List<PriorityOrderMstAttrSort> record);

    int insertSelective(PriorityOrderMstAttrSort record);

    List<PriorityOrderMstAttrSort> selectByPrimaryKey(@Param("companyCd") String companyCd, @Param("priorityOrderCd") Integer priorityOrderCd);

    //跟据属性获取type值
    int getAttrType(@Param("attrId") Integer attrId);

    //跟据属性获取sort
    int getAttrSort(@Param("attrId") Integer attrId);

    //跟据属性获取tableName
    String getAttrTableName(@Param("attrId") Integer attrId);

    //获取属性列表
    List<PriorityOrderAttrListVo> getAttribute();

    List<PriorityOrderAttrListVo> getAttrValue(@Param("zokuseiId") Integer zokuseiId);

    List<PriorityOrderAttrVO> getAttrValue5(@Param("attr1") Integer a1cd);

    //获取商品分类属性结构
    List<PriorityOrderAttrValue> getGoodsAttrTree();

    //商品分類以外の属性数
    List<PriorityOrderAttrValueVo> getAttr();

    //根据zokuseiId获取对应的属性和属性类型
    List<PriorityOrderAttrValue> getAttrValues(@Param("zokuseiId") Integer zokuseiId);

    //根据对应的属性获取对应的face
    Integer getfeceNum(@Param("janCol1") String janCol1, @Param("janCol2") String janCol2, @Param("attrValue1") String attrValue1, @Param("attrValue2") String attrValue2, @Param("patternCd") Integer patternCd);

    //根据企业cd获取每个台段对应的属性





}
