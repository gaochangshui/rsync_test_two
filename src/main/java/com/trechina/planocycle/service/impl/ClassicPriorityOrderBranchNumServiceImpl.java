package com.trechina.planocycle.service.impl;

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
import com.trechina.planocycle.utils.ExcelUtils;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import com.trechina.planocycle.utils.cgiUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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


    public static final Pattern pattern = Pattern.compile("^[-\\+]?\\d*$");

    /**
     * ??????????????????????????????
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCommodityMust(String companyCd, Integer priorityOrderCd) {
        logger.info("?????????????????????????????????????????????????????????{},{}",companyCd,priorityOrderCd);

        Map<String, Object> tableName = this.getTableName(companyCd, priorityOrderCd);
        String table1 = tableName.get(MagicString.TABLE1).toString();
        String table2 = tableName.get(MagicString.TABLE2).toString();
        String janInfoTable = tableName.get(MagicString.JANINFOTABLE).toString();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityMustMapper
                .selectMystInfo(companyCd,priorityOrderCd,table1,table2,janInfoTable,groupCompany);
        logger.info("???????????????????????????????????????????????????{}",priorityOrderCommodityVOList);

        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderCommodityVOList);
    }

    /**
     * ??????????????????????????????
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderCommodityNot(String companyCd, Integer priorityOrderCd) {
        logger.info("?????????????????????????????????????????????????????????{},{}",companyCd,priorityOrderCd);
        Map<String, Object> tableName = this.getTableName(companyCd, priorityOrderCd);
        String table1 = tableName.get(MagicString.TABLE1).toString();
        String table2 = tableName.get(MagicString.TABLE2).toString();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        String janInfoTable = tableName.get(MagicString.JANINFOTABLE).toString();
        List<PriorityOrderCommodityVO> priorityOrderCommodityVOList = priorityOrderCommodityNotMapper
                                                    .selectNotInfo(companyCd,priorityOrderCd,table1,table2,janInfoTable,groupCompany);
        logger.info("???????????????????????????????????????????????????{}",priorityOrderCommodityVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderCommodityVOList);
    }

    /**
     * ???????????????????????????
     *
     * @param priorityOrderCommodityMust
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCommodityMust(List<PriorityOrderCommodityMust> priorityOrderCommodityMust) {
//        logger.info("??????????????????????????????????????????????????????{}",priorityOrderCommodityMust);
////??????????????????????????????1????????????????????????????????????cd??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//
//            String companyCd = priorityOrderCommodityMust.get(0).getCompanyCd();
//            Integer priorityOrderCd = priorityOrderCommodityMust.get(0).getPriorityOrderCd();
//            dataConverUtils dataConverUtil = new dataConverUtils();
//            // ????????????????????????????????????????????????????????????
//            List<PriorityOrderCommodityMust> mustList =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityMust.class,priorityOrderCommodityMust,
//                    companyCd ,priorityOrderCd);
//            // map.getJan() != null&& !"".equals(map.getJan())
//            mustList = mustList.stream().filter(map -> !Strings.isNullOrEmpty(map.getJan())).collect(Collectors.toList());
//            logger.info("?????????????????????????????????????????????????????????????????????{}",mustList);
//            //??????
//            priorityOrderCommodityMustMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
//            if (mustList.isEmpty()){
//                return ResultMaps.result(ResultEnum.SUCCESS);
//            }
//            PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
//            ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
//            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
//            String proInfoTable = commonTableName.getProInfoTable();
//            String storeInfoTable = commonTableName.getStoreInfoTable();
//            String storeIsCore = commonTableName.getStoreIsCore();
//            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd,storeInfoTable,storeIsCore);
//            int branchLen = branchCd.length();
//            // not??????????????????cd???0???????????????
//            mustList.forEach(item->{
//                item.setBranchOrigin(item.getBranch());
//                if (!Strings.isNullOrEmpty(item.getBranch()) && pattern.matcher(item.getBranch()).matches()){
//                    item.setBranch( Strings.padStart(item.getBranch(), branchLen, '0'));
//                }
//            });
//            logger.info("?????????????????????0???????????????????????????{}",mustList);
//
//            // jancheck
//            List<String> janList = mustList.stream().map(PriorityOrderCommodityMust::getJan).collect(Collectors.toList());
//            List<String> existJan = classicPriorityOrderJanReplaceMapper.selectJanDistinctByJan(proInfoTable, janList,priorityOrderCd);
//            List<String> notExistJan = ListDisparityUtils.getListDisparitStr(janList,existJan);
//            List<PriorityOrderCommodityMust> existJanList = mustList.stream().filter(map -> existJan.contains(map.getJan())).collect(Collectors.toList());
//
//
//            List<String> notBranchExists = new ArrayList<>();
//            List<PriorityOrderCommodityMust> exists = new ArrayList<>();
//            for (int i = 0; i < existJanList.size(); i++) {
//                PriorityOrderCommodityMust must = existJanList.get(i);
//                    String branch = must.getBranch();
//                    List<Integer> patternCdList = priorityOrderCommodityMustMapper.selectPatternByBranch(priorityOrderCd, companyCd, branch);
//                    //! "".equals() &&  must.getBranchOrigin() != null
//                    if(patternCdList.isEmpty()&& !Strings.isNullOrEmpty(must.getBranchOrigin())){
//                        notBranchExists.add(must.getBranchOrigin()+"");
//                    }
//                    for (Integer patternCd : patternCdList) {
//                        PriorityOrderCommodityMust newMust = new PriorityOrderCommodityMust();
//                        BeanUtils.copyProperties(must, newMust);
//
//                        int result = priorityOrderCommodityMustMapper.selectExistJan(patternCd, must.getJan());
//                        if(result>0){
//                            newMust.setBeforeFlag(1);
//                        }else{
//                            newMust.setBeforeFlag(0);
//                        }
//                        if(patternCd!=null){
//                            newMust.setFlag(1);
//                        }else{
//                            newMust.setFlag(0);
//                        }
//                        newMust.setShelfPatternCd(patternCd);
//                        exists.add(newMust);
//                    }
//
//            }
//            if (!exists.isEmpty()){
//                //????????????????????????????????????
//                priorityOrderCommodityMustMapper.insert(exists);
//            }
//           if (!notExistJan.isEmpty()){
//               return ResultMaps.result(ResultEnum.BRANCHNOTESISTS, Joiner.on(",").join(notExistJan));
//           }else if (!notBranchExists.isEmpty()){
//               return ResultMaps.result(ResultEnum.BRANCHNOTESISTS, Joiner.on(",").join(notBranchExists));
//           }

        return ResultMaps.result(ResultEnum.SUCCESS);

    }


    /**
     * ??????????????????????????????
     *
     * @param priorityOrderCommodityNot
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderCommodityNot(List<PriorityOrderCommodityNot> priorityOrderCommodityNot) {
      /*  logger.info("????????????????????????????????????????????????{}",priorityOrderCommodityNot);
        // ???????????????????????????????????????????????????cd??????????????????????????????????????????
        try{
            String companyCd = priorityOrderCommodityNot.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderCommodityNot.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // ?????????????????????????????????
            List<PriorityOrderCommodityNot> not =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderCommodityNot.class,priorityOrderCommodityNot,
                    companyCd ,priorityOrderCd);
//            not = not.stream().filter(map -> map.getJan() != null && !"".equals(map.getJan())).collect(Collectors.toList());
            not = not.stream().filter(map -> !Strings.isNullOrEmpty(map.getJan())).collect(Collectors.toList());
            logger.info("??????????????????????????????????????????????????????????????????{}",not);
            //??????
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
            // ??????????????????cd????????????
            String branchCd = priorityOrderCommodityNotMapper.selectBranchCDForCalcLength(companyCd,storeInfoTable,storeIsCore);
            int branchLen = branchCd.length();
            // ??????not ??????cd???0
            not.forEach(item->{
                item.setBranchOrigin(item.getBranch());
                if (!Strings.isNullOrEmpty(item.getBranch()) && pattern.matcher(item.getBranch()).matches()) {
                    item.setBranch(Strings.padStart(item.getBranch(), branchLen, '0'));
                }
            });
            logger.info("????????????list??????0???????????????????????????????????????{}",not);

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

                    if(patternCdList.isEmpty() && !Strings.isNullOrEmpty(commodityNot.getBranchOrigin()) ){
                        notBranchExists.add(commodityNot.getBranchOrigin()+"");
                    }
                    patternCdList.forEach(patternCd->{
                        PriorityOrderCommodityNot newNot = new PriorityOrderCommodityNot();
                        newNot.setBeforeFlag(0);
                        newNot.setFlag(0);

                        BeanUtils.copyProperties(commodityNot, newNot);
                        int result = priorityOrderCommodityMustMapper.selectExistJan(patternCd, newNot.getJan());
                        if(result>0){
                            newNot.setBeforeFlag(1);
                        }
                        if(patternCd!=null){
                            newNot.setFlag(1);
                        }
                        newNot.setShelfPatternCd(patternCd);
                        exists.add(newNot);
                    });
            }
            //???????????????
            priorityOrderCommodityNotMapper.insert(exists);
            if (!notExistJan.isEmpty()){
                return ResultMaps.result(ResultEnum.JANNOTESISTS,Joiner.on(",").join(notExistJan));
            }else if(!notBranchExists.isEmpty()){
                return ResultMaps.result(ResultEnum.BRANCHNOTESISTS,Joiner.on(",").join(notBranchExists));
            }

            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.error("?????????????????????????????????",e);
            logAspect.setTryErrorLog(e,new Object[]{priorityOrderCommodityNot});

            return ResultMaps.result(ResultEnum.FAILURE);
        } */
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * ???????????????????????????
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
     * ???????????????????????????
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
     * ????????????????????????????????????
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
     * ???????????????
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
            objectMap.put(MagicString.CHILDREN,new ArrayList<>());
        }
        List<Map<String, Object>> priorityOrderMustList = priorityOrderCommodityMustMapper.getPriorityOrderMustList(companyCd, priorityOrderCd, attrList);
        for (Map<String, Object> objectMap : priorityOrderMustList) {

            List<PriorityOrderBranchNumDto> children = priorityOrderCommodityMustMapper.getBranchAndPattern(objectMap.get(MagicString.JAN).toString()
                    , priorityOrderCd,table1,table2,groupCompany);
            objectMap.put(MagicString.CHILDREN,children);
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
        map.put(MagicString.JAN_NAME, "?????????");
        map.put(MagicString.SHELF_PATTERN_NAME, "?????????????????????");
        map.put(MagicString.BRANCH_NAME, "??????");
        map.put("errmsg", "????????????????????????");
        map.putAll(attrMap);
        priorityOrderMustList.add(0,map);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderMustList);
    }
    /**
     * ???????????????
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
            objectMap.put(MagicString.CHILDREN,new ArrayList<>());
        }
        List<Map<String, Object>> priorityOrderNotList = priorityOrderCommodityNotMapper.getPriorityOrderNotList(companyCd, priorityOrderCd, attrList);
        for (Map<String, Object> objectMap : priorityOrderNotList) {

            List<PriorityOrderBranchNumDto> children = priorityOrderCommodityNotMapper.getBranchAndPattern(objectMap.get(MagicString.JAN).toString()
                    , companyCd,priorityOrderCd,table1,table2,groupCompany);
            objectMap.put(MagicString.CHILDREN,children);
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
        map.put(MagicString.JAN_NAME, "?????????");
        map.put(MagicString.SHELF_PATTERN_NAME, "?????????????????????");
        map.put(MagicString.BRANCH_NAME, "??????");
        map.put("errmsg", "????????????????????????");
        map.putAll(attrMap);
        priorityOrderNotList.add(0,map);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderNotList);
    }
    /**
     * ?????????????????????
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
     * ????????????
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
     * ??????????????????
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
     * ??????????????????????????????????????????
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
     * ??????????????????????????????????????????
     * @param commodityBranchPrimaryKeyVO
     * @return
     */
    @Override
    public Map<String, Object> delCommodityMustBranch(CommodityBranchPrimaryKeyVO commodityBranchPrimaryKeyVO) {
        priorityOrderCommodityMustMapper.delCommodityMustBranch(commodityBranchPrimaryKeyVO);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * ??????????????????
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
        StringBuilder column = new StringBuilder("jan,janName,total");
        StringBuilder header = new StringBuilder("JAN,?????????,??????");
        Map<String,Object> mapResult = new HashMap<>();
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
        List<Map<String, Object>> janName = classicPriorityOrderDataMapper.getJanName(starReadingTableDto.getJanList(),priorityOrderCd,commonTableName.getProInfoTable());
        if (starReadingTableDto.getModeCheck() == 1) {
             mapResult = this.calculationBranch(tableName,mapResult,starReadingTableDto,companyCd,priorityOrderCd,column,header,group,janName);

        }else {
            mapResult = this.calculationPattern(mapResult,starReadingTableDto,priorityOrderCd,column,header,group,janName);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,mapResult);
    }
    public Map<String,Object> calculationPattern( Map<String, Object> mapResult, StarReadingTableDto starReadingTableDto, Integer priorityOrderCd, StringBuilder column, StringBuilder header, Map<String, Object> group, List<Map<String, Object>> janName){
        List<String> expressItemList = starReadingTableDto.getExpressItemList();
        List<String> pattern = expressItemList.stream().map(item -> item.split(MagicString.PATTERN)[1]).collect(Collectors.toList());
        List<Map<String, Object>> patternNameList = starReadingTableMapper.getPatternNameList(priorityOrderCd);
        patternNameList = patternNameList.stream().filter(map->expressItemList.contains(map.get("id").toString())).collect(Collectors.toList());
        List<Map<String, Object>> patternDiffForPattern = starReadingTableMapper.getPatterndiffForPattern(starReadingTableDto, pattern);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String,Object> janMap : janName) {
            Map<String,Object> map = new HashMap<>();
            map.put(MagicString.JAN,janMap.get(MagicString.JAN));
            map.put(MagicString.JAN_NAME,janMap.get(MagicString.JAN_NAME));
            map.put(MagicString.TOTAL,"");
            patternNameList.forEach(objectMap-> map.put(objectMap.get("id")+"_"+objectMap.get(MagicString.SHELF_PATTERN_CD).toString(),"???"));

            list.add(map);
        }

        Map<String, List<Map<String, Object>>> janGroup = patternDiffForPattern.stream()
                .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.JAN)));
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
            for (Map<String, Object> map : stringListEntry.getValue()) {
                list.forEach(stringObjectMap->{
                    if (stringObjectMap.get(MagicString.JAN).equals(stringListEntry.getKey())){
                        stringObjectMap.put(map.get("id")+"_"+map.get(MagicString.SHELF_PATTERN_CD),map.get("flag"));
                    }
                });
            }

        }
        patternNameList.forEach(objectMap->{
            column.append(",").append(objectMap.get("id")).append("_").append(objectMap.get(MagicString.SHELF_PATTERN_CD));
            header.append(",").append(objectMap.get(MagicString.SHELF_PATTERN_NAME));
            group.put( objectMap.get("id").toString(), objectMap.get("shelfName"));
        });
        mapResult.put(MagicString.COLUMN, column.toString());
        mapResult.put(MagicString.HEADER, header.toString());
        mapResult.put(MagicString.GROUP, group);
        list = list.stream().sorted(Comparator.comparing(map->MapUtils.getString(map,MagicString.JAN))).collect(Collectors.toList());
        mapResult.put("data", list);
        return mapResult;
    }
    public Map<String,Object> calculationBranch(String tableName, Map<String, Object> mapResult, StarReadingTableDto starReadingTableDto, String companyCd, Integer priorityOrderCd, StringBuilder column, StringBuilder header, Map<String, Object> group, List<Map<String, Object>> janName){
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);
        List<Map<String, Object>> branchList = starReadingTableMapper.getBranchList(priorityOrderCd,groupCompany,tableName);
        branchList=branchList.stream().filter(map -> starReadingTableDto.getExpressItemList().contains(map.get(MagicString.SORT))).collect(Collectors.toList());
        List<Map<String, Object>>  autoBranch = starReadingTableMapper.getBranchdiffForBranch(starReadingTableDto,branchList,tableName,groupCompany);

        if (!autoBranch.isEmpty()){
            List<Map<String, Object>> finalBranchList = branchList;
            autoBranch.forEach(branch-> finalBranchList.forEach(objectMap->{
                if (branch.get(MagicString.AREA).equals(objectMap.get(MagicString.AREA))){
                    branch.put(MagicString.SORT,objectMap.get(MagicString.SORT));
                }
            }));

        }
        List<Map<String, Object>> list = new ArrayList<>();

        for (Map<String,Object> janMap : janName) {
            Map<String,Object> map = new HashMap<>();
            map.put(MagicString.JAN,janMap.get(MagicString.JAN));
            map.put(MagicString.JAN_NAME,janMap.get(MagicString.JAN_NAME));
            map.put(MagicString.TOTAL,"");
            branchList.forEach(objectMap-> map.put(objectMap.get(MagicString.SORT)+"_"+objectMap.get(MagicString.BRANCH_CD).toString(),"???"));
            list.add(map);
        }
        Map<String, List<Map<String, Object>>> janGroup = autoBranch.stream()
                .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.JAN)));
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
            for (Map<String, Object> map : stringListEntry.getValue()) {
                list.forEach(stringObjectMap->{
                    if (stringObjectMap.get(MagicString.JAN).equals(stringListEntry.getKey())){
                        stringObjectMap.put(map.get(MagicString.SORT)+"_"+map.get(MagicString.BRANCH_CD),map.get("flag"));
                    }
                });
            }

        }
        branchList.forEach(objectMap->{
            column.append(",").append(objectMap.get(MagicString.SORT)).append("_").append(objectMap.get(MagicString.BRANCH_CD));
            header.append(",").append(objectMap.get(MagicString.BRANCH_CD)).append(MagicString.BR).append(objectMap.get(MagicString.BRANCH_NAME));
            group.put( objectMap.get(MagicString.SORT).toString(), objectMap.get(MagicString.AREA));
        });
        mapResult.put(MagicString.COLUMN, column.toString());
        mapResult.put(MagicString.HEADER, header.toString());
        mapResult.put(MagicString.GROUP, group);
        list = list.stream().sorted(Comparator.comparing(map->MapUtils.getString(map,MagicString.JAN))).collect(Collectors.toList());
        mapResult.put("data", list);
        return mapResult;
    }

    @Override
    public Map<String, Object> getStarReadingParam(String companyCd,Integer priorityOrderCd) {
        logger.info("start:{}",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
        //String makerCol = classicPriorityOrderMstMapper.getMakerCol(commonTableName.getStoreIsCore(),commonTableName.getStoreMstClass());
        Integer modeCheck = priorityOrderMstMapper.getModeCheck(priorityOrderCd);
        if (modeCheck == null){
            modeCheck =1;
        }

        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int existTableName = mstBranchMapper.checkTableExist("prod_0000_jan_info", coreCompany);

        if (existTableName == 0){
            coreCompany = companyCd;
        }
        String tableName = MessageFormat.format("\"{0}\".ten_0000_ten_info", coreCompany);

        List<Map<String, Object>> expressItemList =new ArrayList<>();
        String janInfoTableName = "";
        StringBuilder column = new StringBuilder("jan,janName,total");
        StringBuilder header = new StringBuilder("JAN,?????????,??????");
        Map<String,Object> mapResult = new HashMap<>();
        LinkedHashMap<String, Object> group = new LinkedHashMap<>();
        List<String> groupCompany = priorityOrderCommodityMustMapper.getGroupCompany(companyCd);
        groupCompany.add(companyCd);

        if (modeCheck == 1){
            janInfoTableName = "priority.work_priority_order_commodity_branch";
          this.getDepositBranch(companyCd,priorityOrderCd,tableName,mapResult,column,header,commonTableName,groupCompany,group);

        }else if (modeCheck == 0){
            janInfoTableName = "priority.work_priority_order_commodity_pattern";
            this.getDepositPattern(priorityOrderCd,mapResult,column,header,commonTableName,group);

        }
        List<Map<String, Object>> areaList = starReadingTableMapper.getAreaList(priorityOrderCd,groupCompany,tableName);
        List<Map<String, Object>>  patternList =  starReadingTableMapper.getPatternList(priorityOrderCd);
        expressItemList.addAll(areaList);
        expressItemList.addAll(patternList);
        List<Map<String, Object>> janList = classicPriorityOrderDataMapper.getJanInfo(priorityOrderCd,janInfoTableName);

        Map<String,Object> map = new HashMap<>();
        map.put("expressItemList",expressItemList);
        map.put("janList",janList);
        map.put("modeCheck",modeCheck);
        map.put("data",mapResult.isEmpty()?null:mapResult);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }
    public Map<String,Object> getDepositPattern( Integer priorityOrderCd, Map<String, Object> mapResult
            , StringBuilder column, StringBuilder header, GetCommonPartsDataDto commonTableName
            , Map<String, Object> group){
        List<Map<String, Object>> patternDiff = starReadingTableMapper.getPatterndiff(priorityOrderCd,commonTableName.getProInfoTable());
        if (patternDiff.isEmpty()){
            return mapResult;
        }
        List<Map<String, Object>> patternNameList = starReadingTableMapper.getPatternNameList(priorityOrderCd);
        List<String> shelfNameCd = patternDiff.stream().map(map -> MagicString.PATTERN+map.get("shelfNameCd").toString()).collect(Collectors.toList());
        patternNameList = patternNameList.stream().filter(map->shelfNameCd.contains(map.get("id").toString())).collect(Collectors.toList());
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, List<Map<String, Object>>> janGroup = patternDiff.stream()
                .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.JAN)));
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
            Map<String,Object> patternMap = new HashMap<>();
            patternMap.put(MagicString.JAN,stringListEntry.getKey());
            patternMap.put(MagicString.JAN_NAME,stringListEntry.getValue().get(0).get(MagicString.JAN_NAME));
            long total = stringListEntry.getValue().stream().filter(map1 -> map1.get("flag").equals("???")).count();
            patternMap.put(MagicString.TOTAL,total);
            for (Map<String, Object> map : stringListEntry.getValue()) {
                for (Map<String, Object> stringObjectMap : patternNameList) {
                    if (stringObjectMap.get(MagicString.SHELF_PATTERN_CD).equals(map.get(MagicString.SHELF_PATTERN_CD))){
                        patternMap.put(stringObjectMap.get("id")+"_"+map.get(MagicString.SHELF_PATTERN_CD),map.get("flag"));
                    }
                }
            }
            list.add(patternMap);
        }

        for (Map<String, Object> objectMap : patternNameList) {
            column.append(",").append(objectMap.get("id")).append("_").append(objectMap.get(MagicString.SHELF_PATTERN_CD));
            header.append(",").append(objectMap.get(MagicString.SHELF_PATTERN_NAME));
            group.put( objectMap.get("id").toString(), objectMap.get("shelfName"));
        }
        mapResult.put(MagicString.COLUMN, column.toString());
        mapResult.put(MagicString.HEADER, header.toString());
        mapResult.put(MagicString.GROUP, group);
        list = list.stream().sorted(Comparator.comparing(map->MapUtils.getString(map,MagicString.JAN))).collect(Collectors.toList());
        mapResult.put("data", list);
        return mapResult;
    }
    public Map<String,Object> getDepositBranch(String companyCd, Integer priorityOrderCd, String tableName, Map<String, Object> mapResult
            , StringBuilder column, StringBuilder header, GetCommonPartsDataDto commonTableName, List<String> groupCompany
            , Map<String, Object> group){

        List<Map<String, Object>> branchDiff = starReadingTableMapper.getBranchdiff(priorityOrderCd);
        if (branchDiff.isEmpty()){
            return mapResult;
        }
        List<Map<String, Object>> branchList = starReadingTableMapper.getBranchList(priorityOrderCd,groupCompany,tableName);
        List<Map<String, Object>> janOrName = starReadingTableMapper.getJanOrName(companyCd, priorityOrderCd,commonTableName.getProInfoTable());
        List<Object> branchCd = branchDiff.stream().map(map -> map.get(MagicString.BRANCH_CD)).collect(Collectors.toList());
        branchList=branchList.stream().filter(map ->branchCd.contains(map.get(MagicString.BRANCH_CD))).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> janGroup = branchDiff.stream()
                .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.JAN)));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
            janOrName.forEach(stringObjectMap->{
                if (stringListEntry.getKey().equals(stringObjectMap.get("jan_new"))){
                    stringListEntry.getValue().get(0).put(MagicString.JAN_NAME,stringObjectMap.get("sku"));
                }
            });

            Map<String,Object> map = new HashMap<>();
            map.put(MagicString.JAN,stringListEntry.getValue().get(0).get(MagicString.JAN));
            map.put(MagicString.JAN_NAME,stringListEntry.getValue().get(0).get(MagicString.JAN_NAME));
            long total = stringListEntry.getValue().stream().filter(map1 -> map1.get("flag").equals("???")).count();
            map.put(MagicString.TOTAL,total);
            for (Map<String, Object> objectMap : branchList) {
                for (Map<String, Object> stringObjectMap : stringListEntry.getValue()) {
                    if (objectMap.get(MagicString.BRANCH_CD).equals(stringObjectMap.get(MagicString.BRANCH_CD))){
                        map.put(objectMap.get(MagicString.SORT)+"_"+objectMap.get(MagicString.BRANCH_CD),stringObjectMap.get("flag"));
                    }
                }
            }
            list.add(map);
        }

        for (Map<String, Object> objectMap : branchList) {
            column.append(",").append(objectMap.get(MagicString.SORT)).append("_").append(objectMap.get(MagicString.BRANCH_CD));
            header.append(",").append(objectMap.get(MagicString.BRANCH_CD)).append(MagicString.BR).append(objectMap.get(MagicString.BRANCH_NAME));
            group.put( objectMap.get("sort").toString(), objectMap.get(MagicString.AREA));
        }
        mapResult.put(MagicString.COLUMN, column.toString());
        mapResult.put(MagicString.HEADER, header.toString());
        mapResult.put(MagicString.GROUP, group);
        list = list.stream().sorted(Comparator.comparing(map->MapUtils.getString(map,MagicString.JAN))).collect(Collectors.toList());
        mapResult.put("data", list);
        return mapResult;
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
                        if (!stringObjectEntry.getKey().equals(MagicString.JAN) && !stringObjectEntry.getKey().equals(MagicString.JAN_NAME) && !stringObjectEntry.getKey().equals(MagicString.TOTAL)
                                &&!stringObjectEntry.getKey().equals(MagicString.MAKER)) {
                            Map<String, Object> map = new HashMap<>();
                            map.put(MagicString.JAN, jan);
                            map.put(MagicString.BRANCH, stringObjectEntry.getKey().split("_")[1]);
                            map.put("flag", stringObjectEntry.getValue().equals("???") ? -1 : stringObjectEntry.getValue().equals("") ? 0 : 1);
                            if (finalStarReadingVo.getModeCheck() == 0){
                                map.put("patternNameCd",Integer.valueOf(stringObjectEntry.getKey().split("_")[0].split(MagicString.PATTERN)[1]));
                            }
                            for (Map.Entry<String, Object> objectEntry : finalStarReadingVo.getGroup().entrySet()) {
                                if (objectEntry.getKey().equals(stringObjectEntry.getKey().split("_")[0])) {
                                    map.put(MagicString.AREA, objectEntry.getValue());
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
            logger.error("", e);
        } catch (InterruptedException e){
            logger.error("", e);
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
                long total = datum.entrySet().stream().filter(entry->"???".equals(entry.getValue())).count();
                datum.put(MagicString.TOTAL,total);
            }
            starReadingVo.setData(data);
        }
        List<String> header = Arrays.asList(starReadingVo.getHeader().toString().replaceAll(MagicString.BR,"_").split(","));
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
            String fileName = String.format("%s.xlsx", "??????????????????");
            String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(fileName, "utf-8"));
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            response.setHeader("Content-Disposition", format);
            outputStream = response.getOutputStream();

            ExcelUtils.starReadingExcel(columns, linkedHashMap,starReadingVo.getData() ,outputStream);
            outputStream.flush();
        } catch (IOException e) {
            logger.error("", e);
        }

    }

    @Override
    public Map<String, Object> rowColumnConversion(StarReadingVo starReadingVo) {
        if (starReadingVo.getFlag()==1) {
            StarReadingVo starReadingVo1 = this.branchConversion(starReadingVo);
            return ResultMaps.result(ResultEnum.SUCCESS,starReadingVo1);
        }else {
            StarReadingVo starReadingVo1 = this.patternConversion(starReadingVo);
            return ResultMaps.result(ResultEnum.SUCCESS,starReadingVo1);
        }

    }
    public StarReadingVo branchConversion(StarReadingVo starReadingVo){
        List<String> list = Arrays.asList(starReadingVo.getColumn().split(",")).stream().filter(item -> !item.equals(MagicString.JAN) && !item.equals(MagicString.JAN_NAME) && !item.equals(MagicString.TOTAL) && !item.equals(MagicString.MAKER)).collect(Collectors.toList());
        List<String> header = Arrays.asList(starReadingVo.getHeader().toString().split(",")).stream().filter(item -> !item.equals("?????????") && !item.equals("JAN") && !item.equals("??????") && !item.equals("????????????")).collect(Collectors.toList());
        Map<String, Object> group = starReadingVo.getGroup();
        List<String> columnList = new ArrayList<>(list);
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        StringBuilder header1 = new StringBuilder();
        StringBuilder header2 = new StringBuilder();
        StringBuilder header3 = new StringBuilder();
        StringBuilder header4;
        if (starReadingVo.getModeCheck() == 1) {
            header4 = new StringBuilder("?????????,??????CD,?????????");
        }else {
            header4 = new StringBuilder("?????????,???????????????CD,???????????????");
        }
        StringBuilder column = new StringBuilder("area,branchCd,branch");
        List<LinkedHashMap<String, Object>> data = starReadingVo.getData().stream().sorted(Comparator.comparing(map -> MapUtils.getString(map, "jan"))).collect(Collectors.toList());
        data.forEach(datum->{
            if (header3.toString().equals("")) {
                //header1.append(datum.get(MagicString.MAKER));
                header2.append(datum.get(MagicString.TOTAL));
                header3.append(datum.get(MagicString.JAN));
            }else {
                //header1.append(",").append(datum.get(MagicString.MAKER));
                header2.append(",").append(datum.get(MagicString.TOTAL));
                header3.append(",").append(datum.get(MagicString.JAN));

            }
            header4.append(",").append(datum.get(MagicString.JAN_NAME));
            column.append(",jan").append(datum.get(MagicString.JAN));
        });
        //headers.add(header1.toString());
        headers.add(header3.toString());
        headers.add(header4.toString());
        for (int i = 0; i < columnList.size(); i++) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            if (starReadingVo.getModeCheck() == 1) {
                map.put(MagicString.BRANCH, header.get(i).split(MagicString.BR)[1]);
            }else {
                map.put(MagicString.BRANCH, header.get(i));
            }
            map.put(MagicString.AREA, group.get(columnList.get(i).split("_")[0]));
            map.put(MagicString.BRANCH_CD, columnList.get(i).split("_")[1]);
            map.put(MagicString.AREA_CD, columnList.get(i).split("_")[0]);

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
        return starReadingVo1;
    }
    public StarReadingVo patternConversion(StarReadingVo starReadingVo){
        List<String> headers =  (List<String>) starReadingVo.getHeader();
        List<String> header2 = Arrays.asList(headers.get(0).split(","));
        List<String> header3 = Arrays.asList(headers.get(1).split(","));
        List<LinkedHashMap<String,Object>> resultList = new ArrayList<>();
        for (int i = 0; i < header2.size(); i++) {
            LinkedHashMap<String,Object> map= new LinkedHashMap<>();
            map.put(MagicString.JAN,header2.get(i));
            map.put(MagicString.JAN_NAME,header3.get(i+3));
            //map.put(MagicString.MAKER,header1.get(i));
            map.put(MagicString.TOTAL,"");
            for (LinkedHashMap<String, Object> datum : starReadingVo.getData()) {
                map.put(datum.get(MagicString.AREA_CD)+"_"+datum.get(MagicString.BRANCH_CD),datum.get(MagicString.JAN+header2.get(i)));
            }

            resultList.add(map);
        }
        StringBuilder column = new StringBuilder("jan,janName,total");
        StringBuilder header = new StringBuilder("JAN,?????????,??????");
        Map<String,Object> group = new LinkedHashMap<>();
        starReadingVo.getData().forEach(datum->{
            column.append(",").append(datum.get(MagicString.AREA_CD)).append("_").append(datum.get(MagicString.BRANCH_CD));
            if (starReadingVo.getModeCheck() == 1) {
                header.append(",").append(datum.get(MagicString.BRANCH_CD)).append(MagicString.BR).append(datum.get(MagicString.BRANCH));
            }else {
                header.append(",").append(datum.get(MagicString.BRANCH));
            }
            group.putIfAbsent(datum.get(MagicString.AREA_CD).toString(),datum.get("area"));
        });

        StarReadingVo starReadingVo1 = new StarReadingVo();
        starReadingVo1.setColumn(column.toString());
        starReadingVo1.setHeader(header.toString());
        starReadingVo1.setData(resultList);
        starReadingVo1.setGroup(group);
        starReadingVo1.setFlag(0);
        starReadingVo1.setModeCheck(starReadingVo.getModeCheck());
        return starReadingVo1;
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
