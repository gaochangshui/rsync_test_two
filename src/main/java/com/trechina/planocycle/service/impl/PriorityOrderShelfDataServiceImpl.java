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
import java.lang.reflect.InvocationTargetException;
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
    private BasicPatternRestrictRelationMapper restrictRelationMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
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
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());

        List<Map<String, Object>> ptsGroup = this.getPtsGroup(companyCd, priorityOrderCd);
        logger.info("ptsGroup：{}",ptsGroup);
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<Map<String, Object>> zokuseiNameList = zokuseiMstMapper.getzokuseiName(commonTableName.getProdIsCore(),commonTableName.getProdMstClass());

        Map<String, List<Map<String, Object>>> listMap = ptsGroup.stream().collect(Collectors.groupingBy(map -> {
            String attrKey = "";
            for (Integer col : attrList) {
                if (attrKey.equals("")){
                    attrKey += zokuseiNameList.get(col-1).get("zokusei_nm").toString()+"-"+map.get("zokusei" + col);
                }else {
                    attrKey +="->"+zokuseiNameList.get(col-1).get("zokusei_nm").toString()+"-"+ map.get("zokusei" + col);
                }
            }

            return attrKey;

        }));
        List<Map<String,Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : listMap.entrySet()) {
            String attrKey = "";
            for (Integer col : attrList) {
                if (attrKey.equals("")){
                    attrKey += zokuseiNameList.get(col-1).get("zokusei_nm").toString()+"-"+stringListEntry.getValue().get(0).get("zokuseiName"+col);
                }else {
                    attrKey +="->"+zokuseiNameList.get(col-1).get("zokusei_nm").toString()+"-"+ stringListEntry.getValue().get(0).get("zokuseiName"+col);
                }
            }
            Map<String,Object> map = new HashMap<>();
            map.put("restrictName",attrKey);
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
        list= list.stream().sorted(Comparator.comparing(map7->MapUtils.getInteger(map7,"restrictCd"))).collect(Collectors.toList());
        return ResultMaps.result(ResultEnum.SUCCESS,list);
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
        List<Map<String, Object>> ptsGroup = this.getPtsGroup(companyCd, priorityOrderCd);

        ptsGroup= ptsGroup.stream().filter(map->map.get("restrictCd").toString().equals(priorityOrderRestDto.getRestrictCd()+""))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"tanaCd")))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"taiCd")))
                .collect(Collectors.toList());
        return ResultMaps.result(ResultEnum.SUCCESS,ptsGroup);
    }

    /**
     * 新規では基本的なパタ台棚別jansの詳細情報を入手
     * @param priorityOrderPlatformShedDto
     * @return
     */
    @Override
    public Map<String, Object> getPlatformShedJans(PriorityOrderPlatformShedDto priorityOrderPlatformShedDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Integer priorityOrderCd = priorityOrderPlatformShedDto.getPriorityOrderCd();
        String companyCd = priorityOrderPlatformShedDto.getCompanyCd();
        List<Map<String, Object>> ptsGroup = this.getPtsGroup(companyCd, priorityOrderCd);
        ptsGroup= ptsGroup.stream().filter(map->map.get("taiCd").toString().equals(priorityOrderPlatformShedDto.getTaiCd()+"")&&
                map.get("tanaCd").toString().equals(priorityOrderPlatformShedDto.getTanaCd()+""))
                .sorted(Comparator.comparing(map -> MapUtils.getInteger(map,"rank")))
                .collect(Collectors.toList());

        return ResultMaps.result(ResultEnum.SUCCESS,ptsGroup);
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
    public List<Map<String, Object>> getPtsGroup(String companyCd,Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
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
        List<Map<String, Object>> zokuseiList = basicPatternRestrictResultMapper.selectNewJanZokusei(priorityOrderCd, id, zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol);
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

    @Override
    public Map<String, Object> getNewPlatformShedData(String companyCd, Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderPlatformShedDto> platformShedData = priorityOrderShelfDataMapper.getNewPlatformShedData(companyCd, authorCd,priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,platformShedData);
    }

}
