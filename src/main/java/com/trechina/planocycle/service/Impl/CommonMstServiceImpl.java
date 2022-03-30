package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.PriorityOrderResultDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.Areas;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictRelation;
import com.trechina.planocycle.entity.vo.PtsTaiVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.AreasMapper;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommonMstServiceImpl implements CommonMstService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AreasMapper areasMapper;
    @Override
    public Map<String, Object> getAreaInfo(String companyCd) {
        List<Areas> areasList = areasMapper.select(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    }

    @Override
    public Map<String, Object> getAreaForShelfName(Integer shelfNameCd) {
        List<Areas> areasList = areasMapper.selectForShelfName(shelfNameCd);
        return ResultMaps.result(ResultEnum.SUCCESS,areasList);
    }

    @Override
    public Map<String, List<PriorityOrderResultDataDto>> commSetJan(Short partitionFlag, Short partitionVal,
        List<PtsTaiVo> taiData, List<PriorityOrderResultDataDto> workPriorityOrderResultData,
        List<WorkPriorityOrderRestrictRelation> workPriorityOrderRestrictRelations, Integer minFace) {
        /**
         * 根据tai_tana分类存放已经分配好的商品list
         */
        Map<String, List<PriorityOrderResultDataDto>> finalSetJanResultData = new HashMap<>();

        if (partitionFlag == 0) {
            //没隔板的情况
            partitionVal = 0;
        }

        /**
         * 根据制约条件进行分组进行摆放
         */
        Map<Long, List<WorkPriorityOrderRestrictRelation>> relationByGroup = workPriorityOrderRestrictRelations
                .stream().collect(Collectors.groupingBy(WorkPriorityOrderRestrictRelation::getRestrictCd, LinkedHashMap::new, Collectors.toList()));

        List<WorkPriorityOrderRestrictRelation> relationValue = null;
        List<PriorityOrderResultDataDto> relationSorted = null;
        PriorityOrderResultDataDto priorityOrderResultData = null;
        List<PriorityOrderResultDataDto> resultData = null;

        for (Map.Entry<Long, List<WorkPriorityOrderRestrictRelation>> relationEntry : relationByGroup.entrySet()) {
            Long relationCd = relationEntry.getKey();
            //记录商品放到哪里了-同一个制约的商品可能不同的台、段
            int setResultDataIndex = 0;

            //符合当前制约条件商品按rank排序
            //如果sortrank为null就只按skurank排序
            relationSorted = workPriorityOrderResultData
                    .stream().filter(data -> relationCd.equals(data.getRestrictCd()))
                    .sorted(Comparator.comparing(PriorityOrderResultDataDto::getSkuRank, Comparator.nullsFirst(Long::compareTo))
                            .thenComparingLong(PriorityOrderResultDataDto::getSortRank)).collect(Collectors.toList());

            relationValue = relationEntry.getValue();
            for (WorkPriorityOrderRestrictRelation workPriorityOrderRestrictRelation : relationValue) {
                Integer taiCd = workPriorityOrderRestrictRelation.getTaiCd();
                Integer tanaCd = workPriorityOrderRestrictRelation.getTanaCd();
                short tanaType = workPriorityOrderRestrictRelation.getTanaType();

                //分类key，同一类的商品进行减face数处理
                String taiTanaKey = taiCd + "_" + tanaCd + "_" + tanaType;

                Optional<PtsTaiVo> taiInfo = taiData.stream().filter(ptsTaiVo -> taiCd.equals(ptsTaiVo.getTaiCd())).findFirst();

                if (!taiInfo.isPresent()) {
                    logger.info("{}台信息不存在", taiCd);
                    continue;
                }

                Integer taiWidth = taiInfo.get().getTaiWidth();
                //台或半段的宽度, 已使用的宽度
                double width = taiWidth;
                double usedWidth = 0;

                if (tanaType != 0) {
                    //半段的根据具体位置段的宽度放置
                    width = taiWidth / 2.0;
                }

                //放置商品
                for (int i = setResultDataIndex; i < relationSorted.size(); i++) {
                    priorityOrderResultData = relationSorted.get(i);

                    Long faceSku = Optional.ofNullable(priorityOrderResultData.getFaceSku()).orElse(1L);
                    Long janWidth = Optional.ofNullable(priorityOrderResultData.getJanWidth()).orElse(0L);
                    Long face = priorityOrderResultData.getFace();

                    //商品宽度null或者0时使用默认宽度67mm，faceSku>1的需要乘以faceSku
                    if (janWidth == 0) {
                        janWidth = 67 * faceSku;
                    }
                    priorityOrderResultData.setJanWidth(janWidth);

                    long janTotalWidth = janWidth * face + partitionVal;
                    if (janTotalWidth + usedWidth <= width) {
                        //根据face数进行摆放可以放开
                        setResultDataIndex = i + 1;

                        priorityOrderResultData.setFaceFact(face);
                        priorityOrderResultData.setTaiCd(taiCd);
                        priorityOrderResultData.setTanaCd(tanaCd);
                        priorityOrderResultData.setAdoptFlag(1);
                        priorityOrderResultData.setTanaType((int) tanaType);

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

                            setResultDataIndex = i + 1;
                            //更新已用宽度
                            resultData.add(priorityOrderResultData);
                            finalSetJanResultData.put(taiTanaKey, resultData);
                        }

                        //根据face数进行摆放放不开，直接不放，结束该位置的摆放
                        break;
                    }
                }
            }
        }

        return finalSetJanResultData;
    }


    /**
     * 通过cut face数是否能放下
     * 逻辑：第一步：先对要放置的商品进行face数-1
     *          1.如果face数*商品宽度能放下就放，记下实际存放的face数
     *          2.放不下，走第二步
     *      第二步：对同一台棚区分(taiCd_tanaCd_tanaType)中的商品list倒序进行cut face判断
     *          1.如果没有入数irisu=1的商品则不进行cut，直接结束
     *          2.如果有入数irisu=1，倒序遍历商品list，进行face数-1
     *              3.如果face数*宽度能放下就放，记下实际存放的face数
     *              4.无法放下，结束，该位置不放了
     * @param resultDataDtoList 同台棚区分的商品list
     * @param targetResultData 要放置的商品
     * @param width 当前台棚区分的宽度
     * @param usedWidth 当前台棚区分的已用宽度
     * @param minFace 最小face数
     * @param partitionVal 隔板宽度
     * @return true放，false不放
     */
    private boolean isSetJanByCutFace(List<PriorityOrderResultDataDto> resultDataDtoList, double width, double usedWidth, long partitionVal,
                                      long minFace, PriorityOrderResultDataDto targetResultData){
        Long face = targetResultData.getFace();
        Long janWidth = targetResultData.getJanWidth();

        //剩下可用的宽度（需要考虑有隔板的情况）
        double remainderWidth = width - usedWidth;
        //通过cut目标商品的face数是否能放下
        boolean isSetByCut = false;

        //先看要放的商品一个一个的减能不能放下
        for (Long i = face - 1; i >= minFace; i--) {
            if((janWidth * i + partitionVal) <= remainderWidth){
                //能放下
                isSetByCut = true;
                //保存实际的face数
                targetResultData.setFaceFact(i);
                break;
            }
        }

        if(!isSetByCut){
            //通过cut目标商品的face数不能放下
            //只处理入数=1的，不等于1的不进行cut
            List<PriorityOrderResultDataDto> resultDataDtoByIrisu = resultDataDtoList.stream()
                    .filter(data-> 1 == data.getIrisu()).collect(Collectors.toList());

            if(resultDataDtoByIrisu.isEmpty()){
                //没有符合条件的无法cut
                return false;
            }

            //按照最小face进行放置，需要多少宽度（需要考虑有隔板的情况）+剩下没用的宽度
            long needWidth = targetResultData.getJanWidth() * minFace + partitionVal;
            PriorityOrderResultDataDto currentResultData = null;
            for (int i = resultDataDtoByIrisu.size()-1; i >= 0 ; i--) {
                currentResultData = resultDataDtoByIrisu.get(i);
                Long faceFact = currentResultData.getFaceFact();

                //小于等于最小face不能再cut了
                //当前商品减一个face数，能不能放下目标商品
                //cut 1 个face的宽度再加剩下的宽度是否能满足目标上面放置需要的宽度
                if(faceFact > minFace && currentResultData.getJanWidth() + remainderWidth >= needWidth){
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
     * 先按照台遍历再遍历棚，同一个棚的商品从左到右的顺序从1开始进行标号，棚变了，重新标号
     *
     * @param workPriorityOrderResultData
     * @return
     */
    @Override
    public List<WorkPriorityOrderResultDataDto> calculateTanaPosition(List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData) {
        //有棚位置的resultdata数据
        List<WorkPriorityOrderResultDataDto> positionResultData = new ArrayList<>(workPriorityOrderResultData.size());
        //根据台进行分组遍历
        Map<Integer, List<WorkPriorityOrderResultDataDto>> workPriorityOrderResultDataByTai = workPriorityOrderResultData.stream()
                .collect(Collectors.groupingBy(WorkPriorityOrderResultDataDto::getTaiCd, LinkedHashMap::new, Collectors.toList()));
        Map<Integer, List<WorkPriorityOrderResultDataDto>> workPriorityOrderResultDataByTana = null;
        List<WorkPriorityOrderResultDataDto> workPriorityOrderResultDataDtos = null;

        for (Map.Entry<Integer, List<WorkPriorityOrderResultDataDto>> entrySet : workPriorityOrderResultDataByTai.entrySet()) {
            Integer taiCd = entrySet.getKey();

            workPriorityOrderResultDataDtos = workPriorityOrderResultDataByTai.get(taiCd);
            //根据棚进行分组遍历
            workPriorityOrderResultDataByTana = workPriorityOrderResultDataDtos.stream()
                    .collect(Collectors.groupingBy(WorkPriorityOrderResultDataDto::getTanaCd, LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<Integer, List<WorkPriorityOrderResultDataDto>> entry : workPriorityOrderResultDataByTana.entrySet()) {
                //同一个棚，序号从1开始累加，下一个棚重新从1开始加
                Integer tantaPositionCd=0;
                Integer tanaCd = entry.getKey();

                for (WorkPriorityOrderResultDataDto currentDataDto : workPriorityOrderResultDataByTana.get(tanaCd)) {
                    currentDataDto.setTanaPositionCd(++tantaPositionCd);
                    currentDataDto.setFaceMen(1);
                    currentDataDto.setFaceKaiten(0);
                    currentDataDto.setTumiagesu(1);
                    currentDataDto.setFaceDisplayflg(0);
                    currentDataDto.setFacePosition(1);
                    currentDataDto.setDepthDisplayNum(1);
                    positionResultData.add(currentDataDto);
                }
            }
        }
        return positionResultData;
    }
}
