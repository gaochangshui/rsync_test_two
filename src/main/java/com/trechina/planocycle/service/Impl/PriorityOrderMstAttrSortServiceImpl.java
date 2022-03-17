package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.PriorityOrderSpaceDto;
import com.trechina.planocycle.entity.dto.ShelfPtsDataTanaCount;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictSet;
import com.trechina.planocycle.entity.po.WorkPriorityOrderSpace;
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
     * 获取既存数据的排序
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityAttrSort(String companyCd, Integer priorityOrderCd) {
        List<PriorityOrderMstAttrSort> resultInfo = priorityOrderMstAttrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Map<String, Object>> result = new ArrayList<>();
        resultInfo.forEach(item -> {
            Map<String, Object> maps = new HashMap<>();
            if (item.getCd() == 13 && item.getValue() == resultInfo.size()) {
                maps.put("value", "mulit_attr");
            } else {
                maps.put("value", item.getValue().toString());
            }
            maps.put("cd", item.getCd().toString());
            maps.put("sort", item.getSort());
            result.add(maps);
        });
        return ResultMaps.result(ResultEnum.SUCCESS, result);
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
        logger.info("保存优先顺位表排序的参数{}", priorityOrderMstAttrSort);
        if (!priorityOrderMstAttrSort.isEmpty()) {
            priorityOrderMstAttrSortMapper.deleteByPrimaryKey(priorityOrderMstAttrSort.get(0).getCompanyCd(), priorityOrderMstAttrSort.get(0).getPriorityOrderCd());
            priorityOrderMstAttrSortMapper.insert(priorityOrderMstAttrSort);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 删除数据的排序
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
     * 获取属性1和属性2
     */
    @Override
    public Map<String, Object> getAttribute() {
        List<PriorityOrderAttrListVo> attributeList = priorityOrderMstAttrSortMapper.getAttribute();
        return ResultMaps.result(ResultEnum.SUCCESS, attributeList);
    }

    /**
     * 陈列设定获取属性1和属性2
     */
    @Override
    public Map<String, Object> getAttributeSort() {
        List<PriorityOrderAttrListVo> attributeList = priorityOrderMstAttrSortMapper.getAttributeSort();
        return ResultMaps.result(ResultEnum.SUCCESS, attributeList);
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


        return ResultMaps.result(ResultEnum.SUCCESS, attr);
    }


    /**
     * 获取属性1属性2组合对应的面积
     */
    @Override
    public Map<String, Object> getAttributeArea(Integer patternCd, Integer attr1, Integer attr2) {
        Integer faceNum = shelfPtsDataMapper.getFaceNum(patternCd);
        int attrType1 = priorityOrderMstAttrSortMapper.getAttrType(attr1);
        int attrType2 = priorityOrderMstAttrSortMapper.getAttrType(attr2);
        List<PriorityOrderAttrVO> attrList = new ArrayList<>();
        if (attrType1 == 1 && attrType2 == 1) {

            int attrSort = priorityOrderMstAttrSortMapper.getAttrSort(attr1);
            int attrSort1 = priorityOrderMstAttrSortMapper.getAttrSort(attr2);
            List<PriorityOrderAttrVO> attrList1 = priorityOrderMstAttrSortMapper.getAttrValue5(attrSort);
            List<PriorityOrderAttrVO> attrList2 = priorityOrderMstAttrSortMapper.getAttrValue5(attrSort1);
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
                            attrList.add(priorityOrderAttr);
                            Integer facenum = priorityOrderMstAttrSortMapper.getfeceNum(priorityOrderAttrVO.getJansAColnm(), orderAttrVO.getJansAColnm(), priorityOrderAttrVO.getAttrACd(), orderAttrVO.getAttrACd(), patternCd);
                            if (facenum != null) {
                                Integer result = facenum * 100 / faceNum;
                                priorityOrderAttrVO.setNewZoning(result);
                                priorityOrderAttrVO.setExistingZoning(result);
                            } else {
                                priorityOrderAttrVO.setNewZoning(0);
                                priorityOrderAttrVO.setExistingZoning(0);
                            }
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
            List<PriorityOrderAttrListVo> attrValue = priorityOrderMstAttrSortMapper.getAttrValue(attr2);
            List<PriorityOrderAttrListVo> attrValue1 = priorityOrderMstAttrSortMapper.getAttrValue(attr1);

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
        Collections.sort(attrList, (o1, o2) -> o2.getExistingZoning().compareTo(o1.getExistingZoning()));
        int i = 1;
        for (PriorityOrderAttrVO priorityOrderAttrVO : attrList) {
            priorityOrderAttrVO.setRank(i++);
        }
        logger.info("属性所有组合为：{}", attrList);
        return ResultMaps.result(ResultEnum.SUCCESS, attrList);
    }

    @Override
    public Map<String, Object> setAttribute(PriorityOrderSpaceDto dto) {

        String authorCd = httpSession.getAttribute("aud").toString();
        String companyCd = dto.getCompanyCd();
        Long shelfPatternCd = dto.getPatternCd();
        // 1.保存mst
        workPriorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, dto.getPriorityOrderCd());
        WorkPriorityOrderMst orderMst = new WorkPriorityOrderMst();
        orderMst.setCompanyCd(companyCd);
        orderMst.setAuthorCd(authorCd);
        orderMst.setShelfPatternCd(shelfPatternCd);
        orderMst.setAttribute1(dto.getAttr1());
        orderMst.setAttribute2(dto.getAttr2());
        orderMst.setPriorityOrderCd(dto.getPriorityOrderCd());
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


        // 3.space转化为制约条件
        workPriorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, dto.getPriorityOrderCd());
        List<ShelfPtsDataTanaCount> tanaCountList = shelfPtsDataTanamstMapper.ptsTanaCountByTai(shelfPatternCd);
        List<WorkPriorityOrderRestrictSet> restrictSetList = new ArrayList<>();
        try {
            restrictSetList = this.setRestrict(dataList, tanaCountList, dto.getAttr1(), dto.getAttr2(), companyCd, authorCd, dto.getPriorityOrderCd());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        if (!restrictSetList.isEmpty()) {
            workPriorityOrderRestrictSetMapper.insertAll(restrictSetList);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


    @Override
    public Map<String, Object> getEditAttributeArea(String companyCd) {
        String authorCd = httpSession.getAttribute("aud").toString();
        List<PriorityOrderAttrVO> editAttributeArea = priorityOrderMstAttrSortMapper.getEditAttributeArea(companyCd, authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS, editAttributeArea);
    }

    private List<WorkPriorityOrderRestrictSet> packageRestrict(int begin, int end, Integer taiCd, WorkPriorityOrderRestrictSet tmpRestrictSet) {
        List<WorkPriorityOrderRestrictSet> restrictSetList = new ArrayList<>();
        WorkPriorityOrderRestrictSet restrictSet = null;
        if (begin < end) {
            for (int i = begin; i < end; i++) {
                restrictSet = new WorkPriorityOrderRestrictSet();
                BeanUtils.copyProperties(tmpRestrictSet, restrictSet);
                restrictSet.setTaiCd(taiCd);
                restrictSet.setTanaCd(i + 1);
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


    @Override
    public List<WorkPriorityOrderRestrictSet> setRestrict(List<PriorityOrderAttrVO> dataList, List<ShelfPtsDataTanaCount> tanaCountList,
                                                          Short attr1, Short attr2, String companyCd, String authorCd, Integer priorityOrderCd)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<WorkPriorityOrderRestrictSet> restrictSetList = new ArrayList<>();
        Integer pattan = 0;

        Class<?> clazz = WorkPriorityOrderRestrictSet.class;
        Method setAttrMethod1 = clazz.getMethod("setZokusei" + attr1, String.class);
        Method setAttrMethod2 = clazz.getMethod("setZokusei" + attr2, String.class);
        // 创建一个设置对应属性的公共对象
        WorkPriorityOrderRestrictSet tmpRestrictSet = null;

        // 按照rank排序
        dataList = dataList.stream().sorted(Comparator.comparing(PriorityOrderAttrVO::getRank)).collect(Collectors.toList());
        for (PriorityOrderAttrVO vo : dataList) {
            // 商品数
            pattan = vo.getTanaPattan();
            if (pattan == null || pattan == 0) {
                // 属性不占段
                continue;
            }
            // 创建属性制约的临时值
            tmpRestrictSet = new WorkPriorityOrderRestrictSet();
            tmpRestrictSet.setCompanyCd(companyCd);
            tmpRestrictSet.setAuthorCd(authorCd);
            tmpRestrictSet.setPriorityOrderCd(priorityOrderCd);
            tmpRestrictSet.setTanaType((short) 0);
            setAttrMethod1.invoke(tmpRestrictSet, vo.getAttrACd());
            setAttrMethod2.invoke(tmpRestrictSet, vo.getAttrBCd());

            for (ShelfPtsDataTanaCount tanaCount : tanaCountList) {
                // 判断台是否有空余
                if ((tanaCount.getTanaCount() - tanaCount.getTanaUsedCount()) > 0) {
                    if (tanaCount.getTanaUsedCount() == 0) {
                        // 空台（台上什么属性也没有）
                        if (pattan >= tanaCount.getTanaCount()) {
                            // 商品占用的棚数可以占满台
                            pattan -= tanaCount.getTanaCount();
                            tanaCount.setTanaUsedCount(tanaCount.getTanaCount());
                            //## 整台制约条件
                            restrictSetList.addAll(packageRestrict(0, 0, tanaCount.getTaiCd(), tmpRestrictSet));
                            if (pattan > 0) {
                                // 商品还有，继续下一个台
                                continue;
                            } else {
                                // 商品没有，继续下一个商品
                                break;
                            }
                        } else {
                            // 商品占用部分台
                            tanaCount.setTanaUsedCount(pattan);
                            //## 段制约条件
                            restrictSetList.addAll(packageRestrict(0, pattan, tanaCount.getTaiCd(), tmpRestrictSet));
                            // 跳出循环，继续下一个商品
                            break;
                        }
                    } else {
                        // 台已经放了部分商品了
                        if (pattan >= (tanaCount.getTanaCount() - tanaCount.getTanaUsedCount())) {
                            // 商品占用的棚数可以占满台
                            //## 段制约条件：从上一次接着放
                            restrictSetList.addAll(packageRestrict(tanaCount.getTanaUsedCount(), tanaCount.getTanaCount(), tanaCount.getTaiCd(), tmpRestrictSet));
                            pattan -= (tanaCount.getTanaCount() - tanaCount.getTanaUsedCount());
                            tanaCount.setTanaUsedCount(tanaCount.getTanaCount());
                            if (pattan > 0) {
                                // 商品还有，继续下一个台
                                continue;
                            } else {
                                // 商品没有，继续下一个商品
                                break;
                            }
                        } else {
                            // 商品占用部分台
                            //## 段制约条件：从上一次接着放
                            restrictSetList.addAll(packageRestrict(tanaCount.getTanaUsedCount(), tanaCount.getTanaUsedCount() + pattan, tanaCount.getTaiCd(), tmpRestrictSet));
                            tanaCount.setTanaUsedCount(tanaCount.getTanaUsedCount() + pattan);
                            // 跳出循环，继续下一个商品
                            break;
                        }
                    }
                } else {
                    // 台满了，直接跳过
                    continue;
                }
            }
        }
        return restrictSetList;
    }
}
