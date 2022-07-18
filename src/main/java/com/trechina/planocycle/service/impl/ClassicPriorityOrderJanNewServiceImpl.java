package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.trechina.planocycle.entity.dto.PriorityOrderMstAttrSortDto;
import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanNew;
import com.trechina.planocycle.entity.po.Jans;
import com.trechina.planocycle.entity.po.PriorityOrderJanAttribute;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanNewVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderDataService;
import com.trechina.planocycle.service.ClassicPriorityOrderJanNewService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderJanNewServiceImpl implements ClassicPriorityOrderJanNewService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private ClassicPriorityOrderJanAttributeMapper priorityOrderJanAttributeMapper;
    @Autowired
    private ClassicPriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private ClassicPriorityOrderCatepakAttributeMapper priorityOrderCatepakAttributeMapper;
    @Autowired
    private ClassicPriorityOrderDataService priorityOrderDataService;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper classicPriorityOrderMstAttrSortMapper;
    @Autowired
    private WorkPriorityOrderPtsClassifyMapper workPriorityOrderPtsClassifyMapper;

    /**
     * 新規商品リストの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd) {
        try {
            //列ヘッダーの取得
            List<PriorityOrderMstAttrSortDto> priorityOrderMstAttrSorts = priorityOrderMstAttrSortMapper.selectWKRankSort(companyCd, priorityOrderCd);
            Map<String, String> attrMap = priorityOrderMstAttrSorts.stream()
                    .collect(Collectors.toMap(PriorityOrderMstAttrSortDto::getSort, PriorityOrderMstAttrSortDto::getName,
                            (k1,k2)->k1, LinkedHashMap::new));
            Map<String, String> results = new LinkedHashMap<>();
            results.put("janNew", "新JAN");
            results.put("janName", "商品名");
            results.putAll(attrMap);
            results.put("rank", "Rank");
            results.put("branchNum", "配荷店舗数");
            results.put("branchAccount", "想定店金額");
            results.put("errMsg", "エラーメッセージ");

            logger.info("新しい商品リストパラメータを取得する：{},{}",companyCd, priorityOrderCd);
            List<ClassicPriorityOrderJanNewVO> priorityOrderJanNewVOS = priorityOrderJanNewMapper.selectJanNew(companyCd, priorityOrderCd);
            logger.info("新規商品listリターン結菓セットbを取得する：{}", priorityOrderJanNewVOS);
            JSONArray jsonArray = new JSONArray();

            // 結菓セットを巡回し、動的列を分割する
            if (!priorityOrderJanNewVOS.isEmpty()) {
                List<String> janNewList = priorityOrderJanNewVOS.stream().map(ClassicPriorityOrderJanNewVO::getJanNew).collect(Collectors.toList());
                Map<String, String> errorJan = priorityOrderDataService.checkIsJanNew(janNewList, companyCd, priorityOrderCd, "");

                priorityOrderJanNewVOS.forEach(item -> {
                    Map<String, Object> result = new HashMap<>();
                    String[] attrList = item.getAttr().split(",");
                    String[] valList;
                    result.put("janNew", item.getJanNew());
                    result.put("janName", item.getJanName());
                    for (int i = 0; i < attrList.length; i++) {
                        valList = attrList[i].split(":");
                        if(valList.length<2){
                            result.put("attr" + valList[0], "");
                        }else{
                            result.put("attr" + valList[0], valList[1]);
                        }
                    }
                    result.put("rank", item.getRank());
                    result.put("branchNum", item.getBranchNum());
                    result.put("branchAccount", item.getBranchAccount());
                    result.put("errMsg", errorJan.getOrDefault(item.getJanNew(), ""));
                    //jsonArrayに書き込み
                    jsonArray.add(result);
                });
                //動的列名を下付き0に書き、先頭に動的列を生成させる
                jsonArray.add(0, results);

            } else {
                jsonArray.add(results);
            }
            return ResultMaps.result(ResultEnum.SUCCESS, jsonArray);
        } catch (Exception e) {
            logger.info("新規janListの取得に失敗しました：",e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 新規商品リストの保存
     *
     * @param jsonArray
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanNew(JSONArray jsonArray) {
        //try {
        logger.info("新しい商品リストパラメータを保存する:{}" ,jsonArray);
        List<ClassicPriorityOrderJanNew> janNewList = new ArrayList<>();
        List<PriorityOrderJanAttribute> janAttributeList = new ArrayList<>();
        //パラメータの最初の行の企業と優先順位番号を取得します。
        String companyCd = String.valueOf(((HashMap) jsonArray.get(0)).get("companyCd"));
        Integer priorityOrderCd =Integer.valueOf(String.valueOf(((HashMap) jsonArray.get(0)).get("priorityOrderCd")));
        // 全削除
        priorityOrderJanNewMapper.delete(companyCd, priorityOrderCd);
        priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);

        // フロントエンドから与えられたjsonArrayを巡回して、新しい商品リストと関連する動的属性リストのエンティティークラス文字列を構築し、データを保存します。
        List<Map<String,Object>> janNew = new Gson().fromJson(jsonArray.toJSONString(), new TypeToken< List<Map<String,Object>>>(){}.getType());
        janNew=  janNew.stream().filter(map->map.get("janNew")!=null &&!"".equals(map.get("janNew"))).collect(Collectors.toList());
        for (Map<String, Object> objectMap : janNew) {
            objectMap.put("rank",Double.valueOf(objectMap.get("rank").toString()).intValue());
        }
        if (janNew.isEmpty()){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        List<Map<String,Object>> list = new ArrayList();
        this.setBranchNum(janNew,priorityOrderCd,companyCd,list);

        dataSave(janNew, janNewList, janAttributeList, companyCd, priorityOrderCd,list);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    public void  setBranchNum(List<Map<String,Object>> janNew,Integer priorityOrderCd,String companyCd,List<Map<String,Object>> list){
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrSortList(companyCd, priorityOrderCd);
        List<Map<String, Object>> result = priorityOrderDataMapper.getDataNotExistNewJan(priorityOrderCd);
        for (Map<String, Object> objectMap : result) {
            objectMap.put("janNew", objectMap.get("jan_new"));
        }
        for (Map<String, Object> item : janNew) {
            item.put("jan_new",item.get("janNew"));
            item.put("rank_upd",item.get("rank"));
            item.put("rank",-1);
            result.add(item);
        }
        result = priorityOrderDataService.calRank(result, attrList);
        for (Map<String, Object> objectMap : result) {
            for (Map<String, Object> item : janNew) {
                if (item.get("janNew").equals(objectMap.get("janNew")) && objectMap.get("rank").toString().equals("-1")){
                    item.put("rank",objectMap.get("rank_upd"));
                }
            }
        }

        for (Map<String, Object> jan : janNew) {
            if (jan.get("janNew") != null) {

                Map<String, Object> map = new HashMap();
                map.put("jan_new", jan.get("janNew"));
                jan.put("rank_upd", jan.get("rank"));
                List<Integer> ptsCd = workPriorityOrderPtsClassifyMapper.getJanPtsCd(companyCd, priorityOrderCd, jan);
                if (ptsCd.isEmpty()) {
                    map.put("branch_num", 0);
                } else {
                    Integer branchNum = workPriorityOrderPtsClassifyMapper.getJanBranchNum(ptsCd, jan);
                    map.put("branch_num", branchNum);
                }
                list.add(map);
            }
        }
    }

    /**
     * 新しい商品リストを削除
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delriorityOrderJanNewInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanNewMapper.delete(companyCd, priorityOrderCd);
    }

    @Override
    public Map<String, Object> getSimilarity(Map<String, Object> map) {
        String companyCd = map.get("companyCd").toString();
        Integer priorityOrderCd = Integer.valueOf(map.get("priorityOrderCd").toString());
        List<Map<String,Object>> data = (List<Map<String,Object>>)map.get("data");
        Set<Map<String,Object>> list = new HashSet<>();
        List<Map<String,Object>> errorMsgJan = priorityOrderJanNewMapper.getErrorMsgJan(companyCd, priorityOrderCd);
        for (Map<String, Object> map1 : data) {
            for (Map<String, Object> s : errorMsgJan) {
                if (map1.get("janNew").equals(s.get("jan_new"))) {
                    map1.put("errMsg", s.get("errMsg"));
                    list.add(map1);

                }
            }
        }
        Map<String, Object> objectMap1 = new HashMap<>();

        objectMap1.putAll(data.get(0));
        objectMap1.remove("janNew");
        objectMap1.remove("janName");
        objectMap1.remove("rank");
        objectMap1.remove("branchNum");
        objectMap1.remove("branchAccount");
        objectMap1.remove("errMsg");
        objectMap1.remove("target");
        objectMap1.remove("companyCd");
        objectMap1.remove("priorityOrderCd");
        map.remove("companyCd");
        map.remove("priorityOrderCd");
        map.remove("janNew");
        List<Map<String, Object>> similarity = priorityOrderJanNewMapper.getSimilarity(objectMap1,companyCd,priorityOrderCd);
        for (Map<String, Object> objectMap : similarity) {
            objectMap.put("janNew",objectMap.get("jan_new"));
            objectMap.put("janName",objectMap.get("sku"));
            objectMap.put("rank_upd",objectMap.get("rank"));
            objectMap.remove("jan_new");
            objectMap.remove("sku");
        }
        for (Map<String, Object> objectMap : data) {
            objectMap.put("rank_upd",objectMap.get("rank"));
            objectMap.put("rank",-1);
            objectMap.put("errorMsg","");
        }
        if(!similarity.isEmpty()){
            similarity.addAll(data);
        }
        list.addAll(data);
        similarity = similarity.stream()
                .sorted(Comparator.comparing(map1 -> Integer.parseInt(map1.get("rank_upd").toString()))).collect(Collectors.toList());
        List<Map<String, Object>> list1 = priorityOrderDataService.calRank(similarity, new ArrayList<>());
        for (Map<String, Object> objectMap : list1) {
            objectMap.put("rank",objectMap.get("rank_upd"));
            objectMap.remove("rank_upd");
        }

        List list2 = new ArrayList();
        if (data.isEmpty()){
            list2.add(new ArrayList<>());
        }else {
            List<Map<String,Object>> listMap = new ArrayList<>();
            listMap.addAll(list1);
            for (Map<String, Object> objectMap : listMap) {
                objectMap.put("jan_new",objectMap.get("janNew"));
                objectMap.put("jan_old","_");
                objectMap.put("companyCd",companyCd);
                objectMap.put("priorityOrderCd",priorityOrderCd);
                objectMap.put("rank_upd",objectMap.get("rank"));
            }
            List<Map<String, Object>> branchNum = (List<Map<String, Object>>)priorityOrderDataService.getBranchNum(list1).get("data");
            for (Map<String, Object> objectMap : list1) {
                for (Map<String, Object> stringObjectMap : branchNum) {
                    if (objectMap.get("janNew").equals(stringObjectMap.get("jan_new"))){
                        objectMap.put("branchNum",stringObjectMap.get("branch_num_upd"));
                        objectMap.remove("jan_new");
                        objectMap.remove("jan_old");
                        objectMap.remove("companyCd");
                        objectMap.remove("priorityOrderCd");
                        objectMap.remove("rank_upd");
                    }
                }
            }
            list2.add(list1);
        }

        list2.add(list);

        return ResultMaps.result(ResultEnum.SUCCESS,list2);
    }

    private void dataSave(List<Map<String,Object>> jsonArray, List<ClassicPriorityOrderJanNew> janNewList,
                          List<PriorityOrderJanAttribute> janAttributeList, String companyCd,
                          Integer priorityOrderCd,List<Map<String,Object>> list) {

        for (int i = 0; i < jsonArray.size(); i++) {
            Map item =  jsonArray.get(i);
            // 构造jannew主表的参数
            if (item.get("janNew") != null) {
                janNew(janNewList, companyCd, priorityOrderCd,  jsonArray.get(i));
                // 构造jan动态属性列的参数
                janAttr(janAttributeList, companyCd, priorityOrderCd,  jsonArray.get(i));
            }
        }
        logger.info("保存新规商品list主表处理完后的参数：" + janNewList.toString());
        logger.info("保存新规商品list动态属性列处理完后的参数：" + janAttributeList.toString());
        List<String> janNews = janNewList.stream().map(ClassicPriorityOrderJanNew::getJanNew).collect(Collectors.toList());
        if (!janNews.isEmpty()) {
            List<Jans> janNewMst = priorityOrderJanNewMapper.getJanNewMst(janNews, companyCd);
            if (!janNews.isEmpty()) {
                for (Jans jans : janNewMst) {
                    for (ClassicPriorityOrderJanNew priorityOrderJanNew : janNewList) {
                        if (jans.getJan().equals(priorityOrderJanNew.getJanNew())) {
                            priorityOrderJanNew.setNameNew(jans.getJanname());
                        }
                    }
                }
            }
        }

        //全插入
        if (!janNewList.isEmpty()) {
            priorityOrderJanNewMapper.insert(janNewList);
            priorityOrderJanAttributeMapper.insert(janAttributeList);
            priorityOrderJanNewMapper.updateBranchNum(priorityOrderCd,list);
        }
    }



    /**
     * jannewマスターテーブルを構築するパラメータ
     *
     * @param janAttributeList
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     */
    private void janAttr(List<PriorityOrderJanAttribute> janAttributeList, String companyCd, Integer priorityOrderCd, Map item) {
        for (Object key : item.keySet()) {
            // 動態属性列表
            PriorityOrderJanAttribute priorityOrderJanAttribute = new PriorityOrderJanAttribute();
            priorityOrderJanAttribute.setCompanyCd(companyCd);
            priorityOrderJanAttribute.setPriorityOrderCd(priorityOrderCd);
            priorityOrderJanAttribute.setJanNew(String.valueOf(item.get("janNew")));
            if (key.toString().contains("attr")) {
                priorityOrderJanAttribute.setAttrCd(Integer.valueOf(key.toString().replace("attr", "")));
                priorityOrderJanAttribute.setAttrValue(String.valueOf(item.get(key)));
                janAttributeList.add(priorityOrderJanAttribute);

            }
        }
    }

    /**
     * jan動的属性列を構築するパラメータ
     *
     * @param janNewList
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     */
    private void janNew(List<ClassicPriorityOrderJanNew> janNewList, String companyCd, Integer priorityOrderCd, Map item) {
        // 新規商品リスト
        ClassicPriorityOrderJanNew priorityOrderJanNew = new ClassicPriorityOrderJanNew();
        priorityOrderJanNew.setCompanyCd(companyCd);
        priorityOrderJanNew.setPriorityOrderCd(priorityOrderCd);
        priorityOrderJanNew.setJanNew(String.valueOf(item.get("janNew")));
        priorityOrderJanNew.setRank(Integer.valueOf(String.valueOf(item.get("rank"))));
        priorityOrderJanNew.setBranchAccount(BigDecimal.valueOf(Double.parseDouble(String.valueOf(item.get("branchAccount")))));
        priorityOrderJanNew.setNameNew(String.valueOf(item.get("janName")));
        janNewList.add(priorityOrderJanNew);
    }
}
