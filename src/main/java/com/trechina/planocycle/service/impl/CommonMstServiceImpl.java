package com.trechina.planocycle.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.CommonPartsDto;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import com.trechina.planocycle.entity.po.WorkPriorityOrderResultData;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CommonMstServiceImpl implements CommonMstService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AreasMapper areasMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ProductPowerParamMstMapper productPowerParamMstMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private ClassicPriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ShelfPtsDataJandataMapper jandataMapper;
    @Autowired
    private BasicPatternRestrictRelationMapper restrictRelationMapper;

    @Autowired
    private WorkPriorityOrderJanCutMapper janCutMapper;
    @Autowired
    private WorkPriorityOrderJanNewMapper janNewMapper;
    @Autowired
    private BasicPatternRestrictResultMapper basicRestrictResultMapper;
    @Autowired
    private WorkPriorityOrderResultDataMapper priorityOrderResultDataMapper;
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanNewService priorityOrderJanNewService;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;

    //@Override
    //public Map<String, Object> getAreaInfo(String companyCd) {
    //    List<Areas> areasList = areasMapper.select(companyCd);
    //    return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    //}
    //@Override
    //public Map<String, Object> getAreaForShelfName(Integer shelfNameCd) {
    //    List<Areas> areasList = areasMapper.selectForShelfName(shelfNameCd);
    //    return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    //}

    @Override
    public Map<String, List<PriorityOrderResultDataDto>> commSetJan(Short partitionFlag, Short partitionVal,
        List<PtsTaiVo> taiData, List<PriorityOrderResultDataDto> workPriorityOrderResultData,
        List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations, Integer minFace) {
//        /**
//         * tai_によるとtanaは割り当てられた商品リストを分類して保管します
//         */
//        Map<String, List<PriorityOrderResultDataDto>> finalSetJanResultData = new HashMap<>();
//
//        if (partitionFlag == 0) {
//            //仕切りがない場合
//            partitionVal = 0;
//        }
//
//        /**
//         * 制約条件に従ってグループ分けして配置する
//         */
//        Map<Long, List<WorkPriorityOrderRestrictRelation>> relationByGroup = workPriorityOrderRestrictRelations
//                .stream().collect(Collectors.groupingBy(WorkPriorityOrderRestrictRelation::getRestrictCd, LinkedHashMap::new, Collectors.toList()));
//
//        List<WorkPriorityOrderRestrictRelation> relationValue = null;
//        List<PriorityOrderResultDataDto> relationSorted = null;
//        PriorityOrderResultDataDto priorityOrderResultData = null;
//        List<PriorityOrderResultDataDto> resultData = null;
//        List<String> adoptedJan = new ArrayList<>();
//
//        for (Map.Entry<Long, List<WorkPriorityOrderRestrictRelation>> relationEntry : relationByGroup.entrySet()) {
//            Long relationCd = relationEntry.getKey();
//            //商品がどこに置かれたかを記録する-同じ制約の商品が異なる台、段
//            int setResultDataIndex = 0;
//
//            //現在の制約条件に合致する商品はrankでソートする
//            //sortrankがnullの場合skurankのみでソート
//            relationSorted = workPriorityOrderResultData
//                    .stream().filter(data -> relationCd.equals(data.getRestrictCd()))
//                    .sorted(Comparator.comparing(PriorityOrderResultDataDto::getSortRank, Comparator.nullsFirst(Long::compareTo))
//                            .thenComparingLong(PriorityOrderResultDataDto::getSkuRank)
//                            .thenComparingLong(PriorityOrderResultDataDto::getNewRank)).collect(Collectors.toList());
//
//            relationValue = relationEntry.getValue();
//            for (WorkPriorityOrderRestrictRelation workPriorityOrderRestrictRelation : relationValue) {
//                Integer taiCd = workPriorityOrderRestrictRelation.getTaiCd();
//                Integer tanaCd = workPriorityOrderRestrictRelation.getTanaCd();
//                short tanaType = workPriorityOrderRestrictRelation.getTanaType();
//
//                //分類key、同一類の商品はface数を減らす処理を行う
//                String taiTanaKey = taiCd + "_" + tanaCd + "_" + tanaType;
//
//                Optional<PtsTaiVo> taiInfo = taiData.stream().filter(ptsTaiVo -> taiCd.equals(ptsTaiVo.getTaiCd())).findFirst();
//
//                if (!taiInfo.isPresent()) {
//                    logger.info("{}台信息不存在", taiCd);
//                    continue;
//                }
//
//                Integer taiWidth = taiInfo.get().getTaiWidth();
//                //テーブルまたはセグメントの幅、使用済みの幅
//                double width = taiWidth;
//                double usedWidth = 0;
//
//                if (tanaType != 0) {
//                    //セグメントの幅は、特定の位置セグメントの幅に応じて配置されます。
//                    width = taiWidth / 2.0;
//                }
//
//                //商品を置く
//                for (int i = setResultDataIndex; i < relationSorted.size(); i++) {
//                    priorityOrderResultData = relationSorted.get(i);
//
//                    //if jan is adopted, it will not be set
//                    if(adoptedJan.contains(priorityOrderResultData.getJanCd())){
//                        continue;
//                    }
//
//                    Long faceSku = Optional.ofNullable(priorityOrderResultData.getFaceSku()).orElse(1L);
//                    Long janWidth = Optional.ofNullable(priorityOrderResultData.getPlanoWidth()).orElse(0L);
//                    Long face = priorityOrderResultData.getFace();
//
//                    //商品幅nullまたは0の場合はデフォルト幅67 mm、faceSku>1の必要にfaceSkuを乗じます
//                    if (janWidth == 0) {
//                        janWidth = 67 * faceSku;
//                    }
//                    priorityOrderResultData.setWidth(janWidth);
//
//                    long janTotalWidth = janWidth * face + partitionVal;
//                    if (janTotalWidth + usedWidth <= width) {
//                        //face数に応じて並べて離すことができます
//                        setResultDataIndex = i + 1;
//
//                        priorityOrderResultData.setFaceFact(face);
//                        priorityOrderResultData.setTaiCd(taiCd);
//                        priorityOrderResultData.setTanaCd(tanaCd);
//                        priorityOrderResultData.setAdoptFlag(1);
//                        priorityOrderResultData.setTanaType((int) tanaType);
//
//                        adoptedJan.add(priorityOrderResultData.getJanCd());
//
//                        resultData = finalSetJanResultData.getOrDefault(taiTanaKey, new ArrayList<>());
//                        resultData.add(priorityOrderResultData);
//                        finalSetJanResultData.put(taiTanaKey, resultData);
//
//                        usedWidth += janTotalWidth;
//                    } else {
//                        resultData = finalSetJanResultData.getOrDefault(taiTanaKey, new ArrayList<>());
//                        if(this.isSetJanByCutFace(resultData, width, usedWidth, partitionVal, minFace, priorityOrderResultData)){
//                            priorityOrderResultData.setTaiCd(taiCd);
//                            priorityOrderResultData.setTanaType((int) tanaType);
//                            priorityOrderResultData.setAdoptFlag(1);
//                            priorityOrderResultData.setTanaCd(tanaCd);
//
//                            adoptedJan.add(priorityOrderResultData.getJanCd());
//
//                            setResultDataIndex = i + 1;
//                            resultData.add(priorityOrderResultData);
//                            finalSetJanResultData.put(taiTanaKey, resultData);
//                        }
//
//                        //face数に応じて並べても離せず、そのまま置かず、その位置の並べ替えを終了します
//                        break;
//                    }
//                }
//            }
//        }

        return null;
    }

    /**
     * set jans for shelf
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> commSetJanForShelf(Integer patternCd, String companyCd, Integer priorityOrderCd,
                                                  List<ZokuseiMst> zokuseiMsts, List<Integer> allCdList,
                                                  List<Map<String, Object>> restrictResult, List<Integer> attrList, String aud,
                                                  GetCommonPartsDataDto commonTableName, Short partitionVal, Short topPartitionVal,
                                                  Integer tanaWidthCheck, List<Map<String, Object>> tanaList, List<Map<String, Object>> relationMap,
                                                  List<PriorityOrderResultDataDto> janResult, List<Map<String, Object>> sizeAndIrisu,
                                                  int isReOrder, Integer productPowerCd,List<String> colNmforMst) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        logger.info("start set jan:{}", LocalDateTime.now());
        Integer currentTaiCd=1;
        for (int i = 0; i < tanaList.size(); i++) {
            Map<String, Object> tana = tanaList.get(i);

            Integer taiCd = MapUtils.getInteger(tana, MagicString.TAI_CD);

            if(i==(tanaList.size()-1)){
                tana.put("height", MapUtils.getInteger(tana, "taiHeight")-MapUtils.getInteger(tana, "tanaHeight"));
                continue;
            }

            Map<String, Object> nextTana = tanaList.get(i + 1);
            Integer nextTaiCd = MapUtils.getInteger(nextTana, MagicString.TAI_CD);
            if(!Objects.equals(currentTaiCd, nextTaiCd)){
                tana.put("height", MapUtils.getInteger(tana, "taiHeight")-MapUtils.getInteger(tana, "tanaHeight"));
            }else{
                tana.put("height", MapUtils.getInteger(nextTana, "tanaHeight")-MapUtils.getInteger(tana, "tanaHeight")-MapUtils.getInteger(nextTana, "tanaThickness"));
            }
            currentTaiCd = taiCd;
        }

        String proMstTb = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
        //jan group relation restrict_cd,convert map to PriorityOrderResultDataDto
        List<Map<String, Object>> newList = janNewMapper.selectJanNew(priorityOrderCd, allCdList, zokuseiMsts, commonTableName.getProInfoTable(),
                sizeAndIrisu, proMstTb);
        List<PriorityOrderResultDataDto> newJanDtoList = new ArrayList<>();
        for (int i = 0; i < newList.size(); i++) {
            if(Thread.currentThread().isInterrupted()){
                logger.info("task interrupted, step:{}", "3");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return null;
            }

            PriorityOrderResultDataDto dto = new PriorityOrderResultDataDto();
            Map<String, Object> zokusei = newList.get(i);

            dto.setFace(MapUtils.getLong(zokusei, "face"));
            dto.setSkuRank(MapUtils.getLong(zokusei, "jan_rank"));
            dto.setRank(MapUtils.getLong(zokusei, "jan_rank"));
            dto.setJanCd(MapUtils.getString(zokusei, "jan"));
            dto.setNewFlag(1);
            dto.setPlanoWidth(MapUtils.getLong(zokusei, MagicString.WIDTH_NAME));
            dto.setPlanoHeight(MapUtils.getLong(zokusei, MagicString.HEIGHT_NAME));
            dto.setPlanoDepth(MapUtils.getLong(zokusei, MagicString.DEPTH_NAME));
            dto.setIrisu(MapUtils.getString(zokusei, MagicString.IRISU_NAME));

            for (Map<String, Object> restrict : restrictResult) {
                if (Objects.equals(MapUtils.getLong(restrict, MagicString.RESTRICT_CD_UNDERLINE), MagicString.NO_RESTRICT_CD)) {
                    continue;
                }
                int equalsCount = 0;
                for (Integer integer : attrList) {
                    String restrictKey = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + integer, MagicString.DEFAULT_VALUE);
                    String zokuseiKey = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + integer, MagicString.DEFAULT_VALUE);

                    if(Objects.equals(restrictKey, zokuseiKey)){
                        equalsCount++;
                    }
                }

                if(equalsCount == attrList.size()){
                    int restrictCd = MapUtils.getInteger(restrict, MagicString.RESTRICT_CD_UNDERLINE);
                    dto.setRestrictCd((long)restrictCd);
                }
            }

            newJanDtoList.add(dto);
        }

        newJanDtoList = newJanDtoList.stream().filter(map->map.getRestrictCd()!=null).collect(Collectors.toList());
        newJanDtoList = basicPatternMstService.updateJanSize(newJanDtoList);
        janResult = basicPatternMstService.updateJanSize(janResult);

        Map<Long, List<PriorityOrderResultDataDto>> janResultByRestrictCd = janResult.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd, LinkedHashMap::new, Collectors.toList()));
        Map<String, Object> returnResultMap = this.getBackupJans(janResultByRestrictCd, newJanDtoList);
        List<PriorityOrderResultDataDto> backupJans = (List<PriorityOrderResultDataDto>) returnResultMap.get("backupJan");
        Map<String, PriorityOrderResultDataDto> repeatJanMap = (Map<String, PriorityOrderResultDataDto>) returnResultMap.get("repeatJanMap");

        if(isReOrder>0 && colNmforMst!=null && !backupJans.isEmpty()){
            List<WorkPriorityOrderResultData> reorderByJan = new ArrayList<>();
            if (colNmforMst.size()>1) {
                reorderByJan = priorityOrderResultDataMapper.getReorderByJan(companyCd, aud, productPowerCd,
                        priorityOrderCd, commonTableName, colNmforMst.get(0), colNmforMst.get(1), backupJans);
            }

            if(colNmforMst.size()==1){
                reorderByJan = priorityOrderResultDataMapper.getReorderByJan(companyCd, aud, productPowerCd,priorityOrderCd,
                        commonTableName, colNmforMst.get(0), null, backupJans);
            }

            Map<String, Integer> skuRankMap = reorderByJan.stream().collect(Collectors.toMap(WorkPriorityOrderResultData::getJanCd, WorkPriorityOrderResultData::getResultRank));
            backupJans.forEach(janItem-> janItem.setSkuRank(MapUtils.getLong(skuRankMap, janItem.getJanCd(), 9999L)));
        }

        Map<Long, List<PriorityOrderResultDataDto>> backupJansByRestrict = backupJans.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd));

        Map<Long, List<Map<String, Object>>> relationGroupRestrictCd = relationMap.stream()
                .collect(Collectors.groupingBy(map -> MapUtils.getLong(map, MagicString.RESTRICT_CD),
                        LinkedHashMap::new, Collectors.toList()));

        List<PriorityOrderResultDataDto> adoptJan = new ArrayList<>();
        List<PriorityOrderResultDataDto> finalAdoptJan = new ArrayList<>();
        List<Map<String, Object>> outOfHeight = new ArrayList<>();

        for (Map.Entry<Long, List<Map<String, Object>>> entry : relationGroupRestrictCd.entrySet()) {
            if(Thread.currentThread().isInterrupted()){
                logger.info("task interrupted, step:{}", "11");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return null;
            }

            Long restrictCd = entry.getKey();

            List<Map<String, Object>> relationList = entry.getValue();
            for (Map<String, Object> relation : relationList) {
                String taiCd = MapUtils.getString(relation, MagicString.TAI_CD);
                String tanaCd = MapUtils.getString(relation, MagicString.TANA_CD);

                List<Map<String, Object>> tanaMst = tanaList.stream().filter(map -> MapUtils.getString(map, MagicString.TAI_CD).equals(taiCd) &&
                        MapUtils.getString(map, MagicString.TANA_CD).equals(tanaCd)).collect(Collectors.toList());
                if (tanaMst.isEmpty()) {
                    continue;
                }

                Map<String, Object> mst = tanaMst.get(0);
                Integer tanaWidth = MapUtils.getInteger(mst, "tanaWidth");
                Integer tanaHeight = MapUtils.getInteger(mst, "height");

                List<PriorityOrderResultDataDto> jans = janResultByRestrictCd.get(restrictCd);
                List<PriorityOrderResultDataDto> backupJanList = backupJansByRestrict.getOrDefault(restrictCd, Lists.newArrayList());
                if(jans!=null){
                    List<Map<String, Object>> resultMap = this.doSetJan(partitionVal, topPartitionVal, tanaWidthCheck, adoptJan,
                            jans, relation, tanaWidth, tanaHeight, taiCd, tanaCd, backupJanList, repeatJanMap);
                    backupJansByRestrict.put(restrictCd, backupJanList);
                    outOfHeight.addAll(resultMap);
                }
            }

            //----------
            Map<String, Integer> relationSumJanCount = relationList.stream().collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.TAI_CD) + "_" +
                            MapUtils.getString(map, MagicString.TANA_CD) + "_" + MapUtils.getString(map, MagicString.RESTRICT_CD),
                    Collectors.summingInt(map -> MapUtils.getInteger(map, "janCount", 0))));
            for (Map<String, Object> relation : relationList) {
                if(Thread.currentThread().isInterrupted()){
                    logger.info("task interrupted, step:{}", "4");
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return null;
                }

                String taiCd = MapUtils.getString(relation, MagicString.TAI_CD);
                String tanaCd = MapUtils.getString(relation, MagicString.TANA_CD);
                Integer janCount = relationSumJanCount.get(taiCd+"_"+tanaCd+"_"+restrictCd);

                List<Map<String, Object>> tanaMst = tanaList.stream().filter(map -> MapUtils.getString(map, MagicString.TAI_CD).equals(taiCd) &&
                        MapUtils.getString(map, MagicString.TANA_CD).equals(tanaCd)).collect(Collectors.toList());
                if (tanaMst.isEmpty()) {
                    continue;
                }

                Map<String, Object> mst = tanaMst.get(0);
                Integer tanaWidth = MapUtils.getInteger(mst, "tanaWidth");
                double area = MapUtils.getDouble(relation, "area");
                Integer areaFlag = janCount==0 ? 1:0;
                Map<Long, List<PriorityOrderResultDataDto>> backupJansByRestrictCd = backupJans.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd));
                List<PriorityOrderResultDataDto> backupJansList = new ArrayList<>();
                if(backupJansByRestrictCd.containsKey(restrictCd) ){
                    backupJansList = backupJansByRestrictCd.get(restrictCd).stream().filter(data->!Objects.equals(data.getAdoptFlag(), 1))
                                    .sorted(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank)).collect(Collectors.toList());
                }

                List<PriorityOrderResultDataDto> notAdoptBackupJansList = backupJansList;
                if(janResultByRestrictCd.containsKey(restrictCd)){
                    List<PriorityOrderResultDataDto> notAdoppList = janResultByRestrictCd.get(restrictCd).stream().filter(dto -> !Objects.equals(dto.getAdoptFlag(), 1)).collect(Collectors.toList());
                    if (!notAdoppList.isEmpty()) {
                        backupJansList.addAll(notAdoppList);
                    }
                }

                if(areaFlag==1 && !notAdoptBackupJansList.isEmpty()){
                    //area
                    double groupArea = BigDecimal.valueOf(tanaWidth * area / 100.0).setScale(3, RoundingMode.CEILING).doubleValue();
                    double usedArea = adoptJan.stream().filter(dto -> restrictCd.equals(dto.getRestrictCd()) &&
                                    taiCd.equals(dto.getTaiCd() + "") && tanaCd.equals(dto.getTanaCd() + "")&& Objects.equals(1, dto.getAdoptFlag()))
                            .collect(Collectors.summarizingDouble(dto -> dto.getPlanoWidth() * dto.getFace())).getSum();

                    notAdoptBackupJansList = notAdoptBackupJansList.stream().filter(dto -> !Objects.equals(dto.getAdoptFlag(), 1)).collect(Collectors.toList());
                    for (int i = 0; i < notAdoptBackupJansList.size(); i++) {
                        PriorityOrderResultDataDto currentJan = notAdoptBackupJansList.get(i);
                        long janWidth = currentJan.getPlanoWidth();
                        long face = currentJan.getFace();

                        if(janWidth * face + partitionVal + usedArea <= groupArea){
                            currentJan.setOldTaiCd(currentJan.getTaiCd());
                            currentJan.setOldTanaCd(currentJan.getTanaCd());
                            currentJan.setOldTanapositionCd(currentJan.getOldTanapositionCd());

                            currentJan.setTaiCd(Integer.valueOf(taiCd));
                            currentJan.setTanaCd(Integer.valueOf(tanaCd));
                            currentJan.setTanapositionCd(null);

                            PriorityOrderResultDataDto copyCurrentJan = new PriorityOrderResultDataDto();
                            BeanUtils.copyProperties(currentJan, copyCurrentJan);
                            copyCurrentJan.setCutFlag(0);
                            adoptJan.add(copyCurrentJan);
                            currentJan.setAdoptFlag(1);
                            notAdoptBackupJansList.set(i, currentJan);
                            usedArea+=janWidth * face + partitionVal;
                        }
                    }
                }
            }
        }

        Map<String, List<PriorityOrderResultDataDto>> adoptJanByTaiTana = adoptJan.stream()
                .filter(dto->!Objects.equals(dto.getCutFlag(),1)).collect(Collectors.groupingBy(dto -> dto.getTaiCd() + "_" + dto.getTanaCd()));
        for (Map.Entry<String, List<PriorityOrderResultDataDto>> entry : adoptJanByTaiTana.entrySet()) {
            if(Thread.currentThread().isInterrupted()){
                logger.info("task interrupted, step:{}", "8");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return null;
            }

            List<PriorityOrderResultDataDto> value = entry.getValue();
            List<PriorityOrderResultDataDto> newJanList = value.stream().filter(dto -> dto.getTanapositionCd() == null
                    || dto.getTanapositionCd().equals(0)).collect(Collectors.toList());
            List<PriorityOrderResultDataDto> oldJanList = value.stream().filter(dto -> dto.getTanapositionCd() != null
                    && !dto.getTanapositionCd().equals(0)).collect(Collectors.toList());

            if(!newJanList.isEmpty()){
                AtomicInteger currentIndex = new AtomicInteger();
                for (int i = 0; i < value.size(); i++) {
                    int finalI = i;
                    if (value.stream().noneMatch(dto->Objects.equals(finalI + 1, dto.getTanapositionCd()))) {
                        if(currentIndex.get()<newJanList.size()){
                            PriorityOrderResultDataDto dataDto = newJanList.get(currentIndex.get());
                            dataDto.setTanapositionCd(finalI+1);
                            oldJanList.add(i, dataDto);
                            currentIndex.getAndIncrement();
                        }
                    }
                }

                if(currentIndex.get() < newJanList.size()){
                    List<PriorityOrderResultDataDto> remainList = newJanList.subList(currentIndex.get(), newJanList.size());
                    remainList.forEach(jan->jan.setTanapositionCd(currentIndex.getAndIncrement()));

                    oldJanList.addAll(remainList);
                }
            }

            finalAdoptJan.addAll(oldJanList);
        }

        //exchange jan position, old jan revert to old position
        for (int i = 0; i < finalAdoptJan.size(); i++) {
            if(Thread.currentThread().isInterrupted()){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                logger.info("task interrupted, step:{}", "9");
                return null;
            }

            PriorityOrderResultDataDto dataDto = finalAdoptJan.get(i);
            Integer oldTaiCd = dataDto.getOldTaiCd();
            Integer oldTanaCd = dataDto.getOldTanaCd();
            Integer oldTanaPositionCd = dataDto.getOldTanapositionCd();
            Long oldFace = dataDto.getFace();

            if(dataDto.getOldTaiCd()!=null && (!Objects.equals(oldTaiCd+"_"+oldTanaCd+"_"+oldTanaPositionCd,
                    dataDto.getTaiCd()+"_"+dataDto.getTanaCd()+ "_"+dataDto.getTanapositionCd()))){
                int oldIndex = -1;
                for (int j = 0; j < finalAdoptJan.size(); j++) {
                    PriorityOrderResultDataDto oldJan = finalAdoptJan.get(j);
                    if (oldJan.getTaiCd().equals(oldTaiCd) && oldJan.getTanaCd().equals(oldTanaCd) &&
                            oldJan.getTanapositionCd().equals(oldTanaPositionCd) && oldJan.getRestrictCd().equals(dataDto.getRestrictCd())) {
                        oldIndex = j;
                        break;
                    }
                }
                if (oldIndex>-1) {
                    PriorityOrderResultDataDto oldPositionJan = finalAdoptJan.get(oldIndex);
                    PriorityOrderResultDataDto tmp = new PriorityOrderResultDataDto();
                    BeanUtils.copyProperties(dataDto, tmp);

                    BeanUtils.copyProperties(oldPositionJan, dataDto);
                    dataDto.setTaiCd(tmp.getTaiCd());
                    dataDto.setTanaCd(tmp.getTanaCd());
                    dataDto.setFace(tmp.getFace());
                    dataDto.setTanapositionCd(tmp.getTanapositionCd());

                    BeanUtils.copyProperties(tmp, oldPositionJan);
                    oldPositionJan.setTaiCd(oldTaiCd);
                    oldPositionJan.setTanaCd(oldTanaCd);
                    oldPositionJan.setFace(oldFace);
                    oldPositionJan.setTanapositionCd(oldTanaPositionCd);

                    finalAdoptJan.set(oldIndex, oldPositionJan);
                    finalAdoptJan.set(i, dataDto);
                }else{
                    //check old position contains current group
                    if(relationMap.stream().anyMatch(map->taiTanaEquals(MapUtils.getInteger(map, "tai_cd"),
                            oldTaiCd, MapUtils.getInteger(map, "tana_cd"), oldTanaCd) &&
                            Objects.equals(MapUtils.getLong(map, "restrict_cd"), dataDto.getRestrictCd()))){
                        dataDto.setTaiCd(oldTaiCd);
                        dataDto.setTanaCd(oldTanaCd);
                        dataDto.setFace(oldFace);
                        dataDto.setTanapositionCd(oldTanaPositionCd);
                        finalAdoptJan.set(i, dataDto);
                    }
                }
            }
        }

        logger.info("end set jan:{}", LocalDateTime.now());
        return ResultMaps.result(ResultEnum.SUCCESS, finalAdoptJan);
    }

    /**
     * return the map of the eliminated Jan and the new Jan
     * @param janResultByRestrictCd
     * @param newJanDtoList
     */
    private Map<String, Object> getBackupJans(Map<Long, List<PriorityOrderResultDataDto>> janResultByRestrictCd,
                                                           List<PriorityOrderResultDataDto> newJanDtoList){
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, PriorityOrderResultDataDto> repeatJanMap = new HashMap<>();
        List<PriorityOrderResultDataDto> backupJan = new ArrayList<>();

        if(newJanDtoList.isEmpty()){
            resultMap.put("backupJan", backupJan);
            resultMap.put("repeatJanMap", repeatJanMap);
            return resultMap;
        }

        Map<Long, List<PriorityOrderResultDataDto>> newJanByRestrictCd = newJanDtoList.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd));
        for (Map.Entry<Long, List<PriorityOrderResultDataDto>> entry : janResultByRestrictCd.entrySet()) {
            Long restrictCd = entry.getKey();
            List<PriorityOrderResultDataDto> value = entry.getValue();
            Map<String, Long> janCdCount = value.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getJanCd, Collectors.counting()));

            List<PriorityOrderResultDataDto> uniqueValue = value.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank)
                            .thenComparing(PriorityOrderResultDataDto::getJanCd))), ArrayList::new)
            );

            int cutCount = 0;

            List<PriorityOrderResultDataDto> backupJanByRestrictCd = new ArrayList<>();
            if (newJanByRestrictCd.containsKey(restrictCd)) {
                List<PriorityOrderResultDataDto> janNewList = newJanByRestrictCd.get(restrictCd);
                for (int i = 0; i < janNewList.size(); i++) {
                    int rank = janNewList.get(i).getRank().intValue();
                    PriorityOrderResultDataDto newJanDto = new PriorityOrderResultDataDto();
                    BeanUtils.copyProperties(janNewList.get(i), newJanDto);

                    this.setFaceFromOldPts(newJanDto,rank,uniqueValue);

                    newJanDto.setNewFlag(1);
                    newJanDto.setZaikosu(1);

                    if(rank<=uniqueValue.size()){
                        cutCount++;
                        newJanDto.setUseNewFlag(1);
                    }
                    backupJanByRestrictCd.add(newJanDto);
                }

                this.doRepeatBackupJan(backupJanByRestrictCd, uniqueValue, janCdCount, repeatJanMap, janNewList, cutCount);
            }

            backupJan.addAll(backupJanByRestrictCd);
        }
        resultMap.put("backupJan", backupJan);
        resultMap.put("repeatJanMap", repeatJanMap);
        return resultMap;
    }

    private void setFaceFromOldPts(PriorityOrderResultDataDto newJanDto, int rank, List<PriorityOrderResultDataDto> uniqueValue){
        Optional<PriorityOrderResultDataDto> max = uniqueValue.stream().filter(dto -> dto.getSkuRank() < rank).max(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank));
        if (max.isPresent()) {
            newJanDto.setFace(max.get().getFace());
            newJanDto.setFaceFact(max.get().getFace());
        }else{
            Optional<PriorityOrderResultDataDto> min = uniqueValue.stream().filter(dto -> dto.getSkuRank() >= rank).min(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank));
            min.ifPresent(dto->{
                newJanDto.setFace(dto.getFace());
                newJanDto.setFaceFact(dto.getFace());
            });
        }
    }

    private void doRepeatBackupJan(List<PriorityOrderResultDataDto> backupJanByRestrictCd,
                                                               List<PriorityOrderResultDataDto> uniqueValue,
       Map<String, Long> janCdCount, Map<String, PriorityOrderResultDataDto> repeatJanMap,List<PriorityOrderResultDataDto> janNewList,
                                                               int cutCount){
        List<PriorityOrderResultDataDto> uniqueValueNoCut = uniqueValue.stream()
                .filter(dto -> !Objects.equals(dto.getCutFlag(), 1)).collect(Collectors.toList());
        for (int i = 0; i < cutCount; i++) {
            if(uniqueValueNoCut.size() - i - 1 < 0){
                continue;
            }
            PriorityOrderResultDataDto dataDto = uniqueValueNoCut.get(uniqueValueNoCut.size() - i - 1);
            dataDto.setOldTaiCd(dataDto.getTaiCd());
            dataDto.setOldTanaCd(dataDto.getTanaCd());
            dataDto.setOldTanapositionCd(dataDto.getTanapositionCd());
            dataDto.setFaceFact(dataDto.getFace());
            Long janSum = janCdCount.get(dataDto.getJanCd());
            if(janSum>1){
                repeatJanMap.put(dataDto.getJanCd(), janNewList.get(cutCount-i-1));
            }
            PriorityOrderResultDataDto copyDataDto = new PriorityOrderResultDataDto();
            BeanUtils.copyProperties(dataDto,copyDataDto);
            backupJanByRestrictCd.add(copyDataDto);
        }
    }

    private List<Map<String, Object>> doSetJan(Short partitionVal,Short topPartitionVal, Integer tanaWidthCheck,
                                         List<PriorityOrderResultDataDto> adoptJan, List<PriorityOrderResultDataDto> jans,
                                         Map<String, Object> relation, Integer tanaWidth, Integer tanaHeight, String taiCd, String tanaCd,
                                         List<PriorityOrderResultDataDto> backupJans, Map<String, PriorityOrderResultDataDto> repeatMap){
        String restrictCd = MapUtils.getString(relation, MagicString.RESTRICT_CD);
        double area = MapUtils.getDouble(relation, "area");
        Integer janCount = MapUtils.getInteger(relation, "janCount");
        double groupArea = BigDecimal.valueOf(tanaWidth * area / 100.0).setScale(3, RoundingMode.CEILING).doubleValue();
        Long usedArea = 0L;
        int usedJanCount = 0;
        List<Map<String, Object>> resultHeightMap = new ArrayList<>();

        Short partitionValue = partitionVal;
        if(!Objects.equals(tanaWidthCheck, 1)){
            partitionValue = 0;
        }

        List<PriorityOrderResultDataDto> adoptJanByTaiTana = new ArrayList<>();
        int usedBackupIndex = 0;
        backupJans = backupJans.stream().sorted(Comparator.comparing(PriorityOrderResultDataDto::getUseNewFlag).reversed()
                .thenComparing(PriorityOrderResultDataDto::getSkuRank).thenComparing(PriorityOrderResultDataDto::getJanCd)).collect(Collectors.toList());
        for (PriorityOrderResultDataDto jan : jans) {
            if(Objects.equals(jan.getAdoptFlag(), 1)){
                continue;
            }
            PriorityOrderResultDataDto newJanDto = new PriorityOrderResultDataDto();

            Long width;
            Long face;
            Long janWidth;
            String janOld = jan.getJanCd();

            jan.setOldTaiCd(jan.getTaiCd());
            jan.setOldTanaCd(jan.getTanaCd());
            jan.setOldTanapositionCd(jan.getTanapositionCd());
            jan.setFaceFact(jan.getFace());

            relation.put("areaFlag", 0);
            if (!backupJans.isEmpty() && (backupJans.stream().anyMatch(dto->janOld.equals(dto.getJanCd())
                    && jan.getRestrictCd().equals(dto.getRestrictCd())) || Objects.equals(jan.getCutFlag(), 1))) {
                BeanUtils.copyProperties(jan, newJanDto);
                newJanDto.setFaceFact(jan.getFace());
                newJanDto.setJanOld(janOld);

                if(repeatMap.containsKey(janOld)){
                    PriorityOrderResultDataDto backupJanNew = repeatMap.get(janOld);
                    newJanDto.setJanCd(backupJanNew.getJanCd());
                    newJanDto.setFace(backupJanNew.getFace());
                    newJanDto.setFaceFact(backupJanNew.getFace());
                    newJanDto.setZaikosu(1);
                    newJanDto.setCutFlag(0);
                    newJanDto.setAdoptFlag(1);

                    backupJans.forEach(janItem->{
                        if (janItem.getJanCd().equals(backupJanNew.getJanCd())) {
                            janItem.setAdoptFlag(1);
                        }
                    });
                }else{
                    boolean isCut = true;
                    for (int i = usedBackupIndex; i < backupJans.size(); i++) {
                        PriorityOrderResultDataDto backupJanNew = backupJans.get(i);
                        if(Objects.equals(backupJanNew.getAdoptFlag(), 1)){
                            continue;
                        }
                        newJanDto.setJanCd(backupJanNew.getJanCd());
                        newJanDto.setFace(backupJanNew.getFace());
                        newJanDto.setFaceFact(backupJanNew.getFace());
                        newJanDto.setZaikosu(1);
                        newJanDto.setCutFlag(0);
                        newJanDto.setAdoptFlag(1);

                        backupJanNew.setAdoptFlag(1);
                        backupJans.set(usedBackupIndex++, backupJanNew);
                        isCut = false;
                        break;
                    }

                    if(isCut){
                        newJanDto.setCutFlag(1);
                    }
                }

                jan.setCutFlag(1);
                jan.setAdoptFlag(1);
                if(janCount==null || janCount==0){
                    //1:area change or restrictCd change --> width judge
                    relation.put("areaFlag", 1);
                }else{
                    //jan_count judge
                    relation.put("areaFlag", 0);
                }
                adoptJanByTaiTana.add(newJanDto);
                continue;
            }else{
                if(taiCd.equals(jan.getTaiCd()+"") && tanaCd.equals(jan.getTanaCd()+"")){
                    BeanUtils.copyProperties(jan, newJanDto);
                    newJanDto.setFaceFact(jan.getFace());
                }else{
                    continue;
                }
            }

            if(topPartitionVal!=null){
                Long janHeight = Optional.ofNullable(jan.getHeight()).orElse(MagicString.DEFAULT_HEIGHT) + topPartitionVal;
                if(janHeight+topPartitionVal>tanaHeight){
                    Map<String, Object> errInfo = new HashMap<>();
                    errInfo.put("taiCd", Integer.parseInt(taiCd));
                    errInfo.put("tanaCd", Integer.parseInt(tanaCd));
                    errInfo.put("janHeight", janHeight);
                    errInfo.put("jan", jan.getJanCd());
                    errInfo.put("restrictCd", jan.getRestrictCd());
                    resultHeightMap.add(errInfo);
                }
            }

            width = Optional.ofNullable(jan.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH);
            face = jan.getFace();
            janWidth = width + partitionValue;

            boolean condition = false;
            if((MagicString.NO_RESTRICT_CD+"").equals(restrictCd) || janCount==null || janCount==0){
                condition = janWidth*face + usedArea <= groupArea;
            }else{
                condition = usedJanCount<janCount;
            }

            if(condition){
                usedArea += janWidth*face + partitionValue;

                newJanDto.setOldTaiCd(newJanDto.getTaiCd());
                newJanDto.setOldTanaCd(newJanDto.getTanaCd());
                newJanDto.setOldTanapositionCd(newJanDto.getTanapositionCd());

                newJanDto.setTaiCd(Integer.parseInt(taiCd));
                newJanDto.setTanaCd(Integer.parseInt(tanaCd));
                newJanDto.setFaceFact(face);
                newJanDto.setFace(face);
                newJanDto.setAdoptFlag(1);
                adoptJanByTaiTana.add(newJanDto);
                jan.setAdoptFlag(1);
                usedJanCount++;
            }else{
                break;
            }
        }

        adoptJan.addAll(adoptJanByTaiTana);

        return resultHeightMap;
    }

    /**
     * @deprecated don't cut face
     * cut face数で置けるかどうか
     * ろんり：最初のステップ：先に置く商品に対してface数-1を行います
     *          1.face数*商品幅が置けば置いて、実際に保管しているface数をメモします
     *          2.手放せない
     *      ステップ2：同一棚区分(taiCd_tanaCd_tanaType)における商品リストの逆順についてcut face判定を行う
     *          1.入数irisu=1の商品がなければcutを行わず、そのまま終了
     *          2.入数irisu=1がある場合は、商品リストを逆順に巡回し、face数-1を行います
     *              3.face数*幅が置けば置いて、実際に保管しているface数をメモします
     *              4.置くことができなくて、終わって、この位置は放しません
     * @param resultDataDtoList スタンド別商品リスト
     * @param targetResultData 置くべき商品
     * @param width 現在の棚区分の幅
     * @param usedWidth 現在の棚区分の使用済み幅
     * @param minFace 最小face数
     * @param partitionVal スペーサ幅
     * @return true放す，false放さない
     */
    @Deprecated
    private boolean isSetJanByCutFace(List<PriorityOrderResultDataDto> resultDataDtoList, double width, double usedWidth, long partitionVal,
                                      long minFace, PriorityOrderResultDataDto targetResultData){
        Long face = targetResultData.getFace();
        Long janWidth = Optional.ofNullable(targetResultData.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH);

        //使用可能な幅が残ります(仕切りがある場合を考慮する必要があります)
        double remainderWidth = width - usedWidth;
        //カットでターゲット商品のface数を落とせますか？
        boolean isSetByCut = false;

        //まず置く商品を1つ1つ下げていくかどうかを見てみましょう
        for (Long i = face - 1; i >= minFace; i--) {
            if((janWidth * i + partitionVal) <= remainderWidth){
                //置くことができる
                isSetByCut = true;
                //実際のface数を保存
                targetResultData.setFaceFact(i);
                break;
            }
        }

        if(!isSetByCut){
            //カットで対象商品のface数を落とすことはできません
            //入数=1のもののみを処理し、1に等しくないものはcutを行わない
            List<PriorityOrderResultDataDto> resultDataDtoByIrisu = resultDataDtoList.stream()
                    .filter(data-> 1 == Long.parseLong(Optional.ofNullable(data.getPlanoIrisu()).orElse("1"))).collect(Collectors.toList());

            if(resultDataDtoByIrisu.isEmpty()){
                //条件を満たさないとcutできない
                return false;
            }

            //最小faceで放置すると、どのくらいの幅が必要か(仕切りがある場合を考慮する必要がある)+残りの無駄な幅
            long needWidth = Optional.ofNullable(targetResultData.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH) * minFace + partitionVal;
            PriorityOrderResultDataDto currentResultData = null;
            for (int i = resultDataDtoByIrisu.size()-1; i >= 0 ; i--) {
                currentResultData = resultDataDtoByIrisu.get(i);
                Long faceFact = currentResultData.getFaceFact();

                //最小face以下ではcutできません
                //現在の商品は1つのface数を減らして、目標の商品を置くことができますか
                //Cut 1つのfaceの幅に残りの幅を加えてターゲットの上に置くのに必要な幅を満たすことができるかどうか
                if(faceFact > minFace && Optional.ofNullable(currentResultData.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH) + remainderWidth >= needWidth){
                    currentResultData.setFaceFact(faceFact -  1);
                    targetResultData.setFaceFact(minFace);
                    isSetByCut = true;
                    break;
                }
            }
        }

        return isSetByCut;
    }

    /**
     * まず台遍歴してから棚を遍歴し、同じ棚の商品は左から右の順に1からラベルを付け、棚が変わり、ラベルを付け直す
     *
     * @param workPriorityOrderResultData
     * @return
     */
    @Override
    public List<WorkPriorityOrderResultDataDto> calculateTanaPosition(List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData, int isReOrder) {
        //ハウス位置のresultdataデータ
        List<WorkPriorityOrderResultDataDto> positionResultData = new ArrayList<>(workPriorityOrderResultData.size());
        //テーブルに基づいてグループを分けて巡回する
        Map<Integer, List<WorkPriorityOrderResultDataDto>> workPriorityOrderResultDataByTai = workPriorityOrderResultData.stream()
                .collect(Collectors.groupingBy(WorkPriorityOrderResultDataDto::getTaiCd, LinkedHashMap::new, Collectors.toList()));
        Map<Integer, List<WorkPriorityOrderResultDataDto>> workPriorityOrderResultDataByTana = null;
        List<WorkPriorityOrderResultDataDto> workPriorityOrderResultDataDtos = null;

        for (Map.Entry<Integer, List<WorkPriorityOrderResultDataDto>> entrySet : workPriorityOrderResultDataByTai.entrySet()) {
            Integer taiCd = entrySet.getKey();

            workPriorityOrderResultDataDtos = workPriorityOrderResultDataByTai.get(taiCd);
            //小屋によってグループを分けて遍歴する
            workPriorityOrderResultDataByTana = workPriorityOrderResultDataDtos.stream()
                    .collect(Collectors.groupingBy(WorkPriorityOrderResultDataDto::getTanaCd, LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<Integer, List<WorkPriorityOrderResultDataDto>> entry : workPriorityOrderResultDataByTana.entrySet()) {
                //同じ棚で、番号が1から加算され、次の棚で再び1から加算されます。
                Integer tantaPositionCd=0;
                Integer tanaCd = entry.getKey();
                List<WorkPriorityOrderResultDataDto> resultDataByTanaCdList = workPriorityOrderResultDataByTana.get(tanaCd);

                if(isReOrder>0){
                    resultDataByTanaCdList = resultDataByTanaCdList.stream().sorted(Comparator.comparing(WorkPriorityOrderResultDataDto::getSkuRank)).collect(Collectors.toList());
                }

                for (WorkPriorityOrderResultDataDto currentDataDto : resultDataByTanaCdList) {
                    currentDataDto.setTanapositionCd(++tantaPositionCd);
                    currentDataDto.setFaceMen(Optional.ofNullable(currentDataDto.getFaceMen()).orElse(1));
                    currentDataDto.setFaceKaiten(Optional.ofNullable(currentDataDto.getFaceKaiten()).orElse(0));
                    currentDataDto.setTumiagesu(Optional.ofNullable(currentDataDto.getTumiagesu()).orElse(1));
                    currentDataDto.setFaceDisplayflg(Optional.ofNullable(currentDataDto.getFaceDisplayflg()).orElse(0));
                    currentDataDto.setFacePosition(Optional.ofNullable(currentDataDto.getFacePosition()).orElse(1));
                    currentDataDto.setDepthDisplayNum(Optional.ofNullable(currentDataDto.getDepthDisplayNum()).orElse(1));
                    positionResultData.add(currentDataDto);
                }
            }
        }
        return positionResultData;
    }

    /**
     * get bussiness
     */
    @Override
    public CommonPartsDto getCommonPartsData(Integer productPowerCd, String companyCd){
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        ProductPowerParam param = productPowerParamMstMapper.getParam(companyCd, productPowerCd);
        String commonPartsData = param.getCommonPartsData();
        JSONObject jsonObject = JSON.parseObject(commonPartsData);

        CommonPartsDto commonPartsDto = new CommonPartsDto();
        commonPartsDto.setProdMstClass(jsonObject.getString("prodMstClass"));
        commonPartsDto.setCoreCompany(coreCompany);
        commonPartsDto.setProdIsCore(jsonObject.getString("prodIsCore"));

        return commonPartsDto;
    }

    @Override
    public CommonPartsDto getPriorityCommonPartsData(Integer priorityOrderCd, String companyCd){
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        String commonPartsData = priorityOrderMstMapper.getCommonPartsData(companyCd, priorityOrderCd);
        JSONObject jsonObject = JSON.parseObject(commonPartsData);

        CommonPartsDto commonPartsDto = new CommonPartsDto();
        commonPartsDto.setProdMstClass(jsonObject.getString("prodMstClass"));
        commonPartsDto.setCoreCompany(coreCompany);
        commonPartsDto.setProdIsCore(jsonObject.getString("prodIsCore"));

        return commonPartsDto;
    }

    @Override
    public boolean taiTanaEquals(Integer taiCd1, Integer taiCd2,Integer tanaCd1, Integer tanaCd2){
        return Objects.equals(taiCd1, taiCd2) && Objects.equals(tanaCd1, tanaCd2);
    }

    @Override
    public String getClassifyKey(List<Integer> zokuseiList, Map<String, Object> janMap){
        StringBuilder key = new StringBuilder();
        for (Integer zokusei : zokuseiList) {
            if(key.length()>0){
                key.append(",");
            }
            String val = MapUtils.getString(janMap, zokusei + "", MagicString.DEFAULT_VALUE);
            key.append(Strings.isNullOrEmpty(val)?MagicString.DEFAULT_VALUE: val);
        }

        return key.toString();
    }

    @Override
    public boolean zokuseiEquals(List<Integer> attrList, Map<String, Object> restrict, Map<String, Object> zokusei){
        int equalsCount = 0;
        for (Integer integer : attrList) {
            String restrictKeyVal = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + integer, MagicString.DEFAULT_VALUE);
            String restrictKey = Strings.isNullOrEmpty(restrictKeyVal)?MagicString.DEFAULT_VALUE:restrictKeyVal;
            String zokuseiKeyVal = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + integer, MagicString.DEFAULT_VALUE);
            String zokuseiKey = Strings.isNullOrEmpty(zokuseiKeyVal)?MagicString.DEFAULT_VALUE: zokuseiKeyVal;

            if(Objects.equals(restrictKey, zokuseiKey)){
                equalsCount++;
            }
        }

        return equalsCount == attrList.size();
    }

    @Override
    public List<Map<String, Object>> recalculationArea(List<PriorityOrderResultDataDto> workData, List<Map<String, Object>> tanaList){
        Map<String, List<PriorityOrderResultDataDto>> dataByTaitana = workData.stream().sorted(Comparator.comparing(PriorityOrderResultDataDto::getTaiCd)
                        .thenComparing(PriorityOrderResultDataDto::getTanaCd).thenComparing(PriorityOrderResultDataDto::getTanapositionCd))
                .collect(Collectors.groupingBy(dto-> Joiner.on(",").join(Lists.newArrayList(dto.getTaiCd(), dto.getTanaCd()))));
        Map<String, Map<String, Object>> areaList = new HashMap<>();
        dataByTaitana.forEach((key, value) -> {
            final long[] currentRestrictCd = {-1L};
            final double[] area = {0.0};
            final int[] janCount = {0};
            String[] taiTana = key.split(",");
            String taiCd = taiTana[0];
            String tanaCd = taiTana[1];

            Optional<Map<String, Object>> any = tanaList.stream().filter(map -> MapUtils.getString(map, MagicString.TAI_CD).equals(taiCd) && MapUtils.getString(map, MagicString.TANA_CD).equals(tanaCd)).findAny();
            if (!any.isPresent()) {
                return;
            }

            Integer tanaWidth = MapUtils.getInteger(any.get(), "tanaWidth");
            final int[] tanaPosition = {0};
            value.forEach(v->{
                Map<String, Object> map = Maps.newHashMap();
                if(!Objects.equals(currentRestrictCd[0], v.getRestrictCd())){
                    area[0] = 0;
                    tanaPosition[0]++;
                    janCount[0] = 0;
                }
                area[0] += BigDecimal.valueOf(v.getPlanoWidth()*v.getFaceFact()).divide(BigDecimal.valueOf(tanaWidth), 5,RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                janCount[0]++;

                map.put(MagicString.TAI_CD, taiCd);
                map.put(MagicString.TANA_CD, tanaCd);
                map.put(MagicString.TANA_POSITION, tanaPosition[0]);
                map.put(MagicString.RESTRICT_CD, v.getRestrictCd());
                map.put("area", area[0]);
                map.put("janCount", janCount[0]);

                areaList.put(Joiner.on("_").join(Lists.newArrayList(taiCd, tanaCd, tanaPosition[0])), map);
                currentRestrictCd[0] = v.getRestrictCd();
            });
        });

        return Lists.newArrayList(areaList.values());
    }
}
