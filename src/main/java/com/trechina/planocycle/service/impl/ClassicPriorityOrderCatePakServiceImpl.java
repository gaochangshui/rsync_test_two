package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.PriorityOrderMstAttrSortDto;
import com.trechina.planocycle.entity.po.PriorityOrderCatepak;
import com.trechina.planocycle.entity.po.PriorityOrderCatepakAttribute;
import com.trechina.planocycle.entity.vo.PriorityOrderCatePakVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderCatePakService;
import com.trechina.planocycle.service.ClassicPriorityOrderDataService;
import com.trechina.planocycle.utils.CommonUtil;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderCatePakServiceImpl implements ClassicPriorityOrderCatePakService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderCatepakMapper priorityOrderCatepakMapper;
    @Autowired
    private ClassicPriorityOrderCatepakAttributeMapper priorityOrderCatepakAttributeMapper;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private ClassicPriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private ClassicPriorityOrderDataService classicPriorityOrderDataService;
    @Autowired
    private WorkPriorityOrderPtsClassifyMapper workPriorityOrderPtsClassifyMapper;
    @Autowired
    private LogAspect logAspect;

    /**
     * つかむ取カテパケ拡縮
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCatePak(String companyCd, Integer priorityOrderCd) {

        try {//つかむ取列頭
            List<PriorityOrderMstAttrSortDto> priorityOrderMstAttrSorts = priorityOrderMstAttrSortMapper.selectWKRankSort(companyCd, priorityOrderCd);
            Map<String, String> attrMap = priorityOrderMstAttrSorts.stream()
                    .collect(Collectors.toMap(PriorityOrderMstAttrSortDto::getSort, PriorityOrderMstAttrSortDto::getName,
                            (k1, k2) -> k1, LinkedHashMap::new));

            Map<String, String> colMap = new LinkedHashMap<>(attrMap.size());
            Map<String, String> smallMap = new LinkedHashMap<>(attrMap.size());
            Map<String, String> bigMap = new LinkedHashMap<>(attrMap.size());

            attrMap.forEach((key, value) -> {
                smallMap.put(MagicString.ATTR_SMALL + key.replace("attr", ""), value);
                bigMap.put(MagicString.ATTR_BIG + key.replace("attr", ""), value);
            });

            colMap.putAll(smallMap);
            colMap.put("rank", "RANK");
            colMap.put("branchNum", "店舗数");
            colMap.putAll(bigMap);

            logger.info("つかむ取カテパケ拡縮参数：{},{}", companyCd, priorityOrderCd);
            List<PriorityOrderCatePakVO> priorityOrderCatePakVOS = priorityOrderCatepakMapper.selectByPrimaryKey(companyCd,
                    priorityOrderCd);
            logger.info("つかむ取カテパケ拡縮結果集：{}", priorityOrderCatePakVOS);
            JSONArray jsonArray = new JSONArray();
            if (!priorityOrderCatePakVOS.isEmpty()) {
                // 遍暦結果集，拆分動態列
                priorityOrderCatePakVOS.forEach(item -> {
                    Map<String, Object> result = new HashMap<>();
//                    String[] attrSmall = item.getSmalls().split(",");
//                    String[] attrBig;
//                    String[] valList;
//                    // 遍暦small
//                    for (int i = 0; i < attrSmall.length; i++) {
//                        valList = attrSmall[i].split(":");
//                        result.put(MagicString.ATTR_SMALL + valList[0], valList[1]);
//                    }
                    this.splitCatePakAttr(item.getSmalls(), result, MagicString.ATTR_SMALL);
                    result.put("rank", item.getRank());
                    result.put("branchNum", item.getBranchNum());
                    // big可以為空，需要判断一下
//                    if (item.getBigs() != null && !item.getBigs().equals("")) {
                    if (!Strings.isNullOrEmpty(item.getBigs())) {
                        this.splitCatePakAttr(item.getBigs(), result, MagicString.ATTR_BIG);

//                        attrBig = item.getBigs().split(",");
//                        for (int i = 0; i < attrBig.length; i++) {
//                            valList = attrBig[i].split(":");
//                            if (valList.length == 1) {
//                                result.put(MagicString.ATTR_BIG + valList[0], "");
//                            } else {
//                                result.put(MagicString.ATTR_BIG + valList[0], valList[1]);
//                            }
//                        }
                    }
                    //写入jsonArray
                    jsonArray.add(result);
                });
                //把動態的列名写到下ひょう0，譲前端生成動態列
                jsonArray.add(0, colMap);
            } else {
                logger.info("つかむ取カテパケ拡縮結果集2：{}", attrMap);

                jsonArray.add(colMap);

            }
            logger.info("カテパケ拡縮結果{}", jsonArray);
            return ResultMaps.result(ResultEnum.SUCCESS, jsonArray);
        } catch (Exception e) {
            logger.info("つかむ取カテパケ拡縮失敗：", e);
            logAspect.setTryErrorLog(e,new Object[]{companyCd,priorityOrderCd});
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    private void splitCatePakAttr(String attrs, Map<String, Object> result, String attrKey){
        String[] valList;
        String[] attr = attrs.split(",");
        for (int i = 0; i < attr.length; i++) {
            valList = attr[i].split(":");
            if (valList.length == 1) {
                result.put(attrKey + valList[0], "");
            } else {
                result.put(attrKey + valList[0], valList[1]);
            }
        }
    }

    /**
     * 保存カテパケ拡縮
     *
     * @param jsonArray
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCatePak(JSONArray jsonArray) {
        try {
            logger.info("保存カテパケ拡縮参数:{}", jsonArray);

            String companyCd = String.valueOf(((HashMap) jsonArray.get(0)).get("companyCd"));
            Integer priorityOrderCd = Integer.parseInt(((HashMap) jsonArray.get(0)).get("priorityOrderCd").toString());
            priorityOrderCatepakMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);
            priorityOrderCatepakAttributeMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);
            jsonArray.forEach(item -> {

                if (((HashMap) item).containsKey("rank") && ((HashMap) item).get("rank") != null && !"".equals(((HashMap) item).get("rank"))) {
                    PriorityOrderCatepak priorityOrderCatepak = new PriorityOrderCatepak();
                    if ("".equals(((HashMap<?, ?>) item).get("branchNum"))) {
                        priorityOrderCatepak.setBranchNum(null);
                    }
                    priorityOrderCatepak.setCompanyCd(companyCd);
                    priorityOrderCatepak.setPriorityOrderCd(priorityOrderCd);
                    priorityOrderCatepak.setRank(Integer.valueOf(((HashMap) item).get("rank").toString()));
                    // データを書き込んで番号を取り直し、自増列IDを返し、エンティティークラスは自動的に受信する
                    priorityOrderCatepakMapper.insert(priorityOrderCatepak);
                    logger.info("タテパケ拡大戻り値の保存:{}", priorityOrderCatepak);
                    catePakAttr(companyCd, priorityOrderCd, (HashMap) item, priorityOrderCatepak);
                }

            });
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("カテパケ拡大縮小エラーの保存:", e);
            logAspect.setTryErrorLog(e,new Object[]{jsonArray});
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 削除カテパケ拡縮
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderCatePakInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderCatepakMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);
    }

    /**
     * 削除カテパケ拡縮属性
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderCatePakAttrInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderCatepakAttributeMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);
    }

    @Override
    public Map<String, Object> getCatePakSimilarity(Map<String, Object> map) {
        String companyCd = map.get("companyCd").toString();
        Integer priorityOrderCd = Integer.parseInt(map.get("priorityOrderCd").toString());
        map.remove("companyCd");
        map.remove("priorityOrderCd");
        Map<String, Object> maps = new HashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            String[] attrSmalls = stringObjectEntry.getKey().split(MagicString.ATTR_SMALL);
            maps.put("attr" + (Integer.parseInt(attrSmalls[1])), stringObjectEntry.getValue());

        }

        List<Map<String, Object>> catePakSimilarity = priorityOrderCatepakMapper.getCatePakSimilarity(priorityOrderCd, maps);

        List<Map<String, Object>> janNewList = priorityOrderJanNewMapper.getJanNewList(priorityOrderCd, maps, companyCd);
        Iterator<Map<String, Object>> iterator = catePakSimilarity.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            String janNew = next.get("janNew").toString();
            if (janNewList.stream().anyMatch(map1 -> map1.get("janNew").equals(janNew))) {
                iterator.remove();
            }
        }

        List<Map<String, Object>> list = CommonUtil.janSort(catePakSimilarity, janNewList, "rank");
        int i = 1;
        for (Map<String, Object> objectMap : list) {
            objectMap.put(MagicString.JAN_NEW, objectMap.get("janNew"));
            objectMap.put(MagicString.JAN_OLD, i++);
            objectMap.put("companyCd", companyCd);
            objectMap.put("priorityOrderCd", priorityOrderCd);
            objectMap.put(MagicString.RANK_UPD, objectMap.get("rank"));

        }
        List<Map<String, Object>> branchNum = (List<Map<String, Object>>) classicPriorityOrderDataService.getBranchNum(list).get("data");
        for (Map<String, Object> objectMap : list) {
            for (Map<String, Object> stringObjectMap : branchNum) {
                if (objectMap.get("janNew").equals(stringObjectMap.get(MagicString.JAN_NEW)) && objectMap.get(MagicString.JAN_OLD) == stringObjectMap.get(MagicString.JAN_OLD)) {
                    objectMap.put("branchNum", stringObjectMap.get("branch_num_upd"));
                    objectMap.put("rank", objectMap.get(MagicString.RANK_UPD));
                    objectMap.put("branchAccount", Double.valueOf(objectMap.get("branchAccount").toString()));
                    objectMap.remove(MagicString.JAN_NEW);
                    objectMap.remove(MagicString.JAN_OLD);
                    objectMap.remove("companyCd");
                    objectMap.remove("priorityOrderCd");
                    objectMap.remove(MagicString.RANK_UPD);
                }
            }
        }

        return ResultMaps.result(ResultEnum.SUCCESS, list);
    }

    /**
     * 属性動的列の構築
     *
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     * @param priorityOrderCatepak
     */
    private void catePakAttr(String companyCd, Integer priorityOrderCd, HashMap item, PriorityOrderCatepak priorityOrderCatepak) {
        List<PriorityOrderCatepakAttribute> attributeList = new ArrayList<>();

        List<String> colValueList = new ArrayList<>();
        for (Object key : item.keySet()) {
            if (key.toString().contains(MagicString.ATTR_SMALL) || key.toString().contains(MagicString.ATTR_BIG)) {
                PriorityOrderCatepakAttribute catepakAttribute = new PriorityOrderCatepakAttribute();
                // 動的属性リスト
                catepakAttribute.setCompanyCd(companyCd);
                catepakAttribute.setPriorityOrderCd(priorityOrderCd);
                catepakAttribute.setCatepakCd(priorityOrderCatepak.getId());
                if (key.toString().contains(MagicString.ATTR_SMALL)) {
                    //縮小は0
                    catepakAttribute.setFlg(0);
                    catepakAttribute.setAttrCd(Integer.valueOf(key.toString().replace(MagicString.ATTR_SMALL, "")));
                    // レコード列名+値

                    colValueList.add("attr" + catepakAttribute.getAttrCd() + "='" + item.get(key) + "'");
                }
                if (key.toString().contains(MagicString.ATTR_BIG)) {
                    //拡張は1です。
                    catepakAttribute.setFlg(1);
                    catepakAttribute.setAttrCd(Integer.valueOf(key.toString().replace(MagicString.ATTR_BIG, "")));

                }
                catepakAttribute.setAttrValue(String.valueOf(item.get(key)));

                attributeList.add(catepakAttribute);

            }

        }
        attributeList.forEach(map -> {
            if ("null".equals(map.getAttrValue())) {
                map.setAttrValue("");
            }
        });
        priorityOrderCatepakAttributeMapper.insert(attributeList);
        branchNumSel(priorityOrderCatepak, colValueList);

    }

    private void branchNumSel(PriorityOrderCatepak priorityOrderCatepak, List<String> colValueList) {
        // 定番店舗数の照会
        Integer priorityOrderCd = priorityOrderCatepak.getPriorityOrderCd();
        Integer rank = priorityOrderCatepak.getRank();
        List<Integer> ptsCd = priorityOrderCatepakAttributeMapper.selectForTempTable(colValueList,
                priorityOrderCd, rank);
        logger.info("pts:{}", ptsCd);
        if (!ptsCd.isEmpty()) {
            Integer branchNum = workPriorityOrderPtsClassifyMapper.getJanBranchNum(ptsCd, new HashMap<>());
            logger.info("査詢定番店鋪数{}", branchNum);
            priorityOrderCatepakMapper.updateBranchNum(priorityOrderCatepak.getId(), branchNum);
        } else {
            priorityOrderCatepakMapper.updateBranchNum(priorityOrderCatepak.getId(), 0);
        }
    }
}
