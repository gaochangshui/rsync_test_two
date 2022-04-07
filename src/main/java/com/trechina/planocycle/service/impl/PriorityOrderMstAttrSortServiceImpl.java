package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.PriorityOrderSpaceDto;
import com.trechina.planocycle.entity.dto.ShelfPtsDataTanaCount;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrListVo;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrVO;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrValue;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrValueVo;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    public Map<String, Object> getAttribute() {
        List<PriorityOrderAttrListVo> attributeList = priorityOrderMstAttrSortMapper.getAttribute();
        return ResultMaps.result(ResultEnum.SUCCESS, attributeList);
    }

    /**
     * 陳列設定取得属性1と属性2
     */
    @Override
    public Map<String, Object> getAttributeSort() {
        List<PriorityOrderAttrListVo> attributeList = priorityOrderMstAttrSortMapper.getAttributeSort();
        return ResultMaps.result(ResultEnum.SUCCESS, attributeList);
    }

    /**
     * 属性の分類および商品分類リストの取得
     */
    @Override
    public Map<String, Object> getAttributeList() {

        List<PriorityOrderAttrValue> goodsAttrTree = priorityOrderMstAttrSortMapper.getGoodsAttrTree();
        PriorityOrderAttrValueVo priorityOrderAttrValueVo = new PriorityOrderAttrValueVo();
        priorityOrderAttrValueVo.setValues(goodsAttrTree);
        priorityOrderAttrValueVo.setAttrName("商品分類");
        priorityOrderAttrValueVo.setAttrCd(0);
        List<PriorityOrderAttrValueVo> attr = new ArrayList<>();
        attr.add(priorityOrderAttrValueVo);
        List<PriorityOrderAttrValueVo> attr1 = priorityOrderMstAttrSortMapper.getAttr();
        for (PriorityOrderAttrValueVo orderAttrValueVo : attr1) {
            attr.add(orderAttrValueVo);
        }

        for (PriorityOrderAttrValueVo priorityOrderAttrListVo : attr) {
            if (priorityOrderAttrListVo.getAttrCd() != 0) {
                List<PriorityOrderAttrValue> attrValues = priorityOrderMstAttrSortMapper.getAttrValues(priorityOrderAttrListVo.getAttrCd());
                priorityOrderAttrListVo.setValues(attrValues);
            }
        }

        Stream<PriorityOrderAttrValueVo> sorted = attr.stream().sorted(Comparator.comparing(PriorityOrderAttrValueVo::getAttrCd));
        return ResultMaps.result(ResultEnum.SUCCESS, sorted);
    }


    /**
     * 属性1属性2の組合せに対応する面積を取得
     */
    @Override
    public Map<String, Object> getAttributeArea(Integer patternCd, Integer attr1, Integer attr2) {
        Integer faceNum = shelfPtsDataMapper.getFaceNum(patternCd);
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(patternCd);
        int attrType1 = priorityOrderMstAttrSortMapper.getAttrType(attr1);
        int attrType2 = priorityOrderMstAttrSortMapper.getAttrType(attr2);
        List<PriorityOrderAttrVO> attrList = new ArrayList<>();
        if (attrType1 == 1 && attrType2 == 1) {

            int attrSort = priorityOrderMstAttrSortMapper.getAttrSort(attr1);
            int attrSort1 = priorityOrderMstAttrSortMapper.getAttrSort(attr2);
            List<PriorityOrderAttrVO> attrList1 = priorityOrderMstAttrSortMapper.getAttrValue5(attrSort, ptsCd);
            List<PriorityOrderAttrVO> attrList2 = priorityOrderMstAttrSortMapper.getAttrValue5(attrSort1, ptsCd);
            PriorityOrderAttrVO priorityOrderAttr = null;
            if (attrSort > attrSort1) {


                for (PriorityOrderAttrVO priorityOrderAttrVO : attrList2) {
                    for (PriorityOrderAttrVO orderAttrVO : attrList1) {
                        if (orderAttrVO.getAttrACd().startsWith(priorityOrderAttrVO.getAttrACd() + "_")) {
                            priorityOrderAttr = new PriorityOrderAttrVO();
                            priorityOrderAttr.setAttrBCd(priorityOrderAttrVO.getAttrACd());
                            priorityOrderAttr.setAttrBName(priorityOrderAttrVO.getAttrAName());
                            priorityOrderAttr.setJansBColnm(priorityOrderAttrVO.getJansAColnm());
                            priorityOrderAttr.setAttrACd(orderAttrVO.getAttrACd());
                            priorityOrderAttr.setAttrAName(orderAttrVO.getAttrAName());
                            priorityOrderAttr.setJansAColnm(orderAttrVO.getJansAColnm());

                            Integer facenum = priorityOrderMstAttrSortMapper.getfeceNum(priorityOrderAttrVO.getJansAColnm(), orderAttrVO.getJansAColnm(), priorityOrderAttrVO.getAttrACd(), orderAttrVO.getAttrACd(), patternCd);
                            if (facenum != null) {
                                Integer result = facenum * 100 / faceNum;
                                priorityOrderAttr.setNewZoning(result);
                                priorityOrderAttr.setExistingZoning(result);
                            } else {
                                priorityOrderAttr.setNewZoning(0);
                                priorityOrderAttr.setExistingZoning(0);
                            }
                            attrList.add(priorityOrderAttr);
                        }
                    }
                }

            }
            if (attrSort < attrSort1) {


                for (PriorityOrderAttrVO priorityOrderAttrVO : attrList1) {
                    for (PriorityOrderAttrVO orderAttrVO : attrList2) {
                        if (orderAttrVO.getAttrACd().startsWith(priorityOrderAttrVO.getAttrACd() + "_")) {
                            priorityOrderAttr = new PriorityOrderAttrVO();
                            priorityOrderAttr.setAttrACd(priorityOrderAttrVO.getAttrACd());
                            priorityOrderAttr.setAttrAName(priorityOrderAttrVO.getAttrAName());
                            priorityOrderAttr.setJansAColnm(priorityOrderAttrVO.getJansAColnm());
                            priorityOrderAttr.setAttrBCd(orderAttrVO.getAttrACd());
                            priorityOrderAttr.setAttrBName(orderAttrVO.getAttrAName());
                            priorityOrderAttr.setJansBColnm(orderAttrVO.getJansAColnm());

                            Integer facenum = priorityOrderMstAttrSortMapper.getfeceNum(priorityOrderAttrVO.getJansAColnm(), orderAttrVO.getJansAColnm(), priorityOrderAttrVO.getAttrACd(), orderAttrVO.getAttrACd(), patternCd);
                            if (facenum != null) {
                                Integer result = facenum * 100 / faceNum;
                                priorityOrderAttr.setNewZoning(result);
                                priorityOrderAttr.setExistingZoning(result);
                            } else {
                                priorityOrderAttr.setNewZoning(0);
                                priorityOrderAttr.setExistingZoning(0);
                            }
                            attrList.add(priorityOrderAttr);
                        }
                    }
                }

            }

        } else {

            List<PriorityOrderAttrListVo> attrValue = priorityOrderMstAttrSortMapper.getAttrValue(attr2, ptsCd);
            List<PriorityOrderAttrListVo> attrValue1 = priorityOrderMstAttrSortMapper.getAttrValue(attr1, ptsCd);


            PriorityOrderAttrVO priorityOrderAttrVO = null;
            for (PriorityOrderAttrListVo priorityOrderAttrListVo : attrValue1) {
                for (PriorityOrderAttrListVo orderAttrListVo : attrValue) {
                    priorityOrderAttrVO = new PriorityOrderAttrVO();
                    priorityOrderAttrVO.setAttrACd(priorityOrderAttrListVo.getAttrCd());
                    priorityOrderAttrVO.setAttrAName(priorityOrderAttrListVo.getAttrName());
                    priorityOrderAttrVO.setJansAColnm(priorityOrderAttrListVo.getJansColNm());
                    priorityOrderAttrVO.setAttrBCd(orderAttrListVo.getAttrCd());
                    priorityOrderAttrVO.setJansBColnm(orderAttrListVo.getJansColNm());
                    priorityOrderAttrVO.setAttrBName(orderAttrListVo.getAttrName());
                    Integer facenum = priorityOrderMstAttrSortMapper.getfeceNum(priorityOrderAttrListVo.getJansColNm(), orderAttrListVo.getJansColNm(), priorityOrderAttrListVo.getAttrCd(), orderAttrListVo.getAttrCd(), patternCd);
                    if (facenum != null) {
                        Integer result = facenum * 100 / faceNum;
                        priorityOrderAttrVO.setNewZoning(result);
                        priorityOrderAttrVO.setExistingZoning(result);
                    } else {
                        priorityOrderAttrVO.setNewZoning(0);
                        priorityOrderAttrVO.setExistingZoning(0);
                    }

                    attrList.add(priorityOrderAttrVO);
                }
            }

        }
        logger.info("attrList:{}", attrList);
        attrList.sort((o1, o2) -> o2.getExistingZoning().compareTo(o1.getExistingZoning()));
        int i = 1;
        for (PriorityOrderAttrVO priorityOrderAttrVO : attrList) {
            priorityOrderAttrVO.setRank(i++);
        }
        logger.info("属性所有組合はい：{}", attrList);
        return ResultMaps.result(ResultEnum.SUCCESS, attrList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setAttribute(PriorityOrderSpaceDto dto) {

        String authorCd = httpSession.getAttribute("aud").toString();
        String companyCd = dto.getCompanyCd();
        Long shelfPatternCd = dto.getPatternCd();
        // 1.保存mst
        Integer productPowerCd = workPriorityOrderMstMapper.getProductPowerCd(companyCd, dto.getPriorityOrderCd());
        workPriorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, dto.getPriorityOrderCd());
        WorkPriorityOrderMst orderMst = new WorkPriorityOrderMst();
        orderMst.setCompanyCd(companyCd);
        orderMst.setAuthorCd(authorCd);
        orderMst.setShelfPatternCd(shelfPatternCd);
        orderMst.setAttribute1(dto.getAttr1());
        orderMst.setAttribute2(dto.getAttr2());
        orderMst.setAreaNameCd(dto.getAreaNameCd());
        orderMst.setPriorityOrderCd(dto.getPriorityOrderCd());
        orderMst.setProductPowerCd(productPowerCd);
        workPriorityOrderMstMapper.insert(orderMst);

        // 2.保存space
        workPriorityOrderSpaceMapper.deleteByAuthorCd(companyCd, authorCd, dto.getPriorityOrderCd());
        List<PriorityOrderAttrVO> dataList = dto.getDataList();
        List<WorkPriorityOrderSpace> spaceList = new ArrayList<>();
        WorkPriorityOrderSpace space = null;
        for (PriorityOrderAttrVO vo : dataList) {
            space = new WorkPriorityOrderSpace();
            space.setCompanyCd(companyCd);
            space.setAuthorCd(authorCd);
            space.setAttribute1Value(vo.getAttrACd());
            space.setAttribute1Name(vo.getAttrAName());
            space.setAttribute2Value(vo.getAttrBCd());
            space.setAttribute2Name(vo.getAttrBName());
            space.setOldZoning(vo.getExistingZoning());
            space.setNewZoning(vo.getNewZoning());
            space.setTanaCount(vo.getTanaPattan());
            space.setZoningNum(vo.getRank());
            space.setPriorityOrderCd(dto.getPriorityOrderCd());
            spaceList.add(space);
        }
        if (!spaceList.isEmpty()) {
            workPriorityOrderSpaceMapper.insertAll(spaceList);
        }


        // 3.spaceが制約条件に変換
        workPriorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, dto.getPriorityOrderCd());
        List<ShelfPtsDataTanamst> ptsDataTanamstList = shelfPtsDataTanamstMapper.selectByPatternCd(shelfPatternCd);
        List<ShelfPtsDataTanaCount> tanaCountList = shelfPtsDataTanamstMapper.ptsTanaCountByTai(shelfPatternCd);
        List<WorkPriorityOrderRestrictSet> restrictSetList = new ArrayList<>();
        try {
            restrictSetList = this.setRestrict(dataList, ptsDataTanamstList, tanaCountList, dto.getAttr1(), dto.getAttr2(), companyCd, authorCd, dto.getPriorityOrderCd());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        if (!restrictSetList.isEmpty()) {
            workPriorityOrderRestrictSetMapper.insertAll(restrictSetList);
        }

        //ptsCdの取得
        Integer id = shelfPtsDataMapper.getId(companyCd, dto.getPriorityOrderCd());
        //クリアワーク_priority_order_pts_data
        shelfPtsDataMapper.deletePtsData(id);
        //クリアワーク_priority_order_pts_data_taimst
        shelfPtsDataMapper.deletePtsTaimst(id);
        //クリアワーク_priority_order_pts_data_tanamst
        shelfPtsDataMapper.deletePtsTanamst(id);
        //クリアワーク_priority_order_pts_data_version
        shelfPtsDataMapper.deletePtsVersion(id);
        //クリアワーク_priority_order_pts_data_jandata
        shelfPtsDataMapper.deletePtsDataJandata(id);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


    @Override
    public Map<String, Object> getEditAttributeArea(String companyCd) {
        String authorCd = httpSession.getAttribute("aud").toString();
        List<PriorityOrderAttrVO> editAttributeArea = priorityOrderMstAttrSortMapper.getEditAttributeArea(companyCd, authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS, editAttributeArea);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<WorkPriorityOrderRestrictSet> setRestrict(List<PriorityOrderAttrVO> dataList, List<ShelfPtsDataTanamst> ptsDataTanamstList, List<ShelfPtsDataTanaCount> tanaCountList,
                                                          Short attr1, Short attr2, String companyCd, String authorCd, Integer priorityOrderCd)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<WorkPriorityOrderRestrictSet> restrictSetList = new ArrayList<>();
        Integer pattan = 0;

        Class<?> clazz = WorkPriorityOrderRestrictSet.class;
        Method setAttrMethod1 = clazz.getMethod("setZokusei" + attr1, String.class);
        Method setAttrMethod2 = clazz.getMethod("setZokusei" + attr2, String.class);
        // 対応するプロパティを設定する共通オブジェクトを作成
        WorkPriorityOrderRestrictSet tmpRestrictSet = null;

        // rankでソート
        dataList = dataList.stream().sorted(Comparator.comparing(PriorityOrderAttrVO::getRank)).collect(Collectors.toList());
        for (PriorityOrderAttrVO vo : dataList) {
            // 商品数
            pattan = vo.getTanaPattan();
            if (pattan == null || pattan == 0) {
                // 属性はセグメントを占めません
                continue;
            }
            // 属性制約の一時値の作成
            tmpRestrictSet = new WorkPriorityOrderRestrictSet();
            tmpRestrictSet.setCompanyCd(companyCd);
            tmpRestrictSet.setAuthorCd(authorCd);
            tmpRestrictSet.setPriorityOrderCd(priorityOrderCd);
            tmpRestrictSet.setTanaType((short) 0);
            setAttrMethod1.invoke(tmpRestrictSet, vo.getAttrACd());
            setAttrMethod2.invoke(tmpRestrictSet, vo.getAttrBCd());

            for (ShelfPtsDataTanaCount tanaCount : tanaCountList) {
                // 台中棚の番号は連続ではなく、1,2,3,6,7の可能性があります
                Integer[] tanaCdArray = ptsDataTanamstList.stream()
                        .filter(obj -> tanaCount.getTaiCd().equals(obj.getTaiCd()))
                        .map(ShelfPtsDataTanamst::getTanaCd)
                        .sorted().toArray(Integer[]::new);
                // 判断台に空きがあるかどうか
                if ((tanaCount.getTanaCount() - tanaCount.getTanaUsedCount()) > 0) {
                    if (tanaCount.getTanaUsedCount() == 0) {
                        // 空き台(台には何の属性もありません)
                        if (pattan >= tanaCount.getTanaCount()) {
                            // 商品の占有棚数は満席となっております
                            pattan -= tanaCount.getTanaCount();
                            tanaCount.setTanaUsedCount(tanaCount.getTanaCount());
                            //## 全台制約条件
                            restrictSetList.addAll(packageRestrict(0, 0, tanaCdArray, tanaCount.getTaiCd(), tmpRestrictSet));
                            if (pattan <= 0) {
                                // 商品はあと、次の台に続きます
                                // 商品はございませんが、次の商品に続きます
                                break;
                            }
                        } else {
                            // 商品占有部分台
                            tanaCount.setTanaUsedCount(pattan);
                            //## セグメント制約条件
                            restrictSetList.addAll(packageRestrict(0, pattan, tanaCdArray, tanaCount.getTaiCd(), tmpRestrictSet));
                            // ループを飛び出して、次の商品を続けます
                            break;
                        }
                    } else {
                        // 台にはすでに一部の商品が置いてあります
                        if (pattan >= (tanaCount.getTanaCount() - tanaCount.getTanaUsedCount())) {
                            // 商品の占有棚数は満席となっております
                            //## 段制約条件：前回から続く
                            restrictSetList.addAll(packageRestrict(tanaCount.getTanaUsedCount(), tanaCount.getTanaCount(), tanaCdArray, tanaCount.getTaiCd(), tmpRestrictSet));
                            pattan -= (tanaCount.getTanaCount() - tanaCount.getTanaUsedCount());
                            tanaCount.setTanaUsedCount(tanaCount.getTanaCount());
                            if (pattan <= 0) {
                                // 商品はあと、次の台に続きます
                                // 商品はございませんが、次の商品に続きます
                                break;
                            }
                        } else {
                            // 商品占有部分台
                            //## 段制約条件：前回から続く
                            restrictSetList.addAll(packageRestrict(tanaCount.getTanaUsedCount(), tanaCount.getTanaUsedCount() + pattan, tanaCdArray, tanaCount.getTaiCd(), tmpRestrictSet));
                            tanaCount.setTanaUsedCount(tanaCount.getTanaUsedCount() + pattan);
                            // ループを飛び出して、次の商品を続けます
                            break;
                        }
                    }
                }
                // 台がいっぱいになったので,直接スキップした。
            }
        }
        return restrictSetList;
    }
}
