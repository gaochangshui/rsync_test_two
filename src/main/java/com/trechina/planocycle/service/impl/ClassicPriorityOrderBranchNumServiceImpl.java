package com.trechina.planocycle.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityMust;
import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.vo.CommodityBranchPrimaryKeyVO;
import com.trechina.planocycle.entity.vo.CommodityBranchVO;
import com.trechina.planocycle.entity.vo.PriorityOrderCommodityVO;
import com.trechina.planocycle.entity.vo.StarReadingVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderBranchNumService;
import com.trechina.planocycle.service.ClassicPriorityOrderDataService;
import com.trechina.planocycle.service.ClassicPriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.*;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
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
    private ClassicPriorityOrderJanReplaceMapper classicPriorityOrderJanReplaceMapper;
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
    private LogAspect logAspect;
    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private StarReadingTableMapper starReadingTableMapper;
    @Autowired
    private VehicleNumCache vehicleNumCache;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private MstBranchMapper mstBranchMapper;


    public static final Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");

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
        String table1 = tableName.get(MagicString.TABLE1).toString();
        String table2 = tableName.get(MagicString.TABLE2).toString();
        String janInfoTable = tableName.get(MagicString.JANINFOTABLE).toString();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityMustMapper
                .selectMystInfo(companyCd,priorityOrderCd,table1,table2,janInfoTable,groupCompany);
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
        String table1 = tableName.get(MagicString.TABLE1).toString();
        String table2 = tableName.get(MagicString.TABLE2).toString();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        String janInfoTable = tableName.get(MagicString.JANINFOTABLE).toString();
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityNotMapper
                                                    .selectNotInfo(companyCd,priorityOrderCd,table1,table2,janInfoTable,groupCompany);
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

            String companyCd = priorityOrderCommodityMust.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderCommodityMust.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 共通メソッドを呼び出し、データを処理する
            List<PriorityOrderCommodityMust> mustList =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityMust.class,priorityOrderCommodityMust,
                    companyCd ,priorityOrderCd);
            mustList = mustList.stream().filter(map -> map.getJan() != null && !"".equals(map.getJan())).collect(Collectors.toList());
            logger.info("必須商品リストの処理後のパラメータを保存する：{}",mustList);
            //削除
            priorityOrderCommodityMustMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            if (mustList.isEmpty()){
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
            PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
            ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
            String proInfoTable = commonTableName.getProInfoTable();
            String storeInfoTable = commonTableName.getStoreInfoTable();
            String storeIsCore = commonTableName.getStoreIsCore();
            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd,storeInfoTable,storeIsCore);
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

            // jancheck
            List<String> janList = mustList.stream().map(PriorityOrderCommodityMust::getJan).collect(Collectors.toList());
            List<String> existJan = classicPriorityOrderJanReplaceMapper.selectJanDistinctByJan(proInfoTable, janList,priorityOrderCd);
            List<String> notExistJan = ListDisparityUtils.getListDisparitStr(janList,existJan);
            List<PriorityOrderCommodityMust> existJanList = mustList.stream().filter(map -> existJan.contains(map.getJan())).collect(Collectors.toList());


            List<String> notBranchExists = new ArrayList<>();
            List<PriorityOrderCommodityMust> exists = new ArrayList<>();
            for (int i = 0; i < existJanList.size(); i++) {
                PriorityOrderCommodityMust must = existJanList.get(i);
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
            if (!exists.isEmpty()){
                //データベースへの書き込み
                priorityOrderCommodityMustMapper.insert(exists);
            }
           if (!notExistJan.isEmpty()){
               return ResultMaps.result(ResultEnum.BRANCHNOTESISTS, Joiner.on(",").join(notExistJan));
           }else if (!notBranchExists.isEmpty()){
               return ResultMaps.result(ResultEnum.BRANCHNOTESISTS, Joiner.on(",").join(notBranchExists));
           }
           else{
                return ResultMaps.result(ResultEnum.SUCCESS);
           }

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
            not = not.stream().filter(map -> map.getJan() != null && !"".equals(map.getJan())).collect(Collectors.toList());
            logger.info("不可商品リスト処理後のパラメータを保存する：{}",not);
            //削除
            priorityOrderCommodityNotMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            if (not.isEmpty()){
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
            // jancheck
            PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
            ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
            String proInfoTable = commonTableName.getProInfoTable();
            String storeInfoTable = commonTableName.getStoreInfoTable();
            String storeIsCore = commonTableName.getStoreIsCore();
            // 査詢企業的店cd是几位数
            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd,storeInfoTable,storeIsCore);
            int branchLen = branchCd.length();
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



            List<String> janList = not.stream().map(PriorityOrderCommodityNot::getJan).collect(Collectors.toList());
            List<String> existJan = classicPriorityOrderJanReplaceMapper.selectJanDistinctByJan(proInfoTable, janList,priorityOrderCd);
            List<String> notExistJan = ListDisparityUtils.getListDisparitStr(janList,existJan);
            List<PriorityOrderCommodityNot> existJanList = not.stream().filter(map -> existJan.contains(map.getJan())).collect(Collectors.toList());
            List<String> notBranchExists = new ArrayList<>();
            List<PriorityOrderCommodityNot> exists = new ArrayList<>();
            for (int i = 0; i < existJanList.size(); i++) {
                PriorityOrderCommodityNot commodityNot = existJanList.get(i);
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
            if (!exists.isEmpty()){
                //写入数据庫
                priorityOrderCommodityNotMapper.insert(exists);
            }
            if (!notExistJan.isEmpty()){
                return ResultMaps.result(ResultEnum.JANNOTESISTS,Joiner.on(",").join(notExistJan));
            }else if(!notBranchExists.isEmpty()){
                return ResultMaps.result(ResultEnum.BRANCHNOTESISTS,Joiner.on(",").join(notBranchExists));
            }else{
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("不可商品リストの保存：",e);
            logAspect.setTryErrorLog(e,new Object[]{priorityOrderCommodityNot});
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
        Map<String, Object> tableNameList = this.getTableName(companyCd, priorityOrderCd);
        String table1 = tableNameList.get(MagicString.TABLE1).toString();
        String table2 = tableNameList.get(MagicString.TABLE2).toString();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrSortList(companyCd, priorityOrderCd);
        priorityOrderDataService.getPriorityOrderListInfo(companyCd,priorityOrderCd,attrList);
        List<Map<String, Object>> priorityOrderAttr = classicPriorityOrderDataMapper.getPriorityOrderMustAttr( attrList,mustTable,priorityOrderCd);
        for (Map<String, Object> objectMap : priorityOrderAttr) {
            objectMap.put("__children",new ArrayList<>());
        }
        List<Map<String, Object>> priorityOrderMustList = priorityOrderCommodityMustMapper.getPriorityOrderMustList(companyCd, priorityOrderCd, attrList);
        for (Map<String, Object> objectMap : priorityOrderMustList) {

            List<PriorityOrderBranchNumDto> children = priorityOrderCommodityMustMapper.getBranchAndPattern(objectMap.get(MagicString.JAN).toString()
                    , priorityOrderCd,table1,table2,groupCompany);
            objectMap.put("__children",children);
        }
        priorityOrderMustList.addAll(priorityOrderAttr);
        priorityOrderMustList = priorityOrderMustList.stream()
                .sorted(Comparator.comparing(map1 -> MapUtils.getString(map1, MagicString.JAN))).collect(Collectors.toList());
        Map<String,Object> map = new HashMap<>();
        List<PriorityOrderMstAttrSortDto> priorityOrderMstAttrSorts = classicPriorityOrderMstAttrSortMapper.selectWKRankSort(companyCd, priorityOrderCd);
        Map<String, String> attrMap = priorityOrderMstAttrSorts.stream()
                .collect(Collectors.toMap(PriorityOrderMstAttrSortDto::getSort, PriorityOrderMstAttrSortDto::getName,
                        (k1,k2)->k1, LinkedHashMap::new));
        map.put(MagicString.JAN, "Jan");
        map.put(MagicString.JAN_NAME, "商品名");
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
        Map<String, Object> tableNameList = this.getTableName(companyCd, priorityOrderCd);
        String table1 = tableNameList.get(MagicString.TABLE1).toString();
        String table2 = tableNameList.get(MagicString.TABLE2).toString();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        List<String> attrList = classicPriorityOrderMstAttrSortMapper.getAttrSortList(companyCd, priorityOrderCd);
        priorityOrderDataService.getPriorityOrderListInfo(companyCd,priorityOrderCd,attrList);
        List<Map<String, Object>> priorityOrderAttr = classicPriorityOrderDataMapper.getPriorityOrderMustAttr( attrList,mustTable,priorityOrderCd);
        for (Map<String, Object> objectMap : priorityOrderAttr) {
            objectMap.put("__children",new ArrayList<>());
        }
        List<Map<String, Object>> priorityOrderNotList = priorityOrderCommodityNotMapper.getPriorityOrderNotList(companyCd, priorityOrderCd, attrList);
        for (Map<String, Object> objectMap : priorityOrderNotList) {

            List<PriorityOrderBranchNumDto> children = priorityOrderCommodityNotMapper.getBranchAndPattern(objectMap.get(MagicString.JAN).toString()
                    , companyCd,priorityOrderCd,table1,table2,groupCompany);
            objectMap.put("__children",children);
        }
        priorityOrderNotList.addAll(priorityOrderAttr);
        priorityOrderNotList = priorityOrderNotList.stream()
                .sorted(Comparator.comparing(map1 -> MapUtils.getString(map1, MagicString.JAN))).collect(Collectors.toList());
        Map<String,Object> map = new HashMap<>();
        List<PriorityOrderMstAttrSortDto> priorityOrderMstAttrSorts = classicPriorityOrderMstAttrSortMapper.selectWKRankSort(companyCd, priorityOrderCd);
        Map<String, String> attrMap = priorityOrderMstAttrSorts.stream()
                .collect(Collectors.toMap(PriorityOrderMstAttrSortDto::getSort, PriorityOrderMstAttrSortDto::getName,
                        (k1,k2)->k1, LinkedHashMap::new));
        map.put(MagicString.JAN, "Jan");
        map.put(MagicString.JAN_NAME, "商品名");
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
        Map<String, Object> tableNameList = this.getTableName(companyCd, priorityOrderCd);
        String table1 = tableNameList.get(MagicString.TABLE1).toString();
        String table2 = tableNameList.get(MagicString.TABLE2).toString();
        String tableName = "work_priority_order_commodity_must";
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        List<CommodityBranchVO> existCommodityMustBranchList = priorityOrderCommodityMustMapper.getExistCommodityMustBranchList(companyCd,priorityOrderCd, jan,table1,table2,groupCompany);
        if(existCommodityMustBranchList.isEmpty()){
            priorityOrderCommodityMustMapper.insertCommodityBranchList(companyCd, priorityOrderCd, jan, tableName,table1,table2);
        }else {
            priorityOrderCommodityMustMapper.insertSurplusCommodityBranch(companyCd, priorityOrderCd, jan, tableName,existCommodityMustBranchList,table1,table2);
        }

        existCommodityMustBranchList = priorityOrderCommodityMustMapper.getExistCommodityMustBranchList(companyCd,priorityOrderCd, jan,table1,table2,groupCompany);

        return ResultMaps.result(ResultEnum.SUCCESS, existCommodityMustBranchList);
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
        Map<String, Object> tableNameList = this.getTableName(companyCd, priorityOrderCd);
        String table1 = tableNameList.get(MagicString.TABLE1).toString();
        String table2 = tableNameList.get(MagicString.TABLE2).toString();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        List<CommodityBranchVO> existCommodityNotBranchList = priorityOrderCommodityNotMapper.getExistCommodityNotBranchList(companyCd,priorityOrderCd, jan,table1,table2,groupCompany);
        if(existCommodityNotBranchList.isEmpty()){
            priorityOrderCommodityMustMapper.insertCommodityBranchList(companyCd, priorityOrderCd, jan,tableName,table1,table2);
        }else {
            priorityOrderCommodityMustMapper.insertSurplusCommodityBranch(companyCd, priorityOrderCd, jan, tableName,existCommodityNotBranchList,table1,table2);
        }

        existCommodityNotBranchList = priorityOrderCommodityNotMapper.getExistCommodityNotBranchList(companyCd,priorityOrderCd, jan,table1,table2,groupCompany);

        return ResultMaps.result(ResultEnum.SUCCESS, existCommodityNotBranchList);
    }
    /**
     * 詳細保存不可
     * @param companyCd,priorityOrderCd,jan,commodityList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveCommodityNotBranchList(String companyCd, Integer priorityOrderCd, String jan, String commodityNot) {
        List<Map<String, Object>> commodityNotList = new Gson().fromJson(commodityNot, new TypeToken<List<Map<String, Object>>>(){}.getType());
        priorityOrderCommodityNotMapper.updateFlag(companyCd, priorityOrderCd, jan, commodityNotList);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 詳細を保存する必要があります
     * @param companyCd,priorityOrderCd,jan,commodityList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveCommodityMustBranchList(String companyCd, Integer priorityOrderCd, String jan, String commodityMust) {
        List<Map<String, Object>> commodityNotList = new Gson().fromJson(commodityMust, new TypeToken<List<Map<String, Object>>>(){}.getType());
        priorityOrderCommodityMustMapper.updateFlag(companyCd, priorityOrderCd, jan, commodityNotList);
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

    @Override
    public Map<String, Object> getStarReadingTable(StarReadingTableDto starReadingTableDto) {
        Integer priorityOrderCd = starReadingTableDto.getPriorityOrderCd();
        String companyCd = starReadingTableDto.getCompanyCd();
        StringBuilder column = new StringBuilder("jan,janName,maker,total");
        StringBuilder header = new StringBuilder("JAN,商品名,メーカー,合計");
        Map<String,Object> mapResult = new HashMap();
        LinkedHashMap<String, Object> group = new LinkedHashMap<>();
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int existTableName = mstBranchMapper.checkTableExist("prod_0000_jan_info", coreCompany);

        if (existTableName == 0){
            coreCompany = companyCd;
        }
        String tableName = MessageFormat.format("\"{0}\".ten_0000_ten_info", coreCompany);
        PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
        String makerCol = classicPriorityOrderMstMapper.getMakerCol(commonTableName.getStoreIsCore(),commonTableName.getStoreMstClass());
        List<Map<String, Object>> janName = classicPriorityOrderDataMapper.getJanName(starReadingTableDto.getJanList(),priorityOrderCd,makerCol,commonTableName.getProInfoTable());
        if (starReadingTableDto.getModeCheck() == 1) {
            List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
            groupCompany.add(companyCd);
            List<Map<String, Object>> branchList = starReadingTableMapper.getBranchList(priorityOrderCd,groupCompany,tableName);
            branchList=branchList.stream().filter(map -> starReadingTableDto.getExpressItemList().contains(map.get(MagicString.sort))).collect(Collectors.toList());
            List<Map<String, Object>>  autoBranch = starReadingTableMapper.getBranchdiffForBranch(starReadingTableDto,branchList,tableName,groupCompany);

            if (!autoBranch.isEmpty()){
                for (Map<String, Object> branch : autoBranch) {
                    for (Map<String, Object> objectMap : branchList) {
                        if (branch.get(MagicString.area).equals(objectMap.get(MagicString.area))){
                            branch.put(MagicString.sort,objectMap.get(MagicString.sort));
                        }
                    }
                }
            }
            List<Map<String, Object>> list = new ArrayList();

            for (Map<String,Object> janMap : janName) {
                Map<String,Object> map = new HashMap<>();
                map.put(MagicString.JAN,janMap.get(MagicString.JAN));
                map.put(MagicString.JAN_NAME,janMap.get(MagicString.JAN_NAME));
                map.put("maker",janMap.get("maker"));
                map.put("total","");
                for (Map<String, Object> objectMap : branchList) {
                    map.put(objectMap.get(MagicString.sort)+"_"+objectMap.get("branchCd").toString(),"☓");
                }
                list.add(map);
            }
            Map<String, List<Map<String, Object>>> janGroup = autoBranch.stream()
                    .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.JAN)));
            for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
                for (Map<String, Object> map : stringListEntry.getValue()) {
                    for (Map<String, Object> stringObjectMap : list) {
                        if (stringObjectMap.get(MagicString.JAN).equals(stringListEntry.getKey())){
                                stringObjectMap.put(map.get(MagicString.sort)+"_"+map.get("branchCd"),map.get("flag"));
                        }
                    }
                }

            }
            for (Map<String, Object> objectMap : branchList) {
                column.append(",").append(objectMap.get(MagicString.sort)).append("_").append(objectMap.get("branchCd"));
                header.append(",").append(objectMap.get("branchCd")).append("<br />").append(objectMap.get("branchName"));
                group.put( objectMap.get(MagicString.sort).toString(), objectMap.get(MagicString.area));
            }
            mapResult.put("column", column.toString());
            mapResult.put("header", header.toString());
            mapResult.put("group", group);
            list = list.stream().sorted(Comparator.comparing(map->MapUtils.getString(map,MagicString.JAN))).collect(Collectors.toList());
            mapResult.put("data", list);

        }else {
            List<String> expressItemList = starReadingTableDto.getExpressItemList();
            List<String> pattern = expressItemList.stream().map(item -> item.split("pattern")[1]).collect(Collectors.toList());
            List<Map<String, Object>> patternNameList = starReadingTableMapper.getPatternNameList(priorityOrderCd);
            patternNameList = patternNameList.stream().filter(map->expressItemList.contains(map.get("id").toString())).collect(Collectors.toList());
            List<Map<String, Object>> patternDiffForPattern = starReadingTableMapper.getPatterndiffForPattern(starReadingTableDto, pattern);
            List<Map<String, Object>> list = new ArrayList();
            for (Map<String,Object> janMap : janName) {
                Map<String,Object> map = new HashMap<>();
                map.put(MagicString.JAN,janMap.get(MagicString.JAN));
                map.put(MagicString.JAN_NAME,janMap.get(MagicString.JAN_NAME));
                map.put("maker",janMap.get("maker"));
                map.put("total","");
                for (Map<String, Object> objectMap : patternNameList) {
                    map.put(objectMap.get("id")+"_"+objectMap.get("shelfPatternCd").toString(),"☓");
                }
                list.add(map);
            }

            Map<String, List<Map<String, Object>>> janGroup = patternDiffForPattern.stream()
                    .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.JAN)));
            for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
                for (Map<String, Object> map : stringListEntry.getValue()) {
                    for (Map<String, Object> stringObjectMap : list) {
                        if (stringObjectMap.get(MagicString.JAN).equals(stringListEntry.getKey())){
                            stringObjectMap.put(map.get("id")+"_"+map.get("shelfPatternCd"),map.get("flag"));
                        }
                    }
                }

            }

            for (Map<String, Object> objectMap : patternNameList) {
                column.append(",").append(objectMap.get("id")).append("_").append(objectMap.get("shelfPatternCd"));
                header.append(",").append(objectMap.get("shelfPatternName"));
                group.put( objectMap.get("id").toString(), objectMap.get("shelfName"));
            }
            mapResult.put("column", column.toString());
            mapResult.put("header", header.toString());
            mapResult.put("group", group);
            list = list.stream().sorted(Comparator.comparing(map->MapUtils.getString(map,MagicString.JAN))).collect(Collectors.toList());
            mapResult.put("data", list);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,mapResult);
    }

    @Override
    public Map<String, Object> getStarReadingParam(String companyCd,Integer priorityOrderCd) {
        logger.info("start:{}",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
        String makerCol = classicPriorityOrderMstMapper.getMakerCol(commonTableName.getStoreIsCore(),commonTableName.getStoreMstClass());
        Integer modeCheck = priorityOrderMstMapper.getModeCheck(priorityOrderCd);
        boolean isModeCheck = true;
        if (modeCheck == null){
            modeCheck =1;
            isModeCheck = false;
        }

        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int existTableName = mstBranchMapper.checkTableExist("prod_0000_jan_info", coreCompany);

        if (existTableName == 0){
            coreCompany = companyCd;
        }
        String tableName = MessageFormat.format("\"{0}\".ten_0000_ten_info", coreCompany);

        List<Map<String, Object>> expressItemList =new ArrayList<>();
        String janInfoTableName = "";
        StringBuilder column = new StringBuilder("jan,janName,maker,total");
        StringBuilder header = new StringBuilder("JAN,商品名,メーカー,合計");
        Map<String,Object> mapResult = new HashMap();
        LinkedHashMap<String, Object> group = new LinkedHashMap<>();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);

        if (modeCheck == 1){
            janInfoTableName = "priority.work_priority_order_commodity_branch";
            logger.info("branch:{}",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            List<Map<String, Object>> branchDiff = starReadingTableMapper.getBranchdiff(priorityOrderCd);
            if (!branchDiff.isEmpty()){
                List<Map<String, Object>> janOrName = starReadingTableMapper.getJanOrName(companyCd, priorityOrderCd,commonTableName.getProInfoTable(),makerCol);
                List<Map<String, Object>> branchList = starReadingTableMapper.getBranchList(priorityOrderCd,groupCompany,tableName);
                List<Object> branchCd = branchDiff.stream().map(map -> map.get("branchCd")).collect(Collectors.toList());
                branchList=branchList.stream().filter(map ->branchCd.contains(map.get("branchCd"))).collect(Collectors.toList());
                Map<String, List<Map<String, Object>>> janGroup = branchDiff.stream()
                        .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.JAN)));

                List<Map<String, Object>> list = new ArrayList();
                for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
                    for (Map<String, Object> stringObjectMap : janOrName) {
                        if (stringListEntry.getKey().equals(stringObjectMap.get("jan_new"))){
                            stringListEntry.getValue().get(0).put(MagicString.JAN_NAME,stringObjectMap.get("sku"));
                            stringListEntry.getValue().get(0).put("maker",stringObjectMap.get("maker"));
                        }
                    }
                    Map<String,Object> map = new HashMap<>();
                    map.put(MagicString.JAN,stringListEntry.getValue().get(0).get(MagicString.JAN));
                    map.put(MagicString.JAN_NAME,stringListEntry.getValue().get(0).get(MagicString.JAN_NAME));
                    map.put("maker",stringListEntry.getValue().get(0).get("maker"));
                    map.put("total","");
                    for (Map<String, Object> objectMap : branchList) {
                        for (Map<String, Object> stringObjectMap : stringListEntry.getValue()) {
                            if (objectMap.get("branchCd").equals(stringObjectMap.get("branchCd"))){
                                map.put(objectMap.get(MagicString.sort)+"_"+objectMap.get("branchCd"),stringObjectMap.get("flag"));
                            }
                        }
                    }
                    list.add(map);
                }

                for (Map<String, Object> objectMap : branchList) {
                    column.append(",").append(objectMap.get(MagicString.sort)).append("_").append(objectMap.get("branchCd"));
                    header.append(",").append(objectMap.get("branchCd")).append("<br />").append(objectMap.get("branchName"));
                    group.put( objectMap.get("sort").toString(), objectMap.get(MagicString.area));
                }
                mapResult.put("column", column.toString());
                mapResult.put("header", header.toString());
                mapResult.put("group", group);
                list = list.stream().sorted(Comparator.comparing(map->MapUtils.getString(map,MagicString.JAN))).collect(Collectors.toList());
                mapResult.put("data", list);
            }
        }else if (modeCheck == 0){
            janInfoTableName = "priority.work_priority_order_commodity_pattern";
            List<Map<String, Object>> patternDiff = starReadingTableMapper.getPatterndiff(priorityOrderCd, commonTableName.getProInfoTable(), makerCol);
            if (!patternDiff.isEmpty()) {
                List<Map<String, Object>> patternNameList = starReadingTableMapper.getPatternNameList(priorityOrderCd);
                List<String> shelfNameCd = patternDiff.stream().map(map -> "pattern" + map.get("shelfNameCd").toString()).collect(Collectors.toList());
                patternNameList = patternNameList.stream().filter(map -> shelfNameCd.contains(map.get("id").toString())).collect(Collectors.toList());
                List<Map<String, Object>> list = new ArrayList();
                Map<String, List<Map<String, Object>>> janGroup = patternDiff.stream()
                        .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.JAN)));
                for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
                    Map<String, Object> patternMap = new HashMap<>();
                    patternMap.put(MagicString.JAN, stringListEntry.getKey());
                    patternMap.put(MagicString.JAN_NAME, stringListEntry.getValue().get(0).get(MagicString.JAN_NAME));
                    patternMap.put("maker", stringListEntry.getValue().get(0).get("maker"));
                    patternMap.put("total", "");
                    for (Map<String, Object> map : stringListEntry.getValue()) {
                        for (Map<String, Object> stringObjectMap : patternNameList) {
                            if (stringObjectMap.get("shelfPatternCd").equals(map.get("shelfPatternCd"))) {
                                patternMap.put(stringObjectMap.get("id") + "_" + map.get("shelfPatternCd"), map.get("flag"));
                            }
                        }
                    }
                    list.add(patternMap);
                }

                for (Map<String, Object> objectMap : patternNameList) {
                    column.append(",").append(objectMap.get("id")).append("_").append(objectMap.get("shelfPatternCd"));
                    header.append(",").append(objectMap.get("shelfPatternName"));
                    group.put(objectMap.get("id").toString(), objectMap.get("shelfName"));
                }
                mapResult.put("column", column.toString());
                mapResult.put("header", header.toString());
                mapResult.put("group", group);
                list = list.stream().sorted(Comparator.comparing(map -> MapUtils.getString(map, MagicString.JAN))).collect(Collectors.toList());
                mapResult.put("data", list);
            }
        }
        logger.info("branch end:{}",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        List<Map<String, Object>> areaList = starReadingTableMapper.getAreaList(priorityOrderCd,groupCompany,tableName);
        List<Map<String, Object>>  patternList =  starReadingTableMapper.getPatternList(priorityOrderCd);
        expressItemList.addAll(areaList);
        expressItemList.addAll(patternList);
        List<Map<String, Object>> janList = classicPriorityOrderDataMapper.getJanInfo(priorityOrderCd,janInfoTableName);

        Map<String,Object> map = new HashMap<>();
        map.put("expressItemList",expressItemList);
        map.put("janList",janList);
        map.put("modeCheck",modeCheck);
        map.put("data",!isModeCheck?null:mapResult);
        logger.info("end:{}",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setStarReadingData(StarReadingVo starReadingVo) {
        String save = "save";
        if (starReadingVo.getTaskID()!=null){
            if ("1".equals(vehicleNumCache.get(save+starReadingVo.getTaskID()))) {
                vehicleNumCache.remove(save+starReadingVo.getTaskID());
                return ResultMaps.result(ResultEnum.SUCCESS);
            } else if ("2".equals(vehicleNumCache.get(save+starReadingVo.getTaskID()))) {
                vehicleNumCache.remove(save+starReadingVo.getTaskID());
                return ResultMaps.result(ResultEnum.FAILURE);
            }else {
                Map<String,Object> map = new HashMap<>();
                map.put("status","9");
                map.put("taskID",starReadingVo.getTaskID());
                return ResultMaps.result(ResultEnum.SUCCESS,map);
            }
        }
        Integer priorityOrderCd = starReadingVo.getPriorityOrderCd();
        String companyCd = starReadingVo.getCompanyCd();
        starReadingTableMapper.delBranchList(companyCd,priorityOrderCd);
        starReadingTableMapper.delPatternList(companyCd,priorityOrderCd);
        priorityOrderMstMapper.updateModeCheck(starReadingVo);
        if (starReadingVo.getData().isEmpty()){
            starReadingVo.setModeCheck(null);
            priorityOrderMstMapper.updateModeCheck(starReadingVo);
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        if (starReadingVo.getGroup() == null){
            starReadingVo.setFlag(0);
            Map<String, Object> stringObjectMap = this.rowColumnConversion(starReadingVo);
            starReadingVo = (StarReadingVo) stringObjectMap.get("data");
        }
        String uuid = UUID.randomUUID().toString();
        StarReadingVo finalStarReadingVo = starReadingVo;
        Future<?> future = executor.submit(() -> {
            try {
                List<Map<String, Object>> list = new ArrayList<>();

                for (LinkedHashMap<String, Object> datum : finalStarReadingVo.getData()) {
                    String jan = datum.get(MagicString.JAN).toString();
                    for (Map.Entry<String, Object> stringObjectEntry : datum.entrySet()) {
                        if (!stringObjectEntry.getKey().equals(MagicString.JAN) && !stringObjectEntry.getKey().equals(MagicString.JAN_NAME) && !stringObjectEntry.getKey().equals("total")
                                &&!stringObjectEntry.getKey().equals("maker")) {
                            Map<String, Object> map = new HashMap<>();
                            map.put(MagicString.JAN, jan);
                            map.put("branch", stringObjectEntry.getKey().split("_")[1]);
                            map.put("flag", stringObjectEntry.getValue().equals("☓") ? -1 : stringObjectEntry.getValue().equals("") ? 0 : 1);
                            if (finalStarReadingVo.getModeCheck() == 0){
                                map.put("patternNameCd",Integer.valueOf(stringObjectEntry.getKey().split("_")[0].split("pattern")[1]));
                            }
                            for (Map.Entry<String, Object> objectEntry : finalStarReadingVo.getGroup().entrySet()) {
                                if (objectEntry.getKey().equals(stringObjectEntry.getKey().split("_")[0])) {
                                    map.put(MagicString.area, objectEntry.getValue());
                                }
                            }
                            list.add(map);
                        }

                    }


                    if (list.size() >= 5000){
                        if (finalStarReadingVo.getModeCheck() == 1) {
                            starReadingTableMapper.setBranchList(list,companyCd,priorityOrderCd);
                        }else {
                            starReadingTableMapper.setPatternList(list,companyCd,priorityOrderCd);
                        }
                        list.clear();
                    }
                }
                if (!list.isEmpty()) {
                    if (finalStarReadingVo.getModeCheck() == 1) {
                        starReadingTableMapper.setBranchList(list, companyCd, priorityOrderCd);
                    } else {
                        starReadingTableMapper.setPatternList(list, companyCd, priorityOrderCd);
                    }
                }
                vehicleNumCache.put(save + uuid, "1");
            } catch (NumberFormatException e) {
                vehicleNumCache.put(save+uuid,"2");
            }
        });
        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e){
            logger.error("thread is interrupted", e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            Map<String,Object> map = new HashMap<>();
            map.put("status","9");
            map.put("taskID",uuid);
            return ResultMaps.result(ResultEnum.SUCCESS, map);
        }
        if ("2".equals(vehicleNumCache.get(save+uuid))){
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public void starReadingDataForExcel(StarReadingVo starReadingVo, HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        if (starReadingVo.getGroup() == null){
            starReadingVo.setFlag(0);
            Map<String, Object> stringObjectMap = this.rowColumnConversion(starReadingVo);
            starReadingVo = (StarReadingVo) stringObjectMap.get("data");
            List<LinkedHashMap<String, Object>> data = starReadingVo.getData();
            for (LinkedHashMap<String, Object> datum : data) {
                int total = 0;
                for (Map.Entry<String, Object> stringObjectEntry : datum.entrySet()) {
                    if (stringObjectEntry.getValue().equals("◯")){
                        total++;
                    }

                }
                datum.put("total",total);
            }
            starReadingVo.setData(data);
        }
        List<String> header = Arrays.asList(starReadingVo.getHeader().toString().replaceAll("<br />","_").split(","));
        List<String>  column = Arrays.asList(starReadingVo.getColumn().split(","));
        List<String>  columns = new ArrayList<>(column);
        Map<String, Object> group = starReadingVo.getGroup();
        LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : group.entrySet()) {
            List<String> list =new ArrayList<>();
            for (int i = 0; i < header.size(); i++) {

                if (column.get(i).split("_")[0].equals(stringObjectEntry.getKey())){
                    list.add(header.get(i));
                }
            }
            linkedHashMap.put(stringObjectEntry.getValue().toString(),list);
        }

        try {
            String fileName = String.format("%s.xlsx", "必須不可出力");
            String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(fileName, "utf-8"));
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            response.setHeader("Content-Disposition", format);
            outputStream = response.getOutputStream();

            ExcelUtils.starReadingExcel(header,columns, linkedHashMap,starReadingVo.getData() ,outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Map<String, Object> rowColumnConversion(StarReadingVo starReadingVo) {
        if (starReadingVo.getFlag()==1) {
            List<String> list = Arrays.asList(starReadingVo.getColumn().split(",")).stream().filter(item -> !item.equals(MagicString.JAN) && !item.equals(MagicString.JAN_NAME) && !item.equals("total") && !item.equals("maker")).collect(Collectors.toList());
            List<String> header = Arrays.asList(starReadingVo.getHeader().toString().split(",")).stream().filter(item -> !item.equals("商品名") && !item.equals("JAN") && !item.equals("合計") && !item.equals("メーカー")).collect(Collectors.toList());
            Map<String, Object> group = starReadingVo.getGroup();
            List<String> columnList = new ArrayList<>(list);
            List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
            List<String> headers = new ArrayList<>();
            StringBuilder header1 = new StringBuilder();
            StringBuilder header2 = new StringBuilder();
            StringBuilder header3 = new StringBuilder();
            StringBuilder header4 = new StringBuilder();
            if (starReadingVo.getModeCheck() == 1) {
                 header4 = new StringBuilder("エリア,店舗CD,店舗名");
            }else {
                 header4 = new StringBuilder("棚名称,棚パターンCD,棚パターン");
            }
            StringBuilder column = new StringBuilder("area,branchCd,branch");
            List<LinkedHashMap<String, Object>> data = starReadingVo.getData().stream().sorted(Comparator.comparing(map -> MapUtils.getString(map, "jan"))).collect(Collectors.toList());
            for (LinkedHashMap<String, Object> datum : data) {
                if (header1.toString().equals("")) {
                    header1.append(datum.get("maker"));
                    header2.append(datum.get("total"));
                    header3.append(datum.get(MagicString.JAN));
                }else {
                    header1.append(",").append(datum.get("maker"));
                    header2.append(",").append(datum.get("total"));
                    header3.append(",").append(datum.get(MagicString.JAN));

                }
                header4.append(",").append(datum.get(MagicString.JAN_NAME));
                column.append(",jan").append(datum.get(MagicString.JAN));
            }
            headers.add(header1.toString());
            headers.add(header3.toString());
            headers.add(header4.toString());
            for (int i = 0; i < columnList.size(); i++) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                if (starReadingVo.getModeCheck() == 1) {
                    map.put("branch", header.get(i).split("<br />")[1]);
                }else {
                    map.put("branch", header.get(i));
                }
                map.put(MagicString.area, group.get(columnList.get(i).split("_")[0]));
                map.put("branchCd", columnList.get(i).split("_")[1]);
                map.put("areaCd", columnList.get(i).split("_")[0]);
                for (LinkedHashMap<String, Object> datum : starReadingVo.getData()) {
                    map.put(MagicString.JAN + datum.get(MagicString.JAN), datum.get(columnList.get(i)));
                }
                resultList.add(map);
            }
            StarReadingVo starReadingVo1 = new StarReadingVo();
            starReadingVo1.setColumn(column.toString());
            starReadingVo1.setHeader(headers);
            starReadingVo1.setData(resultList);
            starReadingVo1.setFlag(1);
            starReadingVo1.setModeCheck(starReadingVo.getModeCheck());
            return ResultMaps.result(ResultEnum.SUCCESS,starReadingVo1);
        }else {
           List<String> headers =  (List<String>) starReadingVo.getHeader();
            List<String> header1 = Arrays.asList(headers.get(0).split(","));
            List<String> header2 = Arrays.asList(headers.get(1).split(","));
            List<String> header3 = Arrays.asList(headers.get(2).split(","));
            List<LinkedHashMap<String,Object>> resultList = new ArrayList<>();
            for (int i = 0; i < header2.size(); i++) {
                LinkedHashMap<String,Object> map= new LinkedHashMap<>();
                map.put(MagicString.JAN,header2.get(i));
                map.put(MagicString.JAN_NAME,header3.get(i+3));
                map.put("maker",header1.get(i));
                map.put("total","");
                for (LinkedHashMap<String, Object> datum : starReadingVo.getData()) {
                    map.put(datum.get("areaCd")+"_"+datum.get("branchCd"),datum.get(MagicString.JAN+header2.get(i)));
                }

                resultList.add(map);
            }
            StringBuilder column = new StringBuilder("jan,janName,maker,total");
            StringBuilder header = new StringBuilder("JAN,商品名,メーカー,合計");
            Map<String,Object> group = new LinkedHashMap<>();
            for (LinkedHashMap<String, Object> datum : starReadingVo.getData()) {
                    column.append(",").append(datum.get("areaCd")).append("_").append(datum.get("branchCd"));
                if (starReadingVo.getModeCheck() == 1) {
                    header.append(",").append(datum.get("branchCd")).append("<br />").append(datum.get("branch"));
                }else {
                    header.append(",").append(datum.get("branch"));
                }
                group.putIfAbsent(datum.get("areaCd").toString(),datum.get("area"));
            }
            StarReadingVo starReadingVo1 = new StarReadingVo();
            starReadingVo1.setColumn(column.toString());
            starReadingVo1.setHeader(header.toString());
            starReadingVo1.setData(resultList);
            starReadingVo1.setGroup(group);
            starReadingVo1.setFlag(0);
            starReadingVo1.setModeCheck(starReadingVo.getModeCheck());
            return ResultMaps.result(ResultEnum.SUCCESS,starReadingVo1);
        }

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
            if (list.get(0).getStoreIsCore().equals("1000")){
                table1 = list.get(0).getStoreInfoTable();
            }else {
                table2 = list.get(0).getStoreInfoTable();
            }

        }
        Map<String,Object> map = new HashMap<>();
        map.put(MagicString.TABLE1,table1);
        map.put(MagicString.TABLE2,table2);
        map.put(MagicString.JANINFOTABLE,janInfoTable);
        return map;
    }

}
