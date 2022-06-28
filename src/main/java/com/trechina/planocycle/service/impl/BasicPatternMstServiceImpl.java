package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.ProductPowerDataDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.service.PriorityOrderMstService;
import com.trechina.planocycle.service.ShelfPtsService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasicPatternMstServiceImpl implements BasicPatternMstService {
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    @Autowired
    private JanInfoMapper janInfoMapper;
    @Autowired
    private HttpSession session;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamst;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private WorkPriorityOrderRestrictRelationMapper workPriorityOrderRestrictRelationMapper;
    @Autowired
    private BasicPatternRestrictResultMapper restrictResultMapper;
    @Autowired
    private BasicPatternRestrictRelationMapper restrictRelationMapper;
    @Autowired
    private BasicPatternAttrMapper basicMapperMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    private WorkPriorityOrderRestrictResultMapper workPriorityOrderRestrictResultMapper;
    @Autowired
    private VehicleNumCache vehicleNumCache;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private BasicPatternRestrictResultDataMapper restrictResultDataMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;
    @Autowired
    private CommonMstService commonMstService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> autoDetect(BasicPatternAutoDetectVO autoDetectVO) {
        Integer shelfPatternCd = autoDetectVO.getShelfPatternCd();
        Integer priorityOrderCd = autoDetectVO.getPriorityOrderCd();
        String companyCd = autoDetectVO.getCompanyCd();
        String commonPartsData = autoDetectVO.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = getCommonTableName(commonPartsData, companyCd);
        String classCd = commonTableName.getProdMstClass();
        String zokuseiIds = autoDetectVO.getAttrList();
        String aud = session.getAttribute("aud").toString();

        WorkPriorityOrderMst priorityOrderMst = new WorkPriorityOrderMst();
        BeanUtils.copyProperties(autoDetectVO, priorityOrderMst);
        priorityOrderMst.setAuthorCd(aud);

        workPriorityOrderMstMapper.deleteByAuthorCd(autoDetectVO.getCompanyCd(), aud, autoDetectVO.getPriorityOrderCd());
        workPriorityOrderMstMapper.insert(priorityOrderMst);
        List<Integer> list = Arrays.asList(zokuseiIds.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());

        List<Integer> cdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(companyCd,
                classCd, zokuseiIds);
        priorityOrderMstAttrSortMapper.deleteAttrList(companyCd,priorityOrderCd);
        priorityOrderMstAttrSortMapper.setAttrList(companyCd,priorityOrderCd,list);
        List<ShelfPtsDataTanamst> tanamsts = shelfPtsDataTanamst.selectByPatternCd((long) shelfPatternCd);
        List<Map<String, Object>> classifyList = janInfoMapper.selectJanClassify(commonTableName.getProInfoTable(), shelfPatternCd, zokuseiMsts, cdList);

        basicMapperMapper.delete(priorityOrderCd, companyCd);

        Map<String, BasicPatternRestrictResult> classify = getJanInfoClassify(classifyList, companyCd,
                zokuseiIds, aud, (long) autoDetectVO.getPriorityOrderCd());
        restrictResultMapper.deleteByPriorityCd(autoDetectVO.getPriorityOrderCd(), companyCd);
        long restrictCd = 1;
        for (Map.Entry<String, BasicPatternRestrictResult> entry : classify.entrySet()) {
            BasicPatternRestrictResult value = entry.getValue();
            value.setRestrictCd(restrictCd);
            classify.put(entry.getKey(), value);
            restrictCd++;
        }
        List<BasicPatternRestrictResult> basicPatternRestrictResults = new ArrayList<>(classify.values());
        BasicPatternRestrictResult result = new BasicPatternRestrictResult();
        result.setRestrictCd(9999L);
        result.setAuthorCd(aud);
        result.setCompanyCd(companyCd);
        result.setPriorityOrderCd((long)priorityOrderCd);
        basicPatternRestrictResults.add(result);
        restrictResultMapper.insertBatch(basicPatternRestrictResults);
        basicMapperMapper.insertBatch(basicPatternRestrictResults);

        ArrayList<String> zokuseiList = Lists.newArrayList(zokuseiIds.split(","));

        restrictRelationMapper.deleteByPrimaryKey(priorityOrderCd, companyCd);
        for (ShelfPtsDataTanamst tanamst : tanamsts) {
            Integer taiCd = tanamst.getTaiCd();
            Integer tanaCd = tanamst.getTanaCd();
            Integer tanaWidth = tanamst.getTanaWidth();

            List<Map<String, Object>> jans = classifyList.stream().filter(map -> MapUtils.getInteger(map, MagicString.TAI_CD).equals(taiCd) &&
                    MapUtils.getInteger(map, MagicString.TANA_CD).equals(tanaCd)).collect(Collectors.toList());

            for (int i = 0; i < jans.size(); i++) {
                Map<String, Object> janMap = jans.get(i);
                double width = MapUtils.getDouble(janMap, "width");
                int percent = BigDecimal.valueOf(width).divide(BigDecimal.valueOf(tanaWidth), BigDecimal.ROUND_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_UP).intValue();
                janMap.put("area", percent);
                janMap.put("priorityOrderCd", priorityOrderCd);
                janMap.put("companyCd", companyCd);
                janMap.put("authorCd", aud);

                StringBuilder key = new StringBuilder();
                for (String zokusei : zokuseiList) {
                    if(key.length()>0){
                        key.append(",");
                    }
                    key.append(MapUtils.getString(janMap, zokusei));
                }

                BasicPatternRestrictResult basicPatternRestrictResult = classify.get(key.toString());
                janMap.put("restrictCd", basicPatternRestrictResult.getRestrictCd());

                jans.set(i, janMap);
            }

            final int[] index = {1};
            Comparator<Map<String, Object>> area = Comparator.comparing(map->MapUtils.getInteger(map, "area"));
            jans.stream().sorted(area.reversed()).forEach(map->{
                map.put("tanaPosition", index[0]);
                index[0]++;
            });
            restrictRelationMapper.insertBatch(jans);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    private Map<String, BasicPatternRestrictResult> getJanInfoClassify(List<Map<String, Object>> classifyList,
            String companyCd, String zokuseiIds, String authorCd, Long priorityOrderCd){
        Map<String, BasicPatternRestrictResult> classify = new HashMap<>();

        ArrayList<String> zokuseiList = Lists.newArrayList(zokuseiIds.split(","));
        classifyList.stream().forEach(map->{
            StringBuilder classifyId = new StringBuilder();
            for (String id : zokuseiList) {
                if(classifyId.length()>0){
                    classifyId.append(",");
                }
                classifyId.append(MapUtils.getString(map, id));
            }

            if (!classify.containsKey(classifyId.toString())) {
                BasicPatternRestrictResult result = new BasicPatternRestrictResult();
                for (String zokusei : zokuseiList) {
                    Method method = null;
                    try {
                        method = result.getClass().getMethod("setZokusei" + zokusei, String.class);
                        method.invoke(result, MapUtils.getString(map, zokusei));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                result.setCompanyCd(companyCd);
                result.setAuthorCd(authorCd);
                result.setPriorityOrderCd(priorityOrderCd);

                classify.put(classifyId.toString(), result);
            }
        });

        return classify;
    }

    public GetCommonPartsDataDto getCommonTableName(String commonPartsData, String companyCd ) {
        //{"dateIsCore":"1","storeLevel":"3","storeIsCore":"1","storeMstClass":"0000","prodIsCore":"1","prodMstClass":"0000"}

        JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();

        GetCommonPartsDataDto getCommonPartsDataDto = new GetCommonPartsDataDto();
        getCommonPartsDataDto.setProdMstClass(prodMstClass);

        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        String isCompanyCd = null;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        getCommonPartsDataDto.setProdIsCore(isCompanyCd);
        //if (jsonObject.get("storeIsCore").toString() !=null) {
        //    String storeIsCore = jsonObject.get("storeIsCore").toString();
        //    String storeMstClass = jsonObject.get("storeMstClass").toString();
        //    String storeIsCompanyCd = null;
        //    if ("1".equals(storeIsCore)) {
        //        storeIsCompanyCd = coreCompany;
        //    } else {
        //        storeIsCompanyCd = companyCd;
        //    }
        //    getCommonPartsDataDto.setStoreInfoTable(MessageFormat.format("\"{0}\".ten_{1}_ten_info", storeIsCompanyCd, storeMstClass));
        //    getCommonPartsDataDto.setStoreKaisouTable(MessageFormat.format("\"{0}\".ten_{1}_ten_kaisou_header_sys", storeIsCompanyCd, storeMstClass));
        //}

        getCommonPartsDataDto.setProKaisouTable(MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProAttrTable(MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProInfoTable(MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProZokuseiDataTable(MessageFormat.format("\"{0}\".zokusei_{1}_mst_data", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProZokuseiMstTable(MessageFormat.format("\"{0}\".zokusei_{1}_mst", isCompanyCd, prodMstClass));

        return getCommonPartsDataDto;
    }

    @Override
    public Map<String, Object> getAttrDisplay(String companyCd, Integer priorityOrderCd) {
        List<PriorityOrderMstAttrSort> priorityOrderMstAttrSorts = priorityOrderMstAttrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<String> zokuseiList = priorityOrderMstAttrSorts.stream().map(map->MagicString.ZOKUSEI_PREFIX+map.getValue()).collect(Collectors.toList());

        String aud = session.getAttribute("aud").toString();
        WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, aud,priorityOrderCd);
        GetCommonPartsDataDto commonTableName = getCommonTableName(priorityOrderMst.getCommonPartsData(), companyCd);
        List<Map<String, Object>> basicPatternRestrictRelationDto = restrictRelationMapper.selectByPrimaryKey(priorityOrderCd,
                commonTableName.getProdIsCore(), zokuseiList,commonTableName.getProdMstClass());
        Map<String, List<Map<String, Object>>> relationByTaiTana = basicPatternRestrictRelationDto.stream()
                .collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.TAI_CD) + "," + MapUtils.getString(map, MagicString.TANA_CD)));

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : relationByTaiTana.entrySet()) {
            String key = entry.getKey();

            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put(MagicString.TAI_CD, Integer.valueOf(key.split(",")[0]));
            resultMap.put(MagicString.TANA_CD, Integer.valueOf(key.split(",")[1]));

            List<Map<String, Object>> groups = new ArrayList<>();

            for (Map<String, Object> itemMap : entry.getValue()) {
                for (String zokuseiId : zokuseiList) {
                    if (itemMap.containsKey(zokuseiId)) {
                        String attrCd = MapUtils.getString(itemMap, zokuseiId);
                        itemMap.put(zokuseiId.replace(MagicString.ZOKUSEI_PREFIX, "zokuseiName"), JSONObject.parseObject(itemMap.get("json").toString()).get(attrCd));
                    }
                }
                itemMap.remove("json");
                if (itemMap.containsKey(zokuseiList.get(0))) {
                    groups.add(itemMap);
                }
                resultMap.put("groups", groups);
            }

            resultList.add(resultMap);
        }

        return ResultMaps.result(ResultEnum.SUCCESS, resultList);
    }

    @Override
    public Map<String, Object> autoCalculation(String companyCd, Integer priorityOrderCd, Integer partition) {
        String authorCd = session.getAttribute("aud").toString();
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        String uuid = UUID.randomUUID().toString();
        executor.execute(() -> {
            //商品力点数表cdを取得する
            Integer patternCd = productPowerMstMapper.getpatternCd(companyCd, authorCd, priorityOrderCd);
            List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
            List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
            WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
            String commonPartsData = workPriorityOrderMst.getCommonPartsData();
            GetCommonPartsDataDto commonTableName = getCommonTableName(commonPartsData, companyCd);
            List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
            List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
            List<Map<String, Object>> restrictResult = restrictResultMapper.selectByPrimaryKey(priorityOrderCd);

            this.setPtsJandataByRestrictCd(priorityOrderCd, patternCd,companyCd, authorCd, attrList, zokuseiMsts,
                    commonTableName, allCdList,restrictResult);
            Integer productPowerCd = productPowerMstMapper.getProductPowerCd(companyCd, authorCd, priorityOrderCd);

            Integer minFaceNum = 1;
            //仕切り板の厚さと仕切り板を使用して保存するかどうか
            priorityOrderMstMapper.setPartition(companyCd,priorityOrderCd,authorCd,partition);
            //まず社員番号に従ってワークシートのデータを削除します
            workPriorityOrderResultDataMapper.delResultData(companyCd, authorCd, priorityOrderCd);
            //制約条件の取得
            List<WorkPriorityOrderRestrictResult> resultList = workPriorityOrderRestrictResultMapper.getResultList(companyCd, authorCd, priorityOrderCd);
            // 1.制約条件で該当商品を探す
            for (WorkPriorityOrderRestrictResult workPriorityOrderRestrictResult : resultList) {
                List<ProductPowerDataDto> newList = new ArrayList<>();
                workPriorityOrderRestrictResult.setPriorityOrderCd(priorityOrderCd);
                List<ProductPowerDataDto> productPowerData = workPriorityOrderRestrictResultMapper.getProductPowerData(workPriorityOrderRestrictResult, companyCd, productPowerCd, authorCd);

                for (ProductPowerDataDto productPowerDatum : productPowerData) {

                    if (productPowerDatum.getJanNew() != null) {
                        productPowerDatum.setJan(productPowerDatum.getJanNew());
                    }
                    if (productPowerDatum.getRankNewResult()!=null){
                        productPowerDatum.setRankResult(productPowerDatum.getRankNewResult());
                    }
                }
                for (ProductPowerDataDto productPowerDatum : productPowerData) {
                    newList.add(productPowerDatum);
                    if (newList.size() % 1000 == 0 && !newList.isEmpty()) {
                        workPriorityOrderResultDataMapper.setResultDataList(newList, workPriorityOrderRestrictResult.getRestrictCd(), companyCd, authorCd, priorityOrderCd);
                        newList.clear();

                    }
                }
                if (!newList.isEmpty()) {
                    workPriorityOrderResultDataMapper.setResultDataList(newList, workPriorityOrderRestrictResult.getRestrictCd(), companyCd, authorCd, priorityOrderCd);
                }

            }

            String resultDataList = workPriorityOrderResultDataMapper.getResultDataList(companyCd, authorCd, priorityOrderCd);
            if (resultDataList == null) {
                //return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
                vehicleNumCache.put("janNotExist"+uuid,1);
            }
            String[] array = resultDataList.split(",");
            //cgiを呼び出す

            Map<String, Object> data = priorityOrderMstService.getFaceKeisanForCgi(array, companyCd, patternCd, authorCd,tokenInfo);
            if (data.get("data") != null && data.get("data") != "") {
                String[] strResult = data.get("data").toString().split("@");
                String[] strSplit = null;
                List<WorkPriorityOrderResultData> list = new ArrayList<>();
                WorkPriorityOrderResultData orderResultData = null;
                for (int i = 0; i < strResult.length; i++) {
                    orderResultData = new WorkPriorityOrderResultData();
                    strSplit = strResult[i].split(" ");
                    orderResultData.setSalesCnt(Double.valueOf(strSplit[1]));
                    orderResultData.setJanCd(strSplit[0]);


                    list.add(orderResultData);
                }
                workPriorityOrderResultDataMapper.update(list, companyCd, authorCd, priorityOrderCd);


                //古いptsの平均値、最大値最小値を取得
                FaceNumDataDto faceNum = productPowerMstMapper.getFaceNum(patternCd);
                minFaceNum = faceNum.getFaceMinNum();
                DecimalFormat df = new DecimalFormat("#.00");
                //salescntAvgを取得し、小数点を2桁保持
                Double salesCntAvg = productPowerMstMapper.getSalesCntAvg(companyCd, authorCd, priorityOrderCd);
                String format = df.format(salesCntAvg);
                salesCntAvg = Double.valueOf(format);

                List<WorkPriorityOrderResultData> resultDatas = workPriorityOrderResultDataMapper.getResultDatas(companyCd, authorCd, priorityOrderCd);

                Double d = null;
                for (WorkPriorityOrderResultData resultData : resultDatas) {
                    resultData.setPriorityOrderCd(priorityOrderCd);
                    if (resultData.getSalesCnt() != null) {
                        d = resultData.getSalesCnt() * faceNum.getFaceAvgNum() / salesCntAvg;

                        if (d > faceNum.getFaceMaxNum()) {
                            resultData.setFace(Long.valueOf(faceNum.getFaceMaxNum()));
                        } else if (d < faceNum.getFaceMinNum()) {
                            resultData.setFace(Long.valueOf(faceNum.getFaceMinNum()));
                        } else {
                            resultData.setFace(d.longValue());
                        }

                    } else {
                        resultData.setFace(Long.valueOf(faceNum.getFaceMinNum()));
                    }

                }
                workPriorityOrderResultDataMapper.updateFace(resultDatas, companyCd, authorCd);
            }
            //属性別に並べ替える
            priorityOrderMstService.getReorder(companyCd, priorityOrderCd,productPowerCd,authorCd);
            //商品を並べる
            WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
            Long shelfPatternCd = priorityOrderMst.getShelfPatternCd();

            if (shelfPatternCd == null) {
                //logger.info("shelfPatternCd:{}不存在", shelfPatternCd);
                //return ResultMaps.result(ResultEnum.FAILURE);
                vehicleNumCache.put("PatternCdNotExist"+uuid,1);
            }

            Short partitionFlag = Optional.ofNullable(priorityOrderMst.getPartitionFlag()).orElse((short) 0);
            Short partitionVal = Optional.ofNullable(priorityOrderMst.getPartitionVal()).orElse((short) 2);

            Map<String, Object> resultMap = commonMstService.commSetJanForShelf(patternCd.intValue(), companyCd, priorityOrderCd, minFaceNum, zokuseiMsts, allCdList,
                    restrictResult, attrList);

            if (MapUtils.getInteger(resultMap, "code").equals(ResultEnum.FAILURE.getCode())) {
                vehicleNumCache.put(uuid,2);
            }else{
                //ptsを一時テーブルに保存
                String tmpData = MapUtils.getString(resultMap, "data");
                List<WorkPriorityOrderResultDataDto> workData = new Gson().fromJson(tmpData, new TypeToken<List<WorkPriorityOrderResultDataDto>>() {
                }.getType());
                shelfPtsService.basicSaveWorkPtsData(companyCd, authorCd, priorityOrderCd, workData);
                vehicleNumCache.put(uuid,1);

            }
        });
        return ResultMaps.result(ResultEnum.SUCCESS,uuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setAttrDisplay( BasicPatternRestrictRelation basicPatternRestrictRelation) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = basicPatternRestrictRelation.getCompanyCd();
        Long priorityOrderCd = basicPatternRestrictRelation.getPriorityOrderCd();
        if (basicPatternRestrictRelation.getRestrictCd()!= null){
            restrictRelationMapper.deleteForTanaPosition(basicPatternRestrictRelation);
            restrictRelationMapper.update(basicPatternRestrictRelation,authorCd);
        }else {
            List<BasicPatternRestrictRelation> tanaAttrList = restrictRelationMapper.getTanaAttrList(basicPatternRestrictRelation);
            if (tanaAttrList.size()>1){
                tanaAttrList = tanaAttrList.stream()
                        .filter(item -> !item.getTanaPosition().equals(basicPatternRestrictRelation.getTanaPosition())).collect(Collectors.toList());
                int i = 1;
                for (BasicPatternRestrictRelation patternRestrictRelation : tanaAttrList) {
                    patternRestrictRelation.setTanaPosition(i++);
                }

            }else {
                tanaAttrList.get(0).setRestrictCd(9999L);
            }
            Integer taiCd = Integer.valueOf(basicPatternRestrictRelation.getTaiCd().toString());
            Integer tanaCd = Integer.valueOf(basicPatternRestrictRelation.getTanaCd().toString());
            restrictRelationMapper.deleteTanas(taiCd,tanaCd,companyCd,priorityOrderCd.intValue());
            restrictRelationMapper.updateTanaPosition(tanaAttrList,authorCd);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> autoTaskId(String taskId) {
        if (vehicleNumCache.get("janNotExist"+taskId)!=null){
            vehicleNumCache.remove("janNotExist"+taskId);
            return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
        }
        if (vehicleNumCache.get("PatternCdNotExist"+taskId)!=null){
            vehicleNumCache.remove("PatternCdNotExist"+taskId);
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        if (vehicleNumCache.get(taskId) != null){
            vehicleNumCache.remove(taskId);
            if(Objects.equals(vehicleNumCache.get(taskId), 2)){
                return ResultMaps.result(ResultEnum.FAILURE,"tana'height not enough");
            }
            return ResultMaps.result(ResultEnum.SUCCESS,"success");
        }
        return ResultMaps.result(ResultEnum.SUCCESS,"9");
    }

    private void setPtsJandataByRestrictCd(Integer priorityOrderCd, Integer patternCd, String companyCd, String authorCd, List<Integer> attrList,
                                           List<ZokuseiMst> zokuseiMsts, GetCommonPartsDataDto commonTableName, List<Integer> allCdList,
                                           List<Map<String, Object>> restrictResult){
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(patternCd);
        List<Map<String, Object>> zokuseiList = restrictResultMapper.selectJanZokusei(priorityOrderCd, ptsCd, zokuseiMsts, allCdList,
                commonTableName.getProInfoTable());

        for (int i = 0; i < zokuseiList.size(); i++) {
            Map<String, Object> zokusei = zokuseiList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
                int equalsCount = 0;
                for (Integer integer : attrList) {
                    String restrictKey = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + integer);
                    String zokuseiKey = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + integer);

                    if(restrictKey.equals(zokuseiKey)){
                        equalsCount++;
                    }
                }

                if(equalsCount == attrList.size()){
                    int restrictCd = MapUtils.getInteger(restrict, "restrict_cd");
                    zokusei.put("restrictCd", restrictCd);
                }
            }

            zokuseiList.set(i, zokusei);
        }

        restrictResultDataMapper.deleteByPrimaryKey(priorityOrderCd);
        restrictResultDataMapper.insertBatch(attrList, zokuseiList, priorityOrderCd, companyCd, authorCd);
    }
}
