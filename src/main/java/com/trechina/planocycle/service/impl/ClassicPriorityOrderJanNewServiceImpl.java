package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.trechina.planocycle.entity.dto.ClassicPriorityOrderJanNewDto;
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
        dataSave(jsonArray, janNewList, janAttributeList, companyCd, priorityOrderCd);

        List<ClassicPriorityOrderJanNewDto> janNewAll = new Gson().fromJson(jsonArray.toJSONString(), new TypeToken<List<ClassicPriorityOrderJanNewDto>>(){}.getType());
            List<String> janNews = janNewAll.stream().map(ClassicPriorityOrderJanNewDto::getJanNew).collect(Collectors.toList());
            if(!janNews.isEmpty()){
                priorityOrderDataMapper.deleteExistJanNew(janNews,
                        "priority.work_priority_order_result_data");
            }

        return ResultMaps.result(ResultEnum.SUCCESS);
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

    private void dataSave(JSONArray jsonArray, List<ClassicPriorityOrderJanNew> janNewList,
                          List<PriorityOrderJanAttribute> janAttributeList, String companyCd,
                          Integer priorityOrderCd) {
        for (int i = 0; i < jsonArray.size(); i++) {
            HashMap item = (HashMap) jsonArray.get(i);
            // 構造jannew主表的参数
            if (item.get("janNew") != null) {
                janNew(janNewList, companyCd, priorityOrderCd, (HashMap) jsonArray.get(i));
                // 構造jan動態属性列的参数
                janAttr(janAttributeList, companyCd, priorityOrderCd, (HashMap) jsonArray.get(i));
            }
        }
        logger.info("新しい商品リストマスターテーブルの処理後のパラメータを保存します。：{}",janNewList.toString());
        logger.info("新規商品リストの動的属性列の処理後のパラメータを保存します。：{}", janAttributeList.toString());
        List<String> janNews = janNewList.stream().map(item -> item.getJanNew()).collect(Collectors.toList());
        if (!janNews.isEmpty()) {
            List<Jans> janNewMst = priorityOrderJanNewMapper.getJanNewMst(janNews);
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

        //全挿入
        if (!janNewList.isEmpty()) {
            priorityOrderJanNewMapper.insert(janNewList);
            priorityOrderJanAttributeMapper.insert(janAttributeList);
            //すべてのjannewを検索して荷重を変更する店舗数
            List<Map<String, Object>> maps = priorityOrderJanNewMapper.selectJanNewOrAttr(companyCd, priorityOrderCd);
            maps.forEach(item -> {
                String[] attrName = item.get("attr").toString().split(",");
                StringBuilder sel = new StringBuilder();
                for (int i = 1; i <= attrName.length; i++) {
                    sel.append("attr").append(i).append("='").append(attrName[i - 1]).append("',");
                }
                List<String> colValueList = Arrays.asList(sel.toString().split(","));
                String branchNum = priorityOrderCatepakAttributeMapper.selectForTempTable(colValueList, priorityOrderCd);
                logger.info("定番店舗数の照会{}", branchNum);
                if (branchNum != null) {
                    priorityOrderJanNewMapper.updateBranchNum(Integer.valueOf(item.get("priority_order_cd").toString()),
                            item.get("jan_new").toString(), Integer.valueOf(branchNum));
                } else {
                    priorityOrderJanNewMapper.updateBranchNum(Integer.valueOf(item.get("priority_order_cd").toString()),
                            item.get("jan_new").toString(), 0);
                }
            });
        }
    }

    private void notExistsData(String companyCd, Integer priorityOrderCd) {
        List<Map<String, Object>> priorityOrderData = priorityOrderJanNewMapper.selectJanNewNotExistsMst(companyCd, priorityOrderCd,
                "public.priorityorder" + session.getAttribute("aud").toString());
        logger.info("不存在的数据" + priorityOrderData.toString());
        if (priorityOrderData.size() > 0) {
            priorityOrderData.forEach(item -> {
                String[] attrList = item.get("attr").toString().split(",");
                String[] valList;
                for (int i = 0; i < attrList.length - 1; i++) {
                    valList = attrList[i].split(":");
                    item.put("attr" + valList[0], valList[1]);
                }
                // 10番目の属性はマルチ属性の列名に変更されます。
                item.put("mulit_attr", attrList[attrList.length - 1].split(":")[1]);
                item.remove("attr");
//                // 千分記号を回転
//                Float amount = Float.parseFloat(item.get("pos_amount_upd").toString());
//                item.remove("pos_amount_upd");
//                item.put("pos_amount_upd", NumberFormat.getIntegerInstance(Locale.getDefault()).format(amount));
                String janName = priorityOrderDataMapper.selectJanName(item.get("jan_new").toString());
                if (janName == null) {
                    janName = priorityOrderDataMapper.selectJanNameFromJanNew(item.get("jan_new").toString(), companyCd, priorityOrderCd);
                }
                if("".equals(item.get("pos_before_rate"))|| item.get("pos_before_rate")==null){
                    item.put("pos_before_rate", "_");
                }

                if("".equals(item.get("branch_amount"))|| item.get("branch_amount")==null){
                    item.put("branch_amount", "_");
                }
                item.remove("sku");
                item.put("sku", janName);
            });
            //存在しないデータをマスターテーブルに挿入する
            ClassicPriorityOrderDataServiceImpl priorityOrderDataService = new ClassicPriorityOrderDataServiceImpl();
            List<Map<String, String>> keyNameList = new ArrayList<>();
            //ヘッダーの生成
            JSONArray priJsonArray = (JSONArray) JSONObject.toJSON(priorityOrderData);
            priorityOrderDataService.colNameList(priJsonArray, keyNameList);
            //データの挿入

            priorityOrderDataMapper.insert(priJsonArray, keyNameList, "public.priorityorder" + session.getAttribute("aud").toString());

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
    private void janAttr(List<PriorityOrderJanAttribute> janAttributeList, String companyCd, Integer priorityOrderCd, HashMap item) {
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
    private void janNew(List<ClassicPriorityOrderJanNew> janNewList, String companyCd, Integer priorityOrderCd, HashMap item) {
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
