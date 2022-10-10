package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
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
import org.apache.commons.collections4.MapUtils;
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
    @Autowired
    private MstBranchMapper mstBranchMapper;
    @Autowired
    private LogAspect logAspect;

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
            results.put(MagicString.JANNEW, "新JAN");
            results.put(MagicString.JAN_NAME, "商品名");
            results.putAll(attrMap);
            results.put("rank", "Rank");
            results.put(MagicString.BRANCHNUM, "配荷店舗数");
            results.put(MagicString.BRANCH_ACCOUNT, "想定店金額");
            results.put(MagicString.ERROR_MSG, "エラーメッセージ");

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
                    result.put(MagicString.JANNEW, item.getJanNew());
                    result.put(MagicString.JAN_NAME, item.getJanName());
                    for (int i = 0; i < attrList.length; i++) {
                        valList = attrList[i].split(":");
                        if(valList.length<2){
                            result.put("attr" + valList[0], "");
                        }else{
                            result.put("attr" + valList[0], valList[1]);
                        }
                    }
                    result.put("rank", item.getRank());
                    result.put(MagicString.BRANCHNUM, item.getBranchNum());
                    result.put(MagicString.BRANCH_ACCOUNT, item.getBranchAccount());
                    result.put(MagicString.ERROR_MSG, errorJan.getOrDefault(item.getJanNew(), ""));
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
            logAspect.setTryErrorLog(e,new Object[]{companyCd,priorityOrderCd});
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
        
        logger.info("新しい商品リストパラメータを保存する:{}" ,jsonArray);
        List<ClassicPriorityOrderJanNew> janNewList = new ArrayList<>();
        List<PriorityOrderJanAttribute> janAttributeList = new ArrayList<>();
        //パラメータの最初の行の企業と優先順位番号を取得します。
        String companyCd = String.valueOf(((HashMap) jsonArray.get(0)).get(MagicString.COMPANY_CD));
        Integer priorityOrderCd =Integer.valueOf(String.valueOf(((HashMap) jsonArray.get(0)).get(MagicString.PRIORITY_ORDER_CD)));
        // 全削除
        priorityOrderJanNewMapper.delete(companyCd, priorityOrderCd);
        priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);

        // フロントエンドから与えられたjsonArrayを巡回して、新しい商品リストと関連する動的属性リストのエンティティークラス文字列を構築し、データを保存します。
        List<Map<String,Object>> janNew = new Gson().fromJson(jsonArray.toJSONString(), new TypeToken< List<Map<String,Object>>>(){}.getType());
        janNew=  janNew.stream().filter(map->map.get(MagicString.JANNEW)!=null &&!"".equals(map.get(MagicString.JANNEW))).collect(Collectors.toList());
        for (Map<String, Object> objectMap : janNew) {
            objectMap.put("rank",Double.valueOf(objectMap.get("rank").toString()).intValue());
        }
        if (janNew.isEmpty()){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        List<Map<String,Object>> list = new ArrayList<>();
        this.setBranchNum(janNew,priorityOrderCd,companyCd,list);

        dataSave(janNew, janNewList, janAttributeList, companyCd, priorityOrderCd,list);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    public void  setBranchNum(List<Map<String,Object>> janNew,Integer priorityOrderCd,String companyCd,List<Map<String,Object>> list){
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrSortList(companyCd, priorityOrderCd);
        List<Map<String, Object>> result = priorityOrderDataMapper.getDataNotExistNewJan(priorityOrderCd);
        for (Map<String, Object> objectMap : result) {
            objectMap.put(MagicString.JANNEW, objectMap.get(MagicString.JAN_NEW));
        }
        for (Map<String, Object> item : janNew) {
            item.put(MagicString.JAN_NEW,item.get(MagicString.JANNEW));
            item.put(MagicString.RANK_UPD,item.get("rank"));
            item.put("rank",-1);
            result.add(item);
        }
        result = priorityOrderDataService.calRank(result, attrList);
        for (Map<String, Object> objectMap : result) {
            for (Map<String, Object> item : janNew) {
                if (item.get(MagicString.JANNEW).equals(objectMap.get(MagicString.JANNEW)) && objectMap.get("rank").toString().equals("-1")){
                    item.put("rank",objectMap.get(MagicString.RANK_UPD));
                }
            }
        }

        janNew.stream().filter(jan->jan.get(MagicString.JANNEW)!=null).forEach(jan->{
            Map<String, Object> map = new HashMap<>();
            map.put(MagicString.JAN_NEW, jan.get(MagicString.JANNEW));
            jan.put(MagicString.RANK_UPD, jan.get("rank"));
            List<Integer> ptsCd = workPriorityOrderPtsClassifyMapper.getJanPtsCd(companyCd, priorityOrderCd, jan);
            if (ptsCd.isEmpty()) {
                map.put(MagicString.BRANCH_NUM, 0);
            } else {
                Integer branchNum = workPriorityOrderPtsClassifyMapper.getJanBranchNum(ptsCd, jan);
                map.put(MagicString.BRANCH_NUM, branchNum);
            }
            list.add(map);
        });
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
        String companyCd = map.get(MagicString.COMPANY_CD).toString();
        Integer priorityOrderCd = Integer.valueOf(map.get(MagicString.PRIORITY_ORDER_CD).toString());
        List<Map<String,Object>> data = (List<Map<String,Object>>)map.get("data");
        for (Map<String, Object> datum : data) {
            datum.putIfAbsent("rank",1);
        }
        data = data.stream().sorted(Comparator.comparing(map1 -> MapUtils.getString(map1,MagicString.JANNEW),Comparator.nullsFirst(String::compareTo).reversed()))
                .sorted(Comparator.comparing(map1 -> MapUtils.getInteger(map1,"rank"),Comparator.nullsFirst(Integer::compareTo))).collect(Collectors.toList());
        Set<Map<String,Object>> list = new HashSet<>();
        List<Map<String,Object>> errorMsgJan = priorityOrderJanNewMapper.getErrorMsgJan(companyCd, priorityOrderCd);
        data.forEach(map1-> errorMsgJan.forEach(s->{
            if (map1.get(MagicString.JANNEW).equals(s.get(MagicString.JAN_NEW))) {
                map1.put(MagicString.ERROR_MSG, s.get(MagicString.ERROR_MSG));
                list.add(map1);
            }
        }));
        Map<String, Object> objectMap1 = new HashMap<>();

        objectMap1.putAll(data.get(0));
        objectMap1.remove(MagicString.JANNEW);
        objectMap1.remove(MagicString.JAN_NAME);
        objectMap1.remove("rank");
        objectMap1.remove(MagicString.BRANCHNUM);
        objectMap1.remove(MagicString.BRANCH_ACCOUNT);
        objectMap1.remove(MagicString.ERROR_MSG);
        objectMap1.remove("target");
        objectMap1.remove(MagicString.COMPANY_CD);
        objectMap1.remove(MagicString.PRIORITY_ORDER_CD);
        map.remove(MagicString.COMPANY_CD);
        map.remove(MagicString.PRIORITY_ORDER_CD);
        map.remove(MagicString.JANNEW);
        List<Map<String, Object>> similarity = priorityOrderJanNewMapper.getSimilarity(objectMap1,companyCd,priorityOrderCd);
        similarity.forEach(objectMap->{
            objectMap.put(MagicString.JANNEW,objectMap.get(MagicString.JAN_NEW));
            objectMap.put(MagicString.JAN_NAME,objectMap.get("sku"));
            objectMap.put(MagicString.RANK_UPD,objectMap.get("rank"));
            objectMap.remove(MagicString.JAN_NEW);
            objectMap.remove("sku");
        });
        data.forEach(objectMap->{
            objectMap.put(MagicString.RANK_UPD,objectMap.get("rank"));
            objectMap.put("rank",-1);
            objectMap.put("errorMsg","");
        });
        if(!similarity.isEmpty()){
            similarity.addAll(data);
        }
        list.addAll(data);
        similarity = similarity.stream()
                .sorted(Comparator.comparing(map1 -> Integer.parseInt(map1.get(MagicString.RANK_UPD).toString()))).collect(Collectors.toList());
        List<Map<String, Object>> list1 = priorityOrderDataService.calRank(similarity, new ArrayList<>());
        list1.forEach(objectMap->{
            objectMap.put("rank",objectMap.get(MagicString.RANK_UPD));
        });

        List<Object> list2 = new ArrayList<>();
        if (data.isEmpty()){
            list2.add(new ArrayList<>());
        }else {
            List<Map<String,Object>> listMap = new ArrayList<>();
            listMap.addAll(list1);
            listMap.forEach(objectMap->{
                objectMap.put(MagicString.JAN_NEW,objectMap.get(MagicString.JANNEW));
                objectMap.put("jan_old","_");
                objectMap.put(MagicString.COMPANY_CD,companyCd);
                objectMap.put(MagicString.PRIORITY_ORDER_CD,priorityOrderCd);
                objectMap.put(MagicString.RANK_UPD,objectMap.get("rank"));
            });

            List<Map<String, Object>> branchNum = (List<Map<String, Object>>)priorityOrderDataService.getBranchNum(list1).get("data");
            list1.forEach(objectMap-> branchNum.forEach(stringObjectMap->{
                if (objectMap.get(MagicString.JANNEW).equals(stringObjectMap.get(MagicString.JAN_NEW))){
                    objectMap.put(MagicString.BRANCHNUM,stringObjectMap.get("branch_num_upd"));
                    objectMap.remove(MagicString.JAN_NEW);
                    objectMap.remove("jan_old");
                    objectMap.remove(MagicString.COMPANY_CD);
                    objectMap.remove(MagicString.PRIORITY_ORDER_CD);
                    objectMap.remove(MagicString.RANK_UPD);
                }
            }));
            list2.add(list1);
        }

        list2.add(list);

        return ResultMaps.result(ResultEnum.SUCCESS,list2);
    }

    private void dataSave(List<Map<String,Object>> jsonArray, List<ClassicPriorityOrderJanNew> janNewList,
                          List<PriorityOrderJanAttribute> janAttributeList, String companyCd,
                          Integer priorityOrderCd,List<Map<String,Object>> list) {

        for (int i = 0; i < jsonArray.size(); i++) {
            Map<String,Object> item =  jsonArray.get(i);
            // jannewマスターテーブルを作成するためのパラメータ
            if (item.get(MagicString.JANNEW) != null) {
                janNew(janNewList, companyCd, priorityOrderCd,  jsonArray.get(i));
                // jan動的属性列を構築するためのパラメータ
                janAttr(janAttributeList, companyCd, priorityOrderCd,  jsonArray.get(i));
            }
        }
        logger.info("新規商品リストマスターテーブルの処理後のパラメータを保存する：{}" , janNewList);
        logger.info("新規商品リスト動的属性列の処理後のパラメータを保存する：{}" , janAttributeList);
        List<String> janNews = janNewList.stream().map(ClassicPriorityOrderJanNew::getJanNew).collect(Collectors.toList());
        if (!janNews.isEmpty()) {
            int existTableName = mstBranchMapper.checkTableExist("prod_0000_jan_info", "1000");
            String tableName ="";
            if (existTableName == 0){
                 tableName = String.format("\"%s\".prod_0000_jan_info", companyCd);
            }else {
                 tableName = String.format("\"%s\".prod_0000_jan_info", "1000");
            }
            List<Jans> janNewMst = priorityOrderJanNewMapper.getJanNewMst(janNews, companyCd,tableName);
            for (ClassicPriorityOrderJanNew priorityOrderJanNew : janNewList) {
                Optional<Jans> any = janNewMst.stream().filter(jans -> jans.getJan().equals(priorityOrderJanNew.getJanNew())).findAny();
                any.ifPresent(jans -> priorityOrderJanNew.setNameNew(jans.getJanname()));
            }
        }

        //すべて挿入
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
    private void janAttr(List<PriorityOrderJanAttribute> janAttributeList, String companyCd, Integer priorityOrderCd, Map<String,Object> item) {
        for (Map.Entry<String, Object> stringObjectEntry : item.entrySet()) {
            Object key = stringObjectEntry.getKey();
            // 動態属性列表
            PriorityOrderJanAttribute priorityOrderJanAttribute = new PriorityOrderJanAttribute();
            priorityOrderJanAttribute.setCompanyCd(companyCd);
            priorityOrderJanAttribute.setPriorityOrderCd(priorityOrderCd);
            priorityOrderJanAttribute.setJanNew(String.valueOf(item.get(MagicString.JANNEW)));
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
    private void janNew(List<ClassicPriorityOrderJanNew> janNewList, String companyCd, Integer priorityOrderCd, Map<String,Object> item) {
        // 新規商品リスト
        ClassicPriorityOrderJanNew priorityOrderJanNew = new ClassicPriorityOrderJanNew();
        priorityOrderJanNew.setCompanyCd(companyCd);
        priorityOrderJanNew.setPriorityOrderCd(priorityOrderCd);
        priorityOrderJanNew.setJanNew(String.valueOf(item.get(MagicString.JANNEW)));
        priorityOrderJanNew.setRank(Integer.valueOf(String.valueOf(item.get("rank"))));
        priorityOrderJanNew.setBranchAccount(BigDecimal.valueOf(Double.parseDouble(String.valueOf(item.get(MagicString.BRANCH_ACCOUNT)))));
        priorityOrderJanNew.setNameNew(String.valueOf(item.get(MagicString.JAN_NAME)));
        janNewList.add(priorityOrderJanNew);
    }
}
