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
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
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
     * 获取新规janList
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd) {
        try {
            //获取列头
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

            logger.info("获取新规商品list参数：{},{}",companyCd, priorityOrderCd);
            List<ClassicPriorityOrderJanNewVO> priorityOrderJanNewVOS = priorityOrderJanNewMapper.selectJanNew(companyCd, priorityOrderCd);
            logger.info("获取新规商品list返回结果集b：{}", priorityOrderJanNewVOS);
            JSONArray jsonArray = new JSONArray();

            // 遍历结果集，拆分动态列
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
                    //写入jsonArray
                    jsonArray.add(result);
                });
                //把动态的列名写到下标0，让前端生成动态列
                jsonArray.add(0, results);

            } else {
                jsonArray.add(results);
            }
            return ResultMaps.result(ResultEnum.SUCCESS, jsonArray);
        } catch (Exception e) {
            logger.info("获取新规janList失败：",e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 保存新规商品list
     *
     * @param jsonArray
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanNew(JSONArray jsonArray) {
        //try {
        logger.info("保存新规商品list参数:{}" ,jsonArray);
        List<ClassicPriorityOrderJanNew> janNewList = new ArrayList<>();
        List<PriorityOrderJanAttribute> janAttributeList = new ArrayList<>();
        //获取参数中第一行的企业和优先顺位号
        String companyCd = String.valueOf(((HashMap) jsonArray.get(0)).get("companyCd"));
        Integer priorityOrderCd =Integer.valueOf(String.valueOf(((HashMap) jsonArray.get(0)).get("priorityOrderCd")));
        // 全删
        priorityOrderJanNewMapper.delete(companyCd, priorityOrderCd);
        priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);

        // 遍历前端给的jsonArray 构筑新规商品list表和关联的动态属性列表的实体类字符串,保存数据
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
     * 删除新规商品list
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
            // 构造jannew主表的参数
            if (item.get("janNew") != null) {
                janNew(janNewList, companyCd, priorityOrderCd, (HashMap) jsonArray.get(i));
                // 构造jan动态属性列的参数
                janAttr(janAttributeList, companyCd, priorityOrderCd, (HashMap) jsonArray.get(i));
            }
        }
        logger.info("保存新规商品list主表处理完后的参数：{}",janNewList.toString());
        logger.info("保存新规商品list动态属性列处理完后的参数：{}", janAttributeList.toString());
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

        //全插入
        if (!janNewList.isEmpty()) {
            priorityOrderJanNewMapper.insert(janNewList);
            priorityOrderJanAttributeMapper.insert(janAttributeList);
            //查询所有jannew 修改配荷店铺数
            List<Map<String, Object>> maps = priorityOrderJanNewMapper.selectJanNewOrAttr(companyCd, priorityOrderCd);
            maps.forEach(item -> {
                String[] attrName = item.get("attr").toString().split(",");
                StringBuilder sel = new StringBuilder();
                for (int i = 1; i <= attrName.length; i++) {
                    sel.append("attr").append(i).append("='").append(attrName[i - 1]).append("',");
                }
                List<String> colValueList = Arrays.asList(sel.toString().split(","));
                String branchNum = priorityOrderCatepakAttributeMapper.selectForTempTable(colValueList, "public.priorityorder" + session.getAttribute("aud").toString());
                logger.info("查询定番店铺数{}", branchNum);
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
        //遍历结果集 拆分动态列
        if (priorityOrderData.size() > 0) {
            priorityOrderData.forEach(item -> {
                String[] attrList = item.get("attr").toString().split(",");
                String[] valList;
                for (int i = 0; i < attrList.length - 1; i++) {
                    valList = attrList[i].split(":");
                    item.put("attr" + valList[0], valList[1]);
                }
                // 第十个属性换成多属性的列名
                item.put("mulit_attr", attrList[attrList.length - 1].split(":")[1]);
                item.remove("attr");
//                // 转千分符
//                Float amount = Float.parseFloat(item.get("pos_amount_upd").toString());
//                item.remove("pos_amount_upd");
//                item.put("pos_amount_upd", NumberFormat.getIntegerInstance(Locale.getDefault()).format(amount));
                // sku就是jan，查询jananme
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
            //把不存在数据传给主表插入
            ClassicPriorityOrderDataServiceImpl priorityOrderDataService = new ClassicPriorityOrderDataServiceImpl();
            List<Map<String, String>> keyNameList = new ArrayList<>();
            //生成表头
            JSONArray priJsonArray = (JSONArray) JSONObject.toJSON(priorityOrderData);
            priorityOrderDataService.colNameList(priJsonArray, keyNameList);
            //插入数据

            priorityOrderDataMapper.insert(priJsonArray, keyNameList, "public.priorityorder" + session.getAttribute("aud").toString());

        }
    }

    /**
     * 构造jannew主表的参数
     *
     * @param janAttributeList
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     */
    private void janAttr(List<PriorityOrderJanAttribute> janAttributeList, String companyCd, Integer priorityOrderCd, HashMap item) {
        for (Object key : item.keySet()) {
            // 动态属性列表
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
     * 构造jan动态属性列的参数
     *
     * @param janNewList
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     */
    private void janNew(List<ClassicPriorityOrderJanNew> janNewList, String companyCd, Integer priorityOrderCd, HashMap item) {
        // 新规商品list表
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
