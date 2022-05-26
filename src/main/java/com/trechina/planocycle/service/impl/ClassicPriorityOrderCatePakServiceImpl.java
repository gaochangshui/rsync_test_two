package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.PriorityOrderMstAttrSortDto;
import com.trechina.planocycle.entity.po.PriorityOrderCatepak;
import com.trechina.planocycle.entity.po.PriorityOrderCatepakAttribute;
import com.trechina.planocycle.entity.vo.PriorityOrderCatePakVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ClassicPriorityOrderCatepakAttributeMapper;
import com.trechina.planocycle.mapper.ClassicPriorityOrderMstAttrSortMapper;
import com.trechina.planocycle.mapper.PriorityOrderCatepakMapper;
import com.trechina.planocycle.service.ClassicPriorityOrderCatePakService;
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
                            (k1,k2)->k1, LinkedHashMap::new));

            Map<String, String> colMap = new LinkedHashMap<>(attrMap.size());
            Map<String, String> smallMap = new LinkedHashMap<>(attrMap.size());
            Map<String, String> bigMap = new LinkedHashMap<>(attrMap.size());

            for (Map.Entry<String, String> entry : attrMap.entrySet()) {
                smallMap.put("attrSmall"+entry.getKey().replace("attr",""), entry.getValue());
                bigMap.put("attrBig"+entry.getKey().replace("attr",""), entry.getValue());
            }

            colMap.putAll(smallMap);
            colMap.put("rank","RANK");
            colMap.put("branchNum","店舗数");
            colMap.putAll(bigMap);

            logger.info("つかむ取カテパケ拡縮参数：{},{}",companyCd,priorityOrderCd);
            List<PriorityOrderCatePakVO> priorityOrderCatePakVOS = priorityOrderCatepakMapper.selectByPrimaryKey(companyCd,
                    priorityOrderCd);
            logger.info("つかむ取カテパケ拡縮結果集：{}",priorityOrderCatePakVOS);
            JSONArray jsonArray = new JSONArray();
            if (!priorityOrderCatePakVOS.isEmpty()) {
                // 遍暦結果集，拆分動態列
                priorityOrderCatePakVOS.forEach(item -> {
                    Map<String, Object> result = new HashMap<>();
                    String[] attrSmall = item.getSmalls().split(",");
                    String[] attrBig;
                    String[] valList;
                    // 遍暦small
                    for (int i = 0; i < attrSmall.length; i++) {
                        valList = attrSmall[i].split(":");
                        result.put("attrSmall" + valList[0], valList[1]);
                    }
                    result.put("rank", item.getRank());
                    result.put("branchNum", item.getBranchNum());
                    // big可以為空，需要判断一下
                    if (item.getBigs() != null && !item.getBigs().equals("") ) {
                        attrBig = item.getBigs().split(",");
                        for (int i = 0; i < attrBig.length; i++) {
                            valList = attrBig[i].split(":");
                            if (valList.length==1){
                                result.put("attrBig" + valList[0], "");
                            }else{
                                result.put("attrBig" + valList[0], valList[1]);
                            }
                        }
                    }
                    //写入jsonArray
                    jsonArray.add(result);
                });
                //把動態的列名写到下ひょう0，譲前端生成動態列
                jsonArray.add(0, colMap);
            } else {
                logger.info("つかむ取カテパケ拡縮結果集2：{}",attrMap);
                if (!attrMap.isEmpty()) {
                    List<String> list = new ArrayList<>();

                    for (int i = 0; i < attrMap.size(); i++) {
                        list.add("attrSmall" + i);
                        list.add("attrBig" + i);
                    }
                    list.add("rank");
                    list.add("branchNum");
                    jsonArray.add(list);
                }

            }
            logger.info("カテパケ拡縮結果{}", jsonArray);
            return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
        }catch (Exception e) {
            logger.info("つかむ取カテパケ拡縮失敗：",e);
            return ResultMaps.result(ResultEnum.FAILURE);
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
            logger.info("保存カテパケ拡縮参数:"+jsonArray);
            //つかむ取参数中第一行的企業和優先順位号
            String companyCd = String.valueOf(((HashMap) jsonArray.get(0)).get("companyCd"));
            Integer priorityOrderCd = Integer.parseInt(((HashMap) jsonArray.get(0)).get("priorityOrderCd").toString());
            // 全削
            priorityOrderCatepakMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            priorityOrderCatepakAttributeMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // 遍暦前端給的jsonArray 構筑カテパケ拡縮表和関連的動態属性列表的實体類字符串
            jsonArray.forEach(item->{
                // 構造主表的参数
                if (((HashMap) item).containsKey("rank") && ((HashMap) item).get("rank")!=null) {
                    PriorityOrderCatepak priorityOrderCatepak = new PriorityOrderCatepak();
                    if("".equals(((HashMap<?, ?>) item).get("branchNum"))){
                        priorityOrderCatepak.setBranchNum(null);
                    }
                    priorityOrderCatepak.setCompanyCd(companyCd);
                    priorityOrderCatepak.setPriorityOrderCd(priorityOrderCd);
                    priorityOrderCatepak.setRank(Integer.valueOf(((HashMap) item).get("rank").toString()));
//                priorityOrderCatepak.setBranchNum(Integer.valueOf(((HashMap) item).get("branchNum").toString()));
                    // 写入数据重新取号，返回自增列id，實体類自動接收
                    priorityOrderCatepakMapper.insert(priorityOrderCatepak);
                    logger.info("保存カテパケ拡縮返回:{}",  priorityOrderCatepak.toString());
                    catePakAttr(companyCd, priorityOrderCd, (HashMap) item, priorityOrderCatepak);
                }

            });
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("保存カテパケ拡縮報錯:"+e);
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
        return priorityOrderCatepakMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
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
        return priorityOrderCatepakAttributeMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }

    /**
     * 属性動的列の構築
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     * @param priorityOrderCatepak
     */
    private void catePakAttr(String companyCd, Integer priorityOrderCd, HashMap item, PriorityOrderCatepak priorityOrderCatepak) {
        List<PriorityOrderCatepakAttribute> attributeList = new ArrayList<>();

        String colValue = "";
        int idx =1;
        for (Object key: item.keySet()) {
            if (key.toString().contains("attrSmall") || key.toString().contains("attrBig")) {
                PriorityOrderCatepakAttribute catepakAttribute = new PriorityOrderCatepakAttribute();
                // 動的属性リスト
                catepakAttribute.setCompanyCd(companyCd);
                catepakAttribute.setPriorityOrderCd(priorityOrderCd);
                catepakAttribute.setCatepakCd(priorityOrderCatepak.getId());
                if (key.toString().contains("attrSmall")) {
                    //縮小は0
                    catepakAttribute.setFlg(0);
                    catepakAttribute.setAttrCd(Integer.valueOf(key.toString().replace("attrSmall", "")));
                    // レコード列名+値
                    colValue+="attr"+idx+"='"+item.get(key)+"',";
                    idx+=1;
                }
                if (key.toString().contains("attrBig")) {
                    //拡張は1です。
                    catepakAttribute.setFlg(1);
                    catepakAttribute.setAttrCd(Integer.valueOf(key.toString().replace("attrBig", "")));

                }
                catepakAttribute.setAttrValue(String.valueOf(item.get(key)));

                attributeList.add(catepakAttribute);

            }

        }
        priorityOrderCatepakAttributeMapper.insert(attributeList);
        branchNumSel(priorityOrderCatepak, colValue, idx);

    }

    private void branchNumSel(PriorityOrderCatepak priorityOrderCatepak, String colValue, int idx) {
        // 定番店舗数の照会
        colValue = colValue.replace(("attr"+(idx -1)),"mulit_attr");
        List<String> colValueList = Arrays.asList(colValue.split(","));
        String branchNum =  priorityOrderCatepakAttributeMapper.selectForTempTable(colValueList,
                "public.priorityorder"+session.getAttribute("aud").toString());
        logger.info("査詢定番店鋪数"+branchNum);
        if (branchNum!=null){
            priorityOrderCatepakMapper.updateBranchNum(priorityOrderCatepak.getId(),Integer.valueOf(branchNum));
        } else {
            priorityOrderCatepakMapper.updateBranchNum(priorityOrderCatepak.getId(),0);
        }
    }
}
