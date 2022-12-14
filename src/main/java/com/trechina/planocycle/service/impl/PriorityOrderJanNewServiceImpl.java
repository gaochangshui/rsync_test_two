package com.trechina.planocycle.service.impl;

import com.google.common.base.Joiner;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import com.trechina.planocycle.utils.CommonUtil;
import com.trechina.planocycle.utils.ListDisparityUtils;
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
public class PriorityOrderJanNewServiceImpl implements PriorityOrderJanNewService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper attrSortMapper;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;

    /**
     * ??????janList?????????
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd,Integer productPowerNo) {

            logger.info("????????????????????????list?????????{},{}",companyCd,priorityOrderCd);
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());

        List<Map<String,Object>> priorityOrderJanNewVOS = priorityOrderJanNewMapper.selectJanNew(companyCd,priorityOrderCd,
                zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol);
        Long shelfPatternCd = workPriorityOrderMst.getShelfPatternCd();
        Integer productPowerCd = workPriorityOrderMst.getProductPowerCd();

        List<Map<String,Object>> productPowerData = priorityOrderJanNewMapper.getProductPowerDataList(priorityOrderCd,
                zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol,shelfPatternCd,productPowerCd);
        for (Map<String, Object> productPowerDatum : productPowerData) {
            for (Map<String, Object> priorityOrderJanNewVO : priorityOrderJanNewVOS) {
                if (productPowerDatum.get(MagicString.JAN_CD).toString().equals(priorityOrderJanNewVO.get(MagicString.JAN_CD))){
                    priorityOrderJanNewVO.put(MagicString.ERROR_MSG,MagicString.DEL_ERROR_MSG);
                }
            }
        }
        logger.info("????????????????????????list???????????????b???{}",priorityOrderJanNewVOS);
           return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanNewVOS);
    }
    /**
     * ?????????jan????????????????????????
     * @param
     * @return
     *
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNewInfo(String[] janCd,String companyCd, Integer priorityOrderCd,Integer model) {

        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        WorkPriorityOrderMst workPriorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, authorCd, priorityOrderCd);
        String commonPartsData = workPriorityOrderMst.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(commonPartsData, companyCd);
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());

        List<Map<String,Object>> priorityOrderJanNewVOList = priorityOrderJanNewMapper.getDynamicJanNameClassify(priorityOrderCd,
                zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol,janCd,model);
        List<String> listNew = new ArrayList<>();
        for (Map<String,Object> priorityOrderJanNewVO : priorityOrderJanNewVOList) {
            listNew.add( priorityOrderJanNewVO.get(MagicString.JAN_CD).toString());
        }
        List<String> list = Arrays.asList(janCd);
        List<String> listDisparitStr = ListDisparityUtils.getListDisparitStr(list, listNew);
        String [] array = new String[listDisparitStr.size()];
        listDisparitStr.toArray(array);
        if (model == 0){
            Long shelfPatternCd = workPriorityOrderMst.getShelfPatternCd();
            Integer productPowerCd = workPriorityOrderMst.getProductPowerCd();
            List<Map<String,Object>> productPowerData = priorityOrderJanNewMapper.getProductPowerDataList(priorityOrderCd,
                    zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol,shelfPatternCd,productPowerCd);
            for (Map<String, Object> productPowerDatum : productPowerData) {
                for (Map<String, Object> priorityOrderJanNewVO : priorityOrderJanNewVOList) {
                    if (productPowerDatum.get(MagicString.JAN_CD).toString().equals(priorityOrderJanNewVO.get(MagicString.JAN_CD))){
                        priorityOrderJanNewVO.put(MagicString.ERROR_MSG,MagicString.DEL_ERROR_MSG);
                    }
                }
            }
        }

        Map<String,Object> map = new HashMap<>();
            map.put("array",array);
        map.put("priorityOrderJanNewVOList",priorityOrderJanNewVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanNew(List<PriorityOrderJanNew> priorityOrderJanNew) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = priorityOrderJanNew.get(0).getCompanyCd();
        Integer priorityOrderCd = priorityOrderJanNew.get(0).getPriorityOrderCd();

        shelfPtsDataMapper.deletePtsJandataByPriorityOrderCd(priorityOrderCd);
        priorityOrderJanNewMapper.workDelete(companyCd, authorCd, priorityOrderCd);
        if(priorityOrderJanNew.get(0).getJanNew() != null) {
            List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
            List<Integer> attrList = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());

            List<String> janList = priorityOrderJanNew.stream().map(PriorityOrderJanNew::getJanNew).collect(Collectors.toList());
            String [] janCd = new String[janList.size()];
            janList.toArray(janCd);
            Map<String, Object> priorityOrderJanNewInfo = this.getPriorityOrderJanNewInfo(janCd, companyCd, priorityOrderCd, 0);

            Map<String,Object> data = (Map<String,Object>)priorityOrderJanNewInfo.get("data");
            List<Map<String,Object>> datas = (List<Map<String,Object>>) data.get("priorityOrderJanNewVOList");
            for (Map<String, Object> objectMap : datas) {
                for (PriorityOrderJanNew orderJanNew : priorityOrderJanNew) {
                    if (objectMap.get(MagicString.JAN_CD).equals(orderJanNew.getJanNew())){
                        objectMap.put("rank",orderJanNew.getRank());
                    }
                }
            }
            Map<String, List<Map<String, Object>>> janGroup = datas.stream().collect(Collectors.groupingBy(map -> {
                StringBuilder attrKey = new StringBuilder();
                for (Integer col : attrList) {
                    attrKey.append(map.get(MagicString.ZOKUSEI_PREFIX + col));
                }
                return attrKey.toString();
            }));
            priorityOrderJanNew = this.rankCalculation(janGroup,companyCd,priorityOrderCd,priorityOrderJanNew);
            priorityOrderJanNewMapper.insert(priorityOrderJanNew, authorCd);

        }
            return ResultMaps.result(ResultEnum.SUCCESS);
    }
        public List<PriorityOrderJanNew> rankCalculation(Map<String, List<Map<String, Object>>> janGroup,String companyCd,Integer priorityOrderCd,List<PriorityOrderJanNew> priorityOrderJanNew){
            for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : janGroup.entrySet()) {
                Map<String,Object> map = new HashMap<>();
                map.put("companyCd",companyCd);
                map.put("priorityOrderCd",priorityOrderCd);
                map.put("data",stringListEntry.getValue());
                Map<String, Object> similarity = this.getSimilarity(map);
                List<Object> list = (List<Object>) similarity.get("data");
                List<Map<String,Object>>maps = (List<Map<String,Object>>)list.get(1);
                for (Map<String, Object> objectMap : maps) {
                    for (PriorityOrderJanNew orderJanNew : priorityOrderJanNew) {
                        if (objectMap.get(MagicString.JAN_CD).equals(orderJanNew.getJanNew())){
                            orderJanNew.setRank(Integer.valueOf(objectMap.get("rank").toString()));
                        }
                    }
                }
            }
            return priorityOrderJanNew;
        }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getSimilarity(Map<String,Object> map) {
        String companyCd = map.get("companyCd").toString();
        Integer priorityOrderCd = Integer.valueOf(map.get("priorityOrderCd").toString());
        List<Map<String,Object>> data = (List<Map<String,Object>>)map.get("data");
        String aud = session.getAttribute("aud").toString();
        //
        data.forEach(datum-> datum.putIfAbsent("rank",1));

        data = data.stream().sorted(Comparator.comparing(map1 -> MapUtils.getString(map1,MagicString.JAN_CD),Comparator.nullsFirst(String::compareTo).reversed()))
                .sorted(Comparator.comparing(map1 -> MapUtils.getInteger(map1,"rank"),Comparator.nullsFirst(Integer::compareTo))).collect(Collectors.toList());
        List<String> errorMsgJan = priorityOrderJanNewMapper.getErrorMsgJan(companyCd, priorityOrderCd);
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);
        WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, aud,priorityOrderCd);
        Integer productPowerCd = priorityOrderMst.getProductPowerCd();
        Long shelfPatternCd = priorityOrderMst.getShelfPatternCd();
        List<PriorityOrderMstAttrSort> mstAttrSorts = attrSortMapper.selectByPrimaryKey(companyCd, priorityOrderCd);
        List<Integer> attrList1 = mstAttrSorts.stream().map(vo->Integer.parseInt(vo.getValue())).collect(Collectors.toList());
        Map<String,Object> mapAttr = new HashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : data.get(0).entrySet()) {
            attrList1.forEach(integer->{
                if (stringObjectEntry.getKey().equals(MagicString.ZOKUSEI_PREFIX+integer)){
                    mapAttr.put(stringObjectEntry.getKey(),stringObjectEntry.getValue());
                }
            });
        }

        List<Map<String,Object>> list = new ArrayList<>();
        List<Integer> attrList = new ArrayList<>();
        mapAttr.entrySet().forEach(stringObjectEntry->{
            Map<String,Object> newMap = new HashMap<>();
            newMap.put("zokuseiId",stringObjectEntry.getKey().split(MagicString.ZOKUSEI_PREFIX)[1]);
            newMap.put("zokuseiValue",stringObjectEntry.getValue());
            list.add(newMap);
            attrList.add(Integer.valueOf(stringObjectEntry.getKey().split(MagicString.ZOKUSEI_PREFIX)[1]));
        });

        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        list.forEach(objectMap-> zokuseiCol.forEach(stringObjectMap->{
            if (objectMap.get("zokuseiId").equals(stringObjectMap.get("zokusei_id")+"")){
                objectMap.put("zokuseiCol",stringObjectMap.get("zokusei_col"));
            }
        }));
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(commonTableName.getProdIsCore(), commonTableName.getProdMstClass(), Joiner.on(",").join(attrList));
        List<Integer> allCdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable());


        List<Map<String,Object>> productPowerData = priorityOrderJanNewMapper.getProductPowerData(priorityOrderCd,
                zokuseiMsts, allCdList, commonTableName.getProInfoTable(),zokuseiCol,shelfPatternCd,productPowerCd,mapAttr);
        data.forEach(datum->{
            if (errorMsgJan.contains(datum.get(MagicString.JAN_CD).toString())){
                datum.put(MagicString.ERROR_MSG,MagicString.DEL_ERROR_MSG);
            }else {
                datum.put(MagicString.ERROR_MSG,"");
            }
        });
        List<Map<String, Object>> finalData = data;
        productPowerData.forEach(productPowerDatum-> finalData.forEach(datum->{
            if (productPowerDatum.get(MagicString.JAN_CD).toString().equals(datum.get(MagicString.JAN_CD))){
                datum.put(MagicString.ERROR_MSG,"pts??????????????????????????????????????????????????????????????????????????????");
            }
        }));
        
        if (!productPowerData.isEmpty()) {
            productPowerData = CommonUtil.janSort(productPowerData, data, "rank");
        }else {
            data = data.stream().sorted(Comparator.comparing(map1->MapUtils.getInteger(map1,"rank"))).collect(Collectors.toList());
            final int[] j = {1};
            data.forEach(datum-> datum.put("rank", j[0]++));
            productPowerData.addAll(data);
        }
        List<Object> list1 = new ArrayList<>();
        list1.add(data);
        list1.add(productPowerData);

        return ResultMaps.result(ResultEnum.SUCCESS,list1);
    }
    /**
     * ?????????????????????????????????????????????
     * @param janMstPlanocycleVo
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setJanNewInfo(List<JanMstPlanocycleVo> janMstPlanocycleVo) {
        String companyCd = janMstPlanocycleVo.get(0).getCompanyCd();

        priorityOrderJanNewMapper.deleteJanNewInfo(companyCd);
        priorityOrderJanNewMapper.setJanNewInfo(janMstPlanocycleVo,companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * ????????????????????????????????????
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getJanNewInfo(String companyCd) {
        List<JanMstPlanocycleVo> janNewInfo = priorityOrderJanNewMapper.getJanNewInfo(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,janNewInfo);
    }


}
