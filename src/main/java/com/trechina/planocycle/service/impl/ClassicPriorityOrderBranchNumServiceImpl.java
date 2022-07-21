package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.vo.CommodityBranchPrimaryKeyVO;
import com.trechina.planocycle.entity.vo.CommodityBranchVO;
import com.trechina.planocycle.entity.vo.PriorityOrderCommodityVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderBranchNumService;
import com.trechina.planocycle.service.ClassicPriorityOrderDataService;
import com.trechina.planocycle.service.ClassicPriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import com.trechina.planocycle.utils.dataConverUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderBranchNumServiceImpl implements ClassicPriorityOrderBranchNumService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderCommodityMustMapper priorityOrderCommodityMustMapper;
    @Autowired
    private ClassicPriorityOrderCommodityNotMapper priorityOrderCommodityNotMapper;
    @Autowired
    private ClassicPriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private ClassicPriorityOrderBranchNumMapper priorityOrderBranchNumMapper;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private ClassicPriorityOrderDataService priorityOrderDataService;
    @Autowired
    private ClassicPriorityOrderMstMapper classicPriorityOrderMstMapper;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private BasicPatternMstServiceImpl basicPatternMstService;
    @Autowired
    private ClassicPriorityOrderJanNewMapper classicPriorityOrderJanNewMapper;
    @Autowired
    private ShelfPatternMstMapper shelfPatternMstMapper;
    @Autowired
    private ClassicPriorityOrderDataMapper classicPriorityOrderDataMapper;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper classicPriorityOrderMstAttrSortMapper;
    @Autowired
    private cgiUtils cgiUtil;

    public static final Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
    /**
     *smart処理後の必須+不可商品の結菓セットを取得し、保存
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> getPriorityOrderBranchNum(String companyCd, Integer priorityOrderCd,String shelfPatternCd) {
        String uuid = UUID.randomUUID().toString();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String pathInfo = resourceBundle.getString("PriorityOrderData");
        PriorityOrderDataForCgiDto priorityOrderDataForCgiDto = new PriorityOrderDataForCgiDto();
        priorityOrderDataForCgiDto.setCompany(companyCd);
        priorityOrderDataForCgiDto.setShelfPatternNo(shelfPatternCd);
        priorityOrderDataForCgiDto.setPriorityNO(priorityOrderCd);
        priorityOrderDataForCgiDto.setGuid(uuid);
        priorityOrderDataForCgiDto.setMode("priority_jan_storecnt");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        logger.info("priority_を呼び出します。jan_storecntのパラメータ{}",priorityOrderDataForCgiDto);

        String result = cgiUtil.postCgi(pathInfo, priorityOrderDataForCgiDto, tokenInfo);
        logger.info("priority_jan_storecnt処理結菓{}",result);
        String queryPath = resourceBundle.getString("TaskQuery");
        // 帶着taskid，再次請求cgiつかむ取運行ステータス/数据
        Map<String,Object> data = cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
        logger.info("priority_を呼び出します。jan_storecntの結菓{}" ,data);
        JSONArray jsonArray = JSON.parseArray(String.valueOf(data.get("data")));
        logger.info("jsonを回した後：{}",jsonArray.toString());
        if (!jsonArray.isEmpty()){
            priorityOrderCommodityMustMapper.deletePriorityBranchNum(companyCd,priorityOrderCd);

            priorityOrderCommodityMustMapper.insertPriorityBranchNum(jsonArray,companyCd,priorityOrderCd);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 必須商品リストの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCommodityMust(String companyCd, Integer priorityOrderCd) {
        logger.info("必須商品リストのパラメータを取得する：{},{}",companyCd,priorityOrderCd);

        Map<String, Object> tableName = this.getTableName(companyCd, priorityOrderCd);
        String table1 = tableName.get("table1").toString();
        String table2 = tableName.get("table2").toString();
        String janInfoTable = tableName.get("janInfoTable").toString();

        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityMustMapper
                .selectMystInfo(companyCd,priorityOrderCd,table1,table2,janInfoTable);
        logger.info("必須商品リストの戻り値を取得する：{}",priorityOrderCommodityVOList);

        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderCommodityVOList);
    }

    /**
     * 不可商品リストの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCommodityNot(String companyCd, Integer priorityOrderCd) {
        logger.info("不可商品リストのパラメータを取得する：{},{}",companyCd,priorityOrderCd);
        Map<String, Object> tableName = this.getTableName(companyCd, priorityOrderCd);
        String table1 = tableName.get("table1").toString();
        String table2 = tableName.get("table2").toString();
        String janInfoTable = tableName.get("janInfoTable").toString();
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityNotMapper
                                                    .selectNotInfo(companyCd,priorityOrderCd,table1,table2,janInfoTable);
        logger.info("不可商品リストの戻り値を取得する：{}",priorityOrderCommodityVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderCommodityVOList);
    }

    /**
     * 保存必須商品リスト
     *
     * @param priorityOrderCommodityMust
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCommodityMust(List<PriorityOrderCommodityMust> priorityOrderCommodityMust) {
        logger.info("必須商品リストパラメータを保存する：{}",priorityOrderCommodityMust);
//取得したパラメータは1行目に企業と順位テーブルcdがあるだけで、パラメータを巡回し、すべての行に値を割り当てる必要があります。
//        try{
            String companyCd = priorityOrderCommodityMust.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderCommodityMust.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 共通メソッドを呼び出し、データを処理する
            List<PriorityOrderCommodityMust> mustList =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityMust.class,priorityOrderCommodityMust,
                    companyCd ,priorityOrderCd);

            logger.info("必須商品リストの処理後のパラメータを保存する：{}",mustList);

            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd);
            int branchLen = branchCd.length();
            // notを巡回して店cdに0を補充する
            mustList.forEach(item->{
                item.setBranchOrigin(item.getBranch());
                if (!Strings.isNullOrEmpty(item.getBranch())){
                    if(pattern.matcher(item.getBranch()).matches()){
                        int length = item.getBranch().length();
                        StringBuilder branchStr = new StringBuilder();
                        int diff = branchLen - length;
                        for (int i = 0; i < diff; i++) {
                            branchStr.append("0");
                        }
                        item.setBranch(branchStr +item.getBranch());
                    }
                }
            });
            logger.info("必須商品の店補0後の結菓を保存する{}",mustList);
            //削除
            priorityOrderCommodityMustMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // jancheck
            PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
            ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
            String proInfoTable = commonTableName.getProInfoTable();
            String janInfo = priorityOrderJanReplaceService.getJanInfo(proInfoTable);
            String jan = classicPriorityOrderJanNewMapper.getJan(companyCd, priorityOrderCd);
            List<String> list= Arrays.asList(janInfo.split(","));
            List<String> newList= new ArrayList<>();
            if (jan != null ){
                newList= Arrays.asList(jan.split(","));
            }

            String notExists = "";
            List<String> notBranchExists = new ArrayList<>();
            List<PriorityOrderCommodityMust> exists = new ArrayList<>();
            for (int i = 0; i < mustList.size(); i++) {
                PriorityOrderCommodityMust must = mustList.get(i);
                if (must.getJan()!=null&& !"".equals(must.getJan()) && !list.contains(must.getJan()) && !newList.contains(must.getJan())){
                    notExists += must.getJan()+",";
                } else {
                    String branch = must.getBranch();
                    List<Integer> patternCdList = priorityOrderCommodityMustMapper.selectPatternByBranch(priorityOrderCd, companyCd, branch);
                    if(patternCdList.isEmpty()&& ! "".equals(must.getBranchOrigin()) &&  must.getBranchOrigin() != null){
                        notBranchExists.add(must.getBranchOrigin()+"");
                    }
                    for (Integer patternCd : patternCdList) {
                        PriorityOrderCommodityMust newMust = new PriorityOrderCommodityMust();
                        BeanUtils.copyProperties(must, newMust);

                        int result = priorityOrderCommodityMustMapper.selectExistJan(patternCd, must.getJan());
                        if(result>0){
                            newMust.setBeforeFlag(1);
                        }else{
                            newMust.setBeforeFlag(0);
                        }
                        if(patternCd!=null){
                            newMust.setFlag(1);
                        }else{
                            newMust.setFlag(0);
                        }
                        newMust.setShelfPatternCd(patternCd);
                        exists.add(newMust);
                    }
                }
            }
            if (!exists.isEmpty()){
                //データベースへの書き込み
                priorityOrderCommodityMustMapper.insert(exists);
            }
           if (notExists.length()>0){
               return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));
           }else if (!notBranchExists.isEmpty()){
               return ResultMaps.result(ResultEnum.BRANCHNOTESISTS, Joiner.on(",").join(notBranchExists));
           }
           else{
                return ResultMaps.result(ResultEnum.SUCCESS);
           }
        //} catch (Exception e) {
        //    logger.error("保存必須商品リスト：",e);
        //    return ResultMaps.result(ResultEnum.FAILURE);
        //}
    }


    /**
     * 不可商品リストの保存
     *
     * @param priorityOrderCommodityNot
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCommodityNot(List<PriorityOrderCommodityNot> priorityOrderCommodityNot) {
        logger.info("不可商品リストパラメータの保存：{}",priorityOrderCommodityNot);
        // 拿到的参数只有第一行有企業和順位表cd，需要遍暦参数，給所有行都賦
        try{
            String companyCd = priorityOrderCommodityNot.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderCommodityNot.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 調用共同方法，處理数据
            List<PriorityOrderCommodityNot> not =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityNot.class,priorityOrderCommodityNot,
                    companyCd ,priorityOrderCd);
            logger.info("不可商品リスト処理後のパラメータを保存する：{}",not);
            // 査詢企業的店cd是几位数
            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd);
            Integer branchLen = branchCd.length();
            // 遍暦not 給店cd補0
            not.forEach(item->{
                item.setBranchOrigin(item.getBranch());
                if (!Strings.isNullOrEmpty(item.getBranch())) {
                    if(pattern.matcher(item.getBranch()).matches()){
                        int length = item.getBranch().length();
                        StringBuilder branchStr = new StringBuilder();
                        int diff = branchLen - length;
                        for (int i = 0; i < diff; i++) {
                            branchStr.append("0");
                        }
                        item.setBranch(branchStr +item.getBranch());
                    }
                }
            });
            logger.info("不可商品list店が0を補充した結菓を保存します{}",not);
            //削除
            priorityOrderCommodityNotMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // jancheck
            PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
            ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
            String proInfoTable = commonTableName.getProInfoTable();
            String janInfo = priorityOrderJanReplaceService.getJanInfo(proInfoTable);
            String jan = classicPriorityOrderJanNewMapper.getJan(companyCd, priorityOrderCd);
            List<String> newList= new ArrayList<>();
            if (jan != null){
                newList= Arrays.asList(jan.split(","));
            }
            List<String> list= Arrays.asList(janInfo.split(","));
            String notExists = "";
            List<String> notBranchExists = new ArrayList<>();
            List<PriorityOrderCommodityNot> exists = new ArrayList<>();
            for (int i = 0; i < not.size(); i++) {
                PriorityOrderCommodityNot commodityNot = not.get(i);
                if (commodityNot.getJan()!=null&& !"".equals(commodityNot.getJan()) && !list.contains(commodityNot.getJan()) && !newList.contains(commodityNot.getJan())){
                    notExists += commodityNot.getJan()+",";
                } else {
                    String branch = commodityNot.getBranch();
                    List<Integer> patternCdList = priorityOrderCommodityMustMapper.selectPatternByBranch(priorityOrderCd, companyCd, branch);
                    if(patternCdList.isEmpty() && ! "".equals(commodityNot.getBranchOrigin()) &&  commodityNot.getBranchOrigin() != null ){
                        notBranchExists.add(commodityNot.getBranchOrigin()+"");
                    }
                    for (Integer  patternCd : patternCdList) {
                        PriorityOrderCommodityNot newNot = new PriorityOrderCommodityNot();
                        BeanUtils.copyProperties(commodityNot, newNot);
                        int result = priorityOrderCommodityMustMapper.selectExistJan(patternCd, newNot.getJan());
                        if(result>0){
                            newNot.setBeforeFlag(1);
                        }else{
                            newNot.setBeforeFlag(0);
                        }
                        if(patternCd!=null){
                            newNot.setFlag(1);
                        }else{
                            newNot.setFlag(0);
                        }
                        newNot.setShelfPatternCd(patternCd);
                        exists.add(newNot);
                    }
                }
            }
            if (!exists.isEmpty()){
                //写入数据庫
                priorityOrderCommodityNotMapper.insert(exists);
            }
            if (notExists.length()>0){
                return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));
            }else if(!notBranchExists.isEmpty()){
                return ResultMaps.result(ResultEnum.BRANCHNOTESISTS,Joiner.on(",").join(notBranchExists));
            }else{
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("不可商品リストの保存：",e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 必須商品を削除する
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderCommodityMustInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderCommodityMustMapper.deleteFinal(companyCd,priorityOrderCd);
    }

    /**
     * 不可商品を削除する
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderCommodityNotInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderCommodityNotMapper.deleteFinal(companyCd,priorityOrderCd);
    }

    /**
     * 必須の中間テーブルを削除
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderBranchNumInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderBranchNumMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }

    /**
     * 必須リスト
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderMustList(String companyCd, Integer priorityOrderCd) {
        String mustTable = "priority.work_priority_order_commodity_must";
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrSortList(companyCd, priorityOrderCd);
        priorityOrderDataService.getPriorityOrderListInfo(companyCd,priorityOrderCd,attrList);
        List<Map<String, Object>> priorityOrderAttr = classicPriorityOrderDataMapper.getPriorityOrderMustAttr( attrList,mustTable,priorityOrderCd);
        for (Map<String, Object> objectMap : priorityOrderAttr) {
            objectMap.put("janName",objectMap.get("sku"));
            objectMap.remove("sku");
            objectMap.put("__children",new ArrayList<>());
        }
        List<Map<String, Object>> priorityOrderMustList = priorityOrderCommodityMustMapper.getPriorityOrderMustList(companyCd, priorityOrderCd, attrList);
        for (Map<String, Object> objectMap : priorityOrderMustList) {
            objectMap.put("janName",objectMap.get("sku"));
            objectMap.remove("sku");
            List<PriorityOrderBranchNumDto> children = priorityOrderCommodityMustMapper.getBranchAndPattern(objectMap.get("jan").toString(), priorityOrderCd);
            objectMap.put("__children",children);
        }
        priorityOrderMustList.addAll(priorityOrderAttr);
        priorityOrderMustList = priorityOrderMustList.stream()
                .sorted(Comparator.comparing(map1 -> MapUtils.getString(map1, "jan"))).collect(Collectors.toList());
        Map<String,Object> map = new HashMap<>();
        List<PriorityOrderMstAttrSortDto> priorityOrderMstAttrSorts = classicPriorityOrderMstAttrSortMapper.selectWKRankSort(companyCd, priorityOrderCd);
        Map<String, String> attrMap = priorityOrderMstAttrSorts.stream()
                .collect(Collectors.toMap(PriorityOrderMstAttrSortDto::getSort, PriorityOrderMstAttrSortDto::getName,
                        (k1,k2)->k1, LinkedHashMap::new));
        map.put("jan", "Jan");
        map.put("janName", "商品名");
        map.put("shelfPatternName", "該当棚パターン");
        map.put("branchName", "店舗");
        map.put("errmsg", "エラーメッセージ");
        map.putAll(attrMap);
        priorityOrderMustList.add(0,map);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderMustList);
    }
    /**
     * リスト不可
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderNotList(String companyCd, Integer priorityOrderCd) {
        String mustTable = "priority.work_priority_order_commodity_not";
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrSortList(companyCd, priorityOrderCd);
        priorityOrderDataService.getPriorityOrderListInfo(companyCd,priorityOrderCd,attrList);
        List<Map<String, Object>> priorityOrderAttr = classicPriorityOrderDataMapper.getPriorityOrderMustAttr( attrList,mustTable,priorityOrderCd);
        for (Map<String, Object> objectMap : priorityOrderAttr) {
            objectMap.put("janName",objectMap.get("sku"));
            objectMap.remove("sku");
            objectMap.put("__children",new ArrayList<>());
        }
        List<Map<String, Object>> priorityOrderNotList = priorityOrderCommodityNotMapper.getPriorityOrderNotList(companyCd, priorityOrderCd, attrList);
        for (Map<String, Object> objectMap : priorityOrderNotList) {
            objectMap.put("janName",objectMap.get("sku"));
            objectMap.remove("sku");
            List<PriorityOrderBranchNumDto> children = priorityOrderCommodityNotMapper.getBranchAndPattern(objectMap.get("jan").toString(), priorityOrderCd);
            objectMap.put("__children",children);
        }
        priorityOrderNotList.addAll(priorityOrderAttr);
        priorityOrderNotList = priorityOrderNotList.stream()
                .sorted(Comparator.comparing(map1 -> MapUtils.getString(map1, "jan"))).collect(Collectors.toList());
        Map<String,Object> map = new HashMap<>();
        List<PriorityOrderMstAttrSortDto> priorityOrderMstAttrSorts = classicPriorityOrderMstAttrSortMapper.selectWKRankSort(companyCd, priorityOrderCd);
        Map<String, String> attrMap = priorityOrderMstAttrSorts.stream()
                .collect(Collectors.toMap(PriorityOrderMstAttrSortDto::getSort, PriorityOrderMstAttrSortDto::getName,
                        (k1,k2)->k1, LinkedHashMap::new));
        map.put("jan", "Jan");
        map.put("janName", "商品名");
        map.put("shelfPatternName", "該当棚パターン");
        map.put("branchName", "店舗");
        map.put("errmsg", "エラーメッセージ");
        map.putAll(attrMap);
        priorityOrderNotList.add(0,map);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderNotList);
    }
    /**
     * 詳細が必要です
     * @param companyCd
     * @param priorityOrderCd
     * @param jan
     * @return
     */
    @Override
    public Map<String, Object> getCommodityMustBranchList(String companyCd, Integer priorityOrderCd, String jan) {
        String tableName = "work_priority_order_commodity_must";
        List<CommodityBranchVO> existCommodityMustBranchList = priorityOrderCommodityMustMapper.getExistCommodityMustBranchList(priorityOrderCd, jan);
        if(existCommodityMustBranchList.isEmpty()){
            priorityOrderCommodityMustMapper.insertCommodityBranchList(companyCd, priorityOrderCd, jan, tableName);
        }else {
            priorityOrderCommodityMustMapper.insertSurplusCommodityBranch(companyCd, priorityOrderCd, jan, tableName,existCommodityMustBranchList);
        }

        existCommodityMustBranchList = priorityOrderCommodityMustMapper.getExistCommodityMustBranchList(priorityOrderCd, jan);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, List<CommodityBranchVO>> existCommodityMustBranchByGroup = existCommodityMustBranchList.stream().collect(Collectors.groupingBy(CommodityBranchVO::getShelfPatternName, Collectors.toList()));

        existCommodityMustBranchByGroup.entrySet().stream().forEach(item->{
            List<CommodityBranchVO> value = item.getValue();

            CommodityBranchVO commodityBranchVO = value.get(0);
            Map<String, Object> itemMap = new HashMap<>(2);
            itemMap.put("shelfPatternName", item.getKey());
            itemMap.put("branchName", value.size()+"店舗");
            itemMap.put("beforeFlag", commodityBranchVO.getBeforeFlag());
            itemMap.put("flag", value.size()== ((Long) value.stream().filter(vo -> vo.getFlag() == 1).count()).intValue()?1:0);
            itemMap.put("__children", value);
            resultList.add(itemMap);
        });

        return ResultMaps.result(ResultEnum.SUCCESS, resultList);
    }
    /**
     * 詳細不可
     * @param companyCd
     * @param priorityOrderCd
     * @param jan
     * @return
     */
    @Override
    public Map<String, Object> getCommodityNotBranchList(String companyCd, Integer priorityOrderCd, String jan) {
        String tableName = "work_priority_order_commodity_not";
        List<CommodityBranchVO> existCommodityNotBranchList = priorityOrderCommodityNotMapper.getExistCommodityNotBranchList(priorityOrderCd, jan);
        if(existCommodityNotBranchList.isEmpty()){
            priorityOrderCommodityMustMapper.insertCommodityBranchList(companyCd, priorityOrderCd, jan,tableName );
        }else {
            priorityOrderCommodityMustMapper.insertSurplusCommodityBranch(companyCd, priorityOrderCd, jan, tableName,existCommodityNotBranchList);
        }

        existCommodityNotBranchList = priorityOrderCommodityNotMapper.getExistCommodityNotBranchList(priorityOrderCd, jan);

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, List<CommodityBranchVO>> existCommodityMustBranchByGroup = existCommodityNotBranchList.stream().collect(Collectors.groupingBy(CommodityBranchVO::getShelfPatternName, Collectors.toList()));

        existCommodityMustBranchByGroup.entrySet().stream().forEach(item->{
            List<CommodityBranchVO> value = item.getValue();
            CommodityBranchVO commodityBranchVO = value.get(0);
            Map<String, Object> itemMap = new HashMap<>(2);
            itemMap.put("shelfPatternName", item.getKey());
            itemMap.put("branchName", value.size()+"店舗");
            itemMap.put("beforeFlag", commodityBranchVO.getBeforeFlag());
            itemMap.put("__children", value);
            itemMap.put("flag", value.size()== ((Long) value.stream().filter(vo -> vo.getFlag() == 1).count()).intValue()?1:0);
            resultList.add(itemMap);
        });

        return ResultMaps.result(ResultEnum.SUCCESS, resultList);
    }
    /**
     * 詳細保存不可
     * @param companyCd,priorityOrderCd,jan,commodityList
     * @return
     */
    @Override
    public Map<String, Object> saveCommodityNotBranchList(String companyCd, Integer priorityOrderCd, String jan, String commodityNot) {
        List<Map<String, Object>> commodityNotList = new Gson().fromJson(commodityNot, new TypeToken<List<Map<String, Object>>>(){}.getType());
        List<Map<String, Object>> updateList = new ArrayList<>(commodityNotList.size());
        for (Map<String, Object> commodityNotMap : commodityNotList) {
            String children = MapUtils.getString(commodityNotMap, "__children");
            List<Map<String, Object>> childrenMap = new Gson().fromJson(children, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            childrenMap.stream().forEach(child -> child.put("branch",MapUtils.getString(child, "branchName").split("_")[0]));
            updateList.addAll(childrenMap);
        }
        priorityOrderCommodityNotMapper.updateFlag(companyCd, priorityOrderCd, jan, updateList);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 詳細を保存する必要があります
     * @param companyCd,priorityOrderCd,jan,commodityList
     * @return
     */
    @Override
    public Map<String, Object> saveCommodityMustBranchList(String companyCd, Integer priorityOrderCd, String jan, String commodityMust) {
        List<Map<String, Object>> commodityNotList = new Gson().fromJson(commodityMust, new TypeToken<List<Map<String, Object>>>(){}.getType());
        List<Map<String, Object>> updateList = new ArrayList<>(commodityNotList.size());
        for (Map<String, Object> commodityNotMap : commodityNotList) {
            String children = MapUtils.getString(commodityNotMap, "__children");
            List<Map<String, Object>> childrenMap = new Gson().fromJson(children, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            childrenMap.stream().forEach(child -> child.put("branch",MapUtils.getString(child, "branchName").split("_")[0]));
            updateList.addAll(childrenMap);
        }
        priorityOrderCommodityMustMapper.updateFlag(companyCd, priorityOrderCd, jan, updateList);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 詳細を削除する必要があります
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    @Override
    public Map<String, Object> delCommodityMustBranch(CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO) {
        priorityOrderCommodityMustMapper.delCommodityMustBranch(commodityBranchPrimaryKeyVO);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 詳細削除不可
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    @Override
    public Map<String, Object> delCommodityNotBranch(CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO) {
        priorityOrderCommodityNotMapper.delCommodityNotBranch(commodityBranchPrimaryKeyVO);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


    public Map<String,Object> getTableName(String companyCd,Integer priorityOrderCd ){
        String table1 = "";
        String table2 = "";
        String janInfoTable = "";
        PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
        GetCommonPartsDataDto productPartsData = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
         janInfoTable = productPartsData.getProInfoTable();
        List<String> shelfPatternList = Arrays.asList(priorityOrderMst.getShelfPatternCd().split(","));
        List<String> commonPartsData = shelfPatternMstMapper.isCompany(shelfPatternList);
        List<GetCommonPartsDataDto> list = new ArrayList<>();
        for (String commonPartsDatum : commonPartsData) {
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsDatum, companyCd);
            list.add(commonTableName);
        }

        if (list.size()>1){
            if (list.get(0).getProdIsCore().equals("1000")){
                table1 = list.get(0).getStoreInfoTable();
                table2 = list.get(1).getStoreInfoTable();
            }else {
                table1 = list.get(1).getStoreInfoTable();
                table2 = list.get(0).getStoreInfoTable();
            }
        }else {
            if (list.get(0).getStoreIsCore().equals("1")){
                table1 = list.get(0).getStoreInfoTable();
            }else {
                table2 = list.get(0).getStoreInfoTable();
            }

        }
        Map<String,Object> map = new HashMap<>();
        map.put("table1",table1);
        map.put("table2",table2);
        map.put("janInfoTable",janInfoTable);
        return map;
    }
}
