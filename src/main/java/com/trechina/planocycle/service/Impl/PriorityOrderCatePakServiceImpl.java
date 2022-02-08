package com.trechina.planocycle.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.po.PriorityOrderCatepak;
import com.trechina.planocycle.entity.po.PriorityOrderCatepakAttribute;
import com.trechina.planocycle.entity.vo.PriorityOrderCatePakVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderCatepakAttributeMapper;
import com.trechina.planocycle.mapper.PriorityOrderCatepakMapper;
import com.trechina.planocycle.service.PriorityOrderCatePakService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class PriorityOrderCatePakServiceImpl implements PriorityOrderCatePakService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderCatepakMapper priorityOrderCatepakMapper;
    @Autowired
    private PriorityOrderCatepakAttributeMapper priorityOrderCatepakAttributeMapper;
    /**
     * 获取カテパケ拡縮
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCatePak(String companyCd, Integer priorityOrderCd,Integer productPowerNo) {
        try {
            logger.info("获取カテパケ拡縮参数："+companyCd+","+priorityOrderCd);
            List<PriorityOrderCatePakVO> priorityOrderCatePakVOS = priorityOrderCatepakMapper.selectByPrimaryKey(companyCd,
                    priorityOrderCd);
            logger.info("获取カテパケ拡縮结果集：" + priorityOrderCatePakVOS);
            JSONArray jsonArray = new JSONArray();
            if (priorityOrderCatePakVOS.size()>0) {
                // 遍历结果集，拆分动态列
                priorityOrderCatePakVOS.forEach(item -> {
                    Map<String, Object> result = new HashMap<>();
                    String[] attrSmall = item.getSmalls().split(",");
                    String[] attrBig;
                    String[] valList;
                    // 遍历small
                    for (int i = 0; i < attrSmall.length; i++) {
                        valList = attrSmall[i].split(":");
                        result.put("attrSmall" + valList[0], valList[1]);
                    }
                    result.put("rank", item.getRank());
                    result.put("branchNum", item.getBranchNum());
                    // big可以为空，需要判断一下
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
                //把动态的列名写到下标0，让前端生成动态列
                jsonArray.add(0, ((HashMap) jsonArray.get(0)).keySet().stream().sorted());
            } else {
                Integer colResult = priorityOrderCatepakMapper.selectColName(companyCd,
                        productPowerNo);
                logger.info("获取カテパケ拡縮结果集2：" + colResult);
                if (colResult!=null) {
                    List<String> list = new ArrayList<>();
                    String[] valList;
                    for (int i = 0; i < colResult; i++) {
                        list.add("attrSmall" + i);
                        list.add("attrBig" + i);
                    }
                    list.add("rank");
                    list.add("branchNum");
                    jsonArray.add(list);
                }

            }
            logger.info("カテパケ拡縮结果"+jsonArray.toString());
            return ResultMaps.result(ResultEnum.SUCCESS,jsonArray);
        }catch (Exception e) {
            logger.info("获取カテパケ拡縮失败："+e);
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
            //获取参数中第一行的企业和优先顺位号
            String companyCd = String.valueOf(((HashMap) jsonArray.get(0)).get("companyCd"));
            Integer priorityOrderCd = (Integer) ((HashMap) jsonArray.get(0)).get("priorityOrderCd");
            // 全删
            priorityOrderCatepakMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            priorityOrderCatepakAttributeMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // 遍历前端给的jsonArray 构筑カテパケ拡縮表和关联的动态属性列表的实体类字符串
            jsonArray.forEach(item->{
                // 构造主表的参数
                if (((HashMap) item).containsKey("rank") && ((HashMap) item).get("rank")!=null) {
                    PriorityOrderCatepak priorityOrderCatepak = new PriorityOrderCatepak();
                    priorityOrderCatepak.setCompanyCd(companyCd);
                    priorityOrderCatepak.setPriorityOrderCd(priorityOrderCd);
                    priorityOrderCatepak.setRank(Integer.valueOf(((HashMap) item).get("rank").toString()));
//                priorityOrderCatepak.setBranchNum(Integer.valueOf(((HashMap) item).get("branchNum").toString()));
                    // 写入数据重新取号，返回自增列id，实体类自动接收
                    priorityOrderCatepakMapper.insert(priorityOrderCatepak);
                    logger.info("保存カテパケ拡縮返回值:" + priorityOrderCatepak.toString());
                    catePakAttr(companyCd, priorityOrderCd, (HashMap) item, priorityOrderCatepak);
                }

            });
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("保存カテパケ拡縮报错:"+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 删除カテパケ拡縮
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
     * 删除カテパケ拡縮属性
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
     * 构筑属性动态列
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
            if (key.toString().indexOf("attrSmall")>-1 || key.toString().indexOf("attrBig")>-1) {
                PriorityOrderCatepakAttribute catepakAttribute = new PriorityOrderCatepakAttribute();
                // 动态属性列表
                catepakAttribute.setCompanyCd(companyCd);
                catepakAttribute.setPriorityOrderCd(priorityOrderCd);
                catepakAttribute.setCatepakCd(priorityOrderCatepak.getId());
                if (key.toString().indexOf("attrSmall") > -1) {
                    //缩小是0
                    catepakAttribute.setFlg(0);
                    catepakAttribute.setAttrCd(Integer.valueOf(key.toString().replace("attrSmall", "")));
                    // 记录列名+值
                    colValue+="attr"+idx+"='"+item.get(key)+"',";
                    idx+=1;
                }
                if (key.toString().indexOf("attrBig") > -1) {
                    //扩张是1
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
        // 查询定番店铺数
        colValue = colValue.replace(("attr"+(idx -1)),"mulit_attr");
        colValue +="rank_upd='"+ priorityOrderCatepak.getRank()+"'";
        List<String> colValueList = Arrays.asList(colValue.split(","));
        String branchNum =  priorityOrderCatepakAttributeMapper.selectForTempTable(colValueList,
                "public.priorityorder"+session.getAttribute("aud").toString());
        logger.info("查询定番店铺数"+branchNum);
        if (branchNum!=null){
            priorityOrderCatepakMapper.updateBranchNum(priorityOrderCatepak.getId(),Integer.valueOf(branchNum));
        } else {
            priorityOrderCatepakMapper.updateBranchNum(priorityOrderCatepak.getId(),0);
        }
    }
}
