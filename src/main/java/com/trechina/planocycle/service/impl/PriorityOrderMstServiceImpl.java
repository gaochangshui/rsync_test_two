package com.trechina.planocycle.service.impl;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityAllSaveDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.dto.PriorityOrderJanCgiDto;
import com.trechina.planocycle.entity.po.PriorityOrderMst;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.CommonUtil;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.VehicleNumCache;
import com.trechina.planocycle.utils.cgiUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriorityOrderMstServiceImpl implements PriorityOrderMstService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${skuPerPattan}")
    Long skuCountPerPattan;
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private ShelfPatternService shelfPatternService;
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private PriorityOrderJanNewService priorityOrderJanNewService;
    @Autowired
    private PriorityOrderJanCardService priorityOrderJanCardService;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private WorkPriorityOrderRestrictSetMapper workPriorityOrderRestrictSetMapper;
    @Autowired
    private WorkPriorityOrderRestrictResultMapper workPriorityOrderRestrictResultMapper;
    @Autowired
    private WorkPriorityOrderRestrictRelationMapper workPriorityOrderRestrictRelationMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private WorkPriorityOrderSpaceMapper workPriorityOrderSpaceMapper;
    @Autowired
    private WorkPriorityOrderSortMapper workPriorityOrderSortMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private PriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private PriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    private PriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private PriorityOrderMstService priorityOrderMstService;
    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private PriorityOrderRestrictRelationMapper priorityOrderRestrictRelationMapper;
    @Autowired
    private PriorityOrderRestrictResultMapper priorityOrderRestrictResultMapper;
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private PriorityOrderResultDataMapper priorityOrderResultDataMapper;
    @Autowired
    private PriorityOrderSpaceMapper priorityOrderSpaceMapper;
    @Autowired
    private PriorityOrderSortRankMapper priorityOrderSortRankMapper;
    @Autowired
    private PriorityOrderSortMapper priorityOrderSortMapper;
    @Autowired
    private PriorityOrderShelfDataMapper priorityOrderShelfDataMapper;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private PriorityOrderShelfDataService priorityOrderShelfDataService;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;
    @Autowired
    private VehicleNumCache vehicleNumCache;
    @Autowired
    private BasicPatternRestrictResultMapper basicPatternRestrictResultMapper;
    @Autowired
    private BasicPatternRestrictRelationMapper basicPatternRestrictRelationMapper;
    @Autowired
    private BasicPatternAttrMapper basicPatternAttrMapper;
    @Autowired
    private BasicPatternRestrictResultDataMapper basicPatternRestrictResultDataMapper;
    @Autowired
    private PriorityAllMstService priorityAllMstService;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private IDGeneratorService idGeneratorService;
    @Autowired
    private ZokuseiMapper zokuseiMapper;

    /**
     * 優先順位テーブルリストの取得
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        logger.info("つかむ取優先順位表参数：{}", companyCd);
        List<PriorityOrderMst> priorityOrderMstList = priorityOrderMstMapper.selectByPrimaryKey(companyCd);
        logger.info("つかむ取優先順位表返回値：{}", priorityOrderMstList);
        return ResultMaps.result(ResultEnum.SUCCESS, priorityOrderMstList);
    }



    /**
     * この企業に優先順位表があるかどうかを取得します。
     *
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderExistsFlg(String companyCd) {
        int result = priorityOrderMstMapper.selectPriorityOrderCount(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS, result);
    }



    /**
     * Productpowercdクエリに関連付けられた優先順位テーブルcd
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public String selPriorityOrderCdForProdCd(String companyCd, Integer productPowerCd) {
        return priorityOrderMstMapper.selectPriorityOrderCdForProdCd(companyCd, productPowerCd);
    }





    /**
     * 表示自動計算実行ステータス
     * @param taskId
     * @return
     */
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
            return ResultMaps.result(ResultEnum.SUCCESS,"success");
        }
        return ResultMaps.result(ResultEnum.SUCCESS,"9");

    }

    /**
     * rankソートの再計算
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getNewReorder(String companyCd, Integer priorityOrderCd, String authorCd) {
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);

        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        String tableName = commonTableName.getProInfoTable();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        String proTableName = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
        List<Map<String,Object>> reorder = workPriorityOrderResultDataMapper.getProductReorder(companyCd, authorCd,
                workPriorityOrderMst.getProductPowerCd(), priorityOrderCd,tableName,zokuseiMsts,allCdList, proTableName);

        List<Map<String,Object>> newJanReorder = workPriorityOrderResultDataMapper.getNewJanProductReorder(companyCd, authorCd,
                workPriorityOrderMst.getProductPowerCd(), priorityOrderCd,tableName,proTableName,zokuseiMsts,allCdList);
        Map<String, List<Map<String, Object>>> ptsJan = reorder.stream().collect(Collectors.groupingBy(map -> {
            StringBuilder attrKey = new StringBuilder();
            for (Map<String, Object> col : attrCol) {
                attrKey.append(map.get("zokusei" + col.get("zokusei_col")));
            }

            return attrKey.toString();
        }));
        Map<String, List<Map<String, Object>>> newJan = newJanReorder.stream().collect(Collectors.groupingBy(map -> {
            StringBuilder attrKey = new StringBuilder();
            for (Map<String, Object> col : attrCol) {
                attrKey.append(map.get("zokusei" + col.get("zokusei_col")));
            }

            return attrKey.toString();
        }));
        Set<Map<String,Object>> set = new HashSet<>();
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : ptsJan.entrySet()) {
            for (Map.Entry<String, List<Map<String, Object>>> listEntry : newJan.entrySet()) {
                if (stringListEntry.getKey().equals(listEntry.getKey())){
                    List<Map<String, Object>> list1 = CommonUtil.janSort(stringListEntry.getValue(), listEntry.getValue(), "resultRank");
                    stringListEntry.setValue(list1);
                }
            }
        }
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : ptsJan.entrySet()) {
            set.addAll(stringListEntry.getValue());
        }
        set.addAll(newJanReorder);
        workPriorityOrderResultDataMapper.setRank(set,companyCd,authorCd,priorityOrderCd);
            workPriorityOrderResultDataMapper.setSortRank(reorder, companyCd, authorCd, priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 新規作成時に対応するテンポラリ・テーブルのすべての情報をクリア
     *
     * @param companyCd
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public  void  deleteWorkTable(String companyCd, Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();

        workPriorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //RestrictRelationテーブルをクリア
        workPriorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //RestrictResultテーブルをクリア
        workPriorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //RestrictSetテーブルをクリア
        workPriorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //ResultDataテーブルをクリア
        workPriorityOrderResultDataMapper.delResultData(companyCd, authorCd, priorityOrderCd);
        //スペーステーブルをクリア
        workPriorityOrderSpaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
        //Sortテーブルをクリア
        workPriorityOrderSortMapper.delete(companyCd, authorCd, priorityOrderCd);
        //janNewテーブルをクリア
        priorityOrderJanNewMapper.workDelete(companyCd, authorCd, priorityOrderCd);
        //クリアjan_replace
        priorityOrderJanReplaceMapper.workDelete(companyCd, authorCd, priorityOrderCd);
        //クリアワーク_priority_order_Cutテーブル
        priorityOrderJanCardMapper.workDelete(companyCd, priorityOrderCd, authorCd);
        //sortテーブルをクリア
        priorityOrderMstAttrSortMapper.delete(companyCd,priorityOrderCd);
        //composeテーブルをクリア
        basicPatternAttrMapper.delete(priorityOrderCd,companyCd);

        basicPatternRestrictRelationMapper.deleteByPrimaryKey(priorityOrderCd,companyCd);

        basicPatternRestrictResultMapper.deleteByPriorityCd(priorityOrderCd,companyCd);

        basicPatternRestrictResultDataMapper.deleteByPrimaryKey(priorityOrderCd);
        //ptsCdの取得
        Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
        //クリアワーク_priority_order_pts_data
        shelfPtsDataMapper.deletePtsData(id);
        //クリアワーク_priority_order_pts_data_taimst
        shelfPtsDataMapper.deletePtsTaimst(id);
        //クリアワーク_priority_order_pts_data_tanamst
        shelfPtsDataMapper.deletePtsTanamst(id);
        //クリアワーク_priority_order_pts_data_version
        shelfPtsDataMapper.deletePtsVersion(id);
        //クリアワーク_priority_order_pts_data_jandata
        shelfPtsDataMapper.deletePtsDataJandata(id);



    }

    @Override
    public Map<String, Object> getFaceKeisanForCgi(String[] array, String companyCd, Integer shelfPatternNo, String authorCd,String tokenInfo) {
        PriorityOrderJanCgiDto priorityOrderJanCgiDto = new PriorityOrderJanCgiDto();
        priorityOrderJanCgiDto.setDataArray(array);
        String uuid = UUID.randomUUID().toString();
        priorityOrderJanCgiDto.setGuid(uuid);
        priorityOrderJanCgiDto.setMode("idposaverage_data");
        priorityOrderJanCgiDto.setCompany(companyCd);
        priorityOrderJanCgiDto.setShelfPatternNo(shelfPatternNo);
        priorityOrderJanCgiDto.setUsercd(authorCd);
        logger.info("計算給FaceKeisancgi的参数{}", priorityOrderJanCgiDto);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");

        Map<String, Object> resultCgi = null;
        //再帰的にcgiを呼び出して、まずtaskidに行きます
        String result = cgiUtil.postCgi(path, priorityOrderJanCgiDto, tokenInfo);
        logger.info("taskId返回：{}", result);
        String queryPath = resourceBundle.getString("TaskQuery");
        //taskIdを持って、再度cgiに運転状態/データの取得を要求する
        resultCgi = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
        logger.info("保存優先順位表結果：{}", resultCgi);
        return resultCgi;
    }


    /**
     * taskIdを持って、再度cgiに運転状態/データの取得を要求する
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> saveAllWorkPriorityOrder(PriorityOrderMstVO primaryKeyVO) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = primaryKeyVO.getCompanyCd();
        Integer priorityOrderCd = primaryKeyVO.getPriorityOrderCd();
        String priorityOrderName = primaryKeyVO.getPriorityOrderName();

        try {
            //OrderMstを保存し、元のデータを削除
            priorityOrderMstMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderMstMapper.insertBySelect(companyCd, authorCd, priorityOrderCd, priorityOrderName);

            //OrderRestrictRelationを保存し、元のデータを削除
            priorityOrderRestrictRelationMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictRelationMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //OrderRestrictResultを保存し、元のデータを削除
            priorityOrderRestrictResultMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictResultMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //OrderRestrictSetを保存し、元のデータを削除
            priorityOrderRestrictSetMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderRestrictSetMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //ResultDataの保存、元のデータの削除
            priorityOrderResultDataMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderResultDataMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);


            //cutを保存し、元のデータを削除
            priorityOrderJanCardMapper.deleteByAuthorCd(companyCd, priorityOrderCd, authorCd);
            priorityOrderJanCardMapper.insertBySelect(companyCd, priorityOrderCd, authorCd);

            //jan_を保存新、元データの削除
            priorityOrderJanNewMapper.deleteByAuthorCd(companyCd, priorityOrderCd, authorCd);
            priorityOrderJanNewMapper.insertBySelect(companyCd, priorityOrderCd, authorCd);

            //jan_を保存replace,元のデータを削除する
            priorityOrderJanReplaceMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderJanReplaceMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //sortを保存し、元のデータを削除
            priorityOrderSortMapper.deleteByAuthorCd(companyCd, authorCd, priorityOrderCd);
            priorityOrderSortMapper.insertBySelect(companyCd, authorCd, priorityOrderCd);

            //ptsデータの保存
            shelfPtsService.saveFinalPtsData(companyCd, authorCd, priorityOrderCd);

            //work_basic_pattern_restrict_relation保存rank、元のデータを削除
            basicPatternRestrictRelationMapper.deleteFinal(companyCd, authorCd, priorityOrderCd);
            basicPatternRestrictRelationMapper.setFinalForWork(companyCd, authorCd, priorityOrderCd);

            //basic_pattern_attr_compose保存＃ホゾン＃
            basicPatternAttrMapper.deleteFinal(companyCd, authorCd, priorityOrderCd);
            basicPatternAttrMapper.setFinalForWork(companyCd, authorCd, priorityOrderCd);

            //basic_pattern_restrict_result保存＃ホゾン＃
            basicPatternRestrictResultMapper.deleteFinal(companyCd, authorCd, priorityOrderCd);
            basicPatternRestrictResultMapper.setFinalForWork(companyCd, authorCd, priorityOrderCd);

            //basic_pattern_restrict_result_data 保存＃ホゾン＃
            basicPatternRestrictResultDataMapper.deleteFinal(companyCd, authorCd, priorityOrderCd);
            basicPatternRestrictResultDataMapper.setFinalForWork(companyCd, authorCd, priorityOrderCd);

            //priority_order_mst_attrsort 保存＃ホゾン＃
            priorityOrderMstAttrSortMapper.deletFinal(companyCd,priorityOrderCd);
            priorityOrderMstAttrSortMapper.setFinalForWork(companyCd,priorityOrderCd);
        } catch (Exception exception) {
            logger.error("保存臨時表数据到實際表報錯", exception);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultMaps.result(ResultEnum.FAILURE);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 編集時にすべての情報を表示
     * @param companyCd
     * @param priorityOrderCd
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> getPriorityOrderAll(String companyCd, Integer priorityOrderCd,Integer isCover) {

        Integer id = shelfPtsDataMapper.getNewId(companyCd, priorityOrderCd);
        String aud = session.getAttribute("aud").toString();
        Integer newPriorityOrderCd = priorityOrderCd;
        Integer newId = id;
        if (isCover ==  null){
            isCover =0;
        }
        if (isCover == 1){
            newPriorityOrderCd = (int)idGeneratorService.priorityOrderNumGenerator().get("data");
             newId = priorityOrderShelfDataMapper.selectRegclass();

        }
        priorityOrderMstService.deleteWorkTable(companyCd, newPriorityOrderCd);

        priorityOrderJanCardMapper.setWorkForFinal(companyCd, priorityOrderCd, aud,newPriorityOrderCd);
        priorityOrderJanReplaceMapper.setWorkForFinal(companyCd, priorityOrderCd, aud,newPriorityOrderCd);
        priorityOrderJanNewMapper.setWorkForFinal(companyCd, priorityOrderCd, aud,newPriorityOrderCd);
        workPriorityOrderMstMapper.setWorkForFinal(companyCd, priorityOrderCd, aud,newPriorityOrderCd);
        workPriorityOrderResultDataMapper.setWorkForFinal(companyCd, priorityOrderCd, aud,newPriorityOrderCd);
        workPriorityOrderSortMapper.setWorkForFinal(companyCd, priorityOrderCd, aud,newPriorityOrderCd);
        priorityOrderMstAttrSortMapper.setWorkForFinal(companyCd,priorityOrderCd,newPriorityOrderCd);
        basicPatternAttrMapper.setWorkForFinal(companyCd,priorityOrderCd,newPriorityOrderCd);
        basicPatternRestrictRelationMapper.setWorkForFinal(companyCd,priorityOrderCd,newPriorityOrderCd);
        basicPatternRestrictResultMapper.setWorkForFinal(companyCd,priorityOrderCd,newPriorityOrderCd);
        basicPatternRestrictResultDataMapper.setWorkForFinal(companyCd,priorityOrderCd,newPriorityOrderCd);

        //ptsIdの取得
        shelfPtsDataMapper.insertWorkPtsData(companyCd, aud, priorityOrderCd,newPriorityOrderCd,newId);
        shelfPtsDataMapper.insertWorkPtsTaiData(companyCd, aud, id,newId);
        shelfPtsDataMapper.insertWorkPtsTanaData(companyCd, aud, id,newId);
        shelfPtsDataMapper.insertWorkPtsVersionData(companyCd, aud, id,newId);
        shelfPtsDataMapper.insertWorkPtsJanData(companyCd, aud, id,newId);
        Map<String, Object> map = new HashMap<>();
        //プライマリ・テーブル情報
        WorkPriorityOrderMstEditVo workPriorityOrderMst = workPriorityOrderMstMapper.getWorkPriorityOrderMst(companyCd, newPriorityOrderCd, aud);
        Integer shelfCd = workPriorityOrderMstMapper.getShelfName(workPriorityOrderMst.getShelfPatternCd().intValue());
        workPriorityOrderMst.setShelfCd(shelfCd);
        //group保存
        PriorityOrderAttrDto priorityOrderAttrDto = new PriorityOrderAttrDto();
        priorityOrderAttrDto.setCompanyCd(companyCd);
        priorityOrderAttrDto.setPriorityOrderCd(priorityOrderCd);
        priorityOrderAttrDto.setCommonPartsData(workPriorityOrderMst.getCommonPartsData());

        //商品力点数表情報
        //sort情報
        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrList(companyCd, newPriorityOrderCd);
        //陳列順情報の取得
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(workPriorityOrderMst.getCommonPartsData(), companyCd);
        List<WorkPriorityOrderSortVo> workPriorityOrderSort = shelfPtsDataMapper.getDisplays(companyCd, aud, newPriorityOrderCd,commonTableName.getProdIsCore(),commonTableName.getProdMstClass());

        //pts詳細の取得
        PtsDetailDataVo ptsDetailData = shelfPtsDataMapper.getPtsDetailData(workPriorityOrderMst.getShelfPatternCd().intValue());


        List<Map<String,Object>> attrList1 = priorityOrderMstAttrSortMapper.getAttrCol(companyCd, newPriorityOrderCd,commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, newPriorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());
        if (ptsDetailData != null){
            String zokuseiNm = Joiner.on(",").join(attrList1.stream().map(map3 -> MapUtils.getString(map3, "zokusei_nm")).collect(Collectors.toList()));
            String janHeader = ptsDetailData.getJanHeader()+",商品名,"+zokuseiNm+",幅,高,奥行";
            ptsDetailData.setJanHeader(janHeader);
            StringBuilder s = new StringBuilder("taiCd,tanaCd,tanapositionCd,jan,faceCount,faceMen,faceKaiten,tumiagesu,zaikosu");
            if ("V3.0".equals(ptsDetailData.getVersioninfo())){
                s.append(",faceDisplayflg,facePosition,depthDisplayNum");
            }
            s.append(",janName");
            for (Map<String, Object> map1 : attrCol) {
                s.append(",").append(map1.get("zokusei_colcd"));
            }
            s.append(",plano_width,plano_height,plano_depth");
            ptsDetailData.setJanColumns(s.toString());
            ptsDetailData.setTaiNum(shelfPtsDataMapper.getTaiNum(workPriorityOrderMst.getShelfPatternCd().intValue()));
            ptsDetailData.setTanaNum(shelfPtsDataMapper.getTanaNum(workPriorityOrderMst.getShelfPatternCd().intValue()));
            ptsDetailData.setFaceNum(shelfPtsDataMapper.getFaceNum(workPriorityOrderMst.getShelfPatternCd().intValue()));
            ptsDetailData.setSkuNum(shelfPtsDataMapper.getSkuNum(workPriorityOrderMst.getShelfPatternCd().intValue()));

            List<PtsTaiVo> taiData = shelfPtsDataMapper.getTaiData(workPriorityOrderMst.getShelfPatternCd().intValue());
            List<PtsTanaVo> tanaData = shelfPtsDataMapper.getTanaData(workPriorityOrderMst.getShelfPatternCd().intValue());
            String proTableName = MessageFormat.format(MagicString.PROD_JAN_INFO, MagicString.SPECIAL_SCHEMA_CD, MagicString.FIRST_CLASS_CD);
            List<LinkedHashMap<String,Object>> janData = shelfPtsDataMapper.getJanData(workPriorityOrderMst.getShelfPatternCd().intValue(),attrCol,
                    commonTableName.getProInfoTable(),janSizeCol, proTableName);
            ptsDetailData.setPtsTaiList(taiData);
            ptsDetailData.setPtsTanaVoList(tanaData);
            ptsDetailData.setPtsJanDataList(janData);
        }

        Map<String, Object> ptsNewDetailData = shelfPtsService.getNewPtsDetailData(workPriorityOrderMst.getShelfPatternCd().intValue(),companyCd, newPriorityOrderCd);
        String data = new Gson().toJson(ptsNewDetailData.get("data"));
        PtsDetailDataVo ptsDetailDataVo = new Gson().fromJson(data, new TypeToken<PtsDetailDataVo>(){}.getType());
        Map<String, Object> ptsInfoTemp = shelfPtsService.getTaiNumTanaNum(workPriorityOrderMst.getShelfPatternCd().intValue(),priorityOrderCd);
        //商品力情報
        ProductPowerMstVo productPowerInfo = productPowerMstMapper.getProductPowerInfo(companyCd, workPriorityOrderMst.getProductPowerCd());
        Integer skuNum = productPowerMstMapper.getSkuNum(companyCd, workPriorityOrderMst.getProductPowerCd());
        productPowerInfo.setSku(skuNum);

        Map<String, Object> ptsInfoTemps =(Map<String, Object>)ptsInfoTemp.get("data");
        Map<String, Object> attrDisplay = basicPatternMstService.getAttrDisplay(companyCd, newPriorityOrderCd);
        Map<String,Object> sortSettings = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String format = simpleDateFormat.format(productPowerInfo.getRegistered());
        sortSettings.put("workPriorityOrderSort",workPriorityOrderSort);
        sortSettings.put("partitionFlag",workPriorityOrderMst.getPartitionFlag());
        sortSettings.put("partitionVal",workPriorityOrderMst.getPartitionVal());
        sortSettings.put("heightSpaceFlag",workPriorityOrderMst.getTopPartitionFlag());
        sortSettings.put("heightSpace",workPriorityOrderMst.getTopPartitionVal());
        sortSettings.put("productPowerCd",workPriorityOrderMst.getProductPowerCd());
        sortSettings.put("productPowerName",productPowerInfo.getProductPowerName());
        sortSettings.put("authorName",productPowerInfo.getAuthorName());
        sortSettings.put("registered",format);
        sortSettings.put("sku",productPowerInfo.getSku());
        sortSettings.put("noRestrictionNum",productPowerInfo.getNoRestrictionNum());
        sortSettings.put("tanaWidthCheck",workPriorityOrderMst.getTanaWidCheck());

        Map<String,Object> shelfPatternSettings = new HashMap<>();
        Map<String,Object> tanapattanNum = new HashMap<>();
        shelfPatternSettings.put("shelfPatternCd",workPriorityOrderMst.getShelfPatternCd());
        shelfPatternSettings.put("shelfCd",workPriorityOrderMst.getShelfCd());
        tanapattanNum.put("shelfPatternName",ptsInfoTemps.get("shelfPatternName"));
        tanapattanNum.put("shelfName",ptsInfoTemps.get("shelfName"));
        shelfPatternSettings.put("commonPartsData",workPriorityOrderMst.getCommonPartsData());
        shelfPatternSettings.put("attrList",attrList);


        tanapattanNum.put("taiNum",shelfPtsDataMapper.getTaiNum(workPriorityOrderMst.getShelfPatternCd().intValue()));
        tanapattanNum.put("tanaNum",shelfPtsDataMapper.getTanaNum(workPriorityOrderMst.getShelfPatternCd().intValue()));
        if (ptsDetailData != null) {
            tanapattanNum.put("faceNum", ptsDetailData.getFaceNum());
            tanapattanNum.put("skuNum", ptsDetailData.getSkuNum());
            tanapattanNum.put("newFaceNum", ptsDetailData.getFaceNum());
        }

        tanapattanNum.put("newTaiNum",ptsDetailDataVo.getTaiNum());
        tanapattanNum.put("newTanaNum",ptsDetailDataVo.getTanaNum());
        tanapattanNum.put("newSkuNum",ptsDetailDataVo.getSkuNum());

        shelfPatternSettings.put("tanapattanNum",tanapattanNum);
        //商品の詳細
        map.put("shelfPatternSettings",shelfPatternSettings);
        map.put("SortSettings",sortSettings);
        map.put("ptsDetailData",ptsDetailData);
        map.put("ptsNewDetailData",ptsDetailDataVo);
        map.put("attrDisplay",attrDisplay.get("data"));
        map.put("ptsInfoTemp",ptsInfoTemp.get("data"));
        map.put("priorityOrderCd",newPriorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }

    /**
     * 編集時にptsの名前が存在するかどうかを確認
     * @param priorityOrderMstVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> checkOrderName(PriorityOrderMstVO priorityOrderMstVO) {
        String priorityOrderName = priorityOrderMstVO.getPriorityOrderName();
        Integer priorityOrderCd = priorityOrderMstVO.getPriorityOrderCd();
        String companyCd = priorityOrderMstVO.getCompanyCd();
        String authorCd = session.getAttribute("aud").toString();

        Integer orderNameCount = priorityOrderMstMapper.selectByOrderName(priorityOrderName, companyCd, authorCd);
        int isEdit = priorityOrderMstMapper.selectByPriorityOrderCd(priorityOrderCd);

        if (isEdit > 0) {
            //編集→名前の更新
            priorityOrderMstMapper.updateOrderName(priorityOrderCd, priorityOrderName);
             priorityOrderMstService.saveAllWorkPriorityOrder(priorityOrderMstVO);
            return ResultMaps.result(ResultEnum.SUCCESS);
        }

        //新しいルール
        if(orderNameCount>0){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        return priorityOrderMstService.saveAllWorkPriorityOrder(priorityOrderMstVO);


    }

    /**
     * 基本パターン削除
     * @param priorityOrderMstVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> deletePriorityOrderAll(PriorityOrderMstVO priorityOrderMstVO) {
        String aud = session.getAttribute("aud").toString();
        String companyCd = priorityOrderMstVO.getCompanyCd();
        Integer priorityOrderCd = priorityOrderMstVO.getPriorityOrderCd();
        //mstテーブルの削除
        priorityOrderMstMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除relation
        priorityOrderRestrictRelationMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除result表
        priorityOrderRestrictResultMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除set表
        priorityOrderRestrictSetMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除data表
        priorityOrderResultDataMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除sort表
        priorityOrderSortMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除rank表
        priorityOrderSortRankMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);
        //削除space表
        priorityOrderSpaceMapper.logicDeleteByPriorityOrderCd(companyCd, aud, priorityOrderCd);

        List<Integer> basicAllList = priorityOrderMstMapper.getBasicAllList(companyCd, priorityOrderCd);
        for (Integer basicAllCd : basicAllList) {
            PriorityAllSaveDto priorityAllSaveDto = new PriorityAllSaveDto();
            priorityAllSaveDto.setPriorityAllCd(basicAllCd);
            priorityAllSaveDto.setCompanyCd(companyCd);
            priorityAllMstService.deletePriorityAll(priorityAllSaveDto);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 各種mst展示
     * @param companyCd
     * @param priorityOrderCd
     * @param flag
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Override
    public Map<String, Object> getVariousMst(String companyCd, Integer priorityOrderCd, Integer flag) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String authorCd = session.getAttribute("aud").toString();
        //商品力点数表情報
        WorkPriorityOrderMstEditVo workPriorityOrderMst = workPriorityOrderMstMapper.getWorkPriorityOrderMst(companyCd, priorityOrderCd, authorCd);
        WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd,priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(priorityOrderMst.getCommonPartsData(), companyCd);
        if (flag == 0) {
            //取得janNew情報
            Map<String, Object> priorityOrderJanNew = priorityOrderJanNewService.getPriorityOrderJanNew(companyCd, priorityOrderCd, workPriorityOrderMst.getProductPowerCd());
            return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanNew.get("data"));
        }
        if (flag == 2) {
            ////取得janCut情報
            List<PriorityOrderJanCardVO> priorityOrderJanCut = priorityOrderJanCardMapper.selectJanCard(companyCd, priorityOrderCd,commonTableName);
            return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanCut);
        }
        if (flag == 1) {
            //取得jan変情報
            List<PriorityOrderJanReplaceVO> priorityOrderJanReplace = priorityOrderJanReplaceMapper.selectJanInfo(companyCd, priorityOrderCd,commonTableName);
            return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanReplace);
        }
        return ResultMaps.result(ResultEnum.FAILURE);
    }


}
