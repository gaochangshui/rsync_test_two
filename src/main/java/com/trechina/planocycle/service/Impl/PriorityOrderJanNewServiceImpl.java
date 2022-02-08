package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.po.PriorityOrderJanAttribute;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.vo.PriorityOrderJanNewVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderCatepakAttributeMapper;
import com.trechina.planocycle.mapper.PriorityOrderDataMapper;
import com.trechina.planocycle.mapper.PriorityOrderJanAttributeMapper;
import com.trechina.planocycle.mapper.PriorityOrderJanNewMapper;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

@Service
public class PriorityOrderJanNewServiceImpl implements PriorityOrderJanNewService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private PriorityOrderJanAttributeMapper priorityOrderJanAttributeMapper;
    @Autowired
    private PriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private PriorityOrderCatepakAttributeMapper priorityOrderCatepakAttributeMapper;
    /**
     * 获取新规janList
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd,Integer productPowerNo) {
        try {
            logger.info("获取新规商品list参数："+companyCd+","+priorityOrderCd);
            List<PriorityOrderJanNewVO> priorityOrderJanNewVOS = priorityOrderJanNewMapper.selectJanNew(companyCd,priorityOrderCd);
            logger.info("获取新规商品list返回结果集b："+priorityOrderJanNewVOS);
            JSONArray jsonArray = new JSONArray();

            // 遍历结果集，拆分动态列
            if (priorityOrderJanNewVOS.size()>0){
                priorityOrderJanNewVOS.forEach(item->{
                    Map<String,Object> result = new HashMap<>();
                    String[] attrList = item.getAttr().split(",");
                    String[] valList;
                    result.put("janNew",item.getJanNew());
                    result.put("janName",item.getJanName());
                    for (int i = 0; i < attrList.length; i++) {
                        valList = attrList[i].split(":");
                        result.put("attr"+valList[0],valList[1]);
                    }
                    result.put("rank",item.getRank());
                    result.put("branchNum",item.getBranchNum());
                    result.put("branchAccount",item.getBranchAccount());
                    result.put("errMsg",item.getErrMsg());
                    //写入jsonArray
                    jsonArray.add(result);
                });
                //把动态的列名写到下标0，让前端生成动态列
                jsonArray.add(0,((HashMap) jsonArray.get(0)).keySet().stream().sorted());

            } else {
                //获取列头
                PriorityOrderJanNewVO colResult = priorityOrderJanNewMapper.selectColName(companyCd,productPowerNo);
                logger.info("获取新规商品list返回结果集e："+colResult);
                String[] attrList = colResult.getAttr().split(",");
                String[] valList ;
                List<String>  results = new ArrayList<>();
                for (int i = 0; i < attrList.length; i++) {
                    valList = attrList[i].split(":");
                    results.add("attr"+valList[0]);
                }
                results.add("janNew");
                results.add("janName");
                results.add("rank");
                results.add("branchNum");
                results.add("branchAccount");
                results.add("errMsg");
                jsonArray.add(results);
            }
            return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
        } catch (Exception e) {
            logger.info("获取新规janList失败："+e);
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
        try {
            logger.info("保存新规商品list参数:"+jsonArray);
            List<PriorityOrderJanNew> janNewList = new ArrayList<>();
            List<PriorityOrderJanAttribute> janAttributeList = new ArrayList<>();
            //获取参数中第一行的企业和优先顺位号
            String companyCd = String.valueOf(((HashMap) jsonArray.get(0)).get("companyCd"));
            Integer priorityOrderCd = (Integer) ((HashMap) jsonArray.get(0)).get("priorityOrderCd");
            // 全删
            priorityOrderJanNewMapper.delete(companyCd,priorityOrderCd);
            priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // 遍历前端给的jsonArray 构筑新规商品list表和关联的动态属性列表的实体类字符串,保存数据
            String notExistsJan="";
            notExistsJan = dataSave(jsonArray, janNewList, janAttributeList, companyCd, priorityOrderCd,notExistsJan);

            //不存在主表的数据查询，保出到主表
            notExistsData(companyCd, priorityOrderCd);
            if (notExistsJan.length()>0){
                return ResultMaps.result(ResultEnum.JANNOTESISTS,notExistsJan.substring(0,notExistsJan.length()-1));
            } else {
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.info("保存新规商品list报错"+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
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
        return priorityOrderJanNewMapper.delete(companyCd,priorityOrderCd);
    }

    private String dataSave(JSONArray jsonArray, List<PriorityOrderJanNew> janNewList,
                          List<PriorityOrderJanAttribute> janAttributeList, String companyCd,
                          Integer priorityOrderCd,String notExistsJan) {
        String janInfo = priorityOrderJanReplaceService.getJanInfo();
        List<String> list= Arrays.asList(janInfo.split(","));
        for (int i = 0; i < jsonArray.size(); i++) {
            if(list.indexOf(((HashMap) jsonArray.get(i)).get("janNew"))==-1){
                notExistsJan += ((HashMap) jsonArray.get(i)).get("janNew")+",";
            } else {
                // 构造jannew主表的参数
                janNew(janNewList, companyCd, priorityOrderCd, (HashMap) jsonArray.get(i));
                // 构造jan动态属性列的参数
                janAttr(janAttributeList, companyCd, priorityOrderCd, (HashMap) jsonArray.get(i));
            }
        }
        logger.info("保存新规商品list主表处理完后的参数："+ janNewList.toString());
        logger.info("保存新规商品list动态属性列处理完后的参数："+ janAttributeList.toString());
        //全插入
        if (janNewList.size()>0){
            priorityOrderJanNewMapper.insert(janNewList);
            priorityOrderJanAttributeMapper.insert(janAttributeList);
            //查询所有jannew 修改配荷店铺数
            List<Map<String,Object>> maps = priorityOrderJanNewMapper.selectJanNewOrAttr(companyCd,priorityOrderCd);
            maps.forEach(item -> {
                String[] attrName = item.get("attr").toString().split(",");
                String sel = "";
                for (int i = 1; i <= attrName.length; i++) {
                    if (i==attrName.length){
                        sel += "mulit_attr='"+attrName[i-1]+"',";
                    }else{
                        sel += "attr"+i+"='"+attrName[i-1]+"',";
                    }
                }
                sel+= "rank_upd='"+item.get("rank")+"'";
                List<String> colValueList = Arrays.asList(sel.split(","));
                String branchNum = priorityOrderCatepakAttributeMapper.selectForTempTable(colValueList,"public.priorityorder"+session.getAttribute("aud").toString());
                logger.info("查询定番店铺数"+branchNum);
                if (branchNum!=null){
                    priorityOrderJanNewMapper.updateBranchNum(Integer.valueOf(item.get("priority_order_cd").toString()),
                            item.get("jan_new").toString(),Integer.valueOf(branchNum));
                } else {
                    priorityOrderJanNewMapper.updateBranchNum(Integer.valueOf(item.get("priority_order_cd").toString()),
                            item.get("jan_new").toString(),0);
                }
            });

        }
        return notExistsJan;
    }

    private void notExistsData(String companyCd, Integer priorityOrderCd) {
        List<Map<String,Object>> priorityOrderData =  priorityOrderJanNewMapper.selectJanNewNotExistsMst(companyCd, priorityOrderCd,
                "public.priorityorder"+session.getAttribute("aud").toString());
        logger.info("不存在的数据"+priorityOrderData.toString());
        //遍历结果集 拆分动态列
        if (priorityOrderData.size()>0) {
            priorityOrderData.forEach(item->{
                String[] attrList = item.get("attr").toString().split(",");
                String[] valList;
                for (int i = 0; i < attrList.length-1; i++) {
                    valList = attrList[i].split(":");
                   item.put("attr"+valList[0],valList[1]);
                }
                // 第十个属性换成多属性的列名
                item.put("mulit_attr",attrList[attrList.length-1].split(":")[1]);
                item.remove("attr");
//                // 转千分符
//                Float amount = Float.parseFloat(item.get("pos_amount_upd").toString());
//                item.remove("pos_amount_upd");
//                item.put("pos_amount_upd", NumberFormat.getIntegerInstance(Locale.getDefault()).format(amount));
                // sku就是jan，查询jananme
                String janName = priorityOrderDataMapper.selectJanName(item.get("jan_new").toString());
                item.remove("sku");
                item.put("sku", janName);
            });
        //把不存在数据传给主表插入
        PriorityOrderDataServiceImpl priorityOrderDataService= new PriorityOrderDataServiceImpl();
        List<Map<String,String>> keyNameList = new ArrayList<>();
        //生成表头
        JSONArray priJsonArray = (JSONArray) JSONObject.toJSON(priorityOrderData);
        priorityOrderDataService.colNameList(priJsonArray,keyNameList);
        //插入数据

        priorityOrderDataMapper.insert(priJsonArray,keyNameList,"public.priorityorder"+session.getAttribute("aud").toString());

        }
    }

    /**
     * 构造jannew主表的参数
     * @param janAttributeList
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     */
    private void janAttr(List<PriorityOrderJanAttribute> janAttributeList, String companyCd, Integer priorityOrderCd, HashMap item) {
        for (Object key : item.keySet()){
            // 动态属性列表
            PriorityOrderJanAttribute priorityOrderJanAttribute = new PriorityOrderJanAttribute();
            priorityOrderJanAttribute.setCompanyCd(companyCd);
            priorityOrderJanAttribute.setPriorityOrderCd(priorityOrderCd);
            priorityOrderJanAttribute.setJanNew(String.valueOf(item.get("janNew")));
            if (key.toString().indexOf("attr")>-1) {
                priorityOrderJanAttribute.setAttrCd(Integer.valueOf(key.toString().replace("attr","")));
                priorityOrderJanAttribute.setAttrValue(String.valueOf(item.get(key)));
                janAttributeList.add(priorityOrderJanAttribute);

            }
        }
    }

    /**
     * 构造jan动态属性列的参数
     * @param janNewList
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     */
    private void janNew(List<PriorityOrderJanNew> janNewList, String companyCd, Integer priorityOrderCd, HashMap item) {
        // 新规商品list表
        PriorityOrderJanNew priorityOrderJanNew = new PriorityOrderJanNew();
        priorityOrderJanNew.setCompanyCd(companyCd);
        priorityOrderJanNew.setPriorityOrderCd(priorityOrderCd);
        priorityOrderJanNew.setJanNew(String.valueOf(item.get("janNew")));
        priorityOrderJanNew.setRank(Integer.valueOf(String.valueOf(item.get("rank"))) );
        priorityOrderJanNew.setBranchAccount(BigDecimal.valueOf(Double.valueOf(String.valueOf(item.get("branchAccount")))));
        janNewList.add(priorityOrderJanNew);
    }
}
