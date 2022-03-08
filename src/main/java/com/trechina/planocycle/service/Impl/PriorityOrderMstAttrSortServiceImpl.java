package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.ShelfPtsDataTanaCount;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.PriorityOrderRestrictSet;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrListVo;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrVO;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrValue;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrValueVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderMstAttrSortMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataTanamstMapper;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

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
        logger.info("保存优先顺位表排序的参数" + priorityOrderMstAttrSort);
        if (priorityOrderMstAttrSort.size() > 0) {
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
            if (priorityOrderAttrListVo.getAttrCd()!=0) {
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
            for (PriorityOrderAttrListVo priorityOrderAttrListVo : attrValue) {
                for (PriorityOrderAttrListVo orderAttrListVo : attrValue1) {
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

        Collections.sort(attrList, new Comparator<PriorityOrderAttrVO>() {
            @Override
            public int compare(PriorityOrderAttrVO o1, PriorityOrderAttrVO o2) {

                return o2.getExistingZoning().compareTo(o1.getExistingZoning());
            }
        });
        int i = 1;
        for (PriorityOrderAttrVO priorityOrderAttrVO : attrList) {
            priorityOrderAttrVO.setRank(i++);
        }
        logger.info("属性所有组合为：{}", attrList);
        return ResultMaps.result(ResultEnum.SUCCESS, attrList);
    }

    private List<PriorityOrderRestrictSet> packageRestrict(int begin, int end, Integer taiCd, String attrACd) {
        List<PriorityOrderRestrictSet> restrictSetList = new ArrayList<>();
        PriorityOrderRestrictSet restrictSet = null;
        if (begin < end) {
            for (int i = begin; i < end; i++) {
                restrictSet = new PriorityOrderRestrictSet();
                restrictSet.setAuthorCd(attrACd);
                restrictSet.setTaiCd(taiCd);
                restrictSet.setTanaCd(i + 1);
                restrictSetList.add(restrictSet);
            }
        } else {
            restrictSet = new PriorityOrderRestrictSet();
            restrictSet.setAuthorCd(attrACd);
            restrictSet.setTaiCd(taiCd);
            restrictSet.setTanaCd(0);
            restrictSetList.add(restrictSet);
        }
        return restrictSetList;
    }

    @Override
    public List<PriorityOrderRestrictSet> setRestrict(List<PriorityOrderAttrVO> dataList, List<ShelfPtsDataTanaCount> tanaCountList) {
//        Date now = Calendar.getInstance().getTime();
//        String authorCd = httpSession.getAttribute("aud").toString();
//        // 1.保存列表
//        // 2.将信息进行拆分(将商品放到棚上，通过外循环商品，内循环台的方式)
//        Integer ptsCd = 48;
//        List<ShelfPtsDataTanaCount> tanaCountList = shelfPtsDataTanamstMapper.ptsTanaCountByTai(ptsCd);
        List<PriorityOrderRestrictSet> restrictSetList = new ArrayList<>();
        Integer pattan = 0;
        PriorityOrderRestrictSet restrictSet = null;
        String attrACd = null;
        for (PriorityOrderAttrVO vo : dataList) {
            // 商品数
            pattan = vo.getTanaPattan();
            attrACd = vo.getAttrACd();

            for (ShelfPtsDataTanaCount tanaCount : tanaCountList) {
                // 判断台是否有空余
                if ((tanaCount.getTanaCount() - tanaCount.getTanaUsedCount()) > 0) {

//                    int notUsedCount = 0;
//                    if (tanaCount.getTanaUsedCount() == 0) {
//                        notUsedCount = tanaCount.getTanaCount();
//                    }else{
//                        notUsedCount = tanaCount.getTanaCount() - tanaCount.getTanaUsedCount();
//                    }
                    if (tanaCount.getTanaUsedCount() == 0) {
                        // 空台
                        if (pattan >= tanaCount.getTanaCount()) {
                            // 商品占用的棚数可以占满台
                            pattan -= tanaCount.getTanaCount();
                            tanaCount.setTanaUsedCount(tanaCount.getTanaCount());
                            //## 整台制约条件
                            restrictSetList.addAll(packageRestrict(0, 0, tanaCount.getTaiCd(), attrACd));
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
                            restrictSetList.addAll(packageRestrict(0, pattan, tanaCount.getTaiCd(), attrACd));
                            // 跳出循环，继续下一个商品
                            break;
                        }
                    } else {
                        // 台已经放了部分商品了
                        if (pattan >= (tanaCount.getTanaCount() - tanaCount.getTanaUsedCount())) {
                            // 商品占用的棚数可以占满台
                            //## 段制约条件：从上一次接着放
                            restrictSetList.addAll(packageRestrict(tanaCount.getTanaUsedCount(), tanaCount.getTanaCount(), tanaCount.getTaiCd(), attrACd));
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
                            restrictSetList.addAll(packageRestrict(tanaCount.getTanaUsedCount(), tanaCount.getTanaUsedCount() + pattan, tanaCount.getTaiCd(), attrACd));
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
