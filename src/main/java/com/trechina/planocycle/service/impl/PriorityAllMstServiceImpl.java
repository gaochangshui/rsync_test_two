package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.dto.TableNameDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.ProductPowerNumGenerator;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.PriorityAllPatternListVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.exception.BusinessException;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private LogAspect logAspect;
    @Value("${skuPerPattan}")
    private Long skuCountPerPattan;

    /**
     * 新規作成＆編集の処理
     *
     * @param jsonObject@return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> addPriorityAllData(JSONObject jsonObject) {
        //try{

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
                logger.info("全pattern表自動取号:{}",p.getId());
                newPriorityAllCd = p.getId();
            }
            //「companyCd、priorityAllCd、Author_cd」によりWKテーブルをクリア
            priorityAllMstMapper.deleteWKTableMst(companyCd, newPriorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableShelfs(companyCd, newPriorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableRestrict(companyCd, newPriorityAllCd, authorCd);
            workPriorityAllRestrictRelationMapper.deleteWKTableRelation(companyCd, newPriorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTableResult(companyCd, newPriorityAllCd, authorCd);
            priorityAllMstMapper.deleteWKTablePtsRelation(companyCd, newPriorityAllCd, authorCd);

               //String ptsCd = priorityAllMstMapper.getPtsCd(companyCd, newPriorityAllCd, authorCd);
                //if (ptsCd != null) {
                //    String[] split = ptsCd.split(",");
                //    int[] array = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
                //    priorityAllMstMapper.deleteWKTablePtsTai(companyCd, array, authorCd);
                //    priorityAllMstMapper.deleteWKTablePtsTana(companyCd, array, authorCd);
                //    priorityAllMstMapper.deleteWKTablePtsJans(companyCd, array, authorCd);
                //    priorityAllMstMapper.deleteWKTablePtsData(companyCd, array, authorCd);
                //    priorityAllMstMapper.deleteWKTablePtsVersion(companyCd, array, authorCd);
                //}

               priorityAllMstMapper.delWKTablePtsTai(companyCd, newPriorityAllCd, authorCd);
               priorityAllMstMapper.delWKTablePtsTana(companyCd, newPriorityAllCd, authorCd);
               priorityAllMstMapper.delWKTablePtsJans(companyCd, newPriorityAllCd, authorCd);
               priorityAllMstMapper.delWKTablePtsData(companyCd, newPriorityAllCd, authorCd);
               priorityAllMstMapper.delWKTablePtsVersion(companyCd, newPriorityAllCd, authorCd);



                // データコピー
                priorityAllMstMapper.copyWKTableMst(companyCd, priorityAllCd, newPriorityAllCd);
                priorityAllMstMapper.copyWKTableShelfs(companyCd, priorityAllCd, newPriorityAllCd);
                //priorityAllMstMapper.copyWKTableRestrict(companyCd, priorityAllCd, newPriorityAllCd);
                //priorityAllMstMapper.copyWKTableRelation(companyCd, priorityAllCd, newPriorityAllCd);
                //priorityAllMstMapper.copyWKTableResult(companyCd, priorityAllCd, newPriorityAllCd);
                priorityAllMstMapper.copyWKTablePtsTai(companyCd, priorityAllCd, newPriorityAllCd);
                priorityAllMstMapper.copyWKTablePtsTana(companyCd, priorityAllCd, newPriorityAllCd);
                priorityAllMstMapper.copyWKTablePtsJans(companyCd, priorityAllCd, newPriorityAllCd);
                priorityAllMstMapper.copyWKTablePtsData(companyCd, priorityAllCd, newPriorityAllCd);
                priorityAllMstMapper.copyWKTablePtsVersion(companyCd, priorityAllCd, newPriorityAllCd);

                Integer priorityOrderCd = priorityAllMstMapper.getPriorityOrderCd(priorityAllCd, companyCd);
                Map<String, Object> allPatternData = getAllPatternData(companyCd, priorityAllCd, priorityOrderCd);
                Map <String ,Object> map = new HashMap<>();
                map.put("priorityAllCd",priorityAllCd);
                map.put("priorityOrderCd",priorityOrderCd);
                map.put("allPatternData",allPatternData.get("data"));
                return ResultMaps.result(ResultEnum.SUCCESS,map);

        //} catch (Exception ex) {
        //    logAspect.setTryErrorLog(ex,new Object[]{});
        //    return ResultMaps.result(ResultEnum.FAILURE, "新規作成失敗しました。");
        //}
    }

    /**
     * 基本パターン一覧のList api①
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        String aud = session.getAttribute("aud").toString();
        List<TableNameDto> resultInfo = priorityOrderMstMapper.getTableNameByCompanyCd(companyCd, aud);
        logger.info("基本パターンList：{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * 選択された基本パターンにより基本パターンの連携棚パターン、そして同じ棚名称したの棚パターンListを取得するapi②
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getAllPatternData(String companyCd, Integer priorityAllCd, Integer priorityOrderCd) {
        // 基本パターンに紐付け棚パターンCDをもらう
        Integer patternCd = priorityAllMstMapper.getPatternCdBYPriorityCd(companyCd, priorityOrderCd);
        // 棚パターンのPTS基本情報をもらう
        Map<String, Object> ptsInfoTemp = new HashMap<>();
        Integer newTaiNum = shelfPtsDataMapper.getNewTaiNumFinal(priorityOrderCd);
        Integer newFaceNum = shelfPtsDataMapper.getNewFaceNumFinal(priorityOrderCd);
        Integer newTanaNum = shelfPtsDataMapper.getNewTanaNumFinal(priorityOrderCd);
        Integer newSkuNum = shelfPtsDataMapper.getNewSkuNumFinal(priorityOrderCd);
        PriorityOrderAttrDto priorityOrderAttrDto = priorityOrderMstMapper.getCommonPartsData(companyCd, priorityOrderCd);
        String commonPartsData = priorityOrderAttrDto.getCommonPartsData();
        ptsInfoTemp.put("taiNum",newTaiNum);
        ptsInfoTemp.put("tanaNum",newTanaNum);
        ptsInfoTemp.put("faceNum",newFaceNum);
        ptsInfoTemp.put("skuNum",newSkuNum);
        ptsInfoTemp.put("commonPartsData",commonPartsData);
        Map<String, Object> result = new HashMap<>();
        result.put("tanaInfo", ptsInfoTemp);

        // 同一棚名称の棚パータンListを取得

        List<PriorityAllPatternListVO> info = priorityAllMstMapper.getAllPatternData(companyCd, priorityAllCd, priorityOrderCd, patternCd);
        result.put("ptsInfo", info);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }

    ///**
    // * 自動計算する前にデータを一時テーブルに保存
    // *
    // * @param priorityAllSaveDto@return
    // */
    //@Transactional(rollbackFor = Exception.class)
    //@Override
    //public Integer saveWKAllPatternData(PriorityAllSaveDto priorityAllSaveDto) {
    //    String authorCd = session.getAttribute("aud").toString();
    //    // mstテーブル
    //    try {
    //        priorityAllMstMapper.deleteWKTableMst(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
    //        // shelfsテーブル
    //        priorityAllMstMapper.deleteWKTableShelfs(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
    //        workPriorityAllRestrictMapper.deleteWKTableRestrict(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd());
    //        workPriorityAllRestrictRelationMapper.deleteWKTableRelation(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
    //        priorityAllMstMapper.insertWKTableMst(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd, priorityAllSaveDto.getPriorityOrderCd());
    //        priorityAllMstMapper.insertWKTableShelfs(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd, priorityAllSaveDto.getPatterns());
    //        priorityAllMstMapper.delWKTablePtsTai(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
    //        priorityAllMstMapper.delWKTablePtsTana(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
    //        priorityAllMstMapper.delWKTablePtsJans(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
    //        priorityAllMstMapper.delWKTablePtsData(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
    //        priorityAllMstMapper.delWKTablePtsVersion(priorityAllSaveDto.getCompanyCd(), priorityAllSaveDto.getPriorityAllCd(), authorCd);
    //    } catch (Exception e) {
    //        logger.error("全patternの保存に失敗しました:{}",e.getMessage());
    //        logAspect.setTryErrorLog(e,new Object[]{priorityAllSaveDto});
    //        return 1;
    //    }
    //    return 0;
    //}



    @Override
    public Map<String, Object> returnAutoCalculationState(String taskId) {
        if (vehicleNumCache.get("janIsNull"+taskId)!=null){
            vehicleNumCache.remove("janIsNull"+taskId);
            return ResultMaps.result(ResultEnum.JANNOTESISTS);
        }
        if (vehicleNumCache.get("IO"+taskId)!=null){
            vehicleNumCache.remove("IO"+taskId);
            String error = vehicleNumCache.get("IOError" + taskId).toString();
            vehicleNumCache.remove("IOError"+taskId);
            return ResultMaps.error(ResultEnum.FAILURE, error);
        }
       if (vehicleNumCache.get(taskId) != null){
            vehicleNumCache.remove(taskId);
           return ResultMaps.result(ResultEnum.SUCCESS,"success");
       }
        return ResultMaps.result(ResultEnum.SUCCESS,"9");
    }

    /**
     * 保存
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
        Integer cd = priorityAllMstMapper.selectPriorityAllName(priorityAllName, companyCd,aud);
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

        }catch (Exception ex){
            logger.error("全patternの保存に失敗しました:", ex);
            throw new BusinessException(ex.getMessage());
        }


    }

    /**
     * pts詳細
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
     * 削除 api⑦
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
            logger.error("全patternの削除に失敗しました:{}",e.getMessage());
            return ResultMaps.result(ResultEnum.FAILURE);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 基本パターンの制約により各パターンの制約一覧を作成
     * @param pattern
     * @param priorityAllCd
     * @param companyCd
     * @param authorCd
     * @param priorityOrderCd
     * @return
     */
    private int makeWKRestrictList(PriorityAllPatternListVO pattern
            , Integer priorityAllCd
            , String companyCd, String  authorCd,Integer priorityOrderCd) {
        workPriorityAllRestrictMapper.deleteBasicPatternResult(companyCd,priorityAllCd,authorCd,pattern.getShelfPatternCd());
        // 全パターンRelationテーブル更新
        return 1;
    }




    public List<Map<String, Object>> getPtsGroup(String companyCd,Integer priorityOrderCd,Integer ptsCd,String authorCd) {

        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String, Object>> restrictResult = restrictResultMapper.selectByPrimaryKey(priorityOrderCd);
        Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<Map<String, Object>> zokuseiList = basicPatternRestrictResultMapper.selectAllPatternResultData(ptsCd, zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol);
        for (int i = 0; i < zokuseiList.size(); i++) {
            Map<String, Object> zokusei = zokuseiList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
                int equalsCount = 0;
                for (Integer integer : attrList) {
                    String restrictKey = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + integer);
                    String zokuseiKey = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + integer);

                    if(restrictKey!=null && restrictKey.equals(zokuseiKey)){
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
        return zokuseiList;
    }
}
