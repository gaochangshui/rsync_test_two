package com.trechina.planocycle.service.impl;

import com.google.common.base.Joiner;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPlatformShedDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestDto;
import com.trechina.planocycle.entity.dto.PriorityOrderRestrictJanDto;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.PriorityOrderShelfDataService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class PriorityOrderShelfDataServiceImpl implements PriorityOrderShelfDataService {

    @Autowired
    private PriorityOrderShelfDataMapper priorityOrderShelfDataMapper;
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    private BasicPatternRestrictResultMapper basicPatternRestrictResultMapper;
    @Autowired
    private BasicPatternRestrictResultDataMapper restrictResultDataMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    @Autowired
    private BasicPatternRestrictResultMapper restrictResultMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private PriorityAllPtsMapper priorityAllPtsMapper;
    @Autowired
    private HttpSession session;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 新規では基本的なパター制約に関する情報を入手
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getRestrictData(String companyCd,Integer priorityOrderCd)  {
        String authorCd = session.getAttribute("aud").toString();
        List<Map<String, Object>> ptsGroup = this.getPtsGroup(companyCd, priorityOrderCd,"planocycle.work_priority_order_pts_data_jandata");
        logger.info("ptsGroup：{}",ptsGroup);
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        Map<String, List<Map<String, Object>>> listMap = ptsGroup.stream().collect(Collectors.groupingBy(map -> {
            String attrKey = "";
            for (Map<String,Object> col : attrCol) {
                    attrKey += map.get( col.get("zokusei_colcd"));
            }

            return attrKey;

        }));
        String colHeader = "";
        String janColumns = "";
        for (Map<String,Object> col : attrCol) {
            if (colHeader.equals("")) {
                janColumns +=  col.get("zokusei_colname");
                colHeader +=col.get("zokusei_nm");
            }else {
                janColumns += "," + col.get("zokusei_colname");
                colHeader +=","+col.get("zokusei_nm");
            }
        }
        colHeader+=",SKU数,フェース数";
        janColumns+=",skuNum,faceNum";
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> mapHeader = new HashMap<>();
        mapHeader.put("groupHeader",colHeader);
        mapHeader.put("groupColumns",janColumns);
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : listMap.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            for (Map<String,Object> col : attrCol) {
                map.put( col.get("zokusei_colname").toString(),stringListEntry.getValue().get(0).getOrDefault(col.get("zokusei_colname"),""));
            }

            map.put("restrictCd",stringListEntry.getValue().get(0).get("restrictCd"));
            int face = 0;
            int sku =stringListEntry.getValue().size();
            for (Map<String, Object> objectMap : stringListEntry.getValue()) {
            face = face + Integer.parseInt(objectMap.get("faceNum").toString());
            }
            map.put("faceNum",face);
            map.put("skuNum",sku);
            list.add(map);
        }
        list= list.stream().sorted(Comparator.comparing(map7->MapUtils.getInteger(map7,"restrictCd",99))).collect(Collectors.toList());
        mapHeader.put("group",list);
        return ResultMaps.result(ResultEnum.SUCCESS,mapHeader);
    }
    /**
     * 新規では基本パター制約別janの詳細情報を取得
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getRestrictJans(PriorityOrderRestDto priorityOrderRestDto) {
        Integer priorityOrderCd = priorityOrderRestDto.getPriorityOrderCd();
        String companyCd = priorityOrderRestDto.getCompanyCd();
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);

        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);

            String commonPartsData = workPriorityOrderMst.getCommonPartsData();
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
            List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());

            List<Map<String, Object>> ptsGroup = this.getPtsGroup(companyCd, priorityOrderCd,"planocycle.work_priority_order_pts_data_jandata");
            ptsGroup= ptsGroup.stream().filter(map->map.get("restrictCd").toString().equals(priorityOrderRestDto.getRestrictCd()+""))
                    .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"rank")))
                    .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"tanaCd")))
                    .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"taiCd")))
                    .collect(Collectors.toList());
            Map<String,Object> mapHeader = new HashMap<>();
            String groupColumns = "taiCd,tanaCd,tanapositionCd,janCd,janName,plano_width,plano_height,plano_depth,rank,faceNum";
            String groupHeader = "台番号,棚段番号,棚位置,JAN,商品名,幅,高,奥行,RANK,フェース数";
            mapHeader.put("groupColumns",groupColumns);
            mapHeader.put("groupHeader",groupHeader);
        ptsGroup = this.ptsProcessing(ptsGroup,zokuseiCol);

        Integer ptsCd = shelfPtsDataMapper.getPtsCd(workPriorityOrderMst.getShelfPatternCd().intValue());
        List<Map<String, Object>> ptsOldGroup = this.getOldPtsGroup(companyCd, priorityOrderCd, "planocycle.shelf_pts_data_jandata",ptsCd);
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        Map<String, Object> groupOld = restrictResultMapper.getGroupOld(priorityOrderRestDto.getRestrictCd(), companyCd, priorityOrderCd, attrCol);
        ptsOldGroup = ptsOldGroup.stream().filter(map -> {
            int a = 0;
            for (Map.Entry<String, Object> stringObjectEntry : groupOld.entrySet()) {
                if (map.get(stringObjectEntry.getKey()).equals(stringObjectEntry.getValue())) {
                    a++;
                }
            }
            return a == groupOld.size();
        }).collect(Collectors.toList());
        ptsOldGroup = this.ptsProcessing(ptsOldGroup,zokuseiCol);
        ptsOldGroup = ptsOldGroup.stream()
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"tanapositionCd")))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"tanaCd")))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"taiCd"))).collect(Collectors.toList());
        mapHeader.put("newData",ptsGroup);
        mapHeader.put("oldData",ptsOldGroup);
        return ResultMaps.result(ResultEnum.SUCCESS,mapHeader);
    }

    List<Map<String,Object>> ptsProcessing(List<Map<String,Object>> ptsMap,List<Map<String,Object>> zokuseiCol){
        for (Map<String, Object> map : ptsMap) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                for (Map<String, Object> objectMap : zokuseiCol) {
                    if (("zokuseiName"+objectMap.get("zokusei_col")).equals(stringObjectEntry.getKey())){
                        map.put("zokusei"+objectMap.get("zokusei_col"),stringObjectEntry.getValue());
                    }
                }
            }
        }
        return ptsMap;
    }

    /**
     * 新規では基本的なパタ台棚別jansの詳細情報を入手
     * @param priorityOrderPlatformShedDto
     * @return
     */
    @Override
    public Map<String, Object> getPlatformShedJans(PriorityOrderPlatformShedDto priorityOrderPlatformShedDto)  {

        Integer priorityOrderCd = priorityOrderPlatformShedDto.getPriorityOrderCd();
        String companyCd = priorityOrderPlatformShedDto.getCompanyCd();
        String authorCd = session.getAttribute("aud").toString();

        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<Map<String, Object>> ptsGroup = this.getPtsGroup(companyCd, priorityOrderCd,"planocycle.work_priority_order_pts_data_jandata");
        ptsGroup= ptsGroup.stream().filter(map->map.get("taiCd").toString().equals(priorityOrderPlatformShedDto.getTaiCd()+"")&&
                map.get("tanaCd").toString().equals(priorityOrderPlatformShedDto.getTanaCd()+""))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"rank",0)))
                .collect(Collectors.toList());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        Map<String,Object> mapHeader = new HashMap<>();
        String groupColumns = "janCd,janName";
        String groupHeader = "JAN,商品名";
        for (Map<String, Object> map : attrCol) {
            groupColumns += ","+map.get("zokusei_colcd");
            groupHeader += ","+map.get("zokusei_nm");
        }
        groupColumns += ",plano_width,plano_height,plano_depth,rank,faceNum";
        groupHeader += ",幅,高,奥行,RANK,フェース数";

        mapHeader.put("groupColumns",groupColumns);
        mapHeader.put("groupHeader",groupHeader);
        for (Map<String, Object> map : ptsGroup) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                for (Map<String, Object> objectMap : attrCol) {
                    if (objectMap.get("zokusei_colname").equals(stringObjectEntry.getKey())){
                        map.put(objectMap.get("zokusei_colcd").toString(),stringObjectEntry.getValue());
                    }
                }
            }
        }
        mapHeader.put("newData",ptsGroup);
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(workPriorityOrderMst.getShelfPatternCd().intValue());
        List<Map<String, Object>> ptsOldGroup = this.getOldPtsGroup(companyCd, priorityOrderCd, "planocycle.shelf_pts_data_jandata",ptsCd);
        ptsOldGroup= ptsOldGroup.stream().filter(map->map.get("taiCd").toString().equals(priorityOrderPlatformShedDto.getTaiCd()+"")&&
                map.get("tanaCd").toString().equals(priorityOrderPlatformShedDto.getTanaCd()+""))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"rank",0)))
                .collect(Collectors.toList());
        for (Map<String, Object> map : ptsOldGroup) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                for (Map<String, Object> objectMap : attrCol) {
                    if (objectMap.get("zokusei_colname").equals(stringObjectEntry.getKey())){
                        map.put(objectMap.get("zokusei_colcd").toString(),stringObjectEntry.getValue());
                    }
                }
            }
        }
        mapHeader.put("oldData",ptsOldGroup);
        return ResultMaps.result(ResultEnum.SUCCESS,mapHeader);
    }
    /**
     * faceNumの保存
     * @param priorityOrderRestrictJanDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setFaceNumForData(List<PriorityOrderRestrictJanDto> priorityOrderRestrictJanDto) {
        logger.info("faceを保存するパラメータは:{}",priorityOrderRestrictJanDto);
        Integer ptsCd = workPriorityOrderResultDataMapper.getPtsCd(priorityOrderRestrictJanDto.get(0).getPriorityOrderCd());
        workPriorityOrderResultDataMapper.updateFaceNum(priorityOrderRestrictJanDto,ptsCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public List<Map<String, Object>> getPtsGroup(String companyCd,Integer priorityOrderCd,String tableName) {
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> restrictResult = restrictResultMapper.selectGroup(priorityOrderCd,attrCol);
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());
        List<Map<String, Object>> zokuseiList = basicPatternRestrictResultMapper.selectNewJanZokusei(priorityOrderCd, id, zokuseiMsts, allCdList,
                commonTableName.getProInfoTable(),attrCol,janSizeCol, tableName,workPriorityOrderMst.getProductPowerCd());
        for (int i = 0; i < zokuseiList.size(); i++) {
            Map<String, Object> zokusei = zokuseiList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
                int equalsCount = 0;
                for (Map<String,Object>  map : attrCol) {
                    String restrictKey = MapUtils.getString(restrict,  map.get("zokusei_colcd").toString());
                    String zokuseiKey = MapUtils.getString(zokusei,   map.get("zokusei_colcd").toString());

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

    @Override
    public Map<String, Object> getNewPlatformShedData(String companyCd, Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderPlatformShedDto> platformShedData = priorityOrderShelfDataMapper.getNewPlatformShedData(companyCd, authorCd,priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,platformShedData);
    }

    @Override
    public Map<String, Object> getPtsJanInfo(String companyCd, Integer priorityOrderCd) {

        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());
        String tableName ="";
        Integer id = null;
        //if (flag == 0){
        //    tableName = "planocycle.shelf_pts_data_jandata";
        //    ShelfPtsData ptsData = shelfPtsDataMapper.selectPtsCdByPatternCd(companyCd, workPriorityOrderMst.getShelfPatternCd());
        //    id = ptsData.getId();
        //}else {
             tableName = "planocycle.work_priority_order_pts_data_jandata";
             id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
        //}

        List<Map<String, Object>> zokuseiList = basicPatternRestrictResultMapper.getPtsJanInfo(priorityOrderCd, id, zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol,tableName,janSizeCol);
        Map<String,Object> map = new HashMap<>();
        map.put("width","幅");
        map.put("height","高さ");
        map.put("depth","奥行");
        for (Map<String, Object> objectMap : zokuseiCol) {
            map.put("zokuseiName"+objectMap.get("zokusei_id").toString(),objectMap.get("zokusei_nm"));
        }
        zokuseiList.add(0,map);
        return ResultMaps.result(ResultEnum.SUCCESS,zokuseiList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setFaceNumAndPositionForData(Map<String,Object> map) {
        String companyCd = map.get("companyCd").toString();
        int priorityOrderCd = 0;
        String ptsTableName="";
        String tableName ="";
        Integer patternCd = 0;
        int ptsFlag = Integer.parseInt(map.get("ptsFlag").toString());
        if ( ptsFlag == 0) {

            priorityOrderCd = Integer.parseInt(map.get("priorityOrderCd").toString());
             ptsTableName = MagicString.BASIC_PATTERN_PTS_TABLE_NAME;
             tableName = MagicString.BASIC_PATTERN_PTS_DATA_TABLE_NAME;
        }else if (ptsFlag == 1){
            patternCd = Integer.parseInt(map.get("patternCd").toString());
            priorityOrderCd = Integer.parseInt(map.get("priorityAllCd").toString());
            ptsTableName = MagicString.BASIC_ALL_PTS_TABLE_NAME;
            tableName = MagicString.BASIC_ALL_PTS_DATA_TABLE_NAME;
        }

        Integer id = shelfPtsDataMapper.getIdCommon(companyCd,priorityOrderCd,ptsTableName,ptsFlag,patternCd);
        if (Integer.parseInt(map.get("flag").toString()) == 0){
            priorityOrderShelfDataMapper.updateFaceNum(map,id,tableName);
        }else if (Integer.parseInt(map.get("flag").toString()) == 1){
            priorityOrderShelfDataMapper.delJan(map,id,tableName);
            List<Map<String, Object>> alikeTana = priorityOrderShelfDataMapper.getAlikeTana(map, id,tableName,ptsFlag);
            priorityOrderShelfDataMapper.updatePositionCd(alikeTana,id);
        }else {
            List<Map<String, Object>> alikeTana = priorityOrderShelfDataMapper.getAlikeTana(map, id,tableName,ptsFlag);
            if (Integer.parseInt(map.get("tanapositionCd").toString())-1>=alikeTana.size()){
                alikeTana.add(map);
            }else {
                alikeTana.add(Integer.parseInt(map.get("tanapositionCd").toString()) - 1, map);
            }
            int i = 1;
            for (Map<String, Object> stringObjectMap : alikeTana) {
                stringObjectMap.put("tanapositionCd",i++);
            }
            priorityOrderShelfDataMapper.delTana(map,id,tableName);
            priorityOrderShelfDataMapper.insertPosition(alikeTana,id,tableName,ptsFlag);
        }
        Map<String,Object> map1 = new HashMap<>();
        if (ptsFlag == 0) {
            map1.put("faceNum",shelfPtsDataMapper.getNewFaceNum(priorityOrderCd));
            map1.put("skuNum",shelfPtsDataMapper.getNewSkuNum(priorityOrderCd));
        }else if (ptsFlag == 1){
            return ResultMaps.result(ResultEnum.SUCCESS);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,map1);
    }

    @Override
    public Map<String, Object> getPtsAll(String companyCd, Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<Map<String, Object>> newPtsGroup = this.getNewPtsGroup(companyCd, priorityOrderCd);
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        Map<String,Object> mapHeader = new HashMap<>();
        String groupColumns = "";
        String groupHeader = "";
        for (Map<String, Object> map : attrCol) {
            if (groupColumns.equals("")){
                    groupColumns += map.get("zokusei_colcd");
                groupHeader += map.get("zokusei_nm");
            }else {
                groupColumns += ","+map.get("zokusei_colcd");
                groupHeader += ","+map.get("zokusei_nm");
            }
        }
        groupColumns += ",janCd,janName,janFlag,rank,taiCd,tanaCd,tanapositionCd,faceNum,faceMen,faceKaiten,tumiagesu,plano_width,plano_height,plano_depth";
        groupHeader += ",JAN,商品名,区分,RANK,台番号,棚段番号,棚位置,フェース数,フェース面,フェース回転,積上数,幅,高,奥行";

        mapHeader.put("groupColumns",groupColumns);
        mapHeader.put("groupHeader",groupHeader);
        for (Map<String, Object> map : newPtsGroup) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                for (Map<String, Object> objectMap : attrCol) {
                    if (objectMap.get("zokusei_colname").equals(stringObjectEntry.getKey())){
                        map.put(objectMap.get("zokusei_colcd").toString(),stringObjectEntry.getValue());
                    }
                }
            }
        }
        mapHeader.put("newData",newPtsGroup);
        return ResultMaps.result(ResultEnum.SUCCESS,mapHeader);
    }


    public List<Map<String, Object>> getOldPtsGroup(String companyCd,Integer priorityOrderCd,String tableName,Integer ptsCd) {
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());
        List<Map<String, Object>> restrictResult = restrictResultMapper.selectGroup(priorityOrderCd,attrCol);
        List<Map<String, Object>> zokuseiList = basicPatternRestrictResultMapper.selectOldJanZokusei(priorityOrderCd,ptsCd , zokuseiMsts, allCdList,
                commonTableName.getProInfoTable(),attrCol,janSizeCol, tableName,workPriorityOrderMst.getProductPowerCd());

        for (int i = 0; i < zokuseiList.size(); i++) {
            Map<String, Object> zokusei = zokuseiList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
                int equalsCount = 0;
                for (Map<String,Object>  map : attrCol) {
                    String restrictKey = MapUtils.getString(restrict,  map.get("zokusei_colcd").toString());
                    String zokuseiKey = MapUtils.getString(zokusei,  map.get("zokusei_colcd").toString());

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


    public List<Map<String, Object>> getNewPtsGroup(String companyCd,Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        Integer id = shelfPtsDataMapper.getId(companyCd, priorityOrderCd);
        List<Map<String, Object>> attrCol = attrSortMapper.getAttrColForName(companyCd, priorityOrderCd, commonTableName.getProdIsCore(),commonTableName.getProdMstClass());
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<Map<String, Object>> janSizeCol = zokuseiMstMapper.getJanSizeCol(commonTableName.getProAttrTable());
        List<Map<String, Object>> restrictResult = restrictResultMapper.selectGroup(priorityOrderCd,attrCol);
        List<Map<String, Object>> zokuseiList = basicPatternRestrictResultMapper.getPtsAllJan(priorityOrderCd,id , zokuseiMsts, allCdList,
                commonTableName.getProInfoTable(),attrCol,janSizeCol, workPriorityOrderMst.getProductPowerCd());

        for (int i = 0; i < zokuseiList.size(); i++) {
            Map<String, Object> zokusei = zokuseiList.get(i);
            for (Map<String, Object> restrict : restrictResult) {
                int equalsCount = 0;
                for (Map<String,Object>  map : zokuseiCol) {
                    String restrictKey = MapUtils.getString(restrict, MagicString.ZOKUSEI_PREFIX + map.get("zokusei_colcd"));
                    String zokuseiKey = MapUtils.getString(zokusei, MagicString.ZOKUSEI_PREFIX + map.get("zokusei_colcd"));

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
