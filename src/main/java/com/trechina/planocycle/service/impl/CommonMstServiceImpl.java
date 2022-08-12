package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.CommonPartsDto;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.ProductPowerParam;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
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

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        /**
         * tai_によるとtanaは割り当てられた商品リストを分類して保管します
         */
        Map<String, List<PriorityOrderResultDataDto>> finalSetJanResultData = new HashMap<>();

        if (partitionFlag == 0) {
            //仕切りがない場合
            partitionVal = 0;
        }

        /**
         * 制約条件に従ってグループ分けして配置する
         */
        Map<Long, List<WorkPriorityOrderRestrictRelation>> relationByGroup = workPriorityOrderRestrictRelations
                .stream().collect(Collectors.groupingBy(WorkPriorityOrderRestrictRelation::getRestrictCd, LinkedHashMap::new, Collectors.toList()));

        List<WorkPriorityOrderRestrictRelation> relationValue = null;
        List<PriorityOrderResultDataDto> relationSorted = null;
        PriorityOrderResultDataDto priorityOrderResultData = null;
        List<PriorityOrderResultDataDto> resultData = null;
        List<String> adoptedJan = new ArrayList<>();

        for (Map.Entry<Long, List<WorkPriorityOrderRestrictRelation>> relationEntry : relationByGroup.entrySet()) {
            Long relationCd = relationEntry.getKey();
            //商品がどこに置かれたかを記録する-同じ制約の商品が異なる台、段
            int setResultDataIndex = 0;

            //現在の制約条件に合致する商品はrankでソートする
            //sortrankがnullの場合skurankのみでソート
            relationSorted = workPriorityOrderResultData
                    .stream().filter(data -> relationCd.equals(data.getRestrictCd()))
                    .sorted(Comparator.comparing(PriorityOrderResultDataDto::getSortRank, Comparator.nullsFirst(Long::compareTo))
                            .thenComparingLong(PriorityOrderResultDataDto::getSkuRank)
                            .thenComparingLong(PriorityOrderResultDataDto::getNewRank)).collect(Collectors.toList());

            relationValue = relationEntry.getValue();
            for (WorkPriorityOrderRestrictRelation workPriorityOrderRestrictRelation : relationValue) {
                Integer taiCd = workPriorityOrderRestrictRelation.getTaiCd();
                Integer tanaCd = workPriorityOrderRestrictRelation.getTanaCd();
                short tanaType = workPriorityOrderRestrictRelation.getTanaType();

                //分類key、同一類の商品はface数を減らす処理を行う
                String taiTanaKey = taiCd + "_" + tanaCd + "_" + tanaType;

                Optional<PtsTaiVo> taiInfo = taiData.stream().filter(ptsTaiVo -> taiCd.equals(ptsTaiVo.getTaiCd())).findFirst();

                if (!taiInfo.isPresent()) {
                    logger.info("{}台信息不存在", taiCd);
                    continue;
                }

                Integer taiWidth = taiInfo.get().getTaiWidth();
                //テーブルまたはセグメントの幅、使用済みの幅
                double width = taiWidth;
                double usedWidth = 0;

                if (tanaType != 0) {
                    //セグメントの幅は、特定の位置セグメントの幅に応じて配置されます。
                    width = taiWidth / 2.0;
                }

                //商品を置く
                for (int i = setResultDataIndex; i < relationSorted.size(); i++) {
                    priorityOrderResultData = relationSorted.get(i);

                    //if jan is adopted, it will not be set
                    if(adoptedJan.contains(priorityOrderResultData.getJanCd())){
                        continue;
                    }

                    Long faceSku = Optional.ofNullable(priorityOrderResultData.getFaceSku()).orElse(1L);
                    Long janWidth = Optional.ofNullable(priorityOrderResultData.getPlanoWidth()).orElse(0L);
                    Long face = priorityOrderResultData.getFace();

                    //商品幅nullまたは0の場合はデフォルト幅67 mm、faceSku>1の必要にfaceSkuを乗じます
                    if (janWidth == 0) {
                        janWidth = 67 * faceSku;
                    }
                    priorityOrderResultData.setWidth(janWidth);

                    long janTotalWidth = janWidth * face + partitionVal;
                    if (janTotalWidth + usedWidth <= width) {
                        //face数に応じて並べて離すことができます
                        setResultDataIndex = i + 1;

                        priorityOrderResultData.setFaceFact(face);
                        priorityOrderResultData.setTaiCd(taiCd);
                        priorityOrderResultData.setTanaCd(tanaCd);
                        priorityOrderResultData.setAdoptFlag(1);
                        priorityOrderResultData.setTanaType((int) tanaType);

                        adoptedJan.add(priorityOrderResultData.getJanCd());

                        resultData = finalSetJanResultData.getOrDefault(taiTanaKey, new ArrayList<>());
                        resultData.add(priorityOrderResultData);
                        finalSetJanResultData.put(taiTanaKey, resultData);

                        usedWidth += janTotalWidth;
                    } else {
                        resultData = finalSetJanResultData.getOrDefault(taiTanaKey, new ArrayList<>());
                        if(this.isSetJanByCutFace(resultData, width, usedWidth, partitionVal, minFace, priorityOrderResultData)){
                            priorityOrderResultData.setTaiCd(taiCd);
                            priorityOrderResultData.setTanaType((int) tanaType);
                            priorityOrderResultData.setAdoptFlag(1);
                            priorityOrderResultData.setTanaCd(tanaCd);

                            adoptedJan.add(priorityOrderResultData.getJanCd());

                            setResultDataIndex = i + 1;
                            resultData.add(priorityOrderResultData);
                            finalSetJanResultData.put(taiTanaKey, resultData);
                        }

                        //face数に応じて並べても離せず、そのまま置かず、その位置の並べ替えを終了します
                        break;
                    }
                }
            }
        }

        return finalSetJanResultData;
    }

    /**
     * set jans for shelf
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> commSetJanForShelf(Integer patternCd, String companyCd, Integer priorityOrderCd,
                                                  Integer minFace, List<ZokuseiMst> zokuseiMsts, List<Integer> allCdList,
                                                  List<Map<String, Object>> restrictResult, List<Integer> attrList, String aud,
                                                  GetCommonPartsDataDto commonTableName, Short partitionVal, Short topPartitionVal,
                                                  Integer tanaWidthCheck, List<Map<String, Object>> tanaList, List<Map<String, Object>> relationMap,
                                                  List<PriorityOrderResultDataDto> janResult, List<Map<String, Object>> sizeAndIrisu,
                                                  int isReOrder) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
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

        //jan group relation restrict_cd,convert map to PriorityOrderResultDataDto
        List<Map<String, Object>> newList = janNewMapper.selectJanNew(priorityOrderCd, allCdList, zokuseiMsts, commonTableName.getProInfoTable(),
                sizeAndIrisu);
        List<PriorityOrderResultDataDto> newJanDtoList = new ArrayList<>();
        for (int i = 0; i < newList.size(); i++) {
            PriorityOrderResultDataDto dto = new PriorityOrderResultDataDto();
            Map<String, Object> zokusei = newList.get(i);

            dto.setFace(MapUtils.getLong(zokusei, "face"));
            dto.setSkuRank(MapUtils.getLong(zokusei, "jan_rank"));
            dto.setJanCd(MapUtils.getString(zokusei, "jan"));
            dto.setNewFlag(1);
            dto.setPlanoWidth(MapUtils.getLong(zokusei, MagicString.WIDTH_NAME));
            dto.setPlanoHeight(MapUtils.getLong(zokusei, MagicString.HEIGHT_NAME));
            dto.setPlanoDepth(MapUtils.getLong(zokusei, MagicString.DEPTH_NAME));
            dto.setIrisu(MapUtils.getString(zokusei, MagicString.IRISU_NAME));

            for (Map<String, Object> restrict : restrictResult) {
                if (Objects.equals(MapUtils.getLong(restrict, "restrict_cd"), MagicString.NO_RESTRICT_CD)) {
                    continue;
                }
                int equalsCount = 0;
                for (Integer integer : attrList) {
                    String restrictKey = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + integer, "");
                    String zokuseiKey = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + integer, "");

                    if(restrictKey!=null && restrictKey.equals(zokuseiKey)){
                        equalsCount++;
                    }
                }

                if(equalsCount == attrList.size()){
                    int restrictCd = MapUtils.getInteger(restrict, "restrict_cd");
                    dto.setRestrictCd((long)restrictCd);
                }
            }

            newJanDtoList.add(dto);
        }

        newJanDtoList = newJanDtoList.stream().filter(map->map.getRestrictCd()!=null).collect(Collectors.toList());
        newJanDtoList = basicPatternMstService.updateJanSize(newJanDtoList);
        janResult = basicPatternMstService.updateJanSize(janResult);

        Map<Long, List<PriorityOrderResultDataDto>> janResultByRestrictCd = janResult.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd, LinkedHashMap::new, Collectors.toList()));
        List<PriorityOrderResultDataDto> backupJans = this.getBackupJans(janResultByRestrictCd, newJanDtoList);
        Map<Long, List<Map<String, Object>>> relationGroupRestrictCd = relationMap.stream()
                .collect(Collectors.groupingBy(map -> MapUtils.getLong(map, MagicString.RESTRICT_CD),
                        LinkedHashMap::new, Collectors.toList()));

        List<PriorityOrderResultDataDto> adoptJan = new ArrayList<>();
        List<PriorityOrderResultDataDto> finalAdoptJan = new ArrayList<>();
        List<Map<String, Object>> outOfHeight = new ArrayList<>();

        for (Map.Entry<Long, List<Map<String, Object>>> entry : relationGroupRestrictCd.entrySet()) {
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
                if(jans!=null){
                    List<Map<String, Object>> resultMap = this.doSetJan(partitionVal, topPartitionVal, tanaWidthCheck, adoptJan,
                            jans, relation, tanaWidth, tanaHeight, taiCd, tanaCd, backupJans);
                    outOfHeight.addAll(resultMap);
                }
            }

            //----------
            adoptJan = adoptJan.stream().filter(dto -> !Objects.equals(dto.getCutFlag(), 1)).collect(Collectors.toList());
            Map<String, Integer> relationSumJanCount = relationList.stream().collect(Collectors.groupingBy(map -> MapUtils.getString(map, MagicString.TAI_CD) + "_" +
                            MapUtils.getString(map, MagicString.TANA_CD) + "_" + MapUtils.getString(map, MagicString.RESTRICT_CD),
                    Collectors.summingInt(map -> MapUtils.getInteger(map, "janCount", 0))));
            relationList = relationList.stream().sorted(Comparator.comparing(map -> MapUtils.getInteger(map, "areaFlag", 0))).collect(Collectors.toList());
            for (Map<String, Object> relation : relationList) {
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
                Integer areaFlag = MapUtils.getInteger(relation, "areaFlag", 0);
                Map<Long, List<PriorityOrderResultDataDto>> backupJansByRestrictCd = backupJans.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd));
                List<PriorityOrderResultDataDto> backupJansList = new ArrayList<>();
                if(backupJansByRestrictCd.containsKey(restrictCd) ){
                    backupJansList = backupJansByRestrictCd.get(restrictCd).stream()
                                    .sorted(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank)).collect(Collectors.toList());
                }
                List<PriorityOrderResultDataDto> notAdoptBackupJansList = backupJansList;
                if(areaFlag==0){
                    //jancount
                    long usedCount = adoptJan.stream().filter(map -> taiCd.equals(map.getTaiCd()+"") &&
                            tanaCd.equals(map.getTanaCd()+"") && restrictCd.equals(map.getRestrictCd())).count();
                    if(usedCount<janCount){
                        long sum = janCount - usedCount;
                        int i = 0;
                        notAdoptBackupJansList = backupJansList.stream().filter(dto -> !Objects.equals(dto.getAdoptFlag(), 1)).collect(Collectors.toList());
                        for (int j = 0; j < notAdoptBackupJansList.size(); j++) {
                            PriorityOrderResultDataDto currentJan = notAdoptBackupJansList.get(j);
                            if(i < sum){
                                currentJan.setOldTaiCd(currentJan.getTaiCd());
                                currentJan.setOldTanaCd(currentJan.getTaiCd());
                                currentJan.setOldTanapositionCd(currentJan.getOldTanapositionCd());

                                currentJan.setTaiCd(Integer.valueOf(taiCd));
                                currentJan.setTanaCd(Integer.valueOf(tanaCd));

                                PriorityOrderResultDataDto copyCurrentJan = new PriorityOrderResultDataDto();
                                BeanUtils.copyProperties(currentJan, copyCurrentJan);
                                copyCurrentJan.setCutFlag(0);
                                adoptJan.add(copyCurrentJan);
                                currentJan.setAdoptFlag(1);
                                notAdoptBackupJansList.set(i, currentJan);
                            }
                            i++;
                        }
                    }
                }

                if(areaFlag==1){
                    //area
                    double groupArea = BigDecimal.valueOf(tanaWidth * area / 100.0).setScale(3, RoundingMode.CEILING).doubleValue();
                    double usedArea = adoptJan.stream().filter(dto -> restrictCd.equals(dto.getRestrictCd()) &&
                                    taiCd.equals(dto.getTaiCd() + "") && tanaCd.equals(dto.getTanaCd() + ""))
                            .collect(Collectors.summarizingDouble(dto -> dto.getPlanoWidth() * dto.getFace())).getSum();

                    notAdoptBackupJansList = notAdoptBackupJansList.stream().filter(dto -> !Objects.equals(dto.getAdoptFlag(), 1)).collect(Collectors.toList());
                    for (int i = 0; i < notAdoptBackupJansList.size(); i++) {
                        PriorityOrderResultDataDto currentJan = notAdoptBackupJansList.get(i);
                        long janWidth = currentJan.getPlanoWidth();
                        long face = currentJan.getFace();

                        if(janWidth * face + partitionVal + usedArea <= groupArea){
                            currentJan.setOldTaiCd(currentJan.getTaiCd());
                            currentJan.setOldTanaCd(currentJan.getTaiCd());
                            currentJan.setOldTanapositionCd(currentJan.getOldTanapositionCd());

                            currentJan.setTaiCd(Integer.valueOf(taiCd));
                            currentJan.setTanaCd(Integer.valueOf(tanaCd));
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

        Map<String, List<PriorityOrderResultDataDto>> adoptJanByTaiTana = adoptJan.stream().collect(Collectors.groupingBy(dto -> dto.getTaiCd() + "_" + dto.getTanaCd()));
        for (Map.Entry<String, List<PriorityOrderResultDataDto>> entry : adoptJanByTaiTana.entrySet()) {
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

        for (int i = 0; i < finalAdoptJan.size(); i++) {
            PriorityOrderResultDataDto dataDto = finalAdoptJan.get(i);
            Integer oldTaiCd = dataDto.getOldTaiCd();
            Integer oldTanaCd = dataDto.getOldTanaCd();
            Integer oldTanaPositionCd = dataDto.getOldTanapositionCd();

            if(dataDto.getOldTaiCd()!=null && (!Objects.equals(oldTaiCd, dataDto.getTaiCd())
                    && !Objects.equals(oldTanaCd, dataDto.getTanaCd()))){
                int oldIndex = -1;
                for (int j = 0; j < finalAdoptJan.size(); j++) {
                    PriorityOrderResultDataDto oldJan = finalAdoptJan.get(j);
                    if (oldJan.getTaiCd().equals(oldTaiCd) && oldJan.getTanaCd().equals(oldTanaCd) &&
                            oldJan.getOldTanapositionCd().equals(oldJan.getTanapositionCd())) {
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
                    dataDto.setTanapositionCd(tmp.getTanapositionCd());

                    BeanUtils.copyProperties(tmp, oldPositionJan);
                    oldPositionJan.setTaiCd(oldTaiCd);
                    oldPositionJan.setTanaCd(oldTanaCd);
                    oldPositionJan.setTanapositionCd(oldTanaPositionCd);

                    finalAdoptJan.set(oldIndex, oldPositionJan);
                    finalAdoptJan.set(i, dataDto);
                }else{
                    dataDto.setTaiCd(oldTaiCd);
                    dataDto.setTanaCd(oldTanaCd);
                    dataDto.setTanapositionCd(oldTanaPositionCd);
                    finalAdoptJan.add(i, dataDto);
                }
            }
        }

        return ResultMaps.result(ResultEnum.SUCCESS, finalAdoptJan);
    }

    /**
     * return the map of the eliminated Jan and the new Jan
     * @param janResultByRestrictCd
     * @param newJanDtoList
     * @return key:cut jan code,value: new jan code
     */
    private List<PriorityOrderResultDataDto> getBackupJans(Map<Long, List<PriorityOrderResultDataDto>> janResultByRestrictCd, List<PriorityOrderResultDataDto> newJanDtoList){
        if(newJanDtoList.isEmpty()){
            return new ArrayList<>();
        }

        List<PriorityOrderResultDataDto> backupJan = new ArrayList<>();
        Map<Long, List<PriorityOrderResultDataDto>> newJanByRestrictCd = newJanDtoList.stream().collect(Collectors.groupingBy(PriorityOrderResultDataDto::getRestrictCd));
        for (Map.Entry<Long, List<PriorityOrderResultDataDto>> entry : janResultByRestrictCd.entrySet()) {
            Long restrictCd = entry.getKey();
            List<PriorityOrderResultDataDto> value = entry.getValue();
            int cutCount = 0;

            if (newJanByRestrictCd.containsKey(restrictCd)) {
                List<PriorityOrderResultDataDto> janNewList = newJanByRestrictCd.get(restrictCd);
                for (int i = 0; i < janNewList.size(); i++) {
                    int skuRank = janNewList.get(i).getSkuRank().intValue();
                    PriorityOrderResultDataDto newJanDto = new PriorityOrderResultDataDto();
                    BeanUtils.copyProperties(janNewList.get(i), newJanDto);

                    Optional<PriorityOrderResultDataDto> max = value.stream().filter(dto -> dto.getSkuRank() < skuRank).max(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank));
                    if (max.isPresent()) {
                        newJanDto.setFace(max.get().getFace());
                        newJanDto.setFaceFact(max.get().getFace());
                    }else{
                        Optional<PriorityOrderResultDataDto> min = value.stream().filter(dto -> dto.getSkuRank() >= skuRank).min(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank));
                        if(min.isPresent()){
                            newJanDto.setFace(min.get().getFace());
                            newJanDto.setFaceFact(min.get().getFace());
                        }
                    }

                    newJanDto.setNewFlag(1);
                    backupJan.add(newJanDto);
                    if(skuRank<=value.size()){
                        cutCount++;
                    }
                }

                for (int i = 0; i < cutCount; i++) {
                    PriorityOrderResultDataDto dataDto = value.get(value.size() - i - 1);
                    dataDto.setOldTaiCd(dataDto.getTaiCd());
                    dataDto.setOldTanaCd(dataDto.getTanaCd());
                    dataDto.setOldTanapositionCd(dataDto.getTanapositionCd());
                    dataDto.setFaceFact(dataDto.getFace());
                    backupJan.add(dataDto);
                }
            }
        }

        return backupJan;
    }

    private List<Map<String, Object>> doSetJan(Short partitionVal,Short topPartitionVal, Integer tanaWidthCheck,
                                         List<PriorityOrderResultDataDto> adoptJan, List<PriorityOrderResultDataDto> jans,
                                         Map<String, Object> relation, Integer tanaWidth, Integer tanaHeight, String taiCd, String tanaCd,
                                         List<PriorityOrderResultDataDto> backupJans){
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
        for (PriorityOrderResultDataDto jan : jans) {
            if(Objects.equals(jan.getAdoptFlag(), 1) || Objects.equals(jan.getCutFlag(), 1)){
                continue;
            }
            PriorityOrderResultDataDto newJanDto = new PriorityOrderResultDataDto();

            Long width = Optional.ofNullable(jan.getPlanoWidth()).orElse(MagicString.DEFAULT_WIDTH);
            Long face = jan.getFace();
            Long janWidth = width + partitionValue;
            String janOld = jan.getJanCd();

            relation.put("areaFlag", 0);
            if (backupJans.stream().anyMatch(dto->janOld.equals(dto.getJanCd()))) {
                BeanUtils.copyProperties(jan, newJanDto);
                newJanDto.setFaceFact(jan.getFace());
                newJanDto.setCutFlag(1);
                jan.setCutFlag(1);
                if(janCount==null || janCount==0){
                    //1:area change or restrictCd change --> width judge
                    relation.put("areaFlag", 1);
                }else{
                    //jan_count judge
                    relation.put("areaFlag", 0);
                }
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
                newJanDto.setTaiCd(Integer.parseInt(taiCd));
                newJanDto.setTanaCd(Integer.parseInt(tanaCd));
                newJanDto.setFaceFact(face);
                newJanDto.setAdoptFlag(1);
                adoptJanByTaiTana.add(newJanDto);
                jan.setAdoptFlag(1);
                usedJanCount++;
            }
        }

        adoptJan.addAll(adoptJanByTaiTana);

        return resultHeightMap;
    }

    /**
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
}
