package com.trechina.planocycle.mapper;

import com.trechina.planocycle.entity.dto.PriorityOrderAttrFaceNum;
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

    //属性に従ってtype値を取得
    int getAttrType(@Param("attrId") Integer attrId);

    //コンプライアンス属性取得sort
    int getAttrSort(@Param("attrId") Integer attrId);

    //属性に従ってtableNameを取得
    String getAttrTableName(@Param("attrId") Integer attrId);

    //属性リストの取得
    List<PriorityOrderAttrListVo> getAttribute(String proZokuseiMstTable);
    //陳列設定属性リストの取得
    List<PriorityOrderAttrListVo> getAttributeSort();

    List<PriorityOrderAttrListVo> getAttrValue(@Param("attr1") Integer zokuseiId,@Param("ptsCd")Integer ptsCd);

    List<PriorityOrderAttrVO> getAttrValue5(@Param("attr1") Integer a1cd,@Param("ptsCd")Integer ptsCd);

    //商品分類属性構造の取得
    List<PriorityOrderAttrValue> getGoodsAttrTree();

    //商品分類以外の属性数
    List<PriorityOrderAttrValueVo> getAttr();

    //zookuseiIdに基づいて対応する属性と属性タイプを取得する
    List<PriorityOrderAttrValue> getAttrValues(@Param("zokuseiId") Integer zokuseiId);


    //対応する属性に基づいて対応するfaceを取得する
    Integer getfeceNum(@Param("janCol1") String janCol1, @Param("janCol2") String janCol2, @Param("attrValue1") String attrValue1, @Param("attrValue2") String attrValue2, @Param("patternCd") Integer patternCd);
    //対応する属性に基づいて対応するfaceを取得する
    List<PriorityOrderAttrFaceNum> getfeceNum1(@Param("janCol1") String janCol1, @Param("janCol2") String janCol2,@Param("patternCd") Integer patternCd);
    //zookuseiIdに基づいて対応するcolを取得する
    String getAttrCol(@Param("zokuseiId") Integer zokuseiId);
    //ワークテーブルのプロパティ1/2に対応する面積を取得

    List<PriorityOrderAttrVO> getEditAttributeArea(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd);


    //つかむ取列名
    String getColNmforMst(@Param("companyCd")String companyCd,@Param("authorCd")String authorCd,@Param("priorityOrderCd")Integer priorityOrderCd);

    int deleteAttrFinal(String companyCd, Integer priorityOrderCd);

    void insertAttrFinal(String companyCd, Integer priorityOrderCd);
    void insertAttrSortFinal(String companyCd, Integer priorityOrderCd);

    void deleteAttrSortFinal(String companyCd, Integer priorityOrderCd);
}
