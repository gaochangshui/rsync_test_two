package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.ProductPowerNumGenerator;
import com.trechina.planocycle.entity.vo.PriorityAllPatternListVO;
import com.trechina.planocycle.entity.vo.WorkPriorityOrderMstEditVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.exception.BusinessException;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriorityAllMstServiceImpl  implements PriorityAllMstService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    HttpSession session;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private PriorityAllMstMapper priorityAllMstMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private PriorityOrderRestrictResultMapper priorityOrderRestrictResultMapper;
    @Autowired
    private WorkPriorityAllRestrictRelationMapper workPriorityAllRestrictRelationMapper;
    @Autowired
    private WorkPriorityAllRestrictMapper workPriorityAllRestrictMapper;
    @Autowired
    private WorkPriorityAllResultDataMapper workPriorityAllResultDataMapper;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private PriorityAllPtsService priorityAllPtsService;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private PriorityAllNumGeneratorMapper priorityAllNumGeneratorMapper;
    @Autowired
    private BasicAllPatternMstServiceImpl basicAllPatternMstService;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private  VehicleNumCache vehicleNumCache;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private BasicPatternRestrictResultMapper basicPatternRestrictResultMapper;
    @Autowired
    private BasicPatternRestrictResultMapper restrictResultMapper;
    @Autowired
    private IDGeneratorService idGeneratorService;
    @Autowired
    private PriorityAllPtsMapper priorityAllPtsMapper;
    @Autowired
    private LogAspect logAspect;
    @Value("${skuPerPattan}")
    private Long skuCountPerPattan;

    /**
     * ??????????????????????????????
     *
     * @param jsonObject@return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> addPriorityAllData(JSONObject jsonObject) {

            String authorCd = session.getAttribute("aud").toString();
            String companyCd = jsonObject.get("companyCd").toString();
            Integer priorityAllCd = (Integer) jsonObject.get("priorityAllCd");
            Integer isCover = (Integer) jsonObject.get("isCover");
            if (priorityAllCd == 0){
                priorityAllCd = (Integer) idGeneratorService.priorityAllID().get("data");
                Map <String ,Object> map = new HashMap<>();
                map.put("priorityAllCd",priorityAllCd);
                map.put("priorityOrderCd",null);
                map.put("allPatternData",null);
                return ResultMaps.result(ResultEnum.SUCCESS,map);
            }
            if (isCover == null){
                isCover = 0;
            }
            Integer newPriorityAllCd = priorityAllCd;
            if (isCover == 1){
                ProductPowerNumGenerator p = new ProductPowerNumGenerator();
                p.setUsercd(session.getAttribute("aud").toString());
                priorityAllNumGeneratorMapper.insert(p);
                logger.info("???pattern???????????????:{}",p.getId());
                newPriorityAllCd = p.getId();

            }
            //companyCd???priorityAllCd???Author_cd?????????WK????????????????????????
            priorityAllMstMapper.deleteWKTableMst(companyCd, newPriorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableShelfs(companyCd, newPriorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableRestrict(companyCd, newPriorityAllCd, authorCd);
            workPriorityAllRestrictRelationMapper.deleteWKTableRelation(companyCd, newPriorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableResult(companyCd, newPriorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsRelation(companyCd, newPriorityAllCd, authorCd);


               priorityAllMstMapper.delWKTablePtsTai(companyCd, newPriorityAllCd, authorCd);
               priorityAllMstMapper.delWKTablePtsTana(companyCd, newPriorityAllCd, authorCd);
               priorityAllMstMapper.delWKTablePtsJans(companyCd, newPriorityAllCd, authorCd);
               priorityAllMstMapper.delWKTablePtsData(companyCd, newPriorityAllCd, authorCd);
               priorityAllMstMapper.delWKTablePtsVersion(companyCd, newPriorityAllCd, authorCd);



                // ??????????????????
                priorityAllMstMapper.copyWKTableMst(companyCd, priorityAllCd, newPriorityAllCd);
                priorityAllMstMapper.copyWKTableShelfs(companyCd, priorityAllCd, newPriorityAllCd);

                String ptsCd = priorityAllMstMapper.getPtsCd(companyCd, priorityAllCd);
                List<String> list = Arrays.asList((ptsCd.split(",")));
                for (String id : list) {
                    Integer newId = Integer.parseInt(id);
                    if (isCover == 1){
                        newId = priorityAllPtsMapper.getPtsCd();
                    }
                    priorityAllMstMapper.copyWKTablePtsTai(companyCd, priorityAllCd, newPriorityAllCd,Integer.parseInt(id),newId);
                    priorityAllMstMapper.copyWKTablePtsTana(companyCd, priorityAllCd, newPriorityAllCd,Integer.parseInt(id),newId);
                    priorityAllMstMapper.copyWKTablePtsJans(companyCd, priorityAllCd, newPriorityAllCd,Integer.parseInt(id),newId);
                    priorityAllMstMapper.copyWKTablePtsData(companyCd, priorityAllCd, newPriorityAllCd,Integer.parseInt(id),newId);
                    priorityAllMstMapper.copyWKTablePtsVersion(companyCd, priorityAllCd, newPriorityAllCd,Integer.parseInt(id),newId);
                }


                Integer priorityOrderCd = priorityAllMstMapper.getPriorityOrderCd(priorityAllCd, companyCd);
                Map<String, Object> allPatternData = getAllPatternData(companyCd, newPriorityAllCd, priorityOrderCd);
                Map <String ,Object> map = new HashMap<>();
                map.put("priorityAllCd",newPriorityAllCd);
                map.put("priorityOrderCd",priorityOrderCd);
                map.put("allPatternData",allPatternData.get("data"));
                return ResultMaps.result(ResultEnum.SUCCESS,map);

    }

    /**
     * ???????????????????????????List api???
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        String aud = session.getAttribute("aud").toString();
        List<TableNameDto> resultInfo = priorityOrderMstMapper.getTableNameByCompanyCd(companyCd, aud);
        logger.info("??????????????????List???{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????List???????????????api???
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getAllPatternData(String companyCd, Integer priorityAllCd, Integer priorityOrderCd) {
        // ?????????????????????????????????????????????CD????????????
        Integer patternCd = priorityAllMstMapper.getPatternCdBYPriorityCd(companyCd, priorityOrderCd);
        // ??????????????????PTS????????????????????????
        Map<String, Object> ptsInfoTemp = new HashMap<>();
        Integer newTaiNum = shelfPtsDataMapper.getNewTaiNumFinal(priorityOrderCd);
        Integer newFaceNum = shelfPtsDataMapper.getNewFaceNumFinal(priorityOrderCd);
        Integer newTanaNum = shelfPtsDataMapper.getNewTanaNumFinal(priorityOrderCd);
        Integer newSkuNum = shelfPtsDataMapper.getNewSkuNumFinal(priorityOrderCd);
        PriorityOrderAttrDto priorityOrderAttrDto = priorityOrderMstMapper.getCommonPartsData(companyCd, priorityOrderCd);
        WorkPriorityOrderMstEditVo workPriorityOrderMst = workPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        String commonPartsData = priorityOrderAttrDto.getCommonPartsData();
        ptsInfoTemp.put("taiNum",newTaiNum);
        ptsInfoTemp.put("tanaNum",newTanaNum);
        ptsInfoTemp.put("faceNum",newFaceNum);
        ptsInfoTemp.put("skuNum",newSkuNum);
        ptsInfoTemp.put("commonPartsData",commonPartsData);
        Map<String,Object> optionSet = new HashMap<>();
        Boolean thickneCheck = workPriorityOrderMst.getPartitionFlag() != 0;
        Short thickneValue = workPriorityOrderMst.getPartitionVal()!=null?workPriorityOrderMst.getPartitionVal():0;
        optionSet.put("thickne", ImmutableMap.of("check",thickneCheck, "value",thickneValue));
        Boolean hSpaceCheck = workPriorityOrderMst.getTopPartitionFlag() != 0;
        Short hSpaceValue = workPriorityOrderMst.getTopPartitionVal()!=null?workPriorityOrderMst.getTopPartitionVal().shortValue():0;
        optionSet.put("hSpace",ImmutableMap.of("check",hSpaceCheck,"value",hSpaceValue));
        optionSet.put("backOpacity",0.25);
        ptsInfoTemp.put("optionSet",optionSet);
        Map<String, Object> result = new HashMap<>();
        result.put("tanaInfo", ptsInfoTemp);

        // ?????????????????????????????????List?????????

        List<PriorityAllPatternListVO> info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, patternCd);
        result.put("ptsInfo", info);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }



    @Override
    public Map<String, Object> returnAutoCalculationState(String taskId) {
        LocalDateTime now = LocalDateTime.now();
        while (true) {

            if (vehicleNumCache.get("janIsNull" + taskId) != null) {
                vehicleNumCache.remove("janIsNull" + taskId);
                return ResultMaps.result(ResultEnum.JANNOTESISTS);
            }
            if (vehicleNumCache.get("IO" + taskId) != null) {
                vehicleNumCache.remove("IO" + taskId);
                String error = vehicleNumCache.get("IOError" + taskId).toString();
                vehicleNumCache.remove("IOError" + taskId);
                return ResultMaps.error(ResultEnum.FAILURE, error);
            }
            if (vehicleNumCache.get(taskId) != null) {
                vehicleNumCache.remove(taskId);
                return ResultMaps.result(ResultEnum.SUCCESS, "success");
            }
            if ("1".equals(vehicleNumCache.get(MessageFormat.format(MagicString.TASK_KEY_CANCEL, taskId)))) {
                return ResultMaps.result(ResultEnum.SUCCESS,"success");
            }
            if (Duration.between(now, LocalDateTime.now()).getSeconds() > MagicString.TASK_TIME_OUT_LONG) {
                return ResultMaps.result(ResultEnum.SUCCESS, taskId);
            }
        }
    }

    /**
     * ??????
     * @param
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> savePriorityAll(PriorityAllSaveDto priorityAllSaveDto) {
        String aud = session.getAttribute("aud").toString();
        String priorityAllName = priorityAllSaveDto.getPriorityAllName();
        String companyCd = priorityAllSaveDto.getCompanyCd();
        Integer priorityAllCd = priorityAllSaveDto.getPriorityAllCd();
        Integer cd = priorityAllMstMapper.selectPriorityAllName(priorityAllName, companyCd);
        if (cd != null && !cd.equals(priorityAllCd) ){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        try{
                priorityAllMstMapper.deleteFinalTableMst(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTableShelfs(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTablePtsTai(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTablePtsTana(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTablePtsJans(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTablePtsData(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTablePtsVersion(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTableRestrictResult(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTableRestrictRelation(companyCd,priorityAllCd,aud);
                priorityAllMstMapper.deleteFinalTableRestrictResultData(companyCd,priorityAllCd,aud);

                priorityAllMstMapper.setFinalTableMst(companyCd,priorityAllCd,priorityAllName);
                priorityAllMstMapper.setFinalTableShelfs(companyCd,priorityAllCd);
                priorityAllMstMapper.setFinalTablePtsTai(companyCd,priorityAllCd);
                priorityAllMstMapper.setFinalTablePtsTana(companyCd,priorityAllCd);
                priorityAllMstMapper.setFinalTablePtsJans(companyCd,priorityAllCd);
                priorityAllMstMapper.setFinalTablePtsData(companyCd,priorityAllCd);
                priorityAllMstMapper.setFinalTablePtsVersion(companyCd,priorityAllCd);
                priorityAllMstMapper.setFinalTableRestrictResult(companyCd,priorityAllCd);
                priorityAllMstMapper.setFinalTableRestrictRelation(companyCd,priorityAllCd);
                priorityAllMstMapper.setFinalTableRestrictResultData(companyCd,priorityAllCd);
                return ResultMaps.result(ResultEnum.SUCCESS,priorityAllCd);

        }catch (Exception e){
            logger.error("???pattern??????????????????????????????", e);
            throw new BusinessException(e.getMessage());
        }


    }

    /**
     * pts??????
     * @param companyCd
     * @param priorityAllCd
     * @param patternCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityPtsInfo(String companyCd, Integer priorityAllCd, Integer patternCd) {
        return priorityAllPtsService.getPtsDetailData(patternCd,companyCd,priorityAllCd);
    }

    /**
     * ?????? api???
     * @param priorityAllSaveDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> deletePriorityAll( PriorityAllSaveDto priorityAllSaveDto) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = priorityAllSaveDto.getCompanyCd();
        Integer priorityAllCd = priorityAllSaveDto.getPriorityAllCd();
        try {
            priorityAllMstMapper.deleteMst(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteShelfs(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteRestrict(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deleteResult(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deletePtsTai(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deletePtsTana(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deletePtsJans(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deletePtsData(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deletePtsRelation(companyCd,priorityAllCd,aud);
            priorityAllMstMapper.deletePtsVersion(companyCd,priorityAllCd,aud);
        } catch (Exception e) {
            logger.error("???pattern??????????????????????????????:{}",e.getMessage());
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


}
